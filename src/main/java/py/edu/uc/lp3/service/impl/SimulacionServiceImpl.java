package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Armas;
import py.edu.uc.lp3.domain.ArmasDeFuego;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3.service.SimulacionService;
import py.edu.uc.lp3.service.dto.SimulacionDanoRequest;
import py.edu.uc.lp3.service.dto.SimulacionDanoResponse;

/**
 * Participan tres clases del modelo, las tres guardadas en H2: el arma
 * (cualquier hija de Armas), el Jugador y su Chaleco. El arma gasta munición
 * (regla de munición) y el jugador recibe el daño a través del chaleco (regla
 * de vida). Es una transacción: si alguna regla falla, no se guarda nada, ni
 * la bala gastada ni la vida perdida.
 */
@Service
@Transactional
public class SimulacionServiceImpl implements SimulacionService {

    private static final double DISTANCIA_POR_DEFECTO = 10.0;

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
        Armas arma = armaRepository.findById(p.armaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("un arma", p.armaId()));
        Jugador jugador = jugadorRepository.findById(p.jugadorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("un jugador", p.jugadorId()));
        double distancia = p.distancia() == null ? DISTANCIA_POR_DEFECTO : p.distancia();

        int vidaAntes = jugador.getVida();
        String disparo = arma.disparar(distancia);              // regla de munición
        int danoDelArma = arma.danoA(distancia);
        int danoRecibido = jugador.recibirDano(danoDelArma);    // regla de vida (pasa por el chaleco)
        armaRepository.save(arma);
        jugadorRepository.save(jugador);

        int cargaDespues = arma instanceof ArmasDeFuego deFuego ? deFuego.getCarga() : 0;
        return new SimulacionDanoResponse(arma.getId(), arma.getNombre(), arma.getClass().getSimpleName(),
                disparo, cargaDespues, danoDelArma, jugador.getId(), jugador.getNombre(), danoRecibido,
                vidaAntes, jugador.getVida(), jugador.getBlindaje(), jugador.estaEliminado());
    }
}
