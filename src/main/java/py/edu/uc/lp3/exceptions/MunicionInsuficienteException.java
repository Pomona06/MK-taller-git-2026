package py.edu.uc.lp3.exceptions;

/**
 * Regla de munición: el arma no tiene las balas que pide el disparo o no le
 * quedan cargadores para recargar. El pedido es válido, pero el estado del
 * arma no lo permite. HTTP 409.
 */
public class MunicionInsuficienteException extends JuegoException {

    public MunicionInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
