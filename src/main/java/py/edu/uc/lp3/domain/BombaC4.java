package py.edu.uc.lp3.domain;

import py.edu.uc.lp3.constants.ReglasJuego;

/**
 * La C4 no es un Armas ni un Vendible: no se compra ni se dispara, es el
 * objetivo de la ronda. Es otra familia del modelo; lo único que comparte con
 * una Granada es que suena, y eso lo expresa la interfaz Audible. El sitio
 * (A o B) y los segundos (0 a 40) los valida el servicio antes de crearla.
 */
public class BombaC4 implements Audible {

    private final String sitio;
    private final int segundosRestantes;

    /** Constructor simple: recién plantada en el sitio indicado. */
    public BombaC4(String sitio) {
        this(sitio, ReglasJuego.SEGUNDOS_C4);
    }

    /** Sobrecarga: plantada hace un rato, con esos segundos por delante. */
    public BombaC4(String sitio, int segundosRestantes) {
        this.sitio = sitio;
        this.segundosRestantes = segundosRestantes;
    }

    /** El bip se acelera a medida que se acaba el tiempo. */
    @Override
    public String sonido() {
        if (segundosRestantes == 0) {
            return "¡BOOM! La C4 explotó en el sitio " + sitio;
        }
        if (segundosRestantes <= ReglasJuego.SEGUNDOS_DESACTIVAR_SIN_KIT) {
            return "bip-bip-bip-bip: quedan " + segundosRestantes + " s en el sitio " + sitio
                    + "; sin kit ya no llega a desactivarse";
        }
        return "bip... bip...: la C4 del sitio " + sitio + " marca " + segundosRestantes + " s";
    }

    /** Reemplaza el default: la C4 se escucha desde cualquier punto del mapa. */
    @Override
    public String alcanceSonido() {
        return "se escucha en todo el mapa";
    }

    public String getSitio() {
        return sitio;
    }

    public int getSegundosRestantes() {
        return segundosRestantes;
    }
}
