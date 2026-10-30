package py.edu.uc.lp3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp3.domain.Granada;

/** Solo las filas de la tabla "arma" cuyo tipo es granada. */
public interface GranadaRepository extends JpaRepository<Granada, Long> {
}
