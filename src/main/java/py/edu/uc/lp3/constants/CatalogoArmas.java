package py.edu.uc.lp3.constants;

import java.util.Arrays;
import java.util.Optional;

/**
 * Las armas que existen en Counter-Strike 2, con sus valores reales. Solo se
 * puede guardar un arma que esté en este catálogo, y sus estadísticas salen
 * de acá: el JSON de creación elige el modelo y el equipo, nada más.
 *
 * Fuente: archivos de datos del juego publicados por SteamDatabase en
 * github.com/SteamDatabase/GameTracking-CS2:
 * - game/csgo/pak01_dir/scripts/weapons.vdata: precio (m_nPrice), daño
 *   (m_nDamage), relación de blindaje (m_flArmorRatio), cargador
 *   (m_iMaxClip1), reserva en cargadores (m_nPrimaryReserveAmmoMax con
 *   m_bReserveAmmoAsClips), automática (m_bIsFullAuto), modo ráfaga
 *   (m_bHasBurstMode) y caída del daño con la distancia (m_flRangeModifier);
 * - game/csgo/pak01_dir/scripts/items/items_game.txt: qué equipo puede
 *   comprarla (used_by_classes).
 *
 * El M4A4 figura en esos archivos como weapon_m4a1.
 */
public enum CatalogoArmas {

    AK_47("AK-47", Categoria.RIFLE, 2700, true, false, 36, 1.55, 30, 3, true, false, 0.98),
    GALIL_AR("Galil AR", Categoria.RIFLE, 1800, true, false, 30, 1.55, 35, 4, true, false, 0.98),
    M4A4("M4A4", Categoria.RIFLE, 2900, false, true, 33, 1.40, 30, 4, true, false, 0.97),
    FAMAS("FAMAS", Categoria.RIFLE, 1950, false, true, 30, 1.40, 25, 4, true, true, 0.96),
    GLOCK_18("Glock-18", Categoria.PISTOLA, 200, true, false, 30, 0.94, 20, 3, false, true, 0.85),
    USP_S("USP-S", Categoria.PISTOLA, 200, false, true, 35, 1.01, 12, 2, false, false, 0.91),
    DESERT_EAGLE("Desert Eagle", Categoria.PISTOLA, 700, true, true, 53, 1.864, 7, 3, false, false, 0.85),
    AWP("AWP", Categoria.SNIPER, 4750, true, true, 115, 1.95, 5, 2, false, false, 0.99),
    SSG_08("SSG 08", Categoria.SNIPER, 1700, true, true, 88, 1.70, 10, 2, false, false, 0.98),
    /** La HE no tiene cargador: se lanza una vez y se gasta. */
    HE("HE", Categoria.GRANADA, 300, true, true, 99, 1.2, 0, 0, false, false, 1.0),
    /** La flashbang ciega y no quita vida al explotar: en el modelo su daño es 0. */
    FLASHBANG("Flashbang", Categoria.GRANADA, 200, true, true, 0, 1.0, 0, 0, false, false, 1.0);

    private final String nombre;
    private final Categoria categoria;
    private final long precio;
    private final boolean terroristas;
    private final boolean antiterroristas;
    private final int dano;
    private final double relacionBlindaje;
    private final int cargador;
    private final int cargadoresDeReserva;
    private final boolean automatica;
    private final boolean tieneRafaga;
    private final double modificadorAlcance;

    CatalogoArmas(String nombre, Categoria categoria, long precio, boolean terroristas,
                  boolean antiterroristas, int dano, double relacionBlindaje, int cargador,
                  int cargadoresDeReserva, boolean automatica, boolean tieneRafaga,
                  double modificadorAlcance) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.terroristas = terroristas;
        this.antiterroristas = antiterroristas;
        this.dano = dano;
        this.relacionBlindaje = relacionBlindaje;
        this.cargador = cargador;
        this.cargadoresDeReserva = cargadoresDeReserva;
        this.automatica = automatica;
        this.tieneRafaga = tieneRafaga;
        this.modificadorAlcance = modificadorAlcance;
    }

    /** Busca por el nombre que se ve en el juego ("AK-47") o por la constante ("AK_47"). */
    public static Optional<CatalogoArmas> buscar(String texto) {
        if (texto == null) {
            return Optional.empty();
        }
        String buscado = texto.strip();
        return Arrays.stream(values())
                .filter(m -> m.nombre.equalsIgnoreCase(buscado) || m.name().equalsIgnoreCase(buscado))
                .findFirst();
    }

    /** true si el equipo ("CT" o "TT") puede comprar este modelo. */
    public boolean loPuedeComprar(String equipo) {
        return ("TT".equals(equipo) && terroristas) || ("CT".equals(equipo) && antiterroristas);
    }

    /** El equipo que se usa si el JSON no lo indica: solo existe cuando el arma es de un solo equipo. */
    public Optional<String> equipoUnico() {
        if (terroristas && !antiterroristas) {
            return Optional.of("TT");
        }
        if (antiterroristas && !terroristas) {
            return Optional.of("CT");
        }
        return Optional.empty();
    }

    public String getNombre() {
        return nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public long getPrecio() {
        return precio;
    }

    public boolean isTerroristas() {
        return terroristas;
    }

    public boolean isAntiterroristas() {
        return antiterroristas;
    }

    public int getDano() {
        return dano;
    }

    public double getRelacionBlindaje() {
        return relacionBlindaje;
    }

    public int getCargador() {
        return cargador;
    }

    public int getCargadoresDeReserva() {
        return cargadoresDeReserva;
    }

    public boolean isAutomatica() {
        return automatica;
    }

    public boolean isTieneRafaga() {
        return tieneRafaga;
    }

    public double getModificadorAlcance() {
        return modificadorAlcance;
    }
}
