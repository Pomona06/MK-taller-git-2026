package py.edu.uc.lp3.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.JugadorRepository;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SonidoControllerTest {

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

    @Test
    void lasGranadasGuardadasYLaC4RespondenComoAudible() throws Exception {
        mvc.perform(post("/api/armas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"modelo\":\"Flashbang\",\"equipo\":\"CT\"}")).andExpect(status().isCreated());
        mvc.perform(get("/api/sonidos").param("sitio", "b").param("segundos", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoConcreto").value("Granada"))
                .andExpect(jsonPath("$[0].alcance").value("se escucha solo cerca"))
                .andExpect(jsonPath("$[1].tipoConcreto").value("BombaC4"))
                .andExpect(jsonPath("$[1].sonido", containsString("quedan 7 s en el sitio B")))
                .andExpect(jsonPath("$[1].alcance").value("se escucha en todo el mapa"));
    }

    @Test
    void unaGranadaYaLanzadaNoSuena() throws Exception {
        mvc.perform(post("/api/armas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"modelo\":\"HE\",\"equipo\":\"TT\"}")).andExpect(status().isCreated());
        Long he = armaRepository.findAll().get(0).getId();
        mvc.perform(post("/api/armas/" + he + "/disparos")).andExpect(status().isOk());
        mvc.perform(get("/api/sonidos")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipoConcreto").value("BombaC4"));
    }

    @Test
    void unaC4ImposibleDevuelve400() throws Exception {
        mvc.perform(get("/api/sonidos").param("sitio", "C")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("A o B")));
        mvc.perform(get("/api/sonidos").param("segundos", "41")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/sonidos").param("segundos", "-1")).andExpect(status().isBadRequest());
    }
}
