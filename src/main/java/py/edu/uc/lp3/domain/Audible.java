package py.edu.uc.lp3.domain;

/**
 * Capacidad de sonar en la partida. La comparten clases de familias
 * distintas: una Granada (familia Armas) y la BombaC4 (que no es un arma ni
 * se vende). Quien la usa habla con Audible y no pregunta el tipo concreto.
 */
public interface Audible {

    /** Qué se escucha cuando este objeto suena. Cada clase lo resuelve a su modo. */
    String sonido();

    /**
     * Hasta dónde llega el sonido. Por defecto solo se escucha cerca; la
     * clase que se oiga más lejos reemplaza este default.
     */
    default String alcanceSonido() {
        return "se escucha solo cerca";
    }
}
