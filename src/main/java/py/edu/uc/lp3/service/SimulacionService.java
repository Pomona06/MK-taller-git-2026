package py.edu.uc.lp3.service;

import py.edu.uc.lp3.service.dto.SimulacionDanoRequest;
import py.edu.uc.lp3.service.dto.SimulacionDanoResponse;

/** Simulación de daño: un arma guardada le dispara a un jugador con o sin chaleco. */
public interface SimulacionService {

    SimulacionDanoResponse simularDano(SimulacionDanoRequest pedido);
}
