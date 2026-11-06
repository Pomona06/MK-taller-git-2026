package py.edu.uc.lp3.service.dto;

/**
 * Pedido de simulación: qué arma guardada (armaId) le dispara a qué jugador
 * guardado (jugadorId), y a qué distancia (omitida = 10 m).
 */
public record SimulacionDanoRequest(Long armaId, Long jugadorId, Double distancia) {}
