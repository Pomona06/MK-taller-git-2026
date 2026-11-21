package py.edu.uc.lp3.service.dto;

/**
 * ¿Qué le pasa a un jugador que recibe un disparo de este modelo a esta
 * distancia? vida omitida = 100; blindaje omitido = 0 (sin chaleco);
 * distancia omitida = 10 m.
 */
public record ConsultaImpactoRequest(String modelo, Double distancia, Integer vida, Integer blindaje) {}
