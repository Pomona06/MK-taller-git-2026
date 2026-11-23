package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import py.edu.uc.lp3.constants.ReglasJuego;

/**
 * El objetivo de un disparo en la simulación de daño. Se guarda en la tabla
 * "jugador", así la vida que dejó la simulación se ve en un GET posterior.
 *
 * Como en cada ronda de CS2, empieza con 100 de vida. No hay setters: la vida
 * y el blindaje solo cambian con recibirImpacto(). Cuánto se pierde y si el
 * impacto es posible lo decide la regla de vida en el servicio.
 */
@Entity
@Table(name = "jugador")
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String equipo; // "CT" o "TT"
    private int vida;

    /** null si no compró chaleco. Se guarda y se borra junto con el jugador. */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "chaleco_id")
    private Chaleco chaleco;

    /**
     * Solo para JPA, que necesita un constructor sin argumentos para armar el
     * objeto desde la fila. Es protected: fuera del paquete domain nadie puede
     * hacer new Jugador() y quedarse con un jugador sin nombre. JPA completa
     * los campos por reflexión con lo que se guardó.
     */
    protected Jugador() {
    }

    /** Constructor simple: empieza la ronda en su equipo, sin chaleco. */
    public Jugador(String nombre, String equipo) {
        this(nombre, equipo, null);
    }

    /** Sobrecarga: empieza la ronda con el chaleco que compró. */
    public Jugador(String nombre, String equipo, Chaleco chaleco) {
        this.nombre = nombre;
        this.equipo = equipo;
        this.vida = ReglasJuego.VIDA_INICIAL;
        this.chaleco = chaleco;
    }

    /** Aplica un impacto ya calculado: lo que pierde la vida y lo que pierde el chaleco. */
    public void recibirImpacto(int vidaPerdida, int blindajePerdido) {
        vida -= vidaPerdida;
        if (chaleco != null) {
            chaleco.gastar(blindajePerdido);
        }
    }

    public boolean estaEliminado() {
        return vida == 0;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEquipo() {
        return equipo;
    }

    public int getVida() {
        return vida;
    }

    public int getBlindaje() {
        return chaleco == null ? 0 : chaleco.getDurabilidad();
    }

    /** Para el JSON: "eliminado": true cuando la vida llegó a 0. */
    public boolean isEliminado() {
        return estaEliminado();
    }

    /** El JSON muestra el blindaje ("blindaje"), no el objeto chaleco. */
    @JsonIgnore
    public Chaleco getChaleco() {
        return chaleco;
    }
}
