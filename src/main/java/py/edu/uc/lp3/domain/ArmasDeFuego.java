package py.edu.uc.lp3.domain;

import jakarta.persistence.Entity;
import py.edu.uc.lp3.constants.CatalogoArmas;
import py.edu.uc.lp3.constants.ReglasJuego;

/**
 * El profesor había dejado acá un solo atributo:
 *
 *     protected int carga;
 *
 * Lo cambié a private. Un campo protected sigue siendo modificable
 * directamente por cualquier subclase, sin pasar por recargar() ni por
 * disparar(), y eso rompe lo que pide la consigna: que la munición no se pise
 * desde afuera, ni siquiera desde una hija descuidada. Desde afuera se lee
 * con getCarga() y solo cambia con disparar() y recargar().
 *
 * La reserva se cuenta en cargadores, como en los datos de CS2
 * (m_bReserveAmmoAsClips). Simplificación: al recargar se descartan las
 * balas que quedaban en el cargador.
 *
 * disparar() y recargar() están implementados una sola vez acá, como final
 * (Template Method). Las hijas sobreescriben métodos gancho chicos:
 * balasPorDisparo(), ranura() y, el Sniper, despuesDeDisparar().
 *
 * Es @Entity abstracta para que sus campos (munición incluida) se guarden en
 * la tabla "arma". Qué disparo es posible lo decide la capa service antes de
 * mandar el mensaje; acá solo se aplica.
 */
@Entity
public abstract class ArmasDeFuego extends Armas {

    private int carga;
    private int cargadoresRestantes;
    private boolean modoRafaga;

    protected ArmasDeFuego(CatalogoArmas modelo, String equipo) {
        super(modelo, equipo);
        this.carga = modelo.getCargador();
        this.cargadoresRestantes = modelo.getCargadoresDeReserva();
        this.modoRafaga = false;
    }

    @Override
    public final String disparar() {
        return efectuarDisparo(getDano(), "");
    }

    /** Sobrecarga: el daño baja con la distancia según el modificador de alcance del modelo. */
    @Override
    public final String disparar(double distanciaMetros) {
        return efectuarDisparo(danoA(distanciaMetros), " a " + distanciaMetros + " m");
    }

    /**
     * Daño en el cuerpo a esa distancia, sin blindaje: el daño base se
     * multiplica por el modificador de alcance una vez cada 500 unidades.
     */
    @Override
    public final int danoA(double distanciaMetros) {
        double unidades = distanciaMetros * ReglasJuego.UNIDADES_POR_METRO;
        double tramos = unidades / ReglasJuego.UNIDADES_POR_TRAMO_DE_ALCANCE;
        return (int) (getDano() * Math.pow(getModelo().getModificadorAlcance(), tramos));
    }

    /** Única forma de gastar munición: las dos versiones de disparar pasan por acá. */
    private String efectuarDisparo(int dano, String detalle) {
        int balas = balasPorDisparo();
        carga -= balas;
        despuesDeDisparar();
        return getNombre() + " disparó" + detalle + " (" + balas + (balas == 1 ? " bala" : " balas")
                + ", " + dano + " de daño por bala). Quedan " + carga + "/" + getCapacidadCargador() + ".";
    }

    /** Usa un cargador de la reserva y deja el cargador lleno. */
    public final String recargar() {
        cargadoresRestantes--;
        carga = getCapacidadCargador();
        return getNombre() + " recargada. Cargadores de reserva: " + cargadoresRestantes;
    }

    /** Cambia entre tiro normal y ráfaga (solo la Glock-18 y la FAMAS la tienen). */
    public void cambiarModoRafaga(boolean rafaga) {
        this.modoRafaga = rafaga;
    }

    @Override
    public String inspeccionar() {
        return "Inspección de " + getNombre() + ": " + carga + "/" + getCapacidadCargador()
                + " balas y " + cargadoresRestantes + " cargadores de reserva";
    }

    /** Método gancho: cuántas balas gasta UN disparo de esta arma. */
    public abstract int balasPorDisparo();

    /** Método gancho: lo que cambia en el arma después de cada disparo. Por defecto, nada. */
    protected void despuesDeDisparar() {
    }

    public int getCarga() {
        return carga;
    }

    public int getCargadoresRestantes() {
        return cargadoresRestantes;
    }

    public int getCapacidadCargador() {
        return getModelo().getCargador();
    }

    public boolean isModoRafaga() {
        return modoRafaga;
    }

    public boolean isAutomatica() {
        return getModelo().isAutomatica();
    }
}
