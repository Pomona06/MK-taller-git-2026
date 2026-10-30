package py.edu.uc.lp3.service.dto;

/**
 * Cuerpo JSON para crear un arma. No lleva id: lo genera la base. "tipo"
 * elige la clase hija; los campos que falten toman los valores del
 * constructor simple de ese tipo (AK-47, Glock-18, AWP o HE).
 * El JSON nunca llega directo a la entidad: el servicio llama al constructor,
 * que es el que valida.
 */
public record ArmaRequest(
        String tipo,
        String nombre,
        Long precio,
        String equipo,
        Double peso,
        Integer dano,
        Integer capacidadCargador,
        Integer cargadoresRestantes,
        Double precision,
        Double retroceso,
        Long tiempoRecargaMs,
        Boolean modoRafaga,
        Boolean esArmaInicial,
        Double radioExplosion,
        Double distanciaLanzamiento,
        Boolean aturde,
        Double visibilidadReducida,
        Long cooldownMs
) {}
