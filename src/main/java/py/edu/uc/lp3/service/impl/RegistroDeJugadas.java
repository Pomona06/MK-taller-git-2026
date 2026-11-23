package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Armas;
import py.edu.uc.lp3.domain.Jugada;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Sniper;
import py.edu.uc.lp3.exceptions.JugadorEliminadoException;
import py.edu.uc.lp3.exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.JugadaRepository;
import py.edu.uc.lp3.repository.JugadorRepository;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;

/**
 * Guarda una jugada que ya aceptaron A2 y A3, en una sola transacción: el
 * francotirador gasta las balas del disparo rápido, el objetivo queda con la
 * vida y el blindaje que calculó A3, y se crea la Jugada. Está separado de
 * EscenarioServiceImpl para que las llamadas HTTP a los compañeros queden
 * fuera de la transacción. Antes de cambiar nada vuelve a verificar las
 * reglas propias (munición, y que el tirador y el objetivo sigan vivos), por
 * si el estado cambió mientras se esperaba a los compañeros.
 */
@Component
public class RegistroDeJugadas {

    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;
    private final JugadaRepository jugadaRepository;
    private final JsonMapper json;

    public RegistroDeJugadas(ArmaRepository armaRepository, JugadorRepository jugadorRepository,
                             JugadaRepository jugadaRepository, JsonMapper json) {
        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
        this.jugadaRepository = jugadaRepository;
        this.json = json;
    }

    @Transactional
    public Jugada guardar(JugadaAceptada j) {
        Armas arma = armaRepository.findById(j.armaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("un arma", j.armaId()));
        Jugador tirador = jugadorRepository.findById(j.tiradorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("un jugador", j.tiradorId()));
        Jugador objetivo = jugadorRepository.findById(j.objetivoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("un jugador", j.objetivoId()));
        Sniper sniper = (Sniper) arma;
        ReglasDeArmas.verificarQuePuedeDisparar(sniper, EscenarioServiceImpl.BALAS_DEL_DISPARO_RAPIDO);
        if (tirador.estaEliminado()) {
            throw new JugadorEliminadoException(tirador.getNombre(), "disparar");
        }
        if (objetivo.estaEliminado()) {
            throw new JugadorEliminadoException(objetivo.getNombre());
        }

        int vidaAntes = objetivo.getVida();
        int blindajeAntes = objetivo.getBlindaje();
        int vidaPerdida = Math.min(vidaAntes, Math.max(0, vidaAntes - j.resultadoA3().vidaFinal()));
        int blindajePerdido = Math.min(blindajeAntes, Math.max(0, blindajeAntes - j.resultadoA3().blindajeFinal()));
        for (int i = 0; i < EscenarioServiceImpl.BALAS_DEL_DISPARO_RAPIDO; i++) {
            sniper.disparar(j.distancia());
        }
        objetivo.recibirImpacto(vidaPerdida, blindajePerdido);
        armaRepository.save(sniper);
        jugadorRepository.save(objetivo);

        return jugadaRepository.save(new Jugada(LocalDateTime.now(), tirador.getId(), tirador.getNombre(),
                tirador.getEquipo(), sniper.getId(),
                sniper.getNombre(), objetivo.getId(), objetivo.getNombre(), objetivo.getEquipo(), j.distancia(),
                j.conMira(), j.estabilidad(), EscenarioServiceImpl.BALAS_DEL_DISPARO_RAPIDO, sniper.getCarga(),
                j.danio(), vidaAntes, objetivo.getVida(), blindajeAntes, objetivo.getBlindaje(),
                objetivo.estaEliminado(), json.writeValueAsString(j.respuestaA2()),
                json.writeValueAsString(j.resultadoA3().respuesta())));
    }
}
