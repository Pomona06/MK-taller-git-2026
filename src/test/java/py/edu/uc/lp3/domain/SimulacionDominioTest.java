package py.edu.uc.lp3.domain;

import org.junit.jupiter.api.Test;
import py.edu.uc.lp3.exceptions.JugadorEliminadoException;
import py.edu.uc.lp3.exceptions.MunicionInsuficienteException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;

import static org.junit.jupiter.api.Assertions.*;

class SimulacionDominioTest {

    @Test
    void elChalecoAbsorbeLaMitadMientrasTengaDurabilidad() {
        Jugador jugador = new Jugador("Enemigo", 100, new Chaleco(10));
        assertEquals(30, jugador.recibirDano(40));   // absorbe solo 10, lo que le queda
        assertEquals(70, jugador.getVida());
        assertEquals(0, jugador.getBlindaje());
    }

    @Test
    void laVidaNuncaBajaDeCeroYUnEliminadoNoRecibeMasDano() {
        Jugador jugador = new Jugador("Enemigo", 20, null);
        jugador.recibirDano(115);
        assertEquals(0, jugador.getVida());
        assertTrue(jugador.estaEliminado());
        assertThrows(JugadorEliminadoException.class, () -> jugador.recibirDano(1));
    }

    @Test
    void elJugadorYElChalecoRechazanValoresImposibles() {
        assertThrows(ValorInvalidoException.class, () -> new Jugador("X", 101, null));
        assertThrows(ValorInvalidoException.class, () -> new Jugador(" "));
        assertThrows(ValorInvalidoException.class, () -> new Chaleco(-1));
    }

    @Test
    void danoADependeDeLaDistanciaYDeLaRama() {
        assertEquals(36, new Rifle().danoA(10));
        assertEquals(18, new Rifle().danoA(50));
        assertEquals(57, new Granada().danoA(3));
        assertEquals(0, new Granada().danoA(8));
        assertEquals(0, new Granada("Flashbang", true).danoA(1));
    }

    @Test
    void sinCargadoresNoSeRecargaYUnaGranadaNoSeRecarga() {
        Rifle rifle = new Rifle("AK-47", 2700, "TT", 3.6, 36, 1, 0, 0.75, 7.0, 2500, false);
        rifle.disparar();
        assertThrows(MunicionInsuficienteException.class, rifle::recargar);
        assertThrows(ValorInvalidoException.class, () -> new Granada().recargar());
    }
}
