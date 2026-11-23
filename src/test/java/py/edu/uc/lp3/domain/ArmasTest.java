package py.edu.uc.lp3.domain;

import org.junit.jupiter.api.Test;
import py.edu.uc.lp3.constants.CatalogoArmas;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArmasTest {

    @Test
    void losConstructoresSimplesDejanUnArmaDelCatalogo() {
        Rifle ak = new Rifle();
        assertEquals("AK-47", ak.getNombre());
        assertEquals("TT", ak.getEquipo());
        assertEquals(2700L, ak.getPrecio());
        assertEquals(30, ak.getCarga());
        assertEquals(3, ak.getCargadoresRestantes());
        assertEquals("CT", new Rifle(CatalogoArmas.M4A4).getEquipo());
        assertEquals("Glock-18", new Pistola().getNombre());
        assertTrue(new Pistola().esInicial());
        assertFalse(new Pistola(CatalogoArmas.DESERT_EAGLE, "TT").esInicial());
        assertEquals("AWP", new Sniper().getNombre());
        assertEquals("HE", new Granada().getNombre());
    }

    @Test
    void disparoEsElMismoMensajeConCuatroRespuestas() {
        List<Armas> inventario = List.of(new Rifle(), new Pistola(), new Sniper(), new Granada());
        for (Armas arma : inventario) {
            assertTrue(arma.disparar().contains(arma.getNombre()));
        }
        assertEquals("principal", inventario.get(0).ranura());
        assertEquals("secundaria", inventario.get(1).ranura());
        assertEquals("principal", inventario.get(2).ranura());
        assertEquals("granadas", inventario.get(3).ranura());
    }

    @Test
    void laMunicionSoloCambiaDisparandoYRecargando() {
        Rifle ak = new Rifle();
        ak.disparar();
        ak.disparar(10);
        assertEquals(28, ak.getCarga());
        ak.recargar();
        assertEquals(30, ak.getCarga());
        assertEquals(2, ak.getCargadoresRestantes());
    }

    @Test
    void enRafagaLaFamasYLaGlockGastanTresBalas() {
        Rifle famas = new Rifle(CatalogoArmas.FAMAS);
        famas.cambiarModoRafaga(true);
        famas.disparar();
        assertEquals(22, famas.getCarga());
        Pistola glock = new Pistola();
        glock.cambiarModoRafaga(true);
        assertEquals(3, glock.balasPorDisparo());
    }

    @Test
    void elDanoBajaConLaDistanciaSegunElModificadorDeAlcance() {
        Rifle ak = new Rifle();
        assertEquals(36, ak.danoA(0));
        assertEquals(35, ak.danoA(10));
        assertTrue(ak.danoA(100) < ak.danoA(10));
        assertEquals(114, new Sniper().danoA(10));
    }

    @Test
    void elSniperBajaLaMiraDespuesDeDisparar() {
        Sniper awp = new Sniper();
        awp.apuntarConMira();
        assertTrue(awp.isConMira());
        awp.disparar(50);
        assertFalse(awp.isConMira());
        assertEquals(4, awp.getCarga());
    }

    @Test
    void laGranadaQuedaLanzadaYLaFlashbangNoHaceDano() {
        Granada he = new Granada();
        assertFalse(he.isLanzada());
        he.disparar(3);
        assertTrue(he.isLanzada());
        assertEquals(99, he.danoA(3));
        assertEquals(0, he.danoA(6));
        Granada flash = new Granada(CatalogoArmas.FLASHBANG);
        assertTrue(flash.isAturde());
        assertEquals(0, flash.danoA(1));
    }

    @Test
    void elJugadorEmpiezaCon100YRecibeElImpactoCalculado() {
        Jugador jugador = new Jugador("Enemigo", "CT", new Chaleco());
        assertEquals("CT", jugador.getEquipo());
        assertEquals(100, jugador.getVida());
        assertEquals(100, jugador.getBlindaje());
        jugador.recibirImpacto(27, 3);
        assertEquals(73, jugador.getVida());
        assertEquals(97, jugador.getBlindaje());
        jugador.recibirImpacto(73, 0);
        assertTrue(jugador.estaEliminado());
    }
}
