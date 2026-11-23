package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.domain.Armas;
import py.edu.uc.lp3.service.ArmaService;
import py.edu.uc.lp3.service.dto.ArmaPatchRequest;
import py.edu.uc.lp3.service.dto.ArmaRequest;
import py.edu.uc.lp3.service.dto.DisparoResponse;

import java.net.URI;
import java.util.List;

/**
 * CRUD REST sobre el recurso "armas". La URL nombra el recurso y el verbo
 * HTTP dice qué hacer. El controller solo traduce HTTP: las validaciones y
 * las reglas están en el servicio.
 */
@RestController
@RequestMapping("/api/armas")
@Tag(name = "Armas")
public class ArmaCrudController {

    private final ArmaService armaService;

    public ArmaCrudController(ArmaService armaService) {
        this.armaService = armaService;
    }

    @PostMapping
    @Operation(summary = "Crear un arma del catálogo",
            description = "Solo lleva el modelo (ver GET /api/catalogo) y el equipo, CT o TT. El equipo se puede "
                    + "omitir si el arma es de un solo equipo. Las estadísticas salen del catálogo.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "AK-47 (solo TT)", value = "{\"modelo\": \"AK-47\"}"),
            @ExampleObject(name = "AWP para CT", value = "{\"modelo\": \"AWP\", \"equipo\": \"CT\"}"),
            @ExampleObject(name = "AK-47 para CT (400)", value = "{\"modelo\": \"AK-47\", \"equipo\": \"CT\"}")}))
    @ApiResponse(responseCode = "201", description = "Creada; el cuerpo trae el id y el encabezado Location la URL")
    @ApiResponse(responseCode = "400", description = "Modelo inexistente, equipo inválido o que no puede comprar ese modelo",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Armas> crear(@RequestBody ArmaRequest pedido) {
        Armas creada = armaService.crear(pedido);
        return ResponseEntity.created(URI.create("/api/armas/" + creada.getId())).body(creada);
    }

    @GetMapping
    @Operation(summary = "Listar las armas guardadas", description = "Con un filtro opcional por equipo o por precio máximo.")
    @ApiResponse(responseCode = "200", description = "Las armas guardadas")
    @ApiResponse(responseCode = "400", description = "Equipo distinto de CT o TT, o precio negativo",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public List<Armas> listar(@Parameter(description = "CT o TT") @RequestParam(required = false) String equipo,
                              @Parameter(description = "Precio máximo en dólares del juego")
                              @RequestParam(required = false) Long precioMaximo) {
        return armaService.listar(equipo, precioMaximo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Leer un arma")
    @ApiResponse(responseCode = "200", description = "El arma")
    @ApiResponse(responseCode = "404", description = "RecursoNoEncontradoException: no hay un arma con ese id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Armas buscar(@PathVariable Long id) {
        return armaService.buscar(id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Cambiar el apodo o el modo ráfaga",
            description = "El apodo (Name Tag) tiene hasta 20 caracteres y no se le pone a una granada. El modo "
                    + "ráfaga solo existe en la Glock-18 y la FAMAS. Un campo omitido queda como estaba.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "Apodo", value = "{\"apodo\": \"La Fiel\"}"),
            @ExampleObject(name = "Ráfaga (FAMAS o Glock-18)", value = "{\"modoRafaga\": true}")}))
    @ApiResponse(responseCode = "200", description = "El arma con el cambio guardado")
    @ApiResponse(responseCode = "400", description = "Apodo vacío o largo, Name Tag en una granada o ráfaga en un arma que no la tiene",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "RecursoNoEncontradoException",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Armas actualizar(@PathVariable Long id, @RequestBody ArmaPatchRequest cambios) {
        return armaService.actualizar(id, cambios);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar un arma")
    @ApiResponse(responseCode = "204", description = "Borrada")
    @ApiResponse(responseCode = "404", description = "RecursoNoEncontradoException",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        armaService.borrar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/disparos")
    @Operation(summary = "Disparar un arma guardada (regla de munición)",
            description = "Gasta las balas y guarda la munición; una granada queda lanzada. Si la regla no se "
                    + "cumple, no cambia nada.")
    @ApiResponse(responseCode = "200", description = "Qué pasó en cada disparo y cómo quedó el arma")
    @ApiResponse(responseCode = "400", description = "Distancia negativa o mayor al alcance del arma, veces menor que 1 "
            + "o más de un lanzamiento de granada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "RecursoNoEncontradoException",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "MunicionInsuficienteException o GranadaUsadaException",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public DisparoResponse disparar(@PathVariable Long id,
                                    @Parameter(description = "Distancia al objetivo en metros")
                                    @RequestParam(required = false) Double distancia,
                                    @Parameter(description = "Cuántas veces disparar (por defecto, 1)")
                                    @RequestParam(required = false) Integer veces) {
        return armaService.disparar(id, distancia, veces);
    }

    @PostMapping("/{id}/recargas")
    @Operation(summary = "Recargar un arma guardada (regla de munición)",
            description = "Usa un cargador de la reserva. Con el cargador lleno no cambia nada.")
    @ApiResponse(responseCode = "200", description = "Cómo quedó el arma")
    @ApiResponse(responseCode = "400", description = "Es una granada: no se recarga",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "RecursoNoEncontradoException",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "MunicionInsuficienteException: sin cargadores de reserva",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public DisparoResponse recargar(@PathVariable Long id) {
        return armaService.recargar(id);
    }
}
