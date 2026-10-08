package py.edu.uc.lp3.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ArmaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void indexConfirmaQueElServicioEstaVivo() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("vivo"));
    }

    @Test
    void crearConstruyeDesdeLaUrl() throws Exception {
        mvc.perform(get("/armas/crear").param("tipo", "rifle").param("nombre", "AK-47")
                        .param("modoRafaga", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoConcreto").value("Rifle"))
                .andExpect(jsonPath("$.disparo", containsString("3 balas")));
    }

    @Test
    void crearSimpleInformaElConstructorUsado() throws Exception {
        mvc.perform(get("/armas/crear-simple").param("tipo", "granada")
                        .param("nombre", "Flashbang").param("aturde", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.constructor").value("Granada(String, boolean)"))
                .andExpect(jsonPath("$.disparo", containsString("aturde")));
    }

    @Test
    void compararMuestraElDisparoDeCadaClaseHija() throws Exception {
        mvc.perform(get("/armas/comparar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoConcreto").value("Rifle"))
                .andExpect(jsonPath("$[3].tipoConcreto").value("Granada"))
                .andExpect(jsonPath("$[3].disparo", containsString("explotó")));
    }

    @Test
    void unValorQueRompeUnaReglaDevuelve400() throws Exception {
        mvc.perform(get("/armas/crear").param("tipo", "rifle").param("dano", "-5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("negativos")));
        mvc.perform(get("/armas/crear").param("tipo", "hacha"))
                .andExpect(status().isBadRequest());
    }
}
