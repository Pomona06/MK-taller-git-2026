package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Chaleco;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3.service.JugadorService;
import py.edu.uc.lp3.service.dto.JugadorRequest;

import java.util.List;

@Service
@Transactional
public class JugadorServiceImpl implements JugadorService {

    private final JugadorRepository jugadorRepository;

    public JugadorServiceImpl(JugadorRepository jugadorRepository) {
        this.jugadorRepository = jugadorRepository;
    }

    @Override
    public Jugador crear(JugadorRequest p) {
        if (p == null) {
            throw new ValorInvalidoException("falta el cuerpo con nombre, vida y blindaje");
        }
        int blindaje = p.blindaje() == null ? 0 : p.blindaje();
        // 0 = no compró chaleco; cualquier otro valor lo valida el constructor de Chaleco.
        Chaleco chaleco = blindaje == 0 ? null : new Chaleco(blindaje);
        Jugador jugador = new Jugador(p.nombre(), p.vida() == null ? Jugador.VIDA_MAXIMA : p.vida(), chaleco);
        return jugadorRepository.save(jugador);
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
