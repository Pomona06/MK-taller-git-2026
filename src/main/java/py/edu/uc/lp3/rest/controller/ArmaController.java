package py.edu.uc.lp3.rest.controller;

import py.edu.uc.lp3.domain.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Construye una instancia concreta según "tipo" (esto SÍ necesita un switch,
 * porque construir un objeto requiere elegir su constructor) y a partir de
 * ahí la maneja únicamente como Armas (tipo padre, de py.edu.uc.lp3.domain):
 * ningún método de acá para abajo pregunta de qué tipo concreto es la
 * instancia.
 */
@RestController
public class ArmaController {

    @GetMapping("/armas/crear")
    public ArmaComportamientoDTO crear(
            @RequestParam String tipo,
            @RequestParam(defaultValue = "Arma sin nombre") String nombre,
            @RequestParam(defaultValue = "1000") long precio,
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
            @RequestParam(defaultValue = "5000") double dineroDisponible,
            @RequestParam(required = false) Double distancia
    ) {
        // Construir el objeto correcto es lo único que necesita conocer el
        // tipo concreto; por eso el switch está acá y en ningún otro lado.
        Armas arma = switch (tipo.toLowerCase()) {
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
            default -> throw new IllegalArgumentException(
                    "tipo debe ser rifle, pistola, sniper o granada (recibido: " + tipo + ")");
        };
        return describir(arma, "completo", dineroDisponible, distancia);
    }

    /**
     * Construye con los constructores simples y sobrecargados. Según qué
     * parámetros vengan en la URL se usa el constructor sin argumentos, el
     * de un argumento o el de dos; en todos los casos el objeto queda en un
     * estado legal con los valores por defecto del arma.
     */
    @GetMapping("/armas/crear-simple")
    public ArmaComportamientoDTO crearSimple(
            @RequestParam String tipo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String equipo,
            @RequestParam(required = false) Boolean modoRafaga,
            @RequestParam(required = false) Boolean aturde,
            @RequestParam(defaultValue = "5000") double dineroDisponible,
            @RequestParam(required = false) Double distancia
    ) {
        Armas arma;
        String constructor;
        switch (tipo.toLowerCase()) {
            case "rifle" -> {
                if (nombre == null) { arma = new Rifle(); constructor = "Rifle()"; }
                else if (modoRafaga == null) { arma = new Rifle(nombre); constructor = "Rifle(String)"; }
                else { arma = new Rifle(nombre, modoRafaga); constructor = "Rifle(String, boolean)"; }
            }
            case "pistola" -> {
                if (nombre == null) { arma = new Pistola(); constructor = "Pistola()"; }
                else if (equipo == null) { arma = new Pistola(nombre); constructor = "Pistola(String)"; }
                else { arma = new Pistola(nombre, equipo); constructor = "Pistola(String, String)"; }
            }
            case "sniper" -> {
                if (nombre == null) { arma = new Sniper(); constructor = "Sniper()"; }
                else if (equipo == null) { arma = new Sniper(nombre); constructor = "Sniper(String)"; }
                else { arma = new Sniper(nombre, equipo); constructor = "Sniper(String, String)"; }
            }
            case "granada" -> {
                if (nombre == null) { arma = new Granada(); constructor = "Granada()"; }
                else if (aturde == null) { arma = new Granada(nombre); constructor = "Granada(String)"; }
                else { arma = new Granada(nombre, aturde); constructor = "Granada(String, boolean)"; }
            }
            default -> throw new IllegalArgumentException(
                    "tipo debe ser rifle, pistola, sniper o granada (recibido: " + tipo + ")");
        }
        return describir(arma, constructor, dineroDisponible, distancia);
    }

    /**
     * Desde acá "arma" se trata SIEMPRE como Armas (tipo padre). Si la URL
     * trae distancia se usa la sobrecarga disparar(double); si no, disparar().
     */
    private ArmaComportamientoDTO describir(Armas arma, String constructor,
                                            double dineroDisponible, Double distancia) {
        String tienda = arma.mostrarEnTienda();
        String compra = arma.comprar(dineroDisponible);
        String mensajeDisparo = distancia == null ? "disparar()" : "disparar(double)";
        String disparo = distancia == null ? arma.disparar() : arma.disparar(distancia);
        return new ArmaComportamientoDTO(
                arma.getClass().getSimpleName(),
                constructor,
                tienda,
                compra,
                mensajeDisparo,
                disparo,
                arma.inspeccionar()
        );
    }

    public record ArmaComportamientoDTO(
            String tipoConcreto,
            String constructor,
            String tienda,
            String compra,
            String mensajeDisparo,
            String disparo,
            String inspeccion
    ) {}
}
