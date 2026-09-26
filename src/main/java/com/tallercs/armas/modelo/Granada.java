package com.tallercs.armas.modelo;

/*
 Granada: NO es un ArmaFuego (no dispara balas ni se recarga), pero
 sigue siendo una Arma y por lo tanto responde a disparar() como
 cualquier otra — acá disparar() delega en lanzar(), que es privado.

 El cooldown (ultimoLanzamiento, cooldownMs) es privado y se controla
 enteramente adentro de la clase: nadie desde afuera puede forzar una
 explosión ni resetear el temporizador.
 */
public class Granada extends Arma {

    private final double radioExplosion;
    private final double distanciaLanzamiento;
    private final boolean aturde;
    private final double visibilidadReducida;
    private final long cooldownMs;
    private long ultimoLanzamiento;
    private boolean explotada;

    public Granada(String nombre, double precio, String equipo, double peso, int dano,
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
