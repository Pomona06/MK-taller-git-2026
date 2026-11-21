package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.Armas;
import py.edu.uc.lp3.service.dto.ArmaPatchRequest;
import py.edu.uc.lp3.service.dto.ArmaRequest;
import py.edu.uc.lp3.service.dto.DisparoResponse;
import py.edu.uc.lp3.service.dto.ModeloCatalogoResponse;

import java.util.List;

/** Capa de negocio de las armas: catálogo, CRUD sobre H2 y simulación de munición. */
public interface ArmaService {

    List<ModeloCatalogoResponse> catalogo();

    Armas crear(ArmaRequest pedido);

    List<Armas> listar(String equipo, Long precioMaximo);

    Armas buscar(Long id);

    Armas actualizar(Long id, ArmaPatchRequest cambios);

    void borrar(Long id);

    long contar();

    DisparoResponse disparar(Long id, Double distancia, Integer veces);

    DisparoResponse recargar(Long id);
}
