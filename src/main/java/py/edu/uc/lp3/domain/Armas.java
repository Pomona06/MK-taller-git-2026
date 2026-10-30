package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;

/**
 * El profesor dejó esta clase vacía (public class Armas extends Vendible {}).
 * Acá se completa con lo que es común a cualquier arma de la jerarquía, y con
 * el contrato uniforme que pide el taller: disparar() e inspeccionar() son
 * abstractos para que cualquier hija (arma de fuego o granada) responda al
 * mismo mensaje sin que nadie pregunte de qué tipo concreto se trata.
 *
 * precio y descripcion ya vienen de Vendible, así que no se repiten acá.
 *
 * Persistencia: toda la familia se guarda en una sola tabla "arma"
 * (SINGLE_TABLE). La columna "tipo" es el discriminador (rifle, pistola,
 * sniper, granada) y el JSON lleva el mismo dato en la propiedad "tipo".
 * Los campos no tienen setters públicos: JPA los carga por reflexión y el
 * resto del código solo los cambia con mensajes del dominio que validan.
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

    private String nombre;
    private String equipo; // "CT" o "TT"
    private double peso;
    private int dano; // daño base; cada rama decide cómo usarlo

    protected Armas(String nombre, long precio, String equipo, double peso, int dano) {
        validarNombre(nombre);
        if (!"CT".equals(equipo) && !"TT".equals(equipo)) {
            throw new ValorInvalidoException("equipo debe ser CT o TT (recibido: " + equipo + ")");
        }
        if (precio < 0 || peso < 0 || dano < 0) {
            throw new ValorInvalidoException("precio, peso y daño no pueden ser negativos");
        }
        this.nombre = nombre;
        this.equipo = equipo;
        this.peso = peso;
        this.dano = dano;
        setPrecio(precio);
    }

    /** Mensaje del dominio para PATCH: cambiar el nombre pasando por la misma validación. */
    public void renombrar(String nuevoNombre) {
        validarNombre(nuevoNombre);
        this.nombre = nuevoNombre;
    }

    /** Mensaje del dominio para PATCH: un cambio de precio (balance de la tienda). */
    public void cambiarPrecio(long nuevoPrecio) {
        setPrecio(nuevoPrecio);
    }

    private static void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ValorInvalidoException("el nombre no puede estar vacío");
        }
    }

    /**
     * Regla de compra única para toda la jerarquía. final: ninguna subclase
     * puede alterar cómo se valida o registra una compra.
     */
    public final String comprar(double dineroDisponible) {
        if (dineroDisponible < getPrecio()) {
            return "No alcanza el dinero para comprar " + nombre + ", cuesta " + getPrecio();
        }
        return nombre + " comprada por " + getPrecio() + ", equipo " + equipo;
    }

    /**
     * Contrato uniforme: el inventario llama esto sobre CUALQUIER Armas
     * (de fuego o granada) sin saber el tipo concreto.
     */
    public abstract String disparar();

    /**
     * Sobrecarga de disparar(): la misma acción con otro argumento. Indicando
     * la distancia al objetivo, cada rama decide qué cambia (un arma de fuego
     * pierde daño fuera de su alcance, una granada puede no llegar).
     */
    public abstract String disparar(double distanciaMetros);

    public abstract String inspeccionar();

    /** Volver a cargar el arma. Cada rama decide si puede hacerlo. */
    public abstract String recargar();

    /**
     * Cuánto daño le hace esta arma a un objetivo que está a esa distancia.
     * Lo usa la simulación de daño; cada rama lo calcula a su modo.
     */
    public abstract int danoA(double distanciaMetros);

    /**
     * Implementación única porque alcanza con los atributos comunes;
     * las subclases pueden sobreescribirla si quieren agregar detalle,
     * pero no están obligadas a hacerlo.
     */
    public String mostrarEnTienda() {
        return String.format("%s | %d$ | equipo %s | %.2fkg", nombre, getPrecio(), equipo, peso);
    }

    /** Regla común para cualquier disparo a distancia: no hay distancias negativas. */
    protected static void validarDistancia(double distanciaMetros) {
        if (Double.isNaN(distanciaMetros) || distanciaMetros < 0) {
            throw new ValorInvalidoException("la distancia no puede ser negativa");
        }
    }

    public Long getId() {
        return id;
    }

    public int getDano() {
        return dano;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEquipo() {
        return equipo;
    }

    public double getPeso() {
        return peso;
    }
}
