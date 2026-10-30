package py.edu.uc.lp3.exceptions;

/** Regla de vida: un jugador con vida 0 ya está eliminado y no recibe más daño. HTTP 409. */
public class JugadorEliminadoException extends JuegoException {

    public JugadorEliminadoException(String nombre) {
        super(nombre + " ya está eliminado y no puede recibir más daño");
    }
}
