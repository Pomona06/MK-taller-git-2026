package py.edu.uc.lp3.service.dto;

/**
 * Cuerpo JSON para crear un jugador. No lleva id. vida omitida = 100;
 * blindaje omitido o 0 = sin chaleco.
 */
public record JugadorRequest(String nombre, Integer vida, Integer blindaje) {}
