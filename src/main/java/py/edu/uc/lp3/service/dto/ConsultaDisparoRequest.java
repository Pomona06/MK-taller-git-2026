package py.edu.uc.lp3.service.dto;

/**
 * ¿Puede esta arma disparar? Se manda su estado, no un id: la consulta no lee
 * ni cambia la base. Para un arma de fuego: carga (omitida = cargador lleno),
 * modoRafaga (omitido = false) y veces (omitido = 1). Para una granada:
 * lanzada (omitido = false).
 */
public record ConsultaDisparoRequest(String modelo, Integer carga, Boolean modoRafaga, Boolean lanzada,
                                     Integer veces) {}
