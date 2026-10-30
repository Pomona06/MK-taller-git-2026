package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Armas;
import py.edu.uc.lp3.domain.Audible;
import py.edu.uc.lp3.domain.Granada;
import py.edu.uc.lp3.domain.Pistola;
import py.edu.uc.lp3.domain.Rifle;
import py.edu.uc.lp3.domain.Sniper;
import py.edu.uc.lp3.exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.GranadaRepository;
import py.edu.uc.lp3.service.ArmaService;
import py.edu.uc.lp3.service.dto.ArmaPatchRequest;
import py.edu.uc.lp3.service.dto.ArmaRequest;
import py.edu.uc.lp3.service.dto.DisparoResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Habla con el repositorio (interfaz) y con el dominio. Las reglas las
 * validan los constructores y los mensajes de las armas; acá se decide qué
 * mensaje mandar, se guarda el resultado y se informa si el id no existe.
 * Cada método es una transacción: si el dominio lanza una excepción, no
 * queda nada a medio guardar.
 */
@Service
@Transactional
public class ArmaServiceImpl implements ArmaService {

    private final ArmaRepository armaRepository;
    private final GranadaRepository granadaRepository;

    public ArmaServiceImpl(ArmaRepository armaRepository, GranadaRepository granadaRepository) {
        this.armaRepository = armaRepository;
        this.granadaRepository = granadaRepository;
    }

    @Override
    public Armas crear(ArmaRequest pedido) {
        return armaRepository.save(construir(pedido));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Armas> listar(String equipo, Long precioMaximo) {
        if (equipo != null) {
            return armaRepository.findByEquipo(equipo);
        }
        if (precioMaximo != null) {
            return armaRepository.findByPrecioLessThanEqual(precioMaximo);
        }
        return armaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Armas buscar(Long id) {
        return armaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("un arma", id));
    }

    @Override
    public Armas actualizar(Long id, ArmaPatchRequest cambios) {
        Armas arma = buscar(id);
        if (cambios.nombre() != null) {
            arma.renombrar(cambios.nombre());
        }
        if (cambios.precio() != null) {
            arma.cambiarPrecio(cambios.precio());
        }
        return armaRepository.save(arma);
    }

    @Override
    public void borrar(Long id) {
        armaRepository.delete(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public long contar() {
        return armaRepository.count();
    }

    @Override
    public DisparoResponse disparar(Long id, Double distancia) {
        Armas arma = buscar(id);
        String resultado = distancia == null ? arma.disparar() : arma.disparar(distancia);
        return new DisparoResponse(resultado, armaRepository.save(arma));
    }

    @Override
    public DisparoResponse recargar(Long id) {
        Armas arma = buscar(id);
        String resultado = arma.recargar();
        return new DisparoResponse(resultado, armaRepository.save(arma));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Audible> audiblesGuardados() {
        return new ArrayList<>(granadaRepository.findAll());
    }

    /**
     * Fábrica: elige la clase hija según "tipo" y llama a su constructor
     * completo. Lo que no venga en el JSON se toma del constructor simple de
     * ese tipo, así cualquier combinación pasa por las mismas validaciones.
     */
    private Armas construir(ArmaRequest p) {
        if (p.tipo() == null) {
            throw new ValorInvalidoException("falta el tipo: rifle, pistola, sniper o granada");
        }
        return switch (p.tipo().toLowerCase()) {
            case "rifle" -> {
                Rifle d = new Rifle();
                yield new Rifle(o(p.nombre(), d.getNombre()), o(p.precio(), d.getPrecio()),
                        o(p.equipo(), d.getEquipo()), o(p.peso(), d.getPeso()), o(p.dano(), d.getDano()),
                        o(p.capacidadCargador(), d.getCapacidadCargador()),
                        o(p.cargadoresRestantes(), d.getCargadoresRestantes()),
                        o(p.precision(), d.getPrecision()), o(p.retroceso(), d.getRetroceso()),
                        o(p.tiempoRecargaMs(), d.getTiempoRecargaMs()), o(p.modoRafaga(), d.isModoRafaga()));
            }
            case "pistola" -> {
                Pistola d = new Pistola();
                yield new Pistola(o(p.nombre(), d.getNombre()), o(p.precio(), d.getPrecio()),
                        o(p.equipo(), d.getEquipo()), o(p.peso(), d.getPeso()), o(p.dano(), d.getDano()),
                        o(p.capacidadCargador(), d.getCapacidadCargador()),
                        o(p.cargadoresRestantes(), d.getCargadoresRestantes()),
                        o(p.precision(), d.getPrecision()), o(p.retroceso(), d.getRetroceso()),
                        o(p.tiempoRecargaMs(), d.getTiempoRecargaMs()),
                        o(p.esArmaInicial(), d.isEsArmaInicial()), o(p.modoRafaga(), d.isModoRafaga()));
            }
            case "sniper" -> {
                Sniper d = new Sniper();
                yield new Sniper(o(p.nombre(), d.getNombre()), o(p.precio(), d.getPrecio()),
                        o(p.equipo(), d.getEquipo()), o(p.peso(), d.getPeso()), o(p.dano(), d.getDano()),
                        o(p.capacidadCargador(), d.getCapacidadCargador()),
                        o(p.cargadoresRestantes(), d.getCargadoresRestantes()),
                        o(p.precision(), d.getPrecision()), o(p.retroceso(), d.getRetroceso()),
                        o(p.tiempoRecargaMs(), d.getTiempoRecargaMs()));
            }
            case "granada" -> {
                boolean aturde = o(p.aturde(), false);
                Granada d = new Granada(o(p.nombre(), aturde ? "Flashbang" : "HE"), aturde);
                yield new Granada(d.getNombre(), o(p.precio(), d.getPrecio()),
                        o(p.equipo(), d.getEquipo()), o(p.peso(), d.getPeso()), o(p.dano(), d.getDano()),
                        o(p.radioExplosion(), d.getRadioExplosion()),
                        o(p.distanciaLanzamiento(), d.getDistanciaLanzamiento()), d.isAturde(),
                        o(p.visibilidadReducida(), d.getVisibilidadReducida()),
                        o(p.cooldownMs(), d.getCooldownMs()));
            }
            default -> throw new ValorInvalidoException(
                    "tipo debe ser rifle, pistola, sniper o granada (recibido: " + p.tipo() + ")");
        };
    }

    /** El valor del JSON si vino; si no, el valor por defecto del tipo. */
    private static <T> T o(T valor, T porDefecto) {
        return valor != null ? valor : porDefecto;
    }
}
