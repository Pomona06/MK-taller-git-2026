package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.service.SimulacionService;
import py.edu.uc.lp3.service.dto.SimulacionDanoRequest;
import py.edu.uc.lp3.service.dto.SimulacionDanoResponse;

@RestController
@RequestMapping("/api/simulaciones")
@Tag(name = "Simulación")
public class SimulacionController {

    private final SimulacionService simulacionService;

    public SimulacionController(SimulacionService simulacionService) {
        this.simulacionService = simulacionService;
    }

    @PostMapping("/dano")
    @Operation(summary = "Un arma guardada le dispara a un jugador guardado (las dos reglas)",
            description = "Verifica la regla de vida y la de munición, reparte el daño entre la vida y el chaleco y "
                    + "guarda el arma y el jugador. Si una regla falla, no se guarda nada. Distancia omitida = 10 m.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(
            name = "AK-47 contra el jugador 1 a 10 m", value = "{\"armaId\": 1, \"jugadorId\": 1, \"distancia\": 10}")))
    @ApiResponse(responseCode = "200", description = "Qué pasó; el arma y el jugador ya quedaron guardados así")
    @ApiResponse(responseCode = "400", description = "Faltan armaId o jugadorId, o la distancia es negativa o mayor al alcance del arma",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "RecursoNoEncontradoException: no existe el arma o el jugador",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "JugadorEliminadoException, MunicionInsuficienteException o GranadaUsadaException",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public SimulacionDanoResponse simularDano(@RequestBody SimulacionDanoRequest pedido) {
        return simulacionService.simularDano(pedido);
    }
}
