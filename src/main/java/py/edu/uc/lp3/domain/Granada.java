package py.edu.uc.lp3.domain;

/**
 * Granada extiende Armas directamente, no ArmasDeFuego, porque no dispara
 * balas ni se recarga. Sigue respondiendo a disparar() como cualquier otra
 * Arma — acá disparar() delega en lanzar(), que es privado.
 */
public class Granada extends Armas {

    private final double radioExplosion;
    private final double distanciaLanzamiento;
    private final boolean aturde;
    private final double visibilidadReducida;
    private final long cooldownMs;
    private long ultimoLanzamiento;
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
            throw new IllegalArgumentException("radio de explosión y distancia de lanzamiento deben ser positivos");
        }
        if (visibilidadReducida < 0 || visibilidadReducida > 100) {
            throw new IllegalArgumentException("la visibilidad reducida debe estar entre 0 y 100");
        }
        if (cooldownMs < 0) {
            throw new IllegalArgumentException("el cooldown no puede ser negativo");
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
        return lanzar();
    }

    private String lanzar() {
        long ahora = System.currentTimeMillis();
        long transcurrido = ahora - ultimoLanzamiento;
        if (transcurrido < cooldownMs) {
            return getNombre() + " en cooldown, faltan " + (cooldownMs - transcurrido) + "ms";
        }
        ultimoLanzamiento = ahora;
        explotada = false;
        return "Lanzada a " + distanciaLanzamiento + "m. " + explotar();
    }

    private String explotar() {
        explotada = true;
        String efecto = aturde
                ? "aturde y reduce visibilidad " + visibilidadReducida + "%"
                : "causa " + getDano() + " de daño";
        return getNombre() + " explotó (radio " + radioExplosion + "m, " + efecto + ")";
    }

    @Override
    public String inspeccionar() {
        return "Animación de inspección: granada " + getNombre()
                + (explotada ? " (ya usada)" : "");
    }
}
