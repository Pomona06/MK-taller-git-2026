package py.edu.uc.lp3.service.dto;

/**
 * Cuerpo JSON para crear un arma. No lleva id: lo genera la base. "modelo" es
 * un arma del catálogo de CS2 ("AK-47", "AWP", "HE"...) y elige la clase hija;
 * "equipo" es CT o TT y se puede omitir si el arma es de un solo equipo. Las
 * estadísticas no se mandan: salen del catálogo.
 */
public record ArmaRequest(String modelo, String equipo) {}
