package py.edu.uc.lp3.exceptions;

/**
 * El servicio de un compañero no respondió, se pasó del tiempo o tuvo un error
 * propio (5xx). No es un rechazo de su regla: la jugada no se pudo evaluar.
 * HTTP 503.
 */
public class CompaneroNoDisponibleException extends JuegoException {

    private final String rol;
    private final String companero;

    public CompaneroNoDisponibleException(String rol, String companero, String motivo) {
        super(rol + " (" + companero + ") no está disponible: " + motivo);
        this.rol = rol;
        this.companero = companero;
    }

    public String getRol() {
        return rol;
    }

    public String getCompanero() {
        return companero;
    }
}
