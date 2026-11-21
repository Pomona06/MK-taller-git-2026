package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import py.edu.uc.lp3.constants.CatalogoArmas;
import py.edu.uc.lp3.constants.ReglasJuego;

/**
 * Granada extiende Armas directamente, no ArmasDeFuego, porque no dispara
 * balas ni se recarga. Sigue respondiendo a disparar() como cualquier otra
 * arma: para una granada, disparar es lanzarla.
 *
 * En CS2 una granada es de un solo uso: se lanza y desaparece. Por eso
 * "lanzada" se guarda en la base. Que no se lance dos veces lo controla la
 * capa service antes de mandar el mensaje.
 */
@Entity
@DiscriminatorValue("granada")
public class Granada extends Armas implements Audible {

    private boolean lanzada;

    /** Constructor simple: una HE, del equipo CT (la compran los dos equipos). */
    public Granada() {
        this(CatalogoArmas.HE);
    }

    /** Sobrecarga: otra granada del catálogo, del equipo CT. */
    public Granada(CatalogoArmas modelo) {
        this(modelo, "CT");
    }

    /** Constructor completo: es el único que llama a super(...). */
    public Granada(CatalogoArmas modelo, String equipo) {
        super(modelo, equipo);
        this.lanzada = false;
    }

    /** Lanzarla a los pies: explota cerca de quien la tira. */
    @Override
    public String disparar() {
        return lanzar("a los pies");
    }

    /** Sobrecarga: lanzarla hacia un objetivo a esa distancia. */
    @Override
    public String disparar(double distanciaMetros) {
        return lanzar("a " + distanciaMetros + " m");
    }

    /**
     * La flashbang no quita vida. La HE hace su daño completo dentro del radio
     * del modelo y nada fuera de él (simplificación: en el juego baja con la
     * distancia).
     */
    @Override
    public int danoA(double distanciaMetros) {
        return distanciaMetros <= ReglasJuego.RADIO_HE_METROS ? getDano() : 0;
    }

    private String lanzar(String destino) {
        lanzada = true;
        String efecto = isAturde() ? "ciega a quien la mira" : "hace hasta " + getDano() + " de daño";
        return getNombre() + " lanzada " + destino + ": explotó y " + efecto + ". Ya no se puede volver a usar.";
    }

    /** Implementación de Audible. Usa el alcance por defecto: una granada se escucha solo cerca. */
    @Override
    public String sonido() {
        return isAturde()
                ? getNombre() + ": ¡pum! seco y después un pitido agudo en los oídos"
                : getNombre() + ": ¡BOOM! grave que retumba";
    }

    @Override
    public String inspeccionar() {
        return "Granada " + getNombre() + (lanzada ? " (ya lanzada)" : " (sin usar)");
    }

    @Override
    public String ranura() {
        return "granadas";
    }

    public boolean isLanzada() {
        return lanzada;
    }

    /** La flashbang ciega en lugar de hacer daño. */
    public boolean isAturde() {
        return getModelo() == CatalogoArmas.FLASHBANG;
    }
}
