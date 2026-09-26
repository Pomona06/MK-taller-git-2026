package com.example.restservice;

public class Rifle extends ArmasDeFuego {

    private final boolean modoRafaga;

    public Rifle(String nombre, long precio, String equipo, double peso, int dano,
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
        return modoRafaga ? (int) Math.round(getDano() * 0.8) : getDano();
    }
}
