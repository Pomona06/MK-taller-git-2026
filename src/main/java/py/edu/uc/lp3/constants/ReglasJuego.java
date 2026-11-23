package py.edu.uc.lp3.constants;

/** Valores fijos de una partida de CS2 que usan el dominio y los servicios. */
public final class ReglasJuego {

    /** Vida con la que empieza cada ronda. */
    public static final int VIDA_INICIAL = 100;
    /** Jugadores por equipo en una partida competitiva (Premier y Competitivo: 5 contra 5). */
    public static final int JUGADORES_POR_EQUIPO = 5;
    /** Blindaje de un Kevlar nuevo. */
    public static final int BLINDAJE_KEVLAR = 100;
    /** Precio del Kevlar (item_kevlar, m_nPrice en weapons.vdata). */
    public static final long PRECIO_KEVLAR = 650;
    /** Balas que gasta un disparo en modo ráfaga (Glock-18 y FAMAS). */
    public static final int BALAS_POR_RAFAGA = 3;
    /**
     * Unidades del motor por metro: en los juegos de Valve una unidad es una
     * pulgada (2,54 cm).
     */
    public static final double UNIDADES_POR_METRO = 1 / 0.0254;
    /** El daño se multiplica por m_flRangeModifier cada 500 unidades recorridas. */
    public static final double UNIDADES_POR_TRAMO_DE_ALCANCE = 500.0;
    /**
     * Radio en el que la HE hace daño en el modelo. Simplificación propia: en
     * el juego el daño baja con la distancia a la explosión.
     */
    public static final double RADIO_HE_METROS = 5.0;
    /** Segundos que tarda en explotar la C4 desde que se planta. */
    public static final int SEGUNDOS_C4 = 40;
    /** Segundos que lleva desactivar la C4 sin kit. */
    public static final int SEGUNDOS_DESACTIVAR_SIN_KIT = 10;

    private ReglasJuego() {
    }
}
