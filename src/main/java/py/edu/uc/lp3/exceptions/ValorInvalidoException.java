package py.edu.uc.lp3.exceptions;

/** Un dato que llega al servicio no puede existir en el juego o rompe una regla. HTTP 400. */
public class ValorInvalidoException extends JuegoException {

    public ValorInvalidoException(String mensaje) {
        super(mensaje);
    }
}
