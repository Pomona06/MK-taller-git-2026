package py.edu.uc.lp3.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.service.JugadorService;
import py.edu.uc.lp3.service.dto.JugadorRequest;

import java.net.URI;
import java.util.List;

/**
 * Los jugadores que reciben el daño. No hay PATCH: la vida y el blindaje
 * solo cambian simulando (POST /api/simulaciones/dano).
 */
@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @PostMapping
    public ResponseEntity<Jugador> crear(@RequestBody JugadorRequest pedido) {
        Jugador creado = jugadorService.crear(pedido);
        return ResponseEntity.created(URI.create("/api/jugadores/" + creado.getId())).body(creado);
    }

    @GetMapping
    public List<Jugador> listar(@RequestParam(required = false) Boolean vivos) {
        return jugadorService.listar(vivos);
    }

    @GetMapping("/{id}")
    public Jugador buscar(@PathVariable Long id) {
        return jugadorService.buscar(id);
    }
}
