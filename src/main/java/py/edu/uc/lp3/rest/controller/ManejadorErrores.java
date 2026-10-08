package py.edu.uc.lp3.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Las reglas viven en el dominio: si un constructor rechaza un valor, acá
 * solo se traduce ese rechazo a un 400 con el mensaje de la clase, en vez
 * de dejar que el servicio responda 500.
 */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> valorInvalido(IllegalArgumentException e) {
        return Map.of("error", e.getMessage());
    }
}
