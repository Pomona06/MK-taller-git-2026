package py.edu.uc.lp3.service.dto;

import py.edu.uc.lp3.domain.Armas;

/** Resultado de disparar o recargar un arma persistida: el texto del dominio y cómo quedó el arma. */
public record DisparoResponse(String resultado, Armas arma) {}
