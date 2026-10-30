package py.edu.uc.lp3.service.dto;

/** Lo que pasó en la simulación, paso por paso. */
public record SimulacionDanoResponse(
        String arma,
        String tipoArma,
        String disparo,
        int danoDelArma,
        int danoRecibido,
        int vidaAntes,
        int vidaDespues,
        int blindajeDespues,
        boolean eliminado
) {}
