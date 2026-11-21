package py.edu.uc.lp3.service.dto;

/**
 * Lo que pasó en la simulación, paso por paso. El arma y el jugador ya
 * quedaron guardados así. cargaDespues es null si el arma es una granada.
 */
public record SimulacionDanoResponse(
        Long armaId,
        String arma,
        String tipoArma,
        String disparo,
        Integer cargaDespues,
        int danoSinBlindaje,
        Long jugadorId,
        String jugador,
        int vidaAntes,
        int vidaPerdida,
        int blindajePerdido,
        int vidaDespues,
        int blindajeDespues,
        boolean eliminado
) {}
