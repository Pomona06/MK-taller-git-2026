package py.edu.uc.lp3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp3.domain.Granada;

import java.util.List;

/** Solo las filas de la tabla "arma" cuyo tipo es granada. */
public interface GranadaRepository extends JpaRepository<Granada, Long> {

    /** Las granadas que todavía no se lanzaron: las únicas que pueden sonar. */
    List<Granada> findByLanzadaFalse();
}
