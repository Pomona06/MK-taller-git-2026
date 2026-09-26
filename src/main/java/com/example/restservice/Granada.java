package com.example.restservice;

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

    public Granada(String nombre, long precio, String equipo, double peso, int dano,
                    double radioExplosion, double distanciaLanzamiento,
                    boolean aturde, double visibilidadReducida, long cooldownMs) {
        super(nombre, precio, equipo, peso, dano);
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
