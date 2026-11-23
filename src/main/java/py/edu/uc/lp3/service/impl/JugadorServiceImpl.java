package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Chaleco;
import py.edu.uc.lp3.constants.ReglasJuego;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.exceptions.EquipoCompletoException;
import py.edu.uc.lp3.exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3.service.JugadorService;
import py.edu.uc.lp3.service.dto.JugadorRequest;

import java.util.List;

@Service
@Transactional
public class JugadorServiceImpl implements JugadorService {

    /** Límite propio del servicio para el nombre del jugador. */
    static final int LARGO_MAXIMO_NOMBRE = 32;

    private final JugadorRepository jugadorRepository;

    public JugadorServiceImpl(JugadorRepository jugadorRepository) {
        this.jugadorRepository = jugadorRepository;
    }

    /**
     * Un jugador nuevo es de un equipo (CT o TT), empieza la ronda con 100 de
     * vida y, si compró Kevlar, con 100 de blindaje. Cada equipo tiene hasta
     * cinco jugadores, como en una partida de CS2.
     */
    @Override
    public Jugador crear(JugadorRequest pedido) {
        if (pedido == null || pedido.nombre() == null || pedido.nombre().isBlank()) {
            throw new ValorInvalidoException("el jugador necesita un nombre");
        }
        String nombre = pedido.nombre().strip();
        if (nombre.length() > LARGO_MAXIMO_NOMBRE) {
            throw new ValorInvalidoException("el nombre puede tener hasta " + LARGO_MAXIMO_NOMBRE + " caracteres");
        }
        String equipo = ReglasDeArmas.equipo(pedido.equipo());
        if (jugadorRepository.countByEquipo(equipo) >= ReglasJuego.JUGADORES_POR_EQUIPO) {
            throw new EquipoCompletoException(equipo, ReglasJuego.JUGADORES_POR_EQUIPO);
        }
        Chaleco chaleco = Boolean.TRUE.equals(pedido.chaleco()) ? new Chaleco() : null;
        return jugadorRepository.save(new Jugador(nombre, equipo, chaleco));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jugador> listar(Boolean vivos) {
        if (Boolean.TRUE.equals(vivos)) {
            return jugadorRepository.findByVidaGreaterThan(0);
        }
        return jugadorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Jugador buscar(Long id) {
        return jugadorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("un jugador", id));
    }
}
