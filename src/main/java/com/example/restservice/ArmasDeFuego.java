package com.example.restservice;

/**
 * El profesor había dejado acá un solo atributo:
 *
 *     protected int carga;
 *
 * Lo cambié a private. Un campo protected sigue siendo modificable
 * directamente por cualquier subclase, sin pasar por recargar() ni por
 * disparar() — eso viola justo lo que pide la consigna, que el control de
 * munición no se pise desde afuera (ni siquiera desde una hija descuidada).
 * Se accede a él solo a través de un getter protegido.
 *
 * Se agregaron el resto de los atributos que hacían falta para que
 * disparar()/recargar() tengan una lógica real (capacidad de cargador,
 * cargadores de repuesto, precisión, retroceso, tiempo de recarga) — no
 * estaban en el esqueleto original porque ahí "carga" era el único campo,
 * sin ningún método que lo usara todavía.
 *
 * disparar() y recargar() están implementados una sola vez acá, como final
 * (Template Method). Rifle, Pistola y Sniper solo sobreescriben dos métodos
 * gancho chicos: balasPorDisparo() y calcularDano().
 */
public abstract class ArmasDeFuego extends Armas {

    private int carga;
    private int cargadoresRestantes;
    private final int capacidadCargador;
    private final double precision;
    private final double retroceso;
    private final long tiempoRecargaMs;
    private final String animacion;

    protected ArmasDeFuego(String nombre, long precio, String equipo, double peso, int dano,
                           int capacidadCargador, int cargadoresRestantes,
                           double precision, double retroceso, long tiempoRecargaMs,
                           String animacion) {
        super(nombre, precio, equipo, peso, dano);
        if (capacidadCargador <= 0 || cargadoresRestantes < 0) {
            throw new IllegalArgumentException("capacidad/cargadores inválidos");
        }
        this.capacidadCargador = capacidadCargador;
        this.carga = capacidadCargador;
        this.cargadoresRestantes = cargadoresRestantes;
        this.precision = precision;
        this.retroceso = retroceso;
        this.tiempoRecargaMs = tiempoRecargaMs;
        this.animacion = animacion;
    }

    @Override
    public final String disparar() {
        int balasNecesarias = balasPorDisparo();
        if (carga < balasNecesarias) {
            return getNombre() + " no puede disparar: cargador insuficiente ("
                    + carga + " balas, necesita " + balasNecesarias + ")";
        }
        carga -= balasNecesarias;
        int dano = calcularDano();
        return getNombre() + " disparó (" + balasNecesarias + " balas, precisión "
                + precision + ") causando " + dano + " de daño. Quedan "
                + carga + "/" + capacidadCargador + " balas.";
    }

    public final String recargar() {
        if (carga == capacidadCargador) {
            return getNombre() + " ya tiene el cargador lleno";
        }
        if (cargadoresRestantes <= 0) {
            return getNombre() + " no tiene cargadores de repuesto";
        }
        cargadoresRestantes--;
        carga = capacidadCargador;
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

    protected int getCarga() {
        return carga;
    }

    protected double getPrecision() {
        return precision;
    }
}
