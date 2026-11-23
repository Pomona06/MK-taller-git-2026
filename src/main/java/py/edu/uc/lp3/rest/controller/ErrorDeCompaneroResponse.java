package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.media.Schema;

/** Cuerpo del error del escenario cuando un compañero rechaza o no responde. */
@Schema(description = "Error del escenario por un compañero: quién fue y qué respondió")
public record ErrorDeCompaneroResponse(
        @Schema(description = "Qué pasó, en una frase", example = "la jugada la rechazó A2 (Matías Jara (Vanzfranhait)) con HTTP 400")
        String error,
        @Schema(description = "RechazoDeA2Exception o RechazoDeA3Exception (409), o CompaneroNoDisponibleException (503)",
                example = "RechazoDeA2Exception")
        String excepcion,
        @Schema(description = "Quién fue: A2 o A3", example = "A2")
        String rechazadoPor,
        @Schema(description = "Nombre del compañero", example = "Matías Jara (Vanzfranhait)")
        String companero,
        @Schema(description = "Código HTTP que respondió el compañero (null si no respondió)", example = "400")
        Integer statusDelCompanero,
        @Schema(description = "El cuerpo que respondió el compañero, tal cual (null si no respondió)")
        Object respuestaDelCompanero
) {}
