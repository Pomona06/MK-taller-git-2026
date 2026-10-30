package py.edu.uc.lp3.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import py.edu.uc.lp3.exceptions.MunicionInsuficienteException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;

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
 *
 * Es @Entity abstracta para que sus campos (munición incluida) se guarden en
 * la tabla "arma". La carga persistida solo cambia con disparar() y recargar().
 */
@Entity
public abstract class ArmasDeFuego extends Armas {

    private int carga;
    private int cargadoresRestantes;
    private int capacidadCargador;
    @Column(name = "precision_tiro")
    private double precision;
    private double retroceso;
    private long tiempoRecargaMs;
    private String animacion;

    protected ArmasDeFuego(String nombre, long precio, String equipo, double peso, int dano,
                           int capacidadCargador, int cargadoresRestantes,
                           double precision, double retroceso, long tiempoRecargaMs,
                           String animacion) {
        super(nombre, precio, equipo, peso, dano);
        if (capacidadCargador <= 0 || cargadoresRestantes < 0) {
            throw new ValorInvalidoException("capacidad/cargadores inválidos");
        }
        if (precision < 0 || precision > 1) {
            throw new ValorInvalidoException("la precisión debe estar entre 0 y 1");
        }
        if (retroceso < 0 || tiempoRecargaMs < 0) {
            throw new ValorInvalidoException("retroceso y tiempo de recarga no pueden ser negativos");
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
        return efectuarDisparo(calcularDano(), "");
    }

    /** Sobrecarga: fuera del alcance efectivo el daño se reduce a la mitad. */
    @Override
    public final String disparar(double distanciaMetros) {
        int dano = danoA(distanciaMetros);
        String detalle = " a " + distanciaMetros + "m";
        if (distanciaMetros > alcanceEfectivo()) {
            detalle += " (fuera del alcance efectivo de " + alcanceEfectivo() + "m, daño a la mitad)";
        }
        return efectuarDisparo(dano, detalle);
    }

    @Override
    public final int danoA(double distanciaMetros) {
        validarDistancia(distanciaMetros);
        int dano = calcularDano();
        return distanciaMetros > alcanceEfectivo() ? (int) Math.round(dano * 0.5) : dano;
    }

    /** Única forma de gastar munición: ambas versiones de disparar pasan por acá. */
    private String efectuarDisparo(int dano, String detalle) {
        int balasNecesarias = balasPorDisparo();
        if (carga < balasNecesarias) {
            throw new MunicionInsuficienteException(getNombre() + " no puede disparar: cargador insuficiente ("
                    + carga + " balas, necesita " + balasNecesarias + ")");
        }
        carga -= balasNecesarias;
        return getNombre() + " disparó" + detalle + " (" + balasNecesarias + " balas, precisión "
                + precision + ") causando " + dano + " de daño. Quedan "
                + carga + "/" + capacidadCargador + " balas.";
    }

    /**
     * Distancia en metros hasta la que el arma conserva todo su daño. Un rifle
     * de asalto usa este valor; Pistola y Sniper lo sobreescriben.
     */
    protected double alcanceEfectivo() {
        return 30.0;
    }

    @Override
    public final String recargar() {
        if (carga == capacidadCargador) {
            return getNombre() + " ya tiene el cargador lleno";
        }
        if (cargadoresRestantes <= 0) {
            throw new MunicionInsuficienteException(getNombre() + " no tiene cargadores de repuesto");
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

    public int getCarga() {
        return carga;
    }

    public int getCargadoresRestantes() {
        return cargadoresRestantes;
    }

    public int getCapacidadCargador() {
        return capacidadCargador;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRetroceso() {
        return retroceso;
    }

    public long getTiempoRecargaMs() {
        return tiempoRecargaMs;
    }
}
