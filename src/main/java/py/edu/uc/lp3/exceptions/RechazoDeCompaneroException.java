package py.edu.uc.lp3.exceptions;

/**
 * La regla de un compañero rechazó la jugada del escenario (respondió 4xx).
 * Es abstracta: siempre se lanza la hija que dice quién rechazó, A2 o A3, y
 * cada una tiene su propio handler. Lleva lo que respondió el compañero para
 * que el cliente vea por qué. HTTP 409.
 */
public abstract class RechazoDeCompaneroException extends JuegoException {

    private final String rol;
    private final String companero;
    private final int statusDelCompanero;
    private final Object respuestaDelCompanero;

    protected RechazoDeCompaneroException(String rol, String companero, int statusDelCompanero,
                                          Object respuestaDelCompanero) {
        super("la jugada la rechazó " + rol + " (" + companero + ") con HTTP " + statusDelCompanero);
        this.rol = rol;
        this.companero = companero;
        this.statusDelCompanero = statusDelCompanero;
        this.respuestaDelCompanero = respuestaDelCompanero;
    }

    public String getRol() {
        return rol;
    }

    public String getCompanero() {
        return companero;
    }

    public int getStatusDelCompanero() {
        return statusDelCompanero;
    }

    public Object getRespuestaDelCompanero() {
        return respuestaDelCompanero;
    }
}
