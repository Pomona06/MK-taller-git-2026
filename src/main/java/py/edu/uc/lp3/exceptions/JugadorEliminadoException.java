package py.edu.uc.lp3.exceptions;

/** Regla de vida: un jugador con vida 0 ya está eliminado: no recibe más daño ni dispara. HTTP 409. */
public class JugadorEliminadoException extends JuegoException {

    public JugadorEliminadoException(String nombre) {
        this(nombre, "recibir más daño");
    }

    /** Sobrecarga: lo que el jugador eliminado ya no puede hacer (por ejemplo, "disparar"). */
    public JugadorEliminadoException(String nombre, String accion) {
        super(nombre + " ya está eliminado y no puede " + accion);
    }
}
