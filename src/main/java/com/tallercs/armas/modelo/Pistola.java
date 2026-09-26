package com.tallercs.armas.modelo;

/* 
 Pistola: arma inicial del jugador, también puede tener modo ráfaga
 (tipo una máquina como la CZ en modo automático).
 Reutiliza el mismo gancho balasPorDisparo() que Rifle en vez de
 inventar un método nuevo, evitando duplicación.
*/
public class Pistola extends ArmaFuego {

    private final boolean esArmaInicial;
    private final boolean modoRafaga;

    public Pistola(String nombre, double precio, String equipo, double peso, int dano,
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
