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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Las consultas de reglas que usan otros servicios: responden sin tocar la base. */
@SpringBootTest
@AutoConfigureMockMvc
class ReglasControllerTest {

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

    private ResultActions consultar(String regla, String json) throws Exception {
        return mvc.perform(post("/api/reglas/" + regla).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void publicaLasDosReglasEnUnaFrase() throws Exception {
        mvc.perform(get("/api/reglas")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Regla de munición"))
                .andExpect(jsonPath("$[1].nombre").value("Regla de vida"));
    }

    @Test
    void reglaDeMunicionParaDisparar() throws Exception {
        consultar("disparo", "{\"modelo\":\"AK-47\",\"carga\":1}").andExpect(status().isOk())
                .andExpect(jsonPath("$.permitido").value(true))
                .andExpect(jsonPath("$.cargaDespues").value(0));
        consultar("disparo", "{\"modelo\":\"AK-47\",\"carga\":0}").andExpect(status().isConflict())
                .andExpect(jsonPath("$.excepcion").value("MunicionInsuficienteException"));
        consultar("disparo", "{\"modelo\":\"FAMAS\",\"carga\":2,\"modoRafaga\":true}")
                .andExpect(status().isConflict());
        consultar("disparo", "{\"modelo\":\"AK-47\",\"modoRafaga\":true}").andExpect(status().isBadRequest());
        consultar("disparo", "{\"modelo\":\"AK-47\",\"carga\":31}").andExpect(status().isBadRequest());
        consultar("disparo", "{\"modelo\":\"HE\"}").andExpect(status().isOk());
        consultar("disparo", "{\"modelo\":\"HE\",\"lanzada\":true}").andExpect(status().isConflict())
                .andExpect(jsonPath("$.excepcion").value("GranadaUsadaException"));
        consultar("disparo", "{\"modelo\":\"Hacha\"}").andExpect(status().isBadRequest());
    }

    @Test
    void reglaDeMunicionParaRecargar() throws Exception {
        consultar("recarga", "{\"modelo\":\"AWP\",\"carga\":0,\"cargadoresRestantes\":1}").andExpect(status().isOk())
                .andExpect(jsonPath("$.cargaDespues").value(5))
                .andExpect(jsonPath("$.cargadoresDespues").value(0));
        consultar("recarga", "{\"modelo\":\"AWP\",\"carga\":0,\"cargadoresRestantes\":0}")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", containsString("cargadores de reserva")));
        consultar("recarga", "{\"modelo\":\"HE\"}").andExpect(status().isBadRequest());
    }

    @Test
    void reglaDeVidaDaLoMismoQueLaSimulacion() throws Exception {
        consultar("impacto", "{\"modelo\":\"AK-47\",\"distancia\":10,\"vida\":100,\"blindaje\":100}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.danoSinBlindaje").value(35))
                .andExpect(jsonPath("$.vidaPerdida").value(27))
                .andExpect(jsonPath("$.blindajePerdido").value(3))
                .andExpect(jsonPath("$.vidaDespues").value(73));
        consultar("impacto", "{\"modelo\":\"AWP\",\"distancia\":10}").andExpect(status().isOk())
                .andExpect(jsonPath("$.eliminado").value(true));
        consultar("impacto", "{\"modelo\":\"AK-47\",\"vida\":0}").andExpect(status().isConflict())
                .andExpect(jsonPath("$.excepcion").value("JugadorEliminadoException"));
        consultar("impacto", "{\"modelo\":\"AK-47\",\"vida\":150}").andExpect(status().isBadRequest());
    }

    @Test
    void lasConsultasNoGuardanNada() throws Exception {
        consultar("disparo", "{\"modelo\":\"AWP\"}").andExpect(status().isOk());
        consultar("impacto", "{\"modelo\":\"AWP\"}").andExpect(status().isOk());
        assertEquals(0, armaRepository.count());
        assertEquals(0, jugadorRepository.count());
    }
}
