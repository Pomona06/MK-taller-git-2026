package py.edu.uc.lp3.exceptions;

/** Un constructor o un mensaje del dominio recibió un valor que rompe una regla. HTTP 400. */
public class ValorInvalidoException extends JuegoException {

    public ValorInvalidoException(String mensaje) {
        super(mensaje);
    }
}
