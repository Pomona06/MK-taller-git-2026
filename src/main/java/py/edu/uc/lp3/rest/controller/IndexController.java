package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, Object> index() {
        return Map.of(
                "servicio", "Taller de Git — Modelado de armas CS2",
                "estado", "vivo",
                "endpoints", List.of(
                        "GET /armas/crear?tipo=rifle|pistola|sniper|granada&... (constructor completo)",
                        "GET /armas/crear-simple?tipo=...&nombre=... (constructores sobrecargados)",
                        "GET /armas/comparar[?distancia=...] (disparar() en cada clase hija)",
                        "Parámetro opcional distancia: usa la sobrecarga disparar(double)")
        );
    }
}
