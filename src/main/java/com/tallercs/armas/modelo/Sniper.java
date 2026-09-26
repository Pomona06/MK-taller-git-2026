package com.tallercs.armas.modelo;

/*
 Sniper: dispara un solo tiro por vez, pero el daño depende de si el
 jugador está apuntando con la mira (conMira). A diferencia de Rifle/
 Pistola, acá lo que cambia entre instancias no es cuántas balas gasta
 sino cómo calcula el daño, por eso sobreescribe calcularDano() en vez
 de balasPorDisparo().
*/
public class Sniper extends ArmaFuego {

    private boolean conMira;

    public Sniper(String nombre, double precio, String equipo, double peso, int dano,
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
        // sin mira, el daño cae fuerte (disparo "a ojo")
        return conMira ? getDano() : (int) Math.round(getDano() * 0.4);
    }
}
