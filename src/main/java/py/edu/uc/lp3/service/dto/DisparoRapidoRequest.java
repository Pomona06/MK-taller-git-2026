package py.edu.uc.lp3.service.dto;

/**
 * Pedido del escenario de POO-09: el francotirador guardado (armaId) hace un
 * disparo rápido contra un jugador guardado (objetivoId). "tirador" es el
 * nombre de quien dispara (omitido = "Tirador"); su equipo es el del arma.
 * "conMira" dice si apuntó con la mira (omitido = false); distancia en metros
 * (omitida = 10).
 */
public record DisparoRapidoRequest(Long armaId, Long objetivoId, String tirador, Double distancia, Boolean conMira) {}
