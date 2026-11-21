package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.media.Schema;

/** Cuerpo de cualquier error del servicio. */
@Schema(description = "Error del servicio: el mensaje y el tipo de excepción que lo produjo")
public record ErrorResponse(
        @Schema(description = "Qué falló, en una frase", example = "AWP no tiene cargadores de reserva")
        String error,
        @Schema(description = "Tipo de excepción: ValorInvalidoException (400), RecursoNoEncontradoException (404), "
                + "MunicionInsuficienteException, GranadaUsadaException o JugadorEliminadoException (409)",
                example = "MunicionInsuficienteException")
        String excepcion
) {}
