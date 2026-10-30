package py.edu.uc.lp3.exceptions;

/**
 * Raíz de la jerarquía de excepciones del servicio. Cada hija representa un
 * tipo de problema distinto y ManejadorErrores tiene un handler por tipo,
 * con su propio código HTTP.
 */
public abstract class JuegoException extends RuntimeException {

    protected JuegoException(String mensaje) {
        super(mensaje);
    }
}
