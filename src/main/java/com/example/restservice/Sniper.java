package com.example.restservice;

public class Sniper extends ArmasDeFuego {

    private boolean conMira;

    public Sniper(String nombre, long precio, String equipo, double peso, int dano,
                  int capacidadCargador, int cargadoresRestantes,
                  double precision, double retroceso, long tiempoRecargaMs) {
        super(nombre, precio, equipo, peso, dano, capacidadCargador, cargadoresRestantes,
                precision, retroceso, tiempoRecargaMs, "sniper_inspect");
        this.conMira = false;
    }

    public void apuntarConMira() {
        this.conMira = true;
    }

    public void bajarMira() {
        this.conMira = false;
    }

    @Override
    protected int balasPorDisparo() {
        return 1;
    }

    @Override
    protected int calcularDano() {
        return conMira ? getDano() : (int) Math.round(getDano() * 0.4);
    }
}
