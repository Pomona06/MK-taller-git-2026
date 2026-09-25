package com.tallercs.armas.modelo;

/**
 * Rama de armas que disparan y recargan.
 *
 * disparar() y recargar() están implementados UNA SOLA VEZ acá, como final:
 * ninguna subclase (Rifle, Pistola, Sniper) puede reescribir cómo se gasta
 * munición o cómo funciona el cooldown de recarga. Lo único que cada
 * subclase aporta es su "gancho" (Template Method):
 *   - balasPorDisparo(): cuántas balas gasta un solo disparo
 *   - calcularDano():    cómo modula el daño base heredado de Arma
 *
 * Esto evita la duplicación que tendríamos si Rifle, Pistola y Sniper
 * reimplementaran disparar()/recargar() cada una por su cuenta.
 *
 * balasEnCargador y cargadoresRestantes son private: el control de munición
 * no se puede pisar desde afuera ni desde una hija descuidada.
 */
public abstract class ArmaFuego extends Arma {

    private int balasEnCargador;
    private int cargadoresRestantes;
    private final int capacidadCargador;
    private final double precision;
    private final double retroceso;
    private final long tiempoRecargaMs;
    private final String animacion;

    protected ArmaFuego(String nombre, double precio, String equipo, double peso, int dano,
                         int capacidadCargador, int cargadoresRestantes,
                         double precision, double retroceso, long tiempoRecargaMs,
                         String animacion) {
        super(nombre, precio, equipo, peso, dano);
        if (capacidadCargador <= 0 || cargadoresRestantes < 0) {
            throw new IllegalArgumentException("capacidad/cargadores inválidos");
        }
        this.capacidadCargador = capacidadCargador;
        this.balasEnCargador = capacidadCargador;
        this.cargadoresRestantes = cargadoresRestantes;
        this.precision = precision;
        this.retroceso = retroceso;
        this.tiempoRecargaMs = tiempoRecargaMs;
        this.animacion = animacion;
    }

    @Override
    public final String disparar() {
        int balasNecesarias = balasPorDisparo();
        if (balasEnCargador < balasNecesarias) {
            return getNombre() + " no puede disparar: cargador insuficiente ("
                    + balasEnCargador + " balas, necesita " + balasNecesarias + ")";
        }
        balasEnCargador -= balasNecesarias;
        int dano = calcularDano();
        return getNombre() + " disparó (" + balasNecesarias + " balas, precisión "
                + precision + ") causando " + dano + " de daño. Quedan "
                + balasEnCargador + "/" + capacidadCargador + " balas.";
    }

    public final String recargar() {
        if (balasEnCargador == capacidadCargador) {
            return getNombre() + " ya tiene el cargador lleno";
        }
        if (cargadoresRestantes <= 0) {
            return getNombre() + " no tiene cargadores de repuesto";
        }
        cargadoresRestantes--;
        balasEnCargador = capacidadCargador;
        return getNombre() + " recargada en " + tiempoRecargaMs
                + "ms. Cargadores restantes: " + cargadoresRestantes;
    }

    @Override
    public String inspeccionar() {
        return "Animación de inspección: " + animacion + " (retroceso " + retroceso + ")";
    }

    /** Método gancho: cuántas balas consume UN disparo de esta arma concreta. */
    protected abstract int balasPorDisparo();

    /** Método gancho: cómo esta arma concreta deriva el daño a partir del daño base. */
    protected abstract int calcularDano();

    protected int getBalasEnCargador() {
        return balasEnCargador;
    }

    protected double getPrecision() {
        return precision;
    }
}
