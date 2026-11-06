package py.edu.uc.lp3.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;

/**
 * Kevlar: se compra en la tienda (es un Vendible) pero no es un arma. Absorbe
 * parte del daño mientras le quede durabilidad.
 *
 * Se guarda en la tabla "chaleco" (precio y descripcion vienen de Vendible,
 * que es @MappedSuperclass). La durabilidad solo baja con absorber(). El
 * constructor sin argumentos que pide JPA es el constructor simple: un
 * chaleco nuevo y legal.
 */
@Entity
@Table(name = "chaleco")
public class Chaleco extends Vendible {

    public static final int DURABILIDAD_MAXIMA = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int durabilidad;

    /** Constructor simple: un chaleco nuevo. */
    public Chaleco() {
        this(DURABILIDAD_MAXIMA);
    }

    public Chaleco(int durabilidad) {
        if (durabilidad < 0 || durabilidad > DURABILIDAD_MAXIMA) {
            throw new ValorInvalidoException("la durabilidad del chaleco debe estar entre 0 y " + DURABILIDAD_MAXIMA);
        }
        this.durabilidad = durabilidad;
        setPrecio(650L);
        setDescripcion("Kevlar");
    }

    /**
     * Absorbe la mitad del daño, sin pasar de la durabilidad que le queda, y
     * se gasta en lo que absorbió. Devuelve el daño que llega al jugador.
     */
    public int absorber(int dano) {
        int absorbido = Math.min(dano / 2, durabilidad);
        durabilidad -= absorbido;
        return dano - absorbido;
    }

    public Long getId() {
        return id;
    }

    public int getDurabilidad() {
        return durabilidad;
    }
}
