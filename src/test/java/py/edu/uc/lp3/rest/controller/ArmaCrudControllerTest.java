package py.edu.uc.lp3.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import py.edu.uc.lp3.domain.Armas;
import py.edu.uc.lp3.domain.Chaleco;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Rifle;
import py.edu.uc.lp3.domain.Sniper;
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

    private ResultActions simular(Long armaId, Long jugadorId, Integer distancia) throws Exception {
        String json = "{\"armaId\":" + armaId + ",\"jugadorId\":" + jugadorId
                + (distancia == null ? "" : ",\"distancia\":" + distancia) + "}";
        return mvc.perform(post("/api/simulaciones/dano").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions crear(String json) throws Exception {
        return mvc.perform(post("/api/armas").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void crearLeerActualizarYBorrar() throws Exception {
        crear("{\"tipo\":\"rifle\",\"nombre\":\"AK-47\",\"modoRafaga\":true}")
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.tipo").value("rifle"));
        Long id = armaRepository.findAll().get(0).getId();

        mvc.perform(get("/api/armas")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/armas/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("AK-47"));

        mvc.perform(patch("/api/armas/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"precio\":2500}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio").value(2500))
                .andExpect(jsonPath("$.nombre").value("AK-47"));
        mvc.perform(get("/api/armas/" + id)).andExpect(jsonPath("$.precio").value(2500));

        mvc.perform(delete("/api/armas/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/armas/" + id)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.excepcion").value("RecursoNoEncontradoException"));
    }

    @Test
    void unPostIlegalNoDejaUnaFilaSucia() throws Exception {
        crear("{\"tipo\":\"rifle\",\"dano\":-5}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.excepcion").value("ValorInvalidoException"));
        crear("{\"tipo\":\"hacha\"}").andExpect(status().isBadRequest());
        assertEquals(0, armaRepository.count());
    }

    @Test
    void unPatchIlegalNoCambiaElArma() throws Exception {
        Long id = armaRepository.save(new Rifle()).getId();
        mvc.perform(patch("/api/armas/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"precio\":-1}"))
                .andExpect(status().isBadRequest());
        assertEquals(2700L, armaRepository.findById(id).orElseThrow().getPrecio());
    }

    @Test
    void borrarUnIdInexistenteDevuelve404() throws Exception {
        mvc.perform(delete("/api/armas/9999")).andExpect(status().isNotFound());
    }

    @Test
    void laMunicionSeGuardaYSinBalasDevuelve409() throws Exception {
        Long id = armaRepository.save(
                new Sniper("AWP", 4750, "CT", 6.5, 115, 1, 0, 0.95, 9.0, 3700)).getId();
        mvc.perform(post("/api/armas/" + id + "/disparos")).andExpect(status().isOk())
                .andExpect(jsonPath("$.arma.carga").value(0));
        mvc.perform(post("/api/armas/" + id + "/disparos")).andExpect(status().isConflict())
                .andExpect(jsonPath("$.excepcion").value("MunicionInsuficienteException"));
        mvc.perform(post("/api/armas/" + id + "/recargas")).andExpect(status().isConflict());
    }

    @Test
    void simulacionDeDanoGuardaLaMunicionYLaVida() throws Exception {
        Long armaId = armaRepository.save(new Sniper()).getId();
        Long jugadorId = jugadorRepository.save(new Jugador("Enemigo", 100, new Chaleco(100))).getId();
        simular(armaId, jugadorId, 50)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.danoDelArma").value(46))
                .andExpect(jsonPath("$.danoRecibido").value(23))
                .andExpect(jsonPath("$.cargaDespues").value(4))
                .andExpect(jsonPath("$.vidaDespues").value(77))
                .andExpect(jsonPath("$.blindajeDespues").value(77));

        // Un GET posterior muestra el estado ya persistido de las tres clases.
        mvc.perform(get("/api/armas/" + armaId)).andExpect(jsonPath("$.carga").value(4));
        mvc.perform(get("/api/jugadores/" + jugadorId))
                .andExpect(jsonPath("$.vida").value(77))
                .andExpect(jsonPath("$.blindaje").value(77))
                .andExpect(jsonPath("$.eliminado").value(false));
    }

    @Test
    void unJugadorEliminadoDevuelve409YNoGastaMunicion() throws Exception {
        Long armaId = armaRepository.save(new Rifle()).getId();
        mvc.perform(post("/api/jugadores").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Bot\",\"vida\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
        Long botId = jugadorRepository.findAll().get(0).getId();

        simular(armaId, botId, 10).andExpect(status().isOk())
                .andExpect(jsonPath("$.eliminado").value(true));
        simular(armaId, botId, 10)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.excepcion").value("JugadorEliminadoException"))
                .andExpect(jsonPath("$.error", containsString("Bot")));
        // Solo la primera bala se gastó: la segunda simulación se deshizo entera.
        assertEquals(29, ((Rifle) armaRepository.findById(armaId).orElseThrow()).getCarga());
        mvc.perform(get("/api/jugadores/" + botId)).andExpect(jsonPath("$.vida").value(0));
    }

    @Test
    void jugadoresInvalidosOInexistentes() throws Exception {
        mvc.perform(post("/api/jugadores").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Enemigo\",\"vida\":150}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.excepcion").value("ValorInvalidoException"));
        mvc.perform(post("/api/jugadores").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Enemigo\",\"blindaje\":-1}"))
                .andExpect(status().isBadRequest());
        assertEquals(0, jugadorRepository.count());

        Long armaId = armaRepository.save(new Rifle()).getId();
        simular(armaId, 9999L, null).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("jugador")));
        mvc.perform(get("/api/jugadores/9999")).andExpect(status().isNotFound());
    }

    @Test
    void sonidosGuardadosUsaLaInterfaz() throws Exception {
        crear("{\"tipo\":\"granada\",\"aturde\":true}").andExpect(status().isCreated());
        mvc.perform(get("/api/sonidos").param("segundos", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoConcreto").value("Granada"))
                .andExpect(jsonPath("$[1].tipoConcreto").value("BombaC4"));
    }
}
