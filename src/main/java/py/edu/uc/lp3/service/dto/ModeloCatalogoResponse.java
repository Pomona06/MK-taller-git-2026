package py.edu.uc.lp3.service.dto;

import py.edu.uc.lp3.constants.CatalogoArmas;

import java.util.ArrayList;
import java.util.List;

/** Una fila del catálogo de CS2, como la muestra GET /api/catalogo. */
public record ModeloCatalogoResponse(
        String modelo,
        String categoria,
        long precio,
        List<String> equipos,
        int dano,
        int cargador,
        int cargadoresDeReserva,
        boolean automatica,
        boolean tieneRafaga
) {
    public static ModeloCatalogoResponse de(CatalogoArmas m) {
        List<String> equipos = new ArrayList<>();
        if (m.isTerroristas()) {
            equipos.add("TT");
        }
        if (m.isAntiterroristas()) {
            equipos.add("CT");
        }
        return new ModeloCatalogoResponse(m.getNombre(), m.getCategoria().name().toLowerCase(), m.getPrecio(),
                equipos, m.getDano(), m.getCargador(), m.getCargadoresDeReserva(), m.isAutomatica(),
                m.isTieneRafaga());
    }
}
