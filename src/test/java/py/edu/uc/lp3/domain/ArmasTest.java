package py.edu.uc.lp3.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArmasTest {

    @Test
    void cadaConstructorSobrecargadoDejaUnEstadoLegal() {
        Armas[] armas = {
                new Rifle(), new Rifle("M4A4"), new Rifle("M4A4", true),
                new Pistola(), new Pistola("USP-S"), new Pistola("USP-S", "CT"),
                new Sniper(), new Sniper("SSG 08"), new Sniper("SSG 08", "TT"),
                new Granada(), new Granada("Molotov"), new Granada("Flashbang", true)
        };
        for (Armas arma : armas) {
            assertFalse(arma.getNombre().isBlank());
            assertTrue(arma.getPrecio() >= 0);
            assertTrue(arma.getEquipo().equals("CT") || arma.getEquipo().equals("TT"));
        }
    }

    @Test
    void elConstructorRechazaValoresQueRompenLasReglas() {
        assertThrows(IllegalArgumentException.class, () -> new Pistola("P250", "XX"));
        assertThrows(IllegalArgumentException.class, () -> new Rifle(" "));
        assertThrows(IllegalArgumentException.class,
                () -> new Sniper("AWP", 4750, "CT", 6.5, 115, 0, 6, 0.95, 9.0, 3700));
        assertThrows(IllegalArgumentException.class,
                () -> new Rifle("AK-47", 2700, "TT", 3.6, 36, 30, 3, 1.5, 7.0, 2500, false));
        assertThrows(IllegalArgumentException.class,
                () -> new Granada("HE", 300, "CT", 0.5, 57, 5.0, 20.0, false, 150, 1000));
    }

    @Test
    void disparoSobreescritoSegunLaClaseHija() {
        Armas rifle = new Rifle();
        Armas granada = new Granada();
        assertTrue(rifle.disparar().contains("Quedan 29/30 balas"));
        assertTrue(granada.disparar().contains("explotó"));
    }

    @Test
    void dispararADistanciaEsUnaSobrecarga() {
        Armas pistola = new Pistola();
        assertTrue(pistola.disparar(10).contains("causando 30 de daño"));
        assertTrue(pistola.disparar(40).contains("causando 15 de daño"));
        assertTrue(new Granada().disparar(50).contains("no llega"));
        assertThrows(IllegalArgumentException.class, () -> pistola.disparar(-1));
    }

    @Test
    void laMunicionSoloCambiaDisparandoYRecargando() {
        ArmasDeFuego sniper = new Sniper();
        for (int i = 0; i < 5; i++) {
            sniper.disparar();
        }
        assertTrue(sniper.disparar().contains("no puede disparar"));
        assertTrue(sniper.recargar().contains("Cargadores restantes: 5"));
        assertEquals(5, sniper.getCarga());
    }
}
