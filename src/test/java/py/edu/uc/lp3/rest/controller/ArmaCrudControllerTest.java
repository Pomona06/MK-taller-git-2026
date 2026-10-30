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
import py.edu.uc.lp3.domain.Rifle;
import py.edu.uc.lp3.domain.Sniper;
import py.edu.uc.lp3.repository.ArmaRepository;

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

    @BeforeEach
    void limpiar() {
        armaRepository.deleteAll();
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
    void simulacionDeDanoConLasTresClases() throws Exception {
        Long id = armaRepository.save(new Sniper()).getId();
        mvc.perform(post("/api/simulaciones/dano").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"armaId\":" + id + ",\"distancia\":50,\"jugador\":\"Enemigo\",\"vida\":100,\"blindaje\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.danoDelArma").value(46))
                .andExpect(jsonPath("$.danoRecibido").value(23))
                .andExpect(jsonPath("$.vidaDespues").value(77))
                .andExpect(jsonPath("$.blindajeDespues").value(77));
        Armas awp = armaRepository.findById(id).orElseThrow();
        assertEquals(4, ((Sniper) awp).getCarga());
    }

    @Test
    void unJugadorEliminadoDevuelve409YNoGastaMunicion() throws Exception {
        Long id = armaRepository.save(new Rifle()).getId();
        mvc.perform(post("/api/simulaciones/dano").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"armaId\":" + id + ",\"jugador\":\"Caido\",\"vida\":0}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.excepcion").value("JugadorEliminadoException"))
                .andExpect(jsonPath("$.error", containsString("Caido")));
        assertEquals(30, ((Rifle) armaRepository.findById(id).orElseThrow()).getCarga());
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
