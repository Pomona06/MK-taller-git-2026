package py.edu.uc.lp3.cliente;

import java.util.Map;

/** Lo que respondió A3 cuando su regla de daño aceptó el impacto, más su respuesta completa. */
public record ResultadoDanioA3(int absorbidoPorBlindaje, int danioALaVida, int vidaFinal, int blindajeFinal,
                               boolean eliminado, Map<String, Object> respuesta) {}
