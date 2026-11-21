package py.edu.uc.lp3.rest.controller;

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
public class SonidoController {

    private final SonidoService sonidoService;

    public SonidoController(SonidoService sonidoService) {
        this.sonidoService = sonidoService;
    }

    @GetMapping("/api/sonidos")
    public List<SonidoDTO> sonidos(
            @RequestParam(defaultValue = "A") String sitio,
            @RequestParam(defaultValue = "40") int segundos
    ) {
        return sonidoService.sonidos(sitio, segundos).stream()
                .map(a -> new SonidoDTO(a.getClass().getSimpleName(), a.sonido(), a.alcanceSonido()))
                .toList();
    }

    public record SonidoDTO(String tipoConcreto, String sonido, String alcance) {}
}
