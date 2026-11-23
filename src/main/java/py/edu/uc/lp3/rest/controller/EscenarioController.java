package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.domain.Jugada;
import py.edu.uc.lp3.service.EscenarioService;
import py.edu.uc.lp3.service.dto.DisparoRapidoRequest;

import java.net.URI;
import java.util.List;

/** POO-09: el escenario de disparo rápido, que consulta las reglas de A2 y de A3. */
@RestController
@RequestMapping("/api/escenarios/disparos-rapidos")
@Tag(name = "Escenario")
public class EscenarioController {

    private final EscenarioService escenarioService;

    public EscenarioController(EscenarioService escenarioService) {
        this.escenarioService = escenarioService;
    }

    @PostMapping
    @Operation(summary = "Disparo rápido con francotirador: consulta a A2 y a A3 y guarda la jugada si los dos aceptan",
            description = "El jugador guardado tiradorId hace un disparo rápido (dos balas) con un francotirador guardado "
                    + "de su equipo (AWP o SSG 08) contra el jugador guardado objetivoId. Antes de consultar a nadie se "
                    + "controla que el tirador esté vivo, que el arma sea de su equipo y que la bala llegue a esa "
                    + "distancia. Primero A2 (Matías) valida el disparo rápido: con mira la estabilidad es 1.0 y sin mira "
                    + "0.5; hacen falta más de 0.7 y dos balas. Después A3 (Joaquín) valida el daño: el objetivo tiene "
                    + "que ser un enemigo vivo, y calcula la vida y el blindaje que le quedan. Si los dos aceptan, el arma "
                    + "gasta dos balas, el objetivo queda como dijo A3 y se guarda la jugada. Si alguno rechaza, no "
                    + "cambia nada y la respuesta dice quién fue.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "Feliz: AWP con mira contra un enemigo",
                    value = "{\"armaId\": 2, \"tiradorId\": 2, \"objetivoId\": 1, \"distancia\": 40, \"conMira\": true}"),
            @ExampleObject(name = "Rechazo de A2: sin mira",
                    value = "{\"armaId\": 2, \"tiradorId\": 2, \"objetivoId\": 1, \"distancia\": 40, \"conMira\": false}")}))
    @ApiResponse(responseCode = "201", description = "Los dos aceptaron: la jugada quedó guardada")
    @ApiResponse(responseCode = "400", description = "Faltan ids, el tirador y el objetivo son el mismo, la distancia es "
            + "negativa o mayor al alcance del arma, el arma no es un francotirador o no es del equipo del tirador",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "No existe el arma, el tirador o el objetivo",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "RechazoDeA2Exception o RechazoDeA3Exception: rechazó la regla de un "
            + "compañero; JugadorEliminadoException si el tirador está eliminado (también MunicionInsuficienteException "
            + "si el estado cambió mientras se consultaba)",
            content = @Content(schema = @Schema(implementation = ErrorDeCompaneroResponse.class)))
    @ApiResponse(responseCode = "503", description = "CompaneroNoDisponibleException: A2 o A3 no respondió",
            content = @Content(schema = @Schema(implementation = ErrorDeCompaneroResponse.class)))
    public ResponseEntity<Jugada> disparoRapido(@RequestBody DisparoRapidoRequest pedido) {
        Jugada jugada = escenarioService.disparoRapido(pedido);
        return ResponseEntity.created(URI.create("/api/escenarios/disparos-rapidos/" + jugada.getId())).body(jugada);
    }

    @GetMapping
    @Operation(summary = "Las jugadas guardadas del escenario")
    public List<Jugada> jugadas() {
        return escenarioService.jugadas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Una jugada guardada")
    @ApiResponse(responseCode = "200", description = "La jugada")
    @ApiResponse(responseCode = "404", description = "No hay una jugada con ese id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Jugada jugada(@PathVariable Long id) {
        return escenarioService.jugada(id);
    }
}
