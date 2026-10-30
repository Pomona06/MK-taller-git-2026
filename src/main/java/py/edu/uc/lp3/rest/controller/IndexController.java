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
                        "POST|GET /api/armas, GET|PATCH|DELETE /api/armas/{id} (CRUD sobre H2)",
                        "POST /api/armas/{id}/disparos[?distancia=...] y /api/armas/{id}/recargas (munición)",
                        "POST /api/simulaciones/dano (arma guardada + Jugador + Chaleco)",
                        "GET /armas/crear?tipo=rifle|pistola|sniper|granada&... (constructor completo)",
                        "GET /armas/crear-simple?tipo=...&nombre=... (constructores sobrecargados)",
                        "GET /armas/comparar[?distancia=...] (disparar() en cada clase hija)",
                        "GET /sonidos[?sitio=A|B&segundos=0..40] (sonido() a través de la interfaz Audible)",
                        "Parámetro opcional distancia: usa la sobrecarga disparar(double)")
        );
    }
}
