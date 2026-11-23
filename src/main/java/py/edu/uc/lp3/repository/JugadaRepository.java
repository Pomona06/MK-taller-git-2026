package py.edu.uc.lp3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp3.domain.Jugada;

import java.util.List;

/** Las jugadas del escenario de POO-09 (tabla "jugada"). */
public interface JugadaRepository extends JpaRepository<Jugada, Long> {

    /** Las jugadas contra un jugador, de la más vieja a la más nueva. */
    List<Jugada> findByObjetivoIdOrderByIdAsc(Long objetivoId);
}
