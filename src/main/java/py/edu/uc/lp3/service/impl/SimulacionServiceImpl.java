package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Armas;
import py.edu.uc.lp3.domain.ArmasDeFuego;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.exceptions.JugadorEliminadoException;
import py.edu.uc.lp3.exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3.service.SimulacionService;
import py.edu.uc.lp3.service.dto.SimulacionDanoRequest;
import py.edu.uc.lp3.service.dto.SimulacionDanoResponse;

/**
 * Las dos reglas del juego viven acá. Participan tres clases, las tres
 * guardadas en H2: el arma (cualquier hija de Armas), el Jugador y su Chaleco.
 *
 * 1. Regla de munición: el arma tiene que tener las balas del disparo y una
 *    granada se lanza una sola vez (ReglasDeArmas).
 * 2. Regla de vida: un jugador eliminado no recibe más daño, y el daño se
 *    reparte entre la vida y el blindaje según la relación de blindaje del
 *    arma (calcularImpacto).
 *
 * Es una transacción: si una regla falla, no se guarda nada.
 */
@Service
@Transactional
public class SimulacionServiceImpl implements SimulacionService {

    /** De cada punto de daño que absorbe, el chaleco pierde la mitad. */
    private static final double BONO_BLINDAJE = 0.5;

    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;

    public SimulacionServiceImpl(ArmaRepository armaRepository, JugadorRepository jugadorRepository) {
        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
    }

    @Override
    public SimulacionDanoResponse simularDano(SimulacionDanoRequest p) {
        if (p == null || p.armaId() == null || p.jugadorId() == null) {
            throw new ValorInvalidoException("la simulación necesita armaId y jugadorId");
        }
        double distancia = ReglasDeArmas.distancia(p.distancia());
        Armas arma = armaRepository.findById(p.armaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("un arma", p.armaId()));
        Jugador jugador = jugadorRepository.findById(p.jugadorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("un jugador", p.jugadorId()));

        if (jugador.estaEliminado()) {
            throw new JugadorEliminadoException(jugador.getNombre());          // regla de vida
        }
        ReglasDeArmas.verificarQuePuedeDisparar(arma, 1);                       // regla de munición

        int vidaAntes = jugador.getVida();
        int dano = arma.danoA(distancia);
        int[] impacto = calcularImpacto(dano, arma.getModelo().getRelacionBlindaje(),
                jugador.getBlindaje(), jugador.getVida());
        String disparo = arma.disparar(distancia);
        jugador.recibirImpacto(impacto[0], impacto[1]);
        armaRepository.save(arma);
        jugadorRepository.save(jugador);

        Integer carga = arma instanceof ArmasDeFuego deFuego ? deFuego.getCarga() : null;
        return new SimulacionDanoResponse(arma.getId(), arma.getNombre(), arma.getClass().getSimpleName(),
                disparo, carga, dano, jugador.getId(), jugador.getNombre(), vidaAntes, impacto[0], impacto[1],
                jugador.getVida(), jugador.getBlindaje(), jugador.estaEliminado());
    }

    /**
     * Regla de vida. Sin blindaje, todo el daño va a la vida. Con blindaje, la
     * vida recibe daño × (relación de blindaje / 2) y el chaleco pierde la
     * mitad de lo que absorbió; si no le alcanza, absorbe lo que le queda y el
     * resto pasa a la vida. La vida nunca baja de 0.
     *
     * @return {vida perdida, blindaje perdido}
     */
    static int[] calcularImpacto(int dano, double relacionBlindaje, int blindaje, int vida) {
        double aLaVida = dano;
        double alBlindaje = 0;
        if (blindaje > 0 && dano > 0) {
            aLaVida = dano * relacionBlindaje * 0.5;
            alBlindaje = (dano - aLaVida) * BONO_BLINDAJE;
            if (alBlindaje > blindaje) {
                alBlindaje = blindaje;
                aLaVida = dano - blindaje / BONO_BLINDAJE;
            }
        }
        int vidaPerdida = Math.min((int) aLaVida, vida);
        return new int[]{vidaPerdida, (int) alBlindaje};
    }
}
