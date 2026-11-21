package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import py.edu.uc.lp3.constants.CatalogoArmas;

/**
 * El profesor dejó esta clase vacía (public class Armas extends Vendible {}).
 * Acá se completa con lo que es común a cualquier arma: el modelo del
 * catálogo, el equipo y el apodo. disparar() es abstracto para que cualquier
 * hija (arma de fuego o granada) responda al mismo mensaje sin que nadie
 * pregunte de qué tipo concreto se trata.
 *
 * El dominio guarda el estado y hace los cálculos de cada tipo de arma. No
 * valida ni lanza excepciones: las reglas del juego y los datos que llegan
 * del JSON los controla la capa service antes de mandar cada mensaje.
 *
 * Persistencia: toda la familia se guarda en una sola tabla "arma"
 * (SINGLE_TABLE). La columna "tipo" es el discriminador (rifle, pistola,
 * sniper, granada) y el JSON lleva el mismo dato en la propiedad "tipo". Las
 * estadísticas (daño, cargador, precio) no se pueden elegir: salen del
 * modelo del catálogo.
 */
@Entity
@Table(name = "arma")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Rifle.class, name = "rifle"),
        @JsonSubTypes.Type(value = Pistola.class, name = "pistola"),
        @JsonSubTypes.Type(value = Sniper.class, name = "sniper"),
        @JsonSubTypes.Type(value = Granada.class, name = "granada")
})
public abstract class Armas extends Vendible {

    /** Clave generada por la base: el JSON de creación no la lleva. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CatalogoArmas modelo;

    private String equipo; // "CT" o "TT"

    /** Name Tag: un nombre propio que el jugador le pone a su arma. Puede no tener. */
    private String apodo;

    protected Armas(CatalogoArmas modelo, String equipo) {
        this.modelo = modelo;
        this.equipo = equipo;
        setPrecio(modelo.getPrecio());
        setDescripcion(modelo.getNombre());
    }

    /**
     * Regla de compra única para toda la jerarquía. final: ninguna subclase
     * puede alterar cómo se compra.
     */
    public final String comprar(double dineroDisponible) {
        if (dineroDisponible < getPrecio()) {
            return "No alcanza el dinero para comprar " + getNombre() + ": cuesta " + getPrecio();
        }
        return getNombre() + " comprada por " + getPrecio() + ", equipo " + equipo;
    }

    /**
     * Contrato uniforme: cualquier arma (de fuego o granada) responde a
     * disparar() sin que quien la usa sepa el tipo concreto.
     */
    public abstract String disparar();

    /**
     * Sobrecarga de disparar(): la misma acción contra un objetivo a esa
     * distancia, en metros.
     */
    public abstract String disparar(double distanciaMetros);

    /** Cuánto daño le hace esta arma a un objetivo sin blindaje que está a esa distancia. */
    public abstract int danoA(double distanciaMetros);

    public abstract String inspeccionar();

    /** Ranura del inventario que ocupa: principal, secundaria o granadas. */
    public abstract String ranura();

    /** El apodo pasa a ser el que se indica; null lo quita. */
    public void ponerApodo(String apodo) {
        this.apodo = apodo;
    }

    /**
     * Implementación única porque alcanza con los atributos comunes; las
     * subclases pueden sobreescribirla, pero no están obligadas.
     */
    public String mostrarEnTienda() {
        return String.format("%s | %d $ | equipo %s | ranura %s", getNombre(), getPrecio(), equipo, ranura());
    }

    public Long getId() {
        return id;
    }

    /** El modelo del catálogo; en el JSON se ve su nombre ("nombre"). */
    @JsonIgnore
    public CatalogoArmas getModelo() {
        return modelo;
    }

    public String getNombre() {
        return modelo.getNombre();
    }

    public int getDano() {
        return modelo.getDano();
    }

    public String getEquipo() {
        return equipo;
    }

    public String getApodo() {
        return apodo;
    }
}
