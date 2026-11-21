package py.edu.uc.lp3.domain;

import org.junit.jupiter.api.Test;
import py.edu.uc.lp3.constants.CatalogoArmas;

import static org.junit.jupiter.api.Assertions.*;

class AudibleTest {

    @Test
    void cadaFamiliaImplementaSonidoASuModo() {
        Audible he = new Granada();
        Audible flash = new Granada(CatalogoArmas.FLASHBANG);
        Audible bomba = new BombaC4("A");
        assertTrue(he.sonido().contains("¡BOOM!"));
        assertTrue(flash.sonido().contains("pitido"));
        assertTrue(bomba.sonido().contains("bip"));
    }

    @Test
    void laGranadaUsaElDefaultYLaC4LoReemplaza() {
        assertEquals("se escucha solo cerca", new Granada().alcanceSonido());
        assertEquals("se escucha en todo el mapa", new BombaC4("B").alcanceSonido());
    }

    @Test
    void elBipDeLaC4CambiaConElTiempo() {
        assertTrue(new BombaC4("A", 30).sonido().startsWith("bip... bip..."));
        assertTrue(new BombaC4("A", 5).sonido().startsWith("bip-bip-bip-bip"));
        assertTrue(new BombaC4("A", 0).sonido().contains("explotó"));
    }
}
