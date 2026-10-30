package py.edu.uc.lp3.domain;

import py.edu.uc.lp3.exceptions.JugadorEliminadoException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;

/**
 * El objetivo de un disparo en la simulación de daño. La vida solo cambia con
 * recibirDano(), que sostiene la regla: siempre entre 0 y 100, y un jugador
 * eliminado no recibe más daño.
 */
public class Jugador {

    public static final int VIDA_MAXIMA = 100;

    private final String nombre;
    private int vida;
    private final Chaleco chaleco; // null si no compró chaleco

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

    public String getNombre() {
        return nombre;
    }

    public int getVida() {
        return vida;
    }

    public int getBlindaje() {
        return chaleco == null ? 0 : chaleco.getDurabilidad();
    }
}
