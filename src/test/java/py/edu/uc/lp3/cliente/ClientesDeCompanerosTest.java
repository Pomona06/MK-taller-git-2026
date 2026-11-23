package py.edu.uc.lp3.cliente;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import py.edu.uc.lp3.exceptions.CompaneroNoDisponibleException;
import py.edu.uc.lp3.exceptions.RechazoDeA2Exception;
import py.edu.uc.lp3.exceptions.RechazoDeA3Exception;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Los clientes hacia A2 y A3 contra un servidor HTTP falso que responde como cada compañero. */
class ClientesDeCompanerosTest {

    private ServidorFalso servidor;
    private DisparoRapidoA2 a2;
    private DanioA3 a3;

    @BeforeEach
    void levantar() throws Exception {
        servidor = new ServidorFalso();
        a2 = new DisparoRapidoA2("Matías", servidor.url(), "/api/reglas/disparo-rapido", 1000, 2000);
        a3 = new DanioA3("Joaquín", servidor.url(), "/api/reglas/danio", 1000, 2000);
    }

    @AfterEach
    void apagar() {
        servidor.close();
    }

    @Test
    void a2AceptaYMandaLosCamposDeSuContrato() {
        servidor.responder(200, "{\"permitido\":true}");
        Map<String, Object> respuesta = a2.consultar(1.0, 5);
        assertEquals(true, respuesta.get("permitido"));
        assertEquals("/api/reglas/disparo-rapido", servidor.getUltimaRuta());
        assertTrue(servidor.getUltimoPedido().contains("\"estabilidad\":1.0"));
        assertTrue(servidor.getUltimoPedido().contains("\"municionActual\":5"));
        assertEquals(String.valueOf(servidor.getUltimoPedido().getBytes(java.nio.charset.StandardCharsets.UTF_8).length),
                servidor.getUltimoLargo(), "el cuerpo va con Content-Length, no por partes");
    }

    @Test
    void unRechazoDeA2LlevaSuCodigoYSuRespuesta() {
        servidor.responder(400, "{\"error\":\"regla_de_juego\",\"mensaje\":\"no está estabilizado\"}");
        RechazoDeA2Exception e = assertThrows(RechazoDeA2Exception.class, () -> a2.consultar(0.5, 5));
        assertEquals("A2", e.getRol());
        assertEquals(400, e.getStatusDelCompanero());
        assertEquals("regla_de_juego", ((Map<?, ?>) e.getRespuestaDelCompanero()).get("error"));
    }

    @Test
    void a3TraduceLosEquiposYLeeElResultado() {
        servidor.responder(200, "{\"regla\":\"DANIO\",\"permitido\":true,\"resultado\":{\"absorbidoPorBlindaje\":90,"
                + "\"danioALaVida\":90,\"vidaFinal\":10,\"blindajeFinal\":10,\"eliminado\":false}}");
        ResultadoDanioA3 resultado = a3.consultar("Sombra", "TT", "Halcón", "CT", 100, 100, 180);
        assertEquals(10, resultado.vidaFinal());
        assertEquals(10, resultado.blindajeFinal());
        assertFalse(resultado.eliminado());
        assertTrue(servidor.getUltimoPedido().contains("\"equipoTirador\":\"TERRORISTAS\""));
        assertTrue(servidor.getUltimoPedido().contains("\"equipoObjetivo\":\"ANTITERRORISTAS\""));
    }

    @Test
    void unRechazoDeA3EsDistintoDeUnoDeA2() {
        servidor.responder(422, "{\"status\":422,\"codigo\":\"DISPARO_NO_PERMITIDO\",\"mensaje\":\"Halcón ya está eliminado\"}");
        RechazoDeA3Exception e = assertThrows(RechazoDeA3Exception.class,
                () -> a3.consultar("Sombra", "TT", "Halcón", "CT", 0, 0, 100));
        assertEquals("A3", e.getRol());
        assertEquals(422, e.getStatusDelCompanero());
    }

    @Test
    void unErrorDelServidorOUnaRespuestaSinResultadoEsNoDisponible() {
        servidor.responder(500, "{}");
        assertThrows(CompaneroNoDisponibleException.class, () -> a2.consultar(1.0, 5));
        servidor.responder(200, "{\"permitido\":true}");
        assertThrows(CompaneroNoDisponibleException.class,
                () -> a3.consultar("Sombra", "TT", "Halcón", "CT", 100, 0, 50));
    }

    @Test
    void unCompaneroApagadoEsNoDisponible() {
        servidor.close();
        CompaneroNoDisponibleException e = assertThrows(CompaneroNoDisponibleException.class, () -> a2.consultar(1.0, 5));
        assertEquals("A2", e.getRol());
    }
}
