package py.edu.uc.lp3.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * POO-09: una jugada del escenario de disparo rápido que aceptaron los dos
 * compañeros (A2 y A3). Se guarda en la tabla "jugada" solo cuando las dos
 * reglas ajenas la permitieron. Es un registro: no cambia después de creado.
 */
@Entity
@Table(name = "jugada")
public class Jugada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime momento;
    private String tirador;
    private String equipoTirador;
    private Long armaId;
    private String arma;
    private Long objetivoId;
    private String objetivo;
    private String equipoObjetivo;
    private double distancia;
    private boolean conMira;
    private double estabilidad;
    private int balasGastadas;
    private int cargaDespues;
    private int danio;
    private int vidaAntes;
    private int vidaDespues;
    private int blindajeAntes;
    private int blindajeDespues;
    private boolean eliminado;
    @Column(length = 2000)
    private String respuestaA2;
    @Column(length = 2000)
    private String respuestaA3;

    /** Solo para JPA. */
    protected Jugada() {
    }

    public Jugada(LocalDateTime momento, String tirador, String equipoTirador, Long armaId, String arma,
                  Long objetivoId, String objetivo, String equipoObjetivo, double distancia, boolean conMira,
                  double estabilidad, int balasGastadas, int cargaDespues, int danio, int vidaAntes,
                  int vidaDespues, int blindajeAntes, int blindajeDespues, boolean eliminado,
                  String respuestaA2, String respuestaA3) {
        this.momento = momento;
        this.tirador = tirador;
        this.equipoTirador = equipoTirador;
        this.armaId = armaId;
        this.arma = arma;
        this.objetivoId = objetivoId;
        this.objetivo = objetivo;
        this.equipoObjetivo = equipoObjetivo;
        this.distancia = distancia;
        this.conMira = conMira;
        this.estabilidad = estabilidad;
        this.balasGastadas = balasGastadas;
        this.cargaDespues = cargaDespues;
        this.danio = danio;
        this.vidaAntes = vidaAntes;
        this.vidaDespues = vidaDespues;
        this.blindajeAntes = blindajeAntes;
        this.blindajeDespues = blindajeDespues;
        this.eliminado = eliminado;
        this.respuestaA2 = respuestaA2;
        this.respuestaA3 = respuestaA3;
    }

    public Long getId() { return id; }
    public LocalDateTime getMomento() { return momento; }
    public String getTirador() { return tirador; }
    public String getEquipoTirador() { return equipoTirador; }
    public Long getArmaId() { return armaId; }
    public String getArma() { return arma; }
    public Long getObjetivoId() { return objetivoId; }
    public String getObjetivo() { return objetivo; }
    public String getEquipoObjetivo() { return equipoObjetivo; }
    public double getDistancia() { return distancia; }
    public boolean isConMira() { return conMira; }
    public double getEstabilidad() { return estabilidad; }
    public int getBalasGastadas() { return balasGastadas; }
    public int getCargaDespues() { return cargaDespues; }
    public int getDanio() { return danio; }
    public int getVidaAntes() { return vidaAntes; }
    public int getVidaDespues() { return vidaDespues; }
    public int getBlindajeAntes() { return blindajeAntes; }
    public int getBlindajeDespues() { return blindajeDespues; }
    public boolean isEliminado() { return eliminado; }
    public String getRespuestaA2() { return respuestaA2; }
    public String getRespuestaA3() { return respuestaA3; }
}
