package py.edu.uc.lp3.rest.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import py.edu.uc.lp3.cliente.ServidorFalso;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.JugadaRepository;
import py.edu.uc.lp3.repository.JugadorRepository;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * El escenario de POO-09 de punta a punta, con dos servidores HTTP falsos que
 * hacen de A2 (disparo rápido) y A3 (daño con blindaje).
 */
@SpringBootTest
@AutoConfigureMockMvc
class EscenarioControllerTest {

    private static final ServidorFalso A2 = arrancar();
    private static final ServidorFalso A3 = arrancar();
    private static final String A3_ACEPTA = "{\"regla\":\"DANIO\",\"permitido\":true,\"resultado\":{"
            + "\"absorbidoPorBlindaje\":50,\"danioALaVida\":70,\"vidaFinal\":30,\"blindajeFinal\":50,\"eliminado\":false}}";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ArmaRepository armaRepository;
    @Autowired
    private JugadorRepository jugadorRepository;
    @Autowired
    private JugadaRepository jugadaRepository;

    private Long awp;
    private Long sombra;
    private Long enemigo;

    private static ServidorFalso arrancar() {
        try {
            return new ServidorFalso();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @DynamicPropertySource
    static void companeros(DynamicPropertyRegistry propiedades) {
        propiedades.add("companeros.a2.url", A2::url);
        propiedades.add("companeros.a3.url", A3::url);
    }

    @AfterAll
    static void apagar() {
        A2.close();
        A3.close();
    }

    @BeforeEach
    void preparar() throws Exception {
        jugadaRepository.deleteAll();
        armaRepository.deleteAll();
        jugadorRepository.deleteAll();
        A2.responder(200, "{\"permitido\":true}");
        A3.responder(200, A3_ACEPTA);
        mvc.perform(post("/api/armas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"modelo\":\"AWP\",\"equipo\":\"CT\"}")).andExpect(status().isCreated());
        awp = armaRepository.findAll().get(0).getId();
        sombra = crearJugador("Sombra", "CT");
        enemigo = crearJugador("Enemigo", "TT");
    }

    private Long crearJugador(String nombre, String equipo) throws Exception {
        String json = mvc.perform(post("/api/jugadores").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"" + nombre + "\",\"equipo\":\"" + equipo + "\",\"chaleco\":true}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return Long.valueOf(json.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    private ResultActions escenario(Object armaId, Object objetivoId, boolean conMira) throws Exception {
        return escenario(armaId, sombra, objetivoId, 40, conMira);
    }

    private ResultActions escenario(Object armaId, Object tiradorId, Object objetivoId, double distancia,
                                    boolean conMira) throws Exception {
        return mvc.perform(post("/api/escenarios/disparos-rapidos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"armaId\":" + armaId + ",\"tiradorId\":" + tiradorId + ",\"objetivoId\":" + objetivoId
                        + ",\"distancia\":" + distancia + ",\"conMira\":" + conMira + "}"));
    }

    private void nadaCambio() throws Exception {
        assertEquals(0, jugadaRepository.count());
        mvc.perform(get("/api/armas/" + awp)).andExpect(jsonPath("$.carga").value(5));
        mvc.perform(get("/api/jugadores/" + enemigo))
                .andExpect(jsonPath("$.vida").value(100))
                .andExpect(jsonPath("$.blindaje").value(100));
    }

    @Test
    void siLosDosAceptanSeGuardaLaJugada() throws Exception {
        escenario(awp, enemigo, true).andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.balasGastadas").value(2))
                .andExpect(jsonPath("$.cargaDespues").value(3))
                .andExpect(jsonPath("$.danio").value(222))
                .andExpect(jsonPath("$.tiradorId").value(sombra))
                .andExpect(jsonPath("$.tirador").value("Sombra"))
                .andExpect(jsonPath("$.vidaDespues").value(30))
                .andExpect(jsonPath("$.blindajeDespues").value(50))
                .andExpect(jsonPath("$.respuestaA3", containsString("vidaFinal")));

        // Lo que se le mandó a cada compañero, con los nombres de su contrato.
        assertTrue(A2.getUltimoPedido().contains("\"estabilidad\":1.0"));
        assertTrue(A2.getUltimoPedido().contains("\"municionActual\":5"));
        assertTrue(A3.getUltimoPedido().contains("\"tirador\":\"Sombra\""));
        assertTrue(A3.getUltimoPedido().contains("\"equipoTirador\":\"ANTITERRORISTAS\""));
        assertTrue(A3.getUltimoPedido().contains("\"equipoObjetivo\":\"TERRORISTAS\""));
        assertTrue(A3.getUltimoPedido().contains("\"danio\":222"));

        // Un GET posterior muestra la jugada y el estado guardado.
        mvc.perform(get("/api/escenarios/disparos-rapidos")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/armas/" + awp)).andExpect(jsonPath("$.carga").value(3));
        mvc.perform(get("/api/jugadores/" + enemigo))
                .andExpect(jsonPath("$.vida").value(30))
                .andExpect(jsonPath("$.blindaje").value(50));
    }

    @Test
    void siA2RechazaNoSeConsultaAA3YNoCambiaNada() throws Exception {
        A2.responder(400, "{\"error\":\"regla_de_juego\",\"mensaje\":\"el francotirador no está estabilizado\"}");
        escenario(awp, enemigo, false).andExpect(status().isConflict())
                .andExpect(jsonPath("$.excepcion").value("RechazoDeA2Exception"))
                .andExpect(jsonPath("$.rechazadoPor").value("A2"))
                .andExpect(jsonPath("$.statusDelCompanero").value(400))
                .andExpect(jsonPath("$.respuestaDelCompanero.error").value("regla_de_juego"));
        assertTrue(A2.getUltimoPedido().contains("\"estabilidad\":0.5"));
        assertEquals(0, A3.getPedidos());
        nadaCambio();
    }

    @Test
    void siA3RechazaNoCambiaNadaYSeVeQueFueA3() throws Exception {
        A3.responder(422, "{\"status\":422,\"codigo\":\"DISPARO_NO_PERMITIDO\","
                + "\"excepcion\":\"DisparoNoPermitidoException\",\"mensaje\":\"fuego amigo\"}");
        escenario(awp, enemigo, true).andExpect(status().isConflict())
                .andExpect(jsonPath("$.excepcion").value("RechazoDeA3Exception"))
                .andExpect(jsonPath("$.rechazadoPor").value("A3"))
                .andExpect(jsonPath("$.statusDelCompanero").value(422))
                .andExpect(jsonPath("$.respuestaDelCompanero.codigo").value("DISPARO_NO_PERMITIDO"));
        nadaCambio();
    }

    @Test
    void unCompaneroQueNoRespondeDa503YNoCambiaNada() throws Exception {
        A2.responder(500, "{}");
        escenario(awp, enemigo, true).andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.excepcion").value("CompaneroNoDisponibleException"))
                .andExpect(jsonPath("$.rechazadoPor").value("A2"));
        nadaCambio();
        A2.responder(200, "{\"permitido\":true}");
        A3.responder(200, "{\"resultado\":{\"absorbidoPorBlindaje\":0,\"danioALaVida\":0,\"vidaFinal\":150,"
                + "\"blindajeFinal\":0,\"eliminado\":false}}");
        escenario(awp, enemigo, true).andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.rechazadoPor").value("A3"));
        nadaCambio();
    }

    @Test
    void datosInvalidosNoLleganALosCompaneros() throws Exception {
        mvc.perform(post("/api/armas").contentType(MediaType.APPLICATION_JSON).content("{\"modelo\":\"AK-47\"}"));
        Long ak = armaRepository.findAll().stream().filter(a -> a.getNombre().equals("AK-47")).findFirst()
                .orElseThrow().getId();
        escenario(ak, enemigo, true).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("no es un francotirador")));
        escenario(awp, 9999, true).andExpect(status().isNotFound());
        escenario("null", enemigo, true).andExpect(status().isBadRequest());
        escenario(awp, "null", enemigo, 40, true).andExpect(status().isBadRequest());
        escenario(awp, 9999, enemigo, 40, true).andExpect(status().isNotFound());
        mvc.perform(get("/api/escenarios/disparos-rapidos/9999")).andExpect(status().isNotFound());
        assertEquals(0, A2.getPedidos());
        nadaCambio();
    }

    /** ¿Esto puede pasar en el juego? Lo que no puede pasar se rechaza antes de consultar a nadie. */
    @Test
    void loQueNoPuedePasarEnElJuegoNoLlegaALosCompaneros() throws Exception {
        // Nadie se dispara a sí mismo.
        escenario(awp, enemigo, enemigo, 40, true).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("mismo jugador")));
        // El AWP es de los CT: un TT no lo usa.
        Long otroTT = crearJugador("Otro", "TT");
        escenario(awp, otroTT, enemigo, 40, true).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("solo lo usa un jugador de su equipo")));
        // La bala del AWP llega hasta 208 m.
        escenario(awp, sombra, enemigo, 300, true).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("llega hasta 208 m")));
        // Un tirador eliminado no dispara: se lo elimina con la simulación de POO-08 (AWP a 1 m, sin chaleco).
        Long caido = crearJugadorSinChaleco("Caído", "CT");
        mvc.perform(post("/api/simulaciones/dano").contentType(MediaType.APPLICATION_JSON)
                .content("{\"armaId\":" + awp + ",\"jugadorId\":" + caido + ",\"distancia\":1}"))
                .andExpect(jsonPath("$.eliminado").value(true));
        escenario(awp, caido, enemigo, 40, true).andExpect(status().isConflict())
                .andExpect(jsonPath("$.excepcion").value("JugadorEliminadoException"))
                .andExpect(jsonPath("$.error", containsString("no puede disparar")));
        assertEquals(0, A2.getPedidos());
        assertEquals(0, A3.getPedidos());
        assertEquals(0, jugadaRepository.count());
    }

    private Long crearJugadorSinChaleco(String nombre, String equipo) throws Exception {
        String json = mvc.perform(post("/api/jugadores").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"" + nombre + "\",\"equipo\":\"" + equipo + "\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return Long.valueOf(json.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }
}
