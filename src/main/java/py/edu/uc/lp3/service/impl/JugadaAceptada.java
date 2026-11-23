package py.edu.uc.lp3.service.impl;

import py.edu.uc.lp3.cliente.ResultadoDanioA3;

import java.util.Map;

/** Lo que el escenario ya consultó y los dos compañeros aceptaron, listo para guardar. */
record JugadaAceptada(Long armaId, Long objetivoId, String tirador, double distancia, boolean conMira,
                      double estabilidad, int danio, Map<String, Object> respuestaA2, ResultadoDanioA3 resultadoA3) {}
