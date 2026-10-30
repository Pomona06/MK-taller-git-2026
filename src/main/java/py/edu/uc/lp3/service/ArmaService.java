package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.Armas;
import py.edu.uc.lp3.domain.Audible;
import py.edu.uc.lp3.service.dto.ArmaPatchRequest;
import py.edu.uc.lp3.service.dto.ArmaRequest;
import py.edu.uc.lp3.service.dto.DisparoResponse;

import java.util.List;

/** Capa de negocio de las armas: CRUD sobre H2 y simulación de munición. */
public interface ArmaService {

    Armas crear(ArmaRequest pedido);

    List<Armas> listar(String equipo, Long precioMaximo);

    Armas buscar(Long id);

    Armas actualizar(Long id, ArmaPatchRequest cambios);

    void borrar(Long id);

    long contar();

    DisparoResponse disparar(Long id, Double distancia);

    DisparoResponse recargar(Long id);

    List<Audible> audiblesGuardados();
}
