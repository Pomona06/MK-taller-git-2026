package py.edu.uc.lp3.cliente;

import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import py.edu.uc.lp3.exceptions.CompaneroNoDisponibleException;
import py.edu.uc.lp3.exceptions.RechazoDeCompaneroException;

import java.time.Duration;
import java.util.Map;

/**
 * Lo común a las llamadas a la API de un compañero: la URL base, la ruta y los
 * tiempos de espera salen de application.properties. Traduce lo que responde
 * el compañero a la jerarquía de excepciones de este servicio:
 * - 2xx: la regla acepta; se devuelve el cuerpo.
 * - 4xx: la regla rechaza; se lanza el rechazo de ese rol (A2 o A3) con lo
 *   que respondió el compañero.
 * - 5xx, sin conexión o fuera de tiempo: CompaneroNoDisponibleException.
 */
abstract class ClienteDeCompanero {

    private final String rol;
    private final String nombre;
    private final String ruta;
    private final RestClient restClient;

    protected ClienteDeCompanero(String rol, String nombre, String urlBase, String ruta,
                                 long esperaConexionMs, long esperaRespuestaMs) {
        this.rol = rol;
        this.nombre = nombre;
        this.ruta = ruta;
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofMillis(esperaConexionMs));
        fabrica.setReadTimeout(Duration.ofMillis(esperaRespuestaMs));
        this.restClient = RestClient.builder().baseUrl(urlBase).requestFactory(fabrica).build();
    }

    /** Hace el POST a la ruta configurada con ese cuerpo y devuelve la respuesta aceptada. */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> consultar(Map<String, Object> cuerpo) {
        try {
            Map<String, Object> respuesta = restClient.post()
                    .uri(ruta)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(cuerpo)
                    .retrieve()
                    .body(Map.class);
            return respuesta == null ? Map.of() : respuesta;
        } catch (HttpClientErrorException e) {
            throw rechazo(e.getStatusCode().value(), cuerpoDelError(e));
        } catch (HttpServerErrorException e) {
            throw new CompaneroNoDisponibleException(rol, nombre, "respondió HTTP " + e.getStatusCode().value());
        } catch (ResourceAccessException e) {
            throw new CompaneroNoDisponibleException(rol, nombre, "no responde en " + ruta);
        }
    }

    /** El rechazo de este rol: cada hija devuelve su excepción (A2 o A3). */
    protected abstract RechazoDeCompaneroException rechazo(int status, Object respuesta);

    private static Object cuerpoDelError(RestClientResponseException e) {
        try {
            Object cuerpo = e.getResponseBodyAs(Map.class);
            return cuerpo != null ? cuerpo : e.getResponseBodyAsString();
        } catch (RuntimeException noEsJson) {
            return e.getResponseBodyAsString();
        }
    }

    public String getRol() {
        return rol;
    }

    public String getNombre() {
        return nombre;
    }
}
