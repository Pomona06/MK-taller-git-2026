package py.edu.uc.lp3.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.JugadorRepository;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ArmaCrudControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ArmaRepository armaRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @BeforeEach
    void limpiar() {
        armaRepository.deleteAll();
        jugadorRepository.deleteAll();
    }

    private ResultActions postJson(String url, String json) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions patchJson(String url, String json) throws Exception {
        return mvc.perform(patch(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private Long crearArma(String json) throws Exception {
        postJson("/api/armas", json).andExpect(status().isCreated());
        return armaRepository.findAll().stream().mapToLong(a -> a.getId()).max().orElseThrow();
    }

    private Long crearJugador(String json) throws Exception {
        postJson("/api/jugadores", json).andExpect(status().isCreated());
        return jugadorRepository.findAll().stream().mapToLong(j -> j.getId()).max().orElseThrow();
    }

    private ResultActions simular(Long armaId, Long jugadorId, double distancia) throws Exception {
        return postJson("/api/simulaciones/dano",
                "{\"armaId\":" + armaId + ",\"jugadorId\":" + jugadorId + ",\"distancia\":" + distancia + "}");
    }

    @Test
    void elCatalogoListaLasArmasDelJuego() throws Exception {
        mvc.perform(get("/api/catalogo")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(11))
                .andExpect(jsonPath("$[0].modelo").value("AK-47"))
                .andExpect(jsonPath("$[0].equipos[0]").value("TT"));
    }

    @Test
    void crearLeerActualizarYBorrar() throws Exception {
        postJson("/api/armas", "{\"modelo\":\"AK-47\"}")
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.tipo").value("rifle"))
                .andExpect(jsonPath("$.equipo").value("TT"))
                .andExpect(jsonPath("$.precio").value(2700))
                .andExpect(jsonPath("$.carga").value(30));
        Long id = armaRepository.findAll().get(0).getId();

        mvc.perform(get("/api/armas")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/armas/" + id)).andExpect(jsonPath("$.nombre").value("AK-47"));

        patchJson("/api/armas/" + id, "{\"apodo\":\"La Fiel\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apodo").value("La Fiel"));
        mvc.perform(get("/api/armas/" + id)).andExpect(jsonPath("$.apodo").value("La Fiel"));

        mvc.perform(delete("/api/armas/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/armas/" + id)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.excepcion").value("RecursoNoEncontradoException"));
        mvc.perform(delete("/api/armas/9999")).andExpect(status().isNotFound());
    }

    @Test
    void unPostDeUnArmaImposibleNoDejaFilas() throws Exception {
        postJson("/api/armas", "{\"modelo\":\"Hacha\"}").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.excepcion").value("ValorInvalidoException"));
        postJson("/api/armas", "{\"modelo\":\"AK-47\",\"equipo\":\"CT\"}").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("solo para el equipo TT")));
        postJson("/api/armas", "{\"modelo\":\"AWP\"}").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("dos equipos")));
        postJson("/api/armas", "{\"modelo\":\"M4A4\",\"equipo\":\"XX\"}").andExpect(status().isBadRequest());
        postJson("/api/armas", "{}").andExpect(status().isBadRequest());
        assertEquals(0, armaRepository.count());
    }

    @Test
    void unPatchIlegalNoCambiaElArma() throws Exception {
        Long id = crearArma("{\"modelo\":\"AK-47\"}");
        patchJson("/api/armas/" + id, "{\"modoRafaga\":true}").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("no tiene modo ráfaga")));
        patchJson("/api/armas/" + id, "{\"apodo\":\"   \"}").andExpect(status().isBadRequest());
        patchJson("/api/armas/" + id, "{\"apodo\":\"Un apodo demasiado largo\"}").andExpect(status().isBadRequest());
        patchJson("/api/armas/" + id, "{\"apodo\":\"Bien\",\"modoRafaga\":true}").andExpect(status().isBadRequest());
        patchJson("/api/armas/" + id, "{}").andExpect(status().isBadRequest());
        mvc.perform(get("/api/armas/" + id))
                .andExpect(jsonPath("$.apodo").doesNotExist())
                .andExpect(jsonPath("$.modoRafaga").value(false));
    }

    @Test
    void laFamasEnRafagaGastaTresBalasPorDisparo() throws Exception {
        Long id = crearArma("{\"modelo\":\"FAMAS\"}");
        patchJson("/api/armas/" + id, "{\"modoRafaga\":true}").andExpect(status().isOk());
        mvc.perform(post("/api/armas/" + id + "/disparos")).andExpect(status().isOk())
                .andExpect(jsonPath("$.arma.carga").value(22));
    }

    @Test
    void reglaDeMunicionConElAwp() throws Exception {
        Long id = crearArma("{\"modelo\":\"AWP\",\"equipo\":\"CT\"}");
        String disparos = "/api/armas/" + id + "/disparos";
        String recargas = "/api/armas/" + id + "/recargas";
        mvc.perform(post(disparos).param("veces", "0")).andExpect(status().isBadRequest());
        mvc.perform(post(recargas)).andExpect(status().isOk())
                .andExpect(jsonPath("$.resultados[0]", containsString("lleno")))
                .andExpect(jsonPath("$.arma.cargadoresRestantes").value(2));
        for (int vuelta = 0; vuelta < 2; vuelta++) {
            mvc.perform(post(disparos).param("veces", "5")).andExpect(status().isOk())
                    .andExpect(jsonPath("$.arma.carga").value(0));
            mvc.perform(post(recargas)).andExpect(status().isOk());
        }
        mvc.perform(post(disparos).param("veces", "5")).andExpect(status().isOk());
        mvc.perform(post(disparos)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.excepcion").value("MunicionInsuficienteException"));
        mvc.perform(post(recargas)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", containsString("cargadores de reserva")));
    }

    @Test
    void unaGranadaSeUsaUnaSolaVez() throws Exception {
        Long he = crearArma("{\"modelo\":\"HE\",\"equipo\":\"TT\"}");
        mvc.perform(post("/api/armas/" + he + "/disparos").param("distancia", "3")).andExpect(status().isOk())
                .andExpect(jsonPath("$.arma.lanzada").value(true));
        mvc.perform(post("/api/armas/" + he + "/disparos")).andExpect(status().isConflict())
                .andExpect(jsonPath("$.excepcion").value("GranadaUsadaException"));
        mvc.perform(post("/api/armas/" + he + "/recargas")).andExpect(status().isBadRequest());
        patchJson("/api/armas/" + he, "{\"apodo\":\"Regalo\"}").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Name Tag")));
        mvc.perform(get("/api/armas/" + he)).andExpect(jsonPath("$.lanzada").value(true));
    }

    @Test
    void laSimulacionGuardaLaMunicionLaVidaYElBlindaje() throws Exception {
        Long ak = crearArma("{\"modelo\":\"AK-47\"}");
        Long enemigo = crearJugador("{\"nombre\":\"Enemigo\",\"chaleco\":true}");
        simular(ak, enemigo, 10).andExpect(status().isOk())
                .andExpect(jsonPath("$.danoSinBlindaje").value(35))
                .andExpect(jsonPath("$.vidaPerdida").value(27))
                .andExpect(jsonPath("$.blindajePerdido").value(3))
                .andExpect(jsonPath("$.cargaDespues").value(29));
        mvc.perform(get("/api/jugadores/" + enemigo))
                .andExpect(jsonPath("$.vida").value(73))
                .andExpect(jsonPath("$.blindaje").value(97))
                .andExpect(jsonPath("$.eliminado").value(false));
        mvc.perform(get("/api/armas/" + ak)).andExpect(jsonPath("$.carga").value(29));
    }

    @Test
    void unJugadorEliminadoDevuelve409YNoSeGastaLaBala() throws Exception {
        Long awp = crearArma("{\"modelo\":\"AWP\",\"equipo\":\"CT\"}");
        Long bot = crearJugador("{\"nombre\":\"Bot\"}");
        simular(awp, bot, 10).andExpect(status().isOk())
                .andExpect(jsonPath("$.vidaPerdida").value(100))
                .andExpect(jsonPath("$.eliminado").value(true));
        simular(awp, bot, 10).andExpect(status().isConflict())
                .andExpect(jsonPath("$.excepcion").value("JugadorEliminadoException"))
                .andExpect(jsonPath("$.error", containsString("Bot")));
        mvc.perform(get("/api/armas/" + awp)).andExpect(jsonPath("$.carga").value(4));
    }

    @Test
    void laGranadaDeLaSimulacionTampocoSeReusa() throws Exception {
        Long he = crearArma("{\"modelo\":\"HE\",\"equipo\":\"CT\"}");
        Long enemigo = crearJugador("{\"nombre\":\"Enemigo\",\"chaleco\":true}");
        simular(he, enemigo, 3).andExpect(status().isOk())
                .andExpect(jsonPath("$.vidaPerdida").value(59))
                .andExpect(jsonPath("$.cargaDespues").doesNotExist());
        simular(he, enemigo, 3).andExpect(status().isConflict())
                .andExpect(jsonPath("$.excepcion").value("GranadaUsadaException"));
        mvc.perform(get("/api/jugadores/" + enemigo)).andExpect(jsonPath("$.vida").value(41));
    }

    @Test
    void datosIlegalesOInexistentesEnJugadoresYSimulacion() throws Exception {
        postJson("/api/jugadores", "{\"nombre\":\"  \"}").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.excepcion").value("ValorInvalidoException"));
        postJson("/api/jugadores", "{\"nombre\":\"" + "x".repeat(33) + "\"}").andExpect(status().isBadRequest());
        assertEquals(0, jugadorRepository.count());

        Long ak = crearArma("{\"modelo\":\"AK-47\"}");
        Long enemigo = crearJugador("{\"nombre\":\"Enemigo\"}");
        simular(ak, 9999L, 10).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("jugador")));
        simular(ak, enemigo, -1).andExpect(status().isBadRequest());
        postJson("/api/simulaciones/dano", "{\"armaId\":" + ak + "}").andExpect(status().isBadRequest());
        mvc.perform(get("/api/jugadores/9999")).andExpect(status().isNotFound());
        mvc.perform(get("/api/armas/" + ak)).andExpect(jsonPath("$.carga").value(30));
    }
}
