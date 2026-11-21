package py.edu.uc.lp3.service;

import py.edu.uc.lp3.service.dto.ConsultaDisparoRequest;
import py.edu.uc.lp3.service.dto.ConsultaDisparoResponse;
import py.edu.uc.lp3.service.dto.ConsultaImpactoRequest;
import py.edu.uc.lp3.service.dto.ConsultaImpactoResponse;
import py.edu.uc.lp3.service.dto.ConsultaRecargaRequest;
import py.edu.uc.lp3.service.dto.ConsultaRecargaResponse;
import py.edu.uc.lp3.service.dto.ReglaResponse;

import java.util.List;

/**
 * Consulta de las dos reglas sin tocar la base: para que el escenario de otro
 * servicio pueda preguntar si una jugada es legal antes de guardarla.
 */
public interface ReglasService {

    List<ReglaResponse> reglas();

    ConsultaDisparoResponse consultarDisparo(ConsultaDisparoRequest pedido);

    ConsultaRecargaResponse consultarRecarga(ConsultaRecargaRequest pedido);

    ConsultaImpactoResponse consultarImpacto(ConsultaImpactoRequest pedido);
}
