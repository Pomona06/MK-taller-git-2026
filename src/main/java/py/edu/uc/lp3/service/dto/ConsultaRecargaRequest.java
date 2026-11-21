package py.edu.uc.lp3.service.dto;

/** ¿Puede esta arma de fuego recargar? carga y cargadoresRestantes omitidos = los de un arma nueva. */
public record ConsultaRecargaRequest(String modelo, Integer carga, Integer cargadoresRestantes) {}
