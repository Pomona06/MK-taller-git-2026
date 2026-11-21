package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import py.edu.uc.lp3.constants.ReglasJuego;

/**
 * Kevlar: se compra en la tienda (es un Vendible) pero no es un arma. Absorbe
 * parte del daño y en eso se gasta.
 *
 * Se guarda en la tabla "chaleco" ({@code precio} y {@code descripcion} vienen
 * de Vendible, que es @MappedSuperclass). El constructor sin argumentos que
 * pide JPA es el constructor simple: un Kevlar nuevo.
 */
@Entity
@Table(name = "chaleco")
public class Chaleco extends Vendible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int durabilidad;

    /** Constructor simple: un Kevlar recién comprado, con 100 de blindaje. */
    public Chaleco() {
        this(ReglasJuego.BLINDAJE_KEVLAR);
    }

    /** Sobrecarga: un chaleco que ya recibió daño en una ronda anterior. */
    public Chaleco(int durabilidad) {
        this.durabilidad = durabilidad;
        setPrecio(ReglasJuego.PRECIO_KEVLAR);
        setDescripcion("Kevlar");
    }

    /** Pierde el blindaje que absorbió. Cuánto es lo calcula la regla de vida del servicio. */
    public void gastar(int blindajePerdido) {
        durabilidad -= blindajePerdido;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    public int getDurabilidad() {
        return durabilidad;
    }
}
