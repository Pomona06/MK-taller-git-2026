package py.edu.uc.lp3.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import py.edu.uc.lp3.exceptions.CompaneroNoDisponibleException;
import py.edu.uc.lp3.exceptions.EquipoCompletoException;
import py.edu.uc.lp3.exceptions.GranadaUsadaException;
import py.edu.uc.lp3.exceptions.JugadorEliminadoException;
import py.edu.uc.lp3.exceptions.JuegoException;
import py.edu.uc.lp3.exceptions.MunicionInsuficienteException;
import py.edu.uc.lp3.exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3.exceptions.RechazoDeA2Exception;
import py.edu.uc.lp3.exceptions.RechazoDeA3Exception;
import py.edu.uc.lp3.exceptions.RechazoDeCompaneroException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;


/**
 * Un handler por cada tipo de la jerarquía JuegoException. Las reglas viven en
 * los servicios; acá solo se elige el código HTTP y se devuelve el mensaje de
 * la excepción junto con su tipo.
 */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ValorInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse valorInvalido(ValorInvalidoException e) {
        return cuerpo(e);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse noEncontrado(RecursoNoEncontradoException e) {
        return cuerpo(e);
    }

    @ExceptionHandler(MunicionInsuficienteException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse municionInsuficiente(MunicionInsuficienteException e) {
        return cuerpo(e);
    }

    @ExceptionHandler(JugadorEliminadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse jugadorEliminado(JugadorEliminadoException e) {
        return cuerpo(e);
    }

    @ExceptionHandler(EquipoCompletoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse equipoCompleto(EquipoCompletoException e) {
        return cuerpo(e);
    }

    @ExceptionHandler(GranadaUsadaException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse granadaUsada(GranadaUsadaException e) {
        return cuerpo(e);
    }

    /** POO-09: la regla de A2 rechazó la jugada del escenario. */
    @ExceptionHandler(RechazoDeA2Exception.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDeCompaneroResponse rechazoDeA2(RechazoDeA2Exception e) {
        return rechazo(e);
    }

    /** POO-09: la regla de A3 rechazó la jugada del escenario. */
    @ExceptionHandler(RechazoDeA3Exception.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDeCompaneroResponse rechazoDeA3(RechazoDeA3Exception e) {
        return rechazo(e);
    }

    /** POO-09: un compañero no respondió; la jugada no se pudo evaluar. */
    @ExceptionHandler(CompaneroNoDisponibleException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorDeCompaneroResponse companeroNoDisponible(CompaneroNoDisponibleException e) {
        return new ErrorDeCompaneroResponse(e.getMessage(), e.getClass().getSimpleName(), e.getRol(),
                e.getCompanero(), null, null);
    }

    private ErrorDeCompaneroResponse rechazo(RechazoDeCompaneroException e) {
        return new ErrorDeCompaneroResponse(e.getMessage(), e.getClass().getSimpleName(), e.getRol(),
                e.getCompanero(), e.getStatusDelCompanero(), e.getRespuestaDelCompanero());
    }

    private ErrorResponse cuerpo(JuegoException e) {
        return new ErrorResponse(e.getMessage(), e.getClass().getSimpleName());
    }
}
