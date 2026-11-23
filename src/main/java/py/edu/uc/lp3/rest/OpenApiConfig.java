package py.edu.uc.lp3.rest;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Datos generales del documento OpenAPI que publica springdoc en
 * /v3/api-docs (y que se puede probar en /swagger-ui.html). La descripción
 * explica las dos reglas para que otro servicio las consuma sin leer el código.
 */
@Configuration
public class OpenApiConfig {

    private static final String DESCRIPCION = """
            Armas, jugadores y chalecos de **Counter-Strike 2**, guardados en H2 (POO-08 y POO-09 de \
            Lenguaje de Programación 3).

            Solo se pueden crear armas que existen en CS2: el catálogo (`GET /api/catalogo`) fija precio, \
            equipo, daño, cargador y reserva con los valores de los archivos del juego.

            ## Las dos reglas

            1. **Regla de munición:** un arma de fuego solo dispara si el cargador tiene las balas de ese \
            disparo (una, o tres en ráfaga) y solo recarga si le queda un cargador de reserva, y una \
            granada se lanza una sola vez. Si no se cumple: **409** con `MunicionInsuficienteException` o \
            `GranadaUsadaException`.
            2. **Regla de vida:** el daño baja con la distancia, el chaleco desvía una parte según la \
            relación de blindaje del arma, la vida queda entre 0 y 100 y un jugador eliminado no recibe más \
            daño. Si no se cumple: **409** con `JugadorEliminadoException`.

            ## Para consumir las reglas desde otro servicio

            Los `POST /api/reglas/...` reciben el estado en el JSON y responden **200** si la regla lo \
            permite. **No leen ni cambian la base.** Un dato imposible en el juego responde **400** \
            (`ValorInvalidoException`).

            ## Errores

            Todos los errores tienen el cuerpo `{"error": "<mensaje>", "excepcion": "<tipo>"}`.
            """;

    @Bean
    public OpenAPI apiDeArmas() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de armas de CS2 — Marcelo Kropff")
                        .version("POO-09")
                        .description(DESCRIPCION)
                        .contact(new Contact().name("Marcelo Kropff (Pomona06)")
                                .url("https://github.com/Pomona06/MK-taller-git-2026"))
                        .license(new License().name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .tags(List.of(
                        new Tag().name("Escenario").description("POO-09: disparo rápido que consulta las reglas de A2 y de A3"),
                        new Tag().name("Reglas").description("Consultar las dos reglas sin tocar la base (para otros servicios)"),
                        new Tag().name("Catálogo").description("Las armas de CS2 que se pueden crear"),
                        new Tag().name("Armas").description("CRUD de armas y regla de munición sobre armas guardadas"),
                        new Tag().name("Jugadores").description("Jugadores y su chaleco"),
                        new Tag().name("Simulación").description("Las dos reglas juntas: un arma guardada le dispara a un jugador guardado"),
                        new Tag().name("Sonidos").description("La interfaz Audible: granadas sin lanzar y una C4"),
                        new Tag().name("Servicio").description("Servicio vivo")));
    }
}
