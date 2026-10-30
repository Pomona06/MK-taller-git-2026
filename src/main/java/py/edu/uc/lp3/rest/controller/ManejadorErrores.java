package py.edu.uc.lp3.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import py.edu.uc.lp3.exceptions.JugadorEliminadoException;
import py.edu.uc.lp3.exceptions.JuegoException;
import py.edu.uc.lp3.exceptions.MunicionInsuficienteException;
import py.edu.uc.lp3.exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;

import java.util.Map;

/**
 * Un handler por cada tipo de la jerarquía JuegoException. Las reglas viven en
 * el dominio y en el servicio; acá solo se elige el código HTTP y se devuelve
 * el mensaje de la excepción junto con su tipo.
 */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ValorInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> valorInvalido(ValorInvalidoException e) {
        return cuerpo(e);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> noEncontrado(RecursoNoEncontradoException e) {
        return cuerpo(e);
    }

    @ExceptionHandler(MunicionInsuficienteException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> municionInsuficiente(MunicionInsuficienteException e) {
        return cuerpo(e);
    }

    @ExceptionHandler(JugadorEliminadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> jugadorEliminado(JugadorEliminadoException e) {
        return cuerpo(e);
    }

    private Map<String, String> cuerpo(JuegoException e) {
        return Map.of("error", e.getMessage(), "excepcion", e.getClass().getSimpleName());
    }
}
