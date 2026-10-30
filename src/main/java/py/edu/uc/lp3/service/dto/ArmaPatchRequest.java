package py.edu.uc.lp3.service.dto;

/**
 * Cuerpo JSON de PATCH. Solo se pueden cambiar el nombre y el precio; un
 * campo omitido (null) queda como estaba.
 */
public record ArmaPatchRequest(String nombre, Long precio) {}
