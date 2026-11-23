package py.edu.uc.lp3.service.dto;

/**
 * Pedido del escenario de POO-09: el jugador guardado tiradorId hace un
 * disparo rápido con el francotirador guardado armaId (de su mismo equipo)
 * contra el jugador guardado objetivoId. "conMira" dice si apuntó con la mira
 * (omitido = false); distancia en metros (omitida = 10).
 */
public record DisparoRapidoRequest(Long armaId, Long tiradorId, Long objetivoId, Double distancia, Boolean conMira) {}
