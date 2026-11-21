package py.edu.uc.lp3.service.dto;

/**
 * Cuerpo JSON para crear un jugador. No lleva id ni vida: como en cada ronda
 * de CS2, empieza con 100. "chaleco": true si compró Kevlar (omitido = no).
 */
public record JugadorRequest(String nombre, Boolean chaleco) {}
