package py.edu.uc.lp3.exceptions;

/** Regla de equipo: en una partida de CS2 se juega cinco contra cinco. HTTP 409. */
public class EquipoCompletoException extends JuegoException {

    public EquipoCompletoException(String equipo, int maximo) {
        super("el equipo " + equipo + " ya tiene " + maximo + " jugadores: en CS2 se juega " + maximo
                + " contra " + maximo);
    }
}
