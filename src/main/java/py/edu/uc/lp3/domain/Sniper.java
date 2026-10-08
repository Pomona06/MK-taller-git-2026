package py.edu.uc.lp3.domain;

public class Sniper extends ArmasDeFuego {

    private boolean conMira;

    /** Constructor simple: el AWP de los antiterroristas. */
    public Sniper() {
        this("AWP");
    }

    /** Sobrecarga: francotirador de los antiterroristas con otro nombre. */
    public Sniper(String nombre) {
        this(nombre, "CT");
    }

    /** Sobrecarga: francotirador para el equipo indicado (CT o TT). */
    public Sniper(String nombre, String equipo) {
        this(nombre, 4750, equipo, 6.5, 115, 5, 6, 0.95, 9.0, 3700);
    }

    /** Constructor completo: es el único que llama a super(...). */
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
    protected double alcanceEfectivo() {
        return 200.0;
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
