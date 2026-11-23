package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.constants.CatalogoArmas;
import py.edu.uc.lp3.domain.Armas;
import py.edu.uc.lp3.domain.ArmasDeFuego;
import py.edu.uc.lp3.domain.Granada;
import py.edu.uc.lp3.exceptions.MunicionInsuficienteException;
import py.edu.uc.lp3.exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.service.ArmaService;
import py.edu.uc.lp3.service.dto.ArmaPatchRequest;
import py.edu.uc.lp3.service.dto.ArmaRequest;
import py.edu.uc.lp3.service.dto.DisparoResponse;
import py.edu.uc.lp3.service.dto.ModeloCatalogoResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Acá viven las validaciones y las reglas de las armas: qué modelo existe en
 * CS2, qué equipo lo puede comprar, qué apodo es válido, qué arma tiene modo
 * ráfaga y cuándo se puede disparar o recargar. Recién cuando todo se cumple
 * se le manda el mensaje al dominio y se guarda. Cada método es una
 * transacción: si se lanza una excepción, no queda nada a medio guardar.
 */
@Service
@Transactional
public class ArmaServiceImpl implements ArmaService {

    /** Límite propio del servicio para el Name Tag. */
    static final int LARGO_MAXIMO_APODO = 20;

    private final ArmaRepository armaRepository;

    public ArmaServiceImpl(ArmaRepository armaRepository) {
        this.armaRepository = armaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModeloCatalogoResponse> catalogo() {
        return Arrays.stream(CatalogoArmas.values()).map(ModeloCatalogoResponse::de).toList();
    }

    @Override
    public Armas crear(ArmaRequest pedido) {
        if (pedido == null || pedido.modelo() == null || pedido.modelo().isBlank()) {
            throw new ValorInvalidoException("falta el modelo; el catálogo tiene: " + nombresDelCatalogo());
        }
        CatalogoArmas modelo = CatalogoArmas.buscar(pedido.modelo())
                .orElseThrow(() -> new ValorInvalidoException("no existe el arma \"" + pedido.modelo()
                        + "\" en el catálogo de CS2; el catálogo tiene: " + nombresDelCatalogo()));
        String equipo = equipoDelPedido(modelo, pedido.equipo());
        return armaRepository.save(ReglasDeArmas.construir(modelo, equipo));
    }

    /** El equipo tiene que ser CT o TT y tiene que poder comprar ese modelo. */
    private String equipoDelPedido(CatalogoArmas modelo, String pedido) {
        if (pedido == null) {
            return modelo.equipoUnico().orElseThrow(() -> new ValorInvalidoException(modelo.getNombre()
                    + " es para los dos equipos: indicá \"equipo\": \"CT\" o \"TT\""));
        }
        String equipo = ReglasDeArmas.equipo(pedido);
        if (!modelo.loPuedeComprar(equipo)) {
            String dueno = modelo.isTerroristas() ? "TT (terroristas)" : "CT (antiterroristas)";
            throw new ValorInvalidoException("en CS2, " + modelo.getNombre() + " es solo para el equipo " + dueno);
        }
        return equipo;
    }

    private static String nombresDelCatalogo() {
        return Arrays.stream(CatalogoArmas.values()).map(CatalogoArmas::getNombre).collect(Collectors.joining(", "));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Armas> listar(String equipo, Long precioMaximo) {
        if (equipo != null) {
            return armaRepository.findByEquipo(ReglasDeArmas.equipo(equipo));
        }
        if (precioMaximo != null) {
            if (precioMaximo < 0) {
                throw new ValorInvalidoException("el precio máximo no puede ser negativo");
            }
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

    /** Se valida todo antes de cambiar algo: si un campo es ilegal, el arma queda igual. */
    @Override
    public Armas actualizar(Long id, ArmaPatchRequest cambios) {
        if (cambios == null || (cambios.apodo() == null && cambios.modoRafaga() == null)) {
            throw new ValorInvalidoException("el PATCH acepta \"apodo\" y \"modoRafaga\"");
        }
        Armas arma = buscar(id);
        if (cambios.apodo() != null && arma instanceof Granada) {
            throw new ValorInvalidoException("en CS2 las granadas no llevan Name Tag: " + arma.getNombre() + " no acepta apodo");
        }
        String apodo = cambios.apodo() == null ? null : validarApodo(cambios.apodo());
        if (cambios.modoRafaga() != null && !arma.getModelo().isTieneRafaga()) {
            throw new ValorInvalidoException(arma.getNombre()
                    + " no tiene modo ráfaga: en CS2 solo lo tienen la Glock-18 y la FAMAS");
        }
        if (apodo != null) {
            arma.ponerApodo(apodo);
        }
        if (cambios.modoRafaga() != null) {
            ((ArmasDeFuego) arma).cambiarModoRafaga(cambios.modoRafaga());
        }
        return armaRepository.save(arma);
    }

    private static String validarApodo(String texto) {
        String apodo = texto.strip();
        if (apodo.isEmpty()) {
            throw new ValorInvalidoException("el apodo no puede estar vacío");
        }
        if (apodo.length() > LARGO_MAXIMO_APODO) {
            throw new ValorInvalidoException("el apodo puede tener hasta " + LARGO_MAXIMO_APODO + " caracteres");
        }
        return apodo;
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

    /** Simulación de munición: primero la regla, después el mensaje al dominio y por último se guarda. */
    @Override
    public DisparoResponse disparar(Long id, Double distancia, Integer veces) {
        Armas arma = buscar(id);
        double metros = ReglasDeArmas.distancia(distancia);
        int cantidad = veces == null ? 1 : veces;
        ReglasDeArmas.verificarAlcance(arma.getModelo(), metros);
        ReglasDeArmas.verificarQuePuedeDisparar(arma, cantidad);
        List<String> resultados = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            resultados.add(distancia == null ? arma.disparar() : arma.disparar(metros));
        }
        return new DisparoResponse(resultados, armaRepository.save(arma));
    }

    /**
     * Una granada no se recarga. Con el cargador lleno no pasa nada (como en el
     * juego); sin cargadores de reserva, es la regla de munición.
     */
    @Override
    public DisparoResponse recargar(Long id) {
        Armas arma = buscar(id);
        if (!(arma instanceof ArmasDeFuego deFuego)) {
            throw new ValorInvalidoException(arma.getNombre() + " es una granada: no se recarga");
        }
        if (deFuego.getCarga() == deFuego.getCapacidadCargador()) {
            return new DisparoResponse(List.of(deFuego.getNombre() + " ya tiene el cargador lleno"), deFuego);
        }
        if (deFuego.getCargadoresRestantes() == 0) {
            throw new MunicionInsuficienteException(deFuego.getNombre() + " no tiene cargadores de reserva");
        }
        String resultado = deFuego.recargar();
        return new DisparoResponse(List.of(resultado), armaRepository.save(deFuego));
    }
}
