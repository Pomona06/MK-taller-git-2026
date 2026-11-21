package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.service.ReglasService;
import py.edu.uc.lp3.service.dto.ConsultaDisparoRequest;
import py.edu.uc.lp3.service.dto.ConsultaDisparoResponse;
import py.edu.uc.lp3.service.dto.ConsultaImpactoRequest;
import py.edu.uc.lp3.service.dto.ConsultaImpactoResponse;
import py.edu.uc.lp3.service.dto.ConsultaRecargaRequest;
import py.edu.uc.lp3.service.dto.ConsultaRecargaResponse;
import py.edu.uc.lp3.service.dto.ReglaResponse;

import java.util.List;

/**
 * Las dos reglas, para que otro servicio las consulte. Son POST porque llevan
 * un cuerpo con el estado a evaluar, pero no crean ni cambian nada.
 */
@RestController
@RequestMapping("/api/reglas")
@Tag(name = "Reglas")
public class ReglasController {

    private final ReglasService reglasService;

    public ReglasController(ReglasService reglasService) {
        this.reglasService = reglasService;
    }

    @GetMapping
    @Operation(summary = "Las dos reglas en una frase",
            description = "Qué regla publica este servicio, cómo consultarla y con qué error la rechaza.")
    public List<ReglaResponse> reglas() {
        return reglasService.reglas();
    }

    @PostMapping("/disparo")
    @Operation(summary = "Regla de munición: ¿puede disparar?",
            description = "Un arma de fuego necesita en el cargador las balas del disparo (una, o tres en ráfaga, "
                    + "por cada vez). Una granada se lanza una sola vez. No lee ni cambia la base.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "AK-47 con una bala", value = "{\"modelo\": \"AK-47\", \"carga\": 1}"),
            @ExampleObject(name = "FAMAS en ráfaga, 2 disparos",
                    value = "{\"modelo\": \"FAMAS\", \"carga\": 25, \"modoRafaga\": true, \"veces\": 2}"),
            @ExampleObject(name = "HE ya lanzada (409)", value = "{\"modelo\": \"HE\", \"lanzada\": true}")}))
    @ApiResponse(responseCode = "200", description = "La regla lo permite")
    @ApiResponse(responseCode = "400", description = "Modelo inexistente, carga fuera de rango o ráfaga en un arma que no la tiene",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "MunicionInsuficienteException o GranadaUsadaException",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ConsultaDisparoResponse disparo(@RequestBody ConsultaDisparoRequest pedido) {
        return reglasService.consultarDisparo(pedido);
    }

    @PostMapping("/recarga")
    @Operation(summary = "Regla de munición: ¿puede recargar?",
            description = "Recargar usa un cargador de la reserva. Con el cargador lleno no hace falta (200). "
                    + "Las granadas no se recargan. No lee ni cambia la base.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "AWP vacío con un cargador de reserva",
                    value = "{\"modelo\": \"AWP\", \"carga\": 0, \"cargadoresRestantes\": 1}"),
            @ExampleObject(name = "AWP sin reserva (409)",
                    value = "{\"modelo\": \"AWP\", \"carga\": 0, \"cargadoresRestantes\": 0}")}))
    @ApiResponse(responseCode = "200", description = "La regla lo permite")
    @ApiResponse(responseCode = "400", description = "Modelo inexistente, granada o valores fuera de rango",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "MunicionInsuficienteException: no quedan cargadores de reserva",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ConsultaRecargaResponse recarga(@RequestBody ConsultaRecargaRequest pedido) {
        return reglasService.consultarRecarga(pedido);
    }

    @PostMapping("/impacto")
    @Operation(summary = "Regla de vida: ¿qué le hace este disparo a un jugador?",
            description = "El daño baja con la distancia; con chaleco, la vida recibe daño × relación de blindaje ÷ 2 "
                    + "y el chaleco pierde la mitad de lo que desvió. La vida no baja de 0. Un jugador con vida 0 "
                    + "ya está eliminado. No lee ni cambia la base.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "AK-47 a 10 m contra un jugador con chaleco",
                    value = "{\"modelo\": \"AK-47\", \"distancia\": 10, \"vida\": 100, \"blindaje\": 100}"),
            @ExampleObject(name = "Jugador eliminado (409)", value = "{\"modelo\": \"AK-47\", \"vida\": 0}")}))
    @ApiResponse(responseCode = "200", description = "La regla lo permite; el cuerpo trae el resultado del impacto")
    @ApiResponse(responseCode = "400", description = "Modelo inexistente, distancia negativa, vida o blindaje fuera de 0 a 100",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "JugadorEliminadoException: el jugador ya tiene vida 0",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ConsultaImpactoResponse impacto(@RequestBody ConsultaImpactoRequest pedido) {
        return reglasService.consultarImpacto(pedido);
    }
}
