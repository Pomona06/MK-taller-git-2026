package py.edu.uc.lp3.service.dto;

/** Una regla del juego que este servicio publica, en una frase, y cómo consultarla. */
public record ReglaResponse(String nombre, String enUnaFrase, String consulta, String rechazo) {}
