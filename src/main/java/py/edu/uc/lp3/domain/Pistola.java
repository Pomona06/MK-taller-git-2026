package py.edu.uc.lp3.domain;

public class Pistola extends ArmasDeFuego {

    private final boolean esArmaInicial;
    private final boolean modoRafaga;

    /** Constructor simple: la Glock-18 con la que arrancan los terroristas. */
    public Pistola() {
        this("Glock-18");
    }

    /** Sobrecarga: pistola inicial de los terroristas con otro nombre. */
    public Pistola(String nombre) {
        this(nombre, "TT");
    }

    /** Sobrecarga: pistola inicial del equipo indicado (CT o TT). */
    public Pistola(String nombre, String equipo) {
        this(nombre, 200, equipo, 1.0, 30, 20, 6, 0.6, 2.0, 2200, true, false);
    }

    /** Constructor completo: es el único que llama a super(...). */
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
