package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Armas;
import py.edu.uc.lp3.domain.Chaleco;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.service.SimulacionService;
import py.edu.uc.lp3.service.dto.SimulacionDanoRequest;
import py.edu.uc.lp3.service.dto.SimulacionDanoResponse;

/**
 * Participan tres clases del modelo: el arma guardada en H2 (cualquier hija de
 * Armas), el Jugador y su Chaleco. El arma gasta munición (regla de munición)
 * y el jugador recibe el daño (regla de vida). Es una transacción: si alguna
 * regla falla, la munición gastada no se guarda.
 */
@Service
@Transactional
public class SimulacionServiceImpl implements SimulacionService {

    private static final double DISTANCIA_POR_DEFECTO = 10.0;

    private final ArmaRepository armaRepository;

    public SimulacionServiceImpl(ArmaRepository armaRepository) {
        this.armaRepository = armaRepository;
    }

    @Override
    public SimulacionDanoResponse simularDano(SimulacionDanoRequest p) {
        if (p.armaId() == null) {
            throw new ValorInvalidoException("falta armaId");
        }
        Armas arma = armaRepository.findById(p.armaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("un arma", p.armaId()));
        int blindaje = p.blindaje() == null ? 0 : p.blindaje();
        Jugador jugador = new Jugador(
                p.jugador() == null ? "Jugador" : p.jugador(),
                p.vida() == null ? Jugador.VIDA_MAXIMA : p.vida(),
                blindaje > 0 ? new Chaleco(blindaje) : null);
        double distancia = p.distancia() == null ? DISTANCIA_POR_DEFECTO : p.distancia();

        int vidaAntes = jugador.getVida();
        String disparo = arma.disparar(distancia);   // regla de munición
        int danoDelArma = arma.danoA(distancia);
        int danoRecibido = jugador.recibirDano(danoDelArma);   // regla de vida
        armaRepository.save(arma);

        return new SimulacionDanoResponse(arma.getNombre(), arma.getClass().getSimpleName(), disparo,
                danoDelArma, danoRecibido, vidaAntes, jugador.getVida(), jugador.getBlindaje(),
                jugador.estaEliminado());
    }
}
