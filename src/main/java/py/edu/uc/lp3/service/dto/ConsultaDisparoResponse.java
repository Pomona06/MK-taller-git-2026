package py.edu.uc.lp3.service.dto;

/** La regla de munición permite el disparo. cargaDespues es null si el arma es una granada. */
public record ConsultaDisparoResponse(boolean permitido, String modelo, int balasNecesarias, Integer cargaDespues,
                                      String detalle) {}
