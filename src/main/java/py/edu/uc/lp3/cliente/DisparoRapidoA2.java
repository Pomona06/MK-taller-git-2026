package py.edu.uc.lp3.cliente;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import py.edu.uc.lp3.exceptions.RechazoDeA2Exception;
import py.edu.uc.lp3.exceptions.RechazoDeCompaneroException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A2 (Matías): su regla de disparo rápido de francotirador. Según lo que
 * publicó, un francotirador con estabilidad mayor a 0.7 puede hacer un
 * disparo rápido que gasta 2 balas; si no está estabilizado o tiene menos de
 * 2 balas, la regla rechaza. Es una consulta pura: no cambia su base.
 * Los nombres de los campos salen de su contrato; si los cambia, se toca solo
 * esta clase, y si cambia la ruta, alcanza con la propiedad A2_RUTA.
 */
@Component
public class DisparoRapidoA2 extends ClienteDeCompanero {

    public DisparoRapidoA2(@Value("${companeros.a2.nombre}") String nombre,
                           @Value("${companeros.a2.url}") String urlBase,
                           @Value("${companeros.a2.ruta-disparo-rapido}") String ruta,
                           @Value("${companeros.espera-conexion-ms}") long esperaConexionMs,
                           @Value("${companeros.espera-respuesta-ms}") long esperaRespuestaMs) {
        super("A2", nombre, urlBase, ruta, esperaConexionMs, esperaRespuestaMs);
    }

    /** ¿Puede hacer el disparo rápido con esa estabilidad y esas balas en el cargador? */
    public Map<String, Object> consultar(double estabilidad, int municionActual) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("estabilidad", estabilidad);
        cuerpo.put("municionActual", municionActual);
        return consultar(cuerpo);
    }

    @Override
    protected RechazoDeCompaneroException rechazo(int status, Object respuesta) {
        return new RechazoDeA2Exception(getNombre(), status, respuesta);
    }
}
