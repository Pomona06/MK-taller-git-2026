package com.tallercs.armas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, String> index() {
        return Map.of(
                "servicio", "Taller de Git — Modelado de armas CS2",
                "endpoint", "GET /armas/crear?tipo=rifle|pistola|sniper|granada&... (ver README)"
        );
    }
}
