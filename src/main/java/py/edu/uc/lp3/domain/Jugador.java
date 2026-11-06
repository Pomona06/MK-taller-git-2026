package py.edu.uc.lp3.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import py.edu.uc.lp3.exceptions.JugadorEliminadoException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;

/**
 * El objetivo de un disparo en la simulación de daño. Se guarda en la tabla
 * "jugador", así la vida que dejó la simulación se ve en un GET posterior.
 *
 * La vida solo cambia con recibirDano(), que sostiene la regla: siempre entre
 * 0 y 100, y un jugador eliminado no recibe más daño. No hay setters.
 */
@Entity
@Table(name = "jugador")
public class Jugador {

    public static final int VIDA_MAXIMA = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private int vida;

    /** null si no compró chaleco. Se guarda y se borra junto con el jugador. */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "chaleco_id")
    private Chaleco chaleco;

    /**
     * Solo para JPA, que necesita un constructor sin argumentos para armar el
     * objeto desde la fila. Es protected: desde afuera del paquete nadie puede
     * hacer new Jugador() y quedarse con un jugador sin nombre. JPA completa
     * los campos por reflexión con valores que ya pasaron la validación.
     */
    protected Jugador() {
    }

    /** Constructor simple: vida completa y sin chaleco. */
    public Jugador(String nombre) {
        this(nombre, VIDA_MAXIMA, null);
    }

    public Jugador(String nombre, int vida, Chaleco chaleco) {
        if (nombre == null || nombre.isBlank()) {
            throw new ValorInvalidoException("el jugador necesita un nombre");
        }
        if (vida < 0 || vida > VIDA_MAXIMA) {
            throw new ValorInvalidoException("la vida debe estar entre 0 y " + VIDA_MAXIMA);
        }
        this.nombre = nombre;
        this.vida = vida;
        this.chaleco = chaleco;
    }

    /** Aplica el daño (primero pasa por el chaleco) y devuelve cuánto llegó a la vida. */
    public int recibirDano(int dano) {
        if (dano < 0) {
            throw new ValorInvalidoException("el daño no puede ser negativo");
        }
        if (estaEliminado()) {
            throw new JugadorEliminadoException(nombre);
        }
        int recibido = chaleco == null ? dano : chaleco.absorber(dano);
        vida = Math.max(0, vida - recibido);
        return recibido;
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
}
