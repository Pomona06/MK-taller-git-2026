package py.edu.uc.lp3.service.dto;

/**
 * Cuerpo JSON de PATCH. Se puede cambiar el apodo (Name Tag) y, en la
 * Glock-18 y la FAMAS, el modo ráfaga. Un campo omitido (null) queda como
 * estaba.
 */
public record ArmaPatchRequest(String apodo, Boolean modoRafaga) {}
