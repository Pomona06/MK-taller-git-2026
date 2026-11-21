package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.service.ArmaService;

import java.util.List;
import java.util.Map;

@RestController
public class IndexController {

    private final ArmaService armaService;

    public IndexController(ArmaService armaService) {
        this.armaService = armaService;
    }

    @GetMapping("/")
    public Map<String, Object> index() {
        return Map.of(
                "servicio", "Taller de Git — Modelado de armas CS2",
                "estado", "vivo",
                "armasGuardadas", armaService.contar(),
                "endpoints", List.of(
                        "GET /api/catalogo (armas de CS2 que se pueden crear)",
                        "POST|GET /api/armas, GET|PATCH|DELETE /api/armas/{id} (CRUD sobre H2)",
                        "POST /api/armas/{id}/disparos[?distancia=...&veces=...] y /api/armas/{id}/recargas (munición)",
                        "POST|GET /api/jugadores, GET /api/jugadores/{id} (jugadores y su chaleco en H2)",
                        "POST /api/simulaciones/dano {armaId, jugadorId, distancia} (arma + Jugador + Chaleco)",
                        "GET /api/sonidos[?sitio=A|B&segundos=0..40] (granadas guardadas y C4 como Audible)")
        );
    }
}
