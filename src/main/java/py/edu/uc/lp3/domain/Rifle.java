package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("rifle")
public class Rifle extends ArmasDeFuego {

    private boolean modoRafaga;

    /**
     * Constructor simple: un AK-47 de los terroristas, tiro a tiro. Es también
     * el constructor sin argumentos que necesita JPA: deja un objeto legal y
     * después JPA le carga los valores de la fila.
     */
    public Rifle() {
        this("AK-47");
    }

    /** Sobrecarga: otro nombre, mismos valores de un rifle de asalto, tiro a tiro. */
    public Rifle(String nombre) {
        this(nombre, false);
    }

    /** Sobrecarga: rifle de asalto eligiendo si dispara en ráfaga. */
    public Rifle(String nombre, boolean modoRafaga) {
        this(nombre, 2700, "TT", 3.6, 36, 30, 3, 0.75, 7.0, 2500, modoRafaga);
    }

    /** Constructor completo: es el único que llama a super(...). */
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

    public boolean isModoRafaga() {
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
