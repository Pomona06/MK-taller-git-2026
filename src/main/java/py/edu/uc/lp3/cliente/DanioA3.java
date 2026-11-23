package py.edu.uc.lp3.cliente;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import py.edu.uc.lp3.exceptions.CompaneroNoDisponibleException;
import py.edu.uc.lp3.exceptions.RechazoDeA3Exception;
import py.edu.uc.lp3.exceptions.RechazoDeCompaneroException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A3 (Joaquín): su regla de daño con blindaje. Según lo que publicó, solo se
 * le dispara a un enemigo vivo (otro equipo, vida mayor a 0); el blindaje
 * absorbe hasta la mitad del daño y el resto baja la vida, que no queda por
 * debajo de 0. Rechaza con 422 (objetivo eliminado o fuego amigo) o 400 (dato
 * imposible). Es una consulta pura: no cambia su base.
 * Sus equipos se llaman TERRORISTAS y ANTITERRORISTAS; acá se traducen desde
 * TT y CT.
 */
@Component
public class DanioA3 extends ClienteDeCompanero {

    public DanioA3(@Value("${companeros.a3.nombre}") String nombre,
                   @Value("${companeros.a3.url}") String urlBase,
                   @Value("${companeros.a3.ruta-danio}") String ruta,
                   @Value("${companeros.espera-conexion-ms}") long esperaConexionMs,
                   @Value("${companeros.espera-respuesta-ms}") long esperaRespuestaMs) {
        super("A3", nombre, urlBase, ruta, esperaConexionMs, esperaRespuestaMs);
    }

    /** ¿Qué le hace ese daño al objetivo? Equipos en CT o TT. */
    @SuppressWarnings("unchecked")
    public ResultadoDanioA3 consultar(String tirador, String equipoTirador, String objetivo, String equipoObjetivo,
                                      int vidaObjetivo, int blindajeObjetivo, int danio) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("tirador", tirador);
        cuerpo.put("equipoTirador", nombreDeEquipo(equipoTirador));
        cuerpo.put("objetivo", objetivo);
        cuerpo.put("equipoObjetivo", nombreDeEquipo(equipoObjetivo));
        cuerpo.put("vidaObjetivo", vidaObjetivo);
        cuerpo.put("blindajeObjetivo", blindajeObjetivo);
        cuerpo.put("danio", danio);
        Map<String, Object> respuesta = consultar(cuerpo);
        try {
            Map<String, Object> resultado = (Map<String, Object>) respuesta.get("resultado");
            return new ResultadoDanioA3(entero(resultado, "absorbidoPorBlindaje"), entero(resultado, "danioALaVida"),
                    entero(resultado, "vidaFinal"), entero(resultado, "blindajeFinal"),
                    Boolean.TRUE.equals(resultado.get("eliminado")), respuesta);
        } catch (RuntimeException e) {
            throw new CompaneroNoDisponibleException(getRol(), getNombre(),
                    "respondió 200 pero sin el \"resultado\" que publica su OpenAPI");
        }
    }

    private static String nombreDeEquipo(String equipo) {
        return "TT".equals(equipo) ? "TERRORISTAS" : "ANTITERRORISTAS";
    }

    private static int entero(Map<String, Object> mapa, String campo) {
        return ((Number) mapa.get(campo)).intValue();
    }

    @Override
    protected RechazoDeCompaneroException rechazo(int status, Object respuesta) {
        return new RechazoDeA3Exception(getNombre(), status, respuesta);
    }
}
