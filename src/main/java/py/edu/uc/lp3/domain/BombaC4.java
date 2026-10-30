package py.edu.uc.lp3.domain;

/**
 * La C4 no es un Armas ni un Vendible: no se compra ni se dispara, es el
 * objetivo de la ronda. Es otra familia del modelo; lo único que comparte con
 * una Granada es que suena, y eso lo expresa la interfaz Audible.
 */
public class BombaC4 implements Audible {

    /** Segundos que tarda en explotar desde que se planta. */
    public static final int TIEMPO_TOTAL = 40;
    /** Segundos que lleva desactivarla sin kit. */
    private static final int DESACTIVAR_SIN_KIT = 10;

    private final String sitio;
    private final int segundosRestantes;

    /** Constructor simple: recién plantada en el sitio indicado. */
    public BombaC4(String sitio) {
        this(sitio, TIEMPO_TOTAL);
    }

    public BombaC4(String sitio, int segundosRestantes) {
        if (!"A".equals(sitio) && !"B".equals(sitio)) {
            throw new IllegalArgumentException("el sitio debe ser A o B (recibido: " + sitio + ")");
        }
        if (segundosRestantes < 0 || segundosRestantes > TIEMPO_TOTAL) {
            throw new IllegalArgumentException("los segundos restantes deben estar entre 0 y " + TIEMPO_TOTAL);
        }
        this.sitio = sitio;
        this.segundosRestantes = segundosRestantes;
    }

    /** El bip se acelera a medida que se acaba el tiempo. */
    @Override
    public String sonido() {
        if (segundosRestantes == 0) {
            return "¡BOOM! La C4 explotó en el sitio " + sitio;
        }
        if (segundosRestantes <= DESACTIVAR_SIN_KIT) {
            return "bip-bip-bip-bip: quedan " + segundosRestantes + " s en el sitio " + sitio
                    + ", sin kit ya no llega a desactivarse";
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
