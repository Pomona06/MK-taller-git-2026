package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.cliente.DanioA3;
import py.edu.uc.lp3.cliente.DisparoRapidoA2;
import py.edu.uc.lp3.cliente.ResultadoDanioA3;
import py.edu.uc.lp3.domain.Armas;
import py.edu.uc.lp3.domain.Jugada;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Sniper;
import py.edu.uc.lp3.exceptions.CompaneroNoDisponibleException;
import py.edu.uc.lp3.exceptions.JugadorEliminadoException;
import py.edu.uc.lp3.exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.JugadaRepository;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3.service.EscenarioService;
import py.edu.uc.lp3.service.dto.DisparoRapidoRequest;

import java.util.List;
import java.util.Map;

/**
 * POO-09. El escenario: un jugador guardado hace un disparo rápido (dos balas
 * de una vez) con un francotirador de su equipo contra otro jugador guardado.
 * Antes de consultar a nadie se controla lo propio: que existan, que el arma
 * sea un francotirador de su equipo, que la bala llegue a esa distancia, que
 * el tirador esté vivo y que no se dispare a sí mismo. Es legal solo si lo
 * aceptan las reglas de los dos compañeros:
 *
 * 1. A2 (Matías), disparo rápido: el francotirador tiene que estar estable
 *    (con mira = 1.0, sin mira = 0.5; su regla pide más de 0.7) y tener al
 *    menos dos balas. Va primero porque, si el disparo no puede ocurrir, no
 *    tiene sentido preguntar qué le haría al objetivo.
 * 2. A3 (Joaquín), daño con blindaje: el objetivo tiene que ser un enemigo
 *    vivo (otro equipo, vida mayor a 0); calcula con cuánta vida y blindaje
 *    queda. El daño que se le manda es el de las dos balas a esa distancia
 *    (danoA de este servicio).
 *
 * Las dos consultas van antes de tocar la base. Si alguna rechaza, se lanza
 * RechazoDeA2Exception o RechazoDeA3Exception y no se guarda nada. Si las dos
 * aceptan, RegistroDeJugadas guarda todo en una sola transacción.
 */
@Service
public class EscenarioServiceImpl implements EscenarioService {

    static final int BALAS_DEL_DISPARO_RAPIDO = 2;
    static final double ESTABILIDAD_CON_MIRA = 1.0;
    static final double ESTABILIDAD_SIN_MIRA = 0.5;

    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;
    private final JugadaRepository jugadaRepository;
    private final DisparoRapidoA2 a2;
    private final DanioA3 a3;
    private final RegistroDeJugadas registro;

    public EscenarioServiceImpl(ArmaRepository armaRepository, JugadorRepository jugadorRepository,
                                JugadaRepository jugadaRepository, DisparoRapidoA2 a2, DanioA3 a3,
                                RegistroDeJugadas registro) {
        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
        this.jugadaRepository = jugadaRepository;
        this.a2 = a2;
        this.a3 = a3;
        this.registro = registro;
    }

    @Override
    public Jugada disparoRapido(DisparoRapidoRequest p) {
        if (p == null || p.armaId() == null || p.tiradorId() == null || p.objetivoId() == null) {
            throw new ValorInvalidoException("el escenario necesita armaId, tiradorId y objetivoId");
        }
        if (p.tiradorId().equals(p.objetivoId())) {
            throw new ValorInvalidoException("el tirador y el objetivo son el mismo jugador: nadie se dispara a sí mismo");
        }
        double distancia = ReglasDeArmas.distancia(p.distancia());
        boolean conMira = Boolean.TRUE.equals(p.conMira());

        Armas arma = armaRepository.findById(p.armaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("un arma", p.armaId()));
        if (!(arma instanceof Sniper sniper)) {
            throw new ValorInvalidoException(arma.getNombre()
                    + " no es un francotirador: el disparo rápido es con AWP o SSG 08");
        }
        ReglasDeArmas.verificarAlcance(sniper.getModelo(), distancia);
        Jugador tirador = jugadorConEquipo(p.tiradorId());
        Jugador objetivo = jugadorConEquipo(p.objetivoId());
        if (!tirador.getEquipo().equals(sniper.getEquipo())) {
            throw new ValorInvalidoException(tirador.getNombre() + " es del equipo " + tirador.getEquipo() + " y el "
                    + sniper.getNombre() + " (id " + sniper.getId() + ") es del equipo " + sniper.getEquipo()
                    + ": solo lo usa un jugador de su equipo");
        }
        if (tirador.estaEliminado()) {
            throw new JugadorEliminadoException(tirador.getNombre(), "disparar");
        }

        // 1. A2: ¿el disparo rápido es posible?
        double estabilidad = conMira ? ESTABILIDAD_CON_MIRA : ESTABILIDAD_SIN_MIRA;
        Map<String, Object> respuestaA2 = a2.consultar(estabilidad, sniper.getCarga());

        // 2. A3: ¿qué le hacen las dos balas al objetivo?
        int danio = sniper.danoA(distancia) * BALAS_DEL_DISPARO_RAPIDO;
        ResultadoDanioA3 resultadoA3 = a3.consultar(tirador.getNombre(), tirador.getEquipo(), objetivo.getNombre(),
                objetivo.getEquipo(), objetivo.getVida(), objetivo.getBlindaje(), danio);
        if (resultadoA3.vidaFinal() < 0 || resultadoA3.vidaFinal() > objetivo.getVida()
                || resultadoA3.blindajeFinal() < 0 || resultadoA3.blindajeFinal() > objetivo.getBlindaje()) {
            throw new CompaneroNoDisponibleException("A3", a3.getNombre(),
                    "respondió una vida o un blindaje imposibles para este objetivo");
        }

        // 3. Los dos aceptaron: recién ahora se toca la base.
        return registro.guardar(new JugadaAceptada(sniper.getId(), tirador.getId(), objetivo.getId(), distancia, conMira,
                estabilidad, danio, respuestaA2, resultadoA3));
    }

    /** Un jugador guardado que tiene equipo (los guardados antes de POO-09 pueden no tenerlo). */
    private Jugador jugadorConEquipo(Long id) {
        Jugador jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("un jugador", id));
        if (jugador.getEquipo() == null) {
            throw new ValorInvalidoException(jugador.getNombre() + " no tiene equipo: creá el jugador con CT o TT");
        }
        return jugador;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jugada> jugadas() {
        return jugadaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Jugada jugada(Long id) {
        return jugadaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("una jugada", id));
    }
}
