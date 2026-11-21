package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.service.ArmaService;
import py.edu.uc.lp3.service.dto.ModeloCatalogoResponse;

import java.util.List;

/** Las armas de CS2 que se pueden crear, con sus valores reales. */
@RestController
public class CatalogoController {

    private final ArmaService armaService;

    public CatalogoController(ArmaService armaService) {
        this.armaService = armaService;
    }

    @GetMapping("/api/catalogo")
    public List<ModeloCatalogoResponse> catalogo() {
        return armaService.catalogo();
    }
}
