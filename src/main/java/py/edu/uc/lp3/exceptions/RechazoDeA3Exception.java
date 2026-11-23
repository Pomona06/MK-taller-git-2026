package py.edu.uc.lp3.exceptions;

/** La regla de A3 (daño con blindaje) rechazó la jugada. HTTP 409. */
public class RechazoDeA3Exception extends RechazoDeCompaneroException {

    public RechazoDeA3Exception(String companero, int statusDelCompanero, Object respuestaDelCompanero) {
        super("A3", companero, statusDelCompanero, respuestaDelCompanero);
    }
}
