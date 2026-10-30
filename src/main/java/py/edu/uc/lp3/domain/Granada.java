package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import py.edu.uc.lp3.exceptions.ValorInvalidoException;

/**
 * Granada extiende Armas directamente, no ArmasDeFuego, porque no dispara
 * balas ni se recarga. Sigue respondiendo a disparar() como cualquier otra
 * Arma — acá disparar() y disparar(double) delegan en lanzar(), que es privado.
 */
@Entity
@DiscriminatorValue("granada")
public class Granada extends Armas implements Audible {

    private double radioExplosion;
    private double distanciaLanzamiento;
    private boolean aturde;
    private double visibilidadReducida;
    private long cooldownMs;
    /** Estado de la partida en curso: no se guarda en la base. */
    @Transient
    private long ultimoLanzamiento;
    @Transient
    private boolean explotada;

    /** Constructor simple: una granada explosiva (HE). */
    public Granada() {
        this("HE");
    }

    /** Sobrecarga: granada explosiva con otro nombre. */
    public Granada(String nombre) {
        this(nombre, false);
    }

    /**
     * Sobrecarga: si aturde es una flashbang (sin daño, ciega por completo),
     * si no es una explosiva.
     */
    public Granada(String nombre, boolean aturde) {
        this(nombre, aturde ? 200 : 300, "CT", 0.5, aturde ? 0 : 57,
                aturde ? 4.0 : 5.0, 20.0, aturde, aturde ? 100 : 0, 1000);
    }

    /** Constructor completo: es el único que llama a super(...). */
    public Granada(String nombre, long precio, String equipo, double peso, int dano,
                    double radioExplosion, double distanciaLanzamiento,
                    boolean aturde, double visibilidadReducida, long cooldownMs) {
        super(nombre, precio, equipo, peso, dano);
        if (radioExplosion <= 0 || distanciaLanzamiento <= 0) {
            throw new ValorInvalidoException("radio de explosión y distancia de lanzamiento deben ser positivos");
        }
        if (visibilidadReducida < 0 || visibilidadReducida > 100) {
            throw new ValorInvalidoException("la visibilidad reducida debe estar entre 0 y 100");
        }
        if (cooldownMs < 0) {
            throw new ValorInvalidoException("el cooldown no puede ser negativo");
        }
        this.radioExplosion = radioExplosion;
        this.distanciaLanzamiento = distanciaLanzamiento;
        this.aturde = aturde;
        this.visibilidadReducida = visibilidadReducida;
        this.cooldownMs = cooldownMs;
        this.ultimoLanzamiento = 0L;
        this.explotada = false;
    }

    @Override
    public String disparar() {
        return lanzar(distanciaLanzamiento);
    }

    /** Sobrecarga: lanzar a una distancia concreta, que no puede pasar del máximo. */
    @Override
    public String disparar(double distanciaMetros) {
        validarDistancia(distanciaMetros);
        if (distanciaMetros > distanciaLanzamiento) {
            return getNombre() + " no llega a " + distanciaMetros
                    + "m: el alcance máximo es " + distanciaLanzamiento + "m";
        }
        return lanzar(distanciaMetros);
    }

    /** Una flashbang no hace daño; una explosiva daña solo dentro de su radio. */
    @Override
    public int danoA(double distanciaMetros) {
        validarDistancia(distanciaMetros);
        if (aturde) {
            return 0;
        }
        return distanciaMetros <= radioExplosion ? getDano() : 0;
    }

    /** Una granada no tiene cargador: se usa una vez y se compra otra. */
    @Override
    public String recargar() {
        throw new ValorInvalidoException(getNombre() + " es una granada y no se recarga");
    }

    private String lanzar(double distanciaMetros) {
        long ahora = System.currentTimeMillis();
        long transcurrido = ahora - ultimoLanzamiento;
        if (transcurrido < cooldownMs) {
            return getNombre() + " en cooldown, faltan " + (cooldownMs - transcurrido) + "ms";
        }
        ultimoLanzamiento = ahora;
        explotada = false;
        return "Lanzada a " + distanciaMetros + "m. " + explotar();
    }

    private String explotar() {
        explotada = true;
        String efecto = aturde
                ? "aturde y reduce visibilidad " + visibilidadReducida + "%"
                : "causa " + getDano() + " de daño";
        return getNombre() + " explotó (radio " + radioExplosion + "m, " + efecto + ")";
    }

    /**
     * Implementación de Audible. Usa el alcance por defecto de la interfaz:
     * una granada se escucha solo cerca.
     */
    @Override
    public String sonido() {
        return aturde
                ? getNombre() + ": ¡pum! seco y después un pitido agudo en los oídos"
                : getNombre() + ": ¡BOOM! grave que retumba";
    }

    public double getRadioExplosion() {
        return radioExplosion;
    }

    public double getDistanciaLanzamiento() {
        return distanciaLanzamiento;
    }

    public boolean isAturde() {
        return aturde;
    }

    public double getVisibilidadReducida() {
        return visibilidadReducida;
    }

    public long getCooldownMs() {
        return cooldownMs;
    }

    @Override
    public String inspeccionar() {
        return "Animación de inspección: granada " + getNombre()
                + (explotada ? " (ya usada)" : "");
    }
}
