package py.edu.uc.lp3.constants;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoArmasTest {

    @Test
    void seBuscaPorElNombreDelJuegoOPorLaConstante() {
        assertEquals(CatalogoArmas.AK_47, CatalogoArmas.buscar("ak-47").orElseThrow());
        assertEquals(CatalogoArmas.SSG_08, CatalogoArmas.buscar("SSG_08").orElseThrow());
        assertTrue(CatalogoArmas.buscar("Hacha").isEmpty());
        assertTrue(CatalogoArmas.buscar(null).isEmpty());
    }

    @Test
    void cadaArmaLaCompraSoloSuEquipo() {
        assertTrue(CatalogoArmas.AK_47.loPuedeComprar("TT"));
        assertFalse(CatalogoArmas.AK_47.loPuedeComprar("CT"));
        assertFalse(CatalogoArmas.M4A4.loPuedeComprar("TT"));
        assertTrue(CatalogoArmas.AWP.loPuedeComprar("CT") && CatalogoArmas.AWP.loPuedeComprar("TT"));
        assertEquals("TT", CatalogoArmas.GLOCK_18.equipoUnico().orElseThrow());
        assertTrue(CatalogoArmas.AWP.equipoUnico().isEmpty());
    }

    @Test
    void soloLaGlockYLaFamasTienenRafaga() {
        Set<CatalogoArmas> conRafaga = Arrays.stream(CatalogoArmas.values())
                .filter(CatalogoArmas::isTieneRafaga).collect(Collectors.toSet());
        assertEquals(Set.of(CatalogoArmas.FAMAS, CatalogoArmas.GLOCK_18), conRafaga);
    }

    @Test
    void losValoresSonLosDelJuego() {
        assertEquals(2900, CatalogoArmas.M4A4.getPrecio());
        assertEquals(115, CatalogoArmas.AWP.getDano());
        assertEquals(5, CatalogoArmas.AWP.getCargador());
        assertEquals(30, CatalogoArmas.AK_47.getCargador());
        assertEquals(0, CatalogoArmas.FLASHBANG.getDano());
        // m_flRange: 8192 unidades (208 m) los rifles y francotiradores, 4096 (104 m) las pistolas.
        assertEquals(208, (int) CatalogoArmas.AWP.getAlcanceMetros());
        assertEquals(104, (int) CatalogoArmas.USP_S.getAlcanceMetros());
        assertEquals(0, CatalogoArmas.HE.getAlcanceMetros());
    }
}
