package py.edu.uc.lp3.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AudibleTest {

    @Test
    void cadaFamiliaImplementaSonidoASuModo() {
        Audible granada = new Granada();
        Audible flash = new Granada("Flashbang", true);
        Audible bomba = new BombaC4("A");
        assertTrue(granada.sonido().contains("¡BOOM!"));
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

    @Test
    void laC4RechazaUnEstadoImposible() {
        assertThrows(IllegalArgumentException.class, () -> new BombaC4("C"));
        assertThrows(IllegalArgumentException.class, () -> new BombaC4("A", -1));
        assertThrows(IllegalArgumentException.class, () -> new BombaC4("A", 41));
    }
}
