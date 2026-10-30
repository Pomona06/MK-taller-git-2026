package py.edu.uc.lp3.service.dto;

/**
 * Pedido de simulación: qué arma guardada dispara (armaId), a qué distancia,
 * y el jugador que recibe el disparo (nombre, vida y blindaje del chaleco;
 * blindaje 0 o ausente = sin chaleco).
 */
public record SimulacionDanoRequest(Long armaId, Double distancia, String jugador, Integer vida, Integer blindaje) {}
