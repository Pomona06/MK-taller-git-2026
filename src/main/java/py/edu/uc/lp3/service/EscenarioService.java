package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.Jugada;
import py.edu.uc.lp3.service.dto.DisparoRapidoRequest;

import java.util.List;

/**
 * POO-09: el escenario de disparo rápido, que necesita que acepten las reglas
 * de dos compañeros (A2 y A3) antes de guardar la jugada.
 */
public interface EscenarioService {

    Jugada disparoRapido(DisparoRapidoRequest pedido);

    List<Jugada> jugadas();

    Jugada jugada(Long id);
}
