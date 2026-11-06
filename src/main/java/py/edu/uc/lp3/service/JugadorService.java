package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.service.dto.JugadorRequest;

import java.util.List;

/** Los jugadores que reciben el daño en la simulación, guardados en H2. */
public interface JugadorService {

    Jugador crear(JugadorRequest pedido);

    /** vivos = true: solo los que tienen vida mayor a 0. */
    List<Jugador> listar(Boolean vivos);

    Jugador buscar(Long id);
}
