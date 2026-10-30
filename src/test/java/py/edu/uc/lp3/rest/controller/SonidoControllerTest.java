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
class SonidoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void sonidosRespondeElMensajeDeCadaAudible() throws Exception {
        mvc.perform(get("/sonidos").param("sitio", "B").param("segundos", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoConcreto").value("Granada"))
                .andExpect(jsonPath("$[0].alcance").value("se escucha solo cerca"))
                .andExpect(jsonPath("$[2].tipoConcreto").value("BombaC4"))
                .andExpect(jsonPath("$[2].sonido", containsString("quedan 7 s en el sitio B")))
                .andExpect(jsonPath("$[2].alcance").value("se escucha en todo el mapa"));
    }

    @Test
    void unSitioInvalidoDevuelve400() throws Exception {
        mvc.perform(get("/sonidos").param("sitio", "C"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("A o B")));
    }
}
