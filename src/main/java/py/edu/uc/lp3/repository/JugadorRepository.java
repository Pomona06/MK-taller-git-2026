package py.edu.uc.lp3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp3.domain.Jugador;

import java.util.List;

/** Acceso a la tabla "jugador" (su chaleco viaja en cascada). */
public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    /** Los jugadores que ya quedaron con vida 0 (o los que siguen vivos). */
    List<Jugador> findByVidaGreaterThan(int vida);
}
