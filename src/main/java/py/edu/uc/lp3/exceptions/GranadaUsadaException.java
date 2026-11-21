package py.edu.uc.lp3.exceptions;

/** Regla de granada: en CS2 una granada se lanza una vez y desaparece. HTTP 409. */
public class GranadaUsadaException extends JuegoException {

    public GranadaUsadaException(String nombre, Long id) {
        super("la granada " + nombre + " (id " + id + ") ya se lanzó: en CS2 cada granada se usa una sola vez");
    }

    /** Para una consulta de reglas, donde la granada no está guardada y no tiene id. */
    public GranadaUsadaException(String nombre) {
        super("la granada " + nombre + " ya se lanzó: en CS2 cada granada se usa una sola vez");
    }
}
