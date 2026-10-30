package py.edu.uc.lp3.domain;

import py.edu.uc.lp3.exceptions.ValorInvalidoException;

/**
 * Kevlar: se compra en la tienda (es un Vendible) pero no es un arma. Absorbe
 * parte del daño mientras le quede durabilidad.
 */
public class Chaleco extends Vendible {

    public static final int DURABILIDAD_MAXIMA = 100;

    private int durabilidad;

    /** Constructor simple: un chaleco nuevo. */
    public Chaleco() {
        this(DURABILIDAD_MAXIMA);
    }

    public Chaleco(int durabilidad) {
        if (durabilidad < 0 || durabilidad > DURABILIDAD_MAXIMA) {
            throw new ValorInvalidoException("la durabilidad del chaleco debe estar entre 0 y " + DURABILIDAD_MAXIMA);
        }
        this.durabilidad = durabilidad;
        setPrecio(650L);
        setDescripcion("Kevlar");
    }

    /**
     * Absorbe la mitad del daño, sin pasar de la durabilidad que le queda, y
     * se gasta en lo que absorbió. Devuelve el daño que llega al jugador.
     */
    public int absorber(int dano) {
        int absorbido = Math.min(dano / 2, durabilidad);
        durabilidad -= absorbido;
        return dano - absorbido;
    }

    public int getDurabilidad() {
        return durabilidad;
    }
}
