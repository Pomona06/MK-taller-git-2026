package com.example.restservice;

public class Pistola extends ArmasDeFuego {

    private final boolean esArmaInicial;
    private final boolean modoRafaga;

    public Pistola(String nombre, long precio, String equipo, double peso, int dano,
                    int capacidadCargador, int cargadoresRestantes,
                    double precision, double retroceso, long tiempoRecargaMs,
                    boolean esArmaInicial, boolean modoRafaga) {
        super(nombre, precio, equipo, peso, dano, capacidadCargador, cargadoresRestantes,
                precision, retroceso, tiempoRecargaMs, "pistola_inspect");
        this.esArmaInicial = esArmaInicial;
        this.modoRafaga = modoRafaga;
    }

    public boolean esInicial() {
        return esArmaInicial;
    }

    public boolean disparoUnicoORafaga() {
        return modoRafaga;
    }

    @Override
    protected int balasPorDisparo() {
        return modoRafaga ? 3 : 1;
    }

    @Override
    protected int calcularDano() {
        return modoRafaga ? (int) Math.round(getDano() * 0.7) : getDano();
    }
}
