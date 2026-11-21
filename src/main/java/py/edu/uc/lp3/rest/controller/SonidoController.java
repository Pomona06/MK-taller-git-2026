package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.service.SonidoService;

import java.util.List;

/**
 * Pide el mensaje sonido() a través del tipo de la interfaz: la lista es de
 * Audible y mezcla las granadas guardadas en H2 (familia Armas) con una C4
 * (otra familia, que no se guarda) sin preguntar el tipo concreto de ninguna.
 */
@RestController
@Tag(name = "Sonidos")
public class SonidoController {

    private final SonidoService sonidoService;

    public SonidoController(SonidoService sonidoService) {
        this.sonidoService = sonidoService;
    }

    @GetMapping("/api/sonidos")
    @Operation(summary = "Sonidos por la interfaz Audible",
            description = "Las granadas guardadas que todavía no se lanzaron y una C4 plantada en el sitio y con los "
                    + "segundos indicados. A todas se les pide sonido() y alcanceSonido() sin preguntar su clase.")
    @ApiResponse(responseCode = "200", description = "Qué se escucha y desde dónde")
    @ApiResponse(responseCode = "400", description = "Sitio distinto de A o B, o segundos fuera de 0 a 40",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public List<SonidoDTO> sonidos(
            @Parameter(description = "Sitio de la C4: A o B") @RequestParam(defaultValue = "A") String sitio,
            @Parameter(description = "Segundos que le quedan a la C4: 0 a 40") @RequestParam(defaultValue = "40") int segundos
    ) {
        return sonidoService.sonidos(sitio, segundos).stream()
                .map(a -> new SonidoDTO(a.getClass().getSimpleName(), a.sonido(), a.alcanceSonido()))
                .toList();
    }

    public record SonidoDTO(String tipoConcreto, String sonido, String alcance) {}
}
