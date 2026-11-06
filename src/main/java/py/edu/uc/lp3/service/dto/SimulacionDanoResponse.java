package py.edu.uc.lp3.service.dto;

/** Lo que pasó en la simulación, paso por paso. Arma y jugador ya quedaron guardados así. */
public record SimulacionDanoResponse(
        Long armaId,
        String arma,
        String tipoArma,
        String disparo,
        int cargaDespues,
        int danoDelArma,
        Long jugadorId,
        String jugador,
        int danoRecibido,
        int vidaAntes,
        int vidaDespues,
        int blindajeDespues,
        boolean eliminado
) {}
