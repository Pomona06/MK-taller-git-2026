package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.domain.Audible;
import py.edu.uc.lp3.domain.BombaC4;
import py.edu.uc.lp3.domain.Granada;

import java.util.List;

/**
 * Pide el mensaje sonido() a través del tipo de la interfaz: la lista es de
 * Audible y mezcla clases de familias distintas (Granada y BombaC4) sin
 * preguntar el tipo concreto de ninguna.
 */
@RestController
public class SonidoController {

    @GetMapping("/sonidos")
    public List<SonidoDTO> sonidos(
            @RequestParam(defaultValue = "A") String sitio,
            @RequestParam(defaultValue = "40") int segundos
    ) {
        List<Audible> audibles = List.of(
                new Granada(),
                new Granada("Flashbang", true),
                new BombaC4(sitio, segundos));
        return audibles.stream()
                .map(a -> new SonidoDTO(a.getClass().getSimpleName(), a.sonido(), a.alcanceSonido()))
                .toList();
    }

    public record SonidoDTO(String tipoConcreto, String sonido, String alcance) {}
}
