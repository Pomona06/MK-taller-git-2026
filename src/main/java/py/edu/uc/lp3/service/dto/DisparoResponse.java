package py.edu.uc.lp3.service.dto;

import py.edu.uc.lp3.domain.Armas;

import java.util.List;

/** Resultado de disparar o recargar un arma guardada: qué pasó en cada disparo y cómo quedó el arma. */
public record DisparoResponse(List<String> resultados, Armas arma) {}
