package py.edu.uc.lp3.exceptions;

/** La regla de A2 (disparo rápido) rechazó la jugada. HTTP 409. */
public class RechazoDeA2Exception extends RechazoDeCompaneroException {

    public RechazoDeA2Exception(String companero, int statusDelCompanero, Object respuestaDelCompanero) {
        super("A2", companero, statusDelCompanero, respuestaDelCompanero);
    }
}
