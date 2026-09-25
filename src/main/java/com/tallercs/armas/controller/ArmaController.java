package com.tallercs.armas.controller;

import com.tallercs.armas.modelo.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * Construye una instancia concreta según "tipo" (esto SÍ necesita un switch,
 * porque construir un objeto requiere elegir su constructor) y a partir de
 * ahí la maneja únicamente como Arma (tipo padre): ningún método de acá
 * para abajo pregunta de qué tipo concreto es la instancia.
 */
@RestController
public class ArmaController {

    @GetMapping("/armas/crear")
    public ArmaComportamientoDTO crear(
            @RequestParam String tipo,
            @RequestParam(defaultValue = "Arma sin nombre") String nombre,
            @RequestParam(defaultValue = "1000") double precio,
            @RequestParam(defaultValue = "CT") String equipo,
            @RequestParam(defaultValue = "2.0") double peso,
            @RequestParam(defaultValue = "30") int dano,
            @RequestParam(defaultValue = "30") int capacidadCargador,
            @RequestParam(defaultValue = "3") int cargadoresRestantes,
            @RequestParam(defaultValue = "0.7") double precision,
            @RequestParam(defaultValue = "5.0") double retroceso,
            @RequestParam(defaultValue = "2000") long tiempoRecargaMs,
            @RequestParam(defaultValue = "false") boolean modoRafaga,
            @RequestParam(defaultValue = "false") boolean esArmaInicial,
            @RequestParam(defaultValue = "4.0") double radioExplosion,
            @RequestParam(defaultValue = "6.0") double distanciaLanzamiento,
            @RequestParam(defaultValue = "false") boolean aturde,
            @RequestParam(defaultValue = "0") double visibilidadReducida,
            @RequestParam(defaultValue = "1000") long cooldownMs,
            @RequestParam(defaultValue = "5000") double dineroDisponible
    ) {
        // Único lugar de toda la app que sabe de tipos concretos: construir
        // el objeto correcto es responsabilidad de este switch.
        Arma arma = switch (tipo.toLowerCase()) {
            case "rifle" -> new Rifle(nombre, precio, equipo, peso, dano,
                    capacidadCargador, cargadoresRestantes, precision, retroceso,
                    tiempoRecargaMs, modoRafaga);
            case "pistola" -> new Pistola(nombre, precio, equipo, peso, dano,
                    capacidadCargador, cargadoresRestantes, precision, retroceso,
                    tiempoRecargaMs, esArmaInicial, modoRafaga);
            case "sniper" -> new Sniper(nombre, precio, equipo, peso, dano,
                    capacidadCargador, cargadoresRestantes, precision, retroceso,
                    tiempoRecargaMs);
            case "granada" -> new Granada(nombre, precio, equipo, peso, dano,
                    radioExplosion, distanciaLanzamiento, aturde, visibilidadReducida,
                    cooldownMs);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "tipo debe ser rifle, pistola, sniper o granada (recibido: " + tipo + ")");
        };

        // De acá para abajo, "arma" se trata SIEMPRE como Arma (tipo padre).
        // El JSON de respuesta muestra que cada tipo concreto se comporta
        // distinto ante EXACTAMENTE los mismos tres mensajes.
        return new ArmaComportamientoDTO(
                arma.getClass().getSimpleName(),
                arma.mostrarEnTienda(),
                arma.comprar(dineroDisponible),
                arma.disparar(),
                arma.inspeccionar()
        );
    }

    public record ArmaComportamientoDTO(
            String tipoConcreto,
            String tienda,
            String compra,
            String disparo,
            String inspeccion
    ) {}
}
