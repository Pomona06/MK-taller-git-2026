package py.edu.uc.lp3.service.dto;

/** La regla de vida permite el impacto y este sería el resultado. */
public record ConsultaImpactoResponse(boolean permitido, String modelo, int danoSinBlindaje, int vidaPerdida,
                                      int blindajePerdido, int vidaDespues, int blindajeDespues,
                                      boolean eliminado) {}
