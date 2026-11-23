package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.service.JugadorService;
import py.edu.uc.lp3.service.dto.JugadorRequest;

import java.net.URI;
import java.util.List;

/**
 * Los jugadores que reciben el daño. No hay PATCH: la vida y el blindaje
 * solo cambian simulando (POST /api/simulaciones/dano).
 */
@RestController
@RequestMapping("/api/jugadores")
@Tag(name = "Jugadores")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @PostMapping
    @Operation(summary = "Crear un jugador",
            description = "Es de un equipo (CT o TT) y empieza con 100 de vida, como en cada ronda. Con \"chaleco\": true, "
                    + "compró Kevlar (100 de blindaje).")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "CT con chaleco", value = "{\"nombre\": \"Enemigo\", \"equipo\": \"CT\", \"chaleco\": true}"),
            @ExampleObject(name = "TT sin chaleco", value = "{\"nombre\": \"Bot\", \"equipo\": \"TT\"}")}))
    @ApiResponse(responseCode = "201", description = "Creado; el cuerpo trae el id")
    @ApiResponse(responseCode = "400", description = "Nombre vacío o de más de 32 caracteres, o equipo distinto de CT o TT",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Jugador> crear(@RequestBody JugadorRequest pedido) {
        Jugador creado = jugadorService.crear(pedido);
        return ResponseEntity.created(URI.create("/api/jugadores/" + creado.getId())).body(creado);
    }

    @GetMapping
    @Operation(summary = "Listar los jugadores guardados")
    public List<Jugador> listar(@Parameter(description = "true: solo los que tienen vida mayor que 0")
                                @RequestParam(required = false) Boolean vivos) {
        return jugadorService.listar(vivos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Leer un jugador", description = "Con la vida y el blindaje que dejó la última simulación.")
    @ApiResponse(responseCode = "200", description = "El jugador")
    @ApiResponse(responseCode = "404", description = "RecursoNoEncontradoException: no hay un jugador con ese id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Jugador buscar(@PathVariable Long id) {
        return jugadorService.buscar(id);
    }
}
