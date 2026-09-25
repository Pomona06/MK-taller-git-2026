package com.tallercs.armas.modelo;

/**
 * Rifle: puede disparar en ráfaga automática o tiro a tiro.
 * Solo aporta sus dos métodos gancho; disparar()/recargar() los hereda
 * sin tocarlos.
 */
public class Rifle extends ArmaFuego {

    private final boolean modoRafaga;

    public Rifle(String nombre, double precio, String equipo, double peso, int dano,
                 int capacidadCargador, int cargadoresRestantes,
                 double precision, double retroceso, long tiempoRecargaMs,
                 boolean modoRafaga) {
        super(nombre, precio, equipo, peso, dano, capacidadCargador, cargadoresRestantes,
                precision, retroceso, tiempoRecargaMs, "rifle_inspect");
        this.modoRafaga = modoRafaga;
    }

    public boolean automatico() {
        return modoRafaga;
    }

    @Override
    protected int balasPorDisparo() {
        return modoRafaga ? 3 : 1;
    }

    @Override
    protected int calcularDano() {
        // en ráfaga cada bala individual pega un poco menos (retroceso acumulado)
        return modoRafaga ? (int) Math.round(getDano() * 0.8) : getDano();
    }
}
