package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.service.ReglasService;
import py.edu.uc.lp3.service.dto.ConsultaDisparoRequest;
import py.edu.uc.lp3.service.dto.ConsultaDisparoResponse;
import py.edu.uc.lp3.service.dto.ConsultaImpactoRequest;
import py.edu.uc.lp3.service.dto.ConsultaImpactoResponse;
import py.edu.uc.lp3.service.dto.ConsultaRecargaRequest;
import py.edu.uc.lp3.service.dto.ConsultaRecargaResponse;
import py.edu.uc.lp3.service.dto.ReglaResponse;

import java.util.List;

/**
 * Las dos reglas, para que otro servicio las consulte. Son POST porque llevan
 * un cuerpo con el estado a evaluar, pero no crean ni cambian nada.
 */
@RestController
@RequestMapping("/api/reglas")
public class ReglasController {

    private final ReglasService reglasService;

    public ReglasController(ReglasService reglasService) {
        this.reglasService = reglasService;
    }

    @GetMapping
    public List<ReglaResponse> reglas() {
        return reglasService.reglas();
    }

    @PostMapping("/disparo")
    public ConsultaDisparoResponse disparo(@RequestBody ConsultaDisparoRequest pedido) {
        return reglasService.consultarDisparo(pedido);
    }

    @PostMapping("/recarga")
    public ConsultaRecargaResponse recarga(@RequestBody ConsultaRecargaRequest pedido) {
        return reglasService.consultarRecarga(pedido);
    }

    @PostMapping("/impacto")
    public ConsultaImpactoResponse impacto(@RequestBody ConsultaImpactoRequest pedido) {
        return reglasService.consultarImpacto(pedido);
    }
}
