package py.edu.uc.lp3.service.dto;

/** La regla de munición permite la recarga (o no hace falta, si el cargador está lleno). */
public record ConsultaRecargaResponse(boolean permitido, String modelo, int cargaDespues, int cargadoresDespues,
                                      String detalle) {}
