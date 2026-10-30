package py.edu.uc.lp3.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.domain.Armas;
import py.edu.uc.lp3.service.ArmaService;
import py.edu.uc.lp3.service.dto.ArmaPatchRequest;
import py.edu.uc.lp3.service.dto.ArmaRequest;
import py.edu.uc.lp3.service.dto.DisparoResponse;

import java.net.URI;
import java.util.List;

/**
 * CRUD REST sobre el recurso "armas". La URL nombra el recurso y el verbo
 * HTTP dice qué hacer. El controller solo traduce HTTP: las reglas están en
 * el dominio y el acceso a datos en el servicio.
 */
@RestController
@RequestMapping("/api/armas")
public class ArmaCrudController {

    private final ArmaService armaService;

    public ArmaCrudController(ArmaService armaService) {
        this.armaService = armaService;
    }

    @PostMapping
    public ResponseEntity<Armas> crear(@RequestBody ArmaRequest pedido) {
        Armas creada = armaService.crear(pedido);
        return ResponseEntity.created(URI.create("/api/armas/" + creada.getId())).body(creada);
    }

    @GetMapping
    public List<Armas> listar(@RequestParam(required = false) String equipo,
                              @RequestParam(required = false) Long precioMaximo) {
        return armaService.listar(equipo, precioMaximo);
    }

    @GetMapping("/{id}")
    public Armas buscar(@PathVariable Long id) {
        return armaService.buscar(id);
    }

    @PatchMapping("/{id}")
    public Armas actualizar(@PathVariable Long id, @RequestBody ArmaPatchRequest cambios) {
        return armaService.actualizar(id, cambios);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        armaService.borrar(id);
        return ResponseEntity.noContent().build();
    }

    /** Simulación de munición: gasta balas del arma guardada. */
    @PostMapping("/{id}/disparos")
    public DisparoResponse disparar(@PathVariable Long id, @RequestParam(required = false) Double distancia) {
        return armaService.disparar(id, distancia);
    }

    /** Simulación de munición: usa un cargador de repuesto. */
    @PostMapping("/{id}/recargas")
    public DisparoResponse recargar(@PathVariable Long id) {
        return armaService.recargar(id);
    }
}
