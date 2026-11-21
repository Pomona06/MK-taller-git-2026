package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.service.ArmaService;
import py.edu.uc.lp3.service.dto.ModeloCatalogoResponse;

import java.util.List;

/** Las armas de CS2 que se pueden crear, con sus valores reales. */
@RestController
@Tag(name = "Catálogo")
public class CatalogoController {

    private final ArmaService armaService;

    public CatalogoController(ArmaService armaService) {
        this.armaService = armaService;
    }

    @GetMapping("/api/catalogo")
    @Operation(summary = "Las 11 armas de CS2 que se pueden crear",
            description = "Precio, equipos que la compran, daño, cargador, reserva en cargadores, si es automática y si tiene ráfaga.")
    public List<ModeloCatalogoResponse> catalogo() {
        return armaService.catalogo();
    }
}
