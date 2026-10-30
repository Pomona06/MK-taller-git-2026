package py.edu.uc.lp3.exceptions;

/** No hay ningún recurso persistido con ese id. HTTP 404. */
public class RecursoNoEncontradoException extends JuegoException {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super("no existe " + recurso + " con id " + id);
    }
}
