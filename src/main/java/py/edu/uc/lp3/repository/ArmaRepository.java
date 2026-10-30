package py.edu.uc.lp3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp3.domain.Armas;

import java.util.List;

/**
 * Acceso a la tabla "arma". Es una interfaz: Spring Data genera la
 * implementación. Devuelve instancias del tipo concreto (Rifle, Granada...)
 * según el discriminador de cada fila.
 */
public interface ArmaRepository extends JpaRepository<Armas, Long> {

    /** Las armas de un equipo: CT o TT. */
    List<Armas> findByEquipo(String equipo);

    /** Lo que se puede comprar con esa plata. */
    List<Armas> findByPrecioLessThanEqual(Long precioMaximo);
}
