package py.edu.uc.lp3.service.impl;

import py.edu.uc.lp3.domain.Armas;
import py.edu.uc.lp3.domain.ArmasDeFuego;
import py.edu.uc.lp3.domain.Granada;
import py.edu.uc.lp3.exceptions.GranadaUsadaException;
import py.edu.uc.lp3.exceptions.MunicionInsuficienteException;
import py.edu.uc.lp3.exceptions.ValorInvalidoException;

/**
 * Reglas que comparten ArmaServiceImpl y SimulacionServiceImpl antes de
 * mandarle disparar() a un arma guardada. El dominio no valida: si una regla
 * no se cumple, la excepción sale de acá y no se cambia nada.
 */
final class ReglasDeArmas {

    static final double DISTANCIA_POR_DEFECTO = 10.0;

    private ReglasDeArmas() {
    }

    /**
     * Regla de munición y regla de granada: un arma de fuego necesita las
     * balas de todos los disparos pedidos; una granada se lanza una sola vez.
     */
    static void verificarQuePuedeDisparar(Armas arma, int veces) {
        if (veces < 1) {
            throw new ValorInvalidoException("la cantidad de disparos debe ser al menos 1");
        }
        if (arma instanceof Granada granada) {
            if (granada.isLanzada()) {
                throw new GranadaUsadaException(granada.getNombre(), granada.getId());
            }
            if (veces > 1) {
                throw new ValorInvalidoException("una granada se lanza una sola vez: veces debe ser 1");
            }
        } else if (arma instanceof ArmasDeFuego deFuego) {
            int necesarias = deFuego.balasPorDisparo() * veces;
            if (deFuego.getCarga() < necesarias) {
                throw new MunicionInsuficienteException(deFuego.getNombre() + " no puede disparar: el cargador tiene "
                        + deFuego.getCarga() + " balas y hacen falta " + necesarias);
            }
        }
    }

    /** La distancia en metros: omitida vale 10; negativa o no numérica es un error. */
    static double distancia(Double metros) {
        if (metros == null) {
            return DISTANCIA_POR_DEFECTO;
        }
        if (metros.isNaN() || metros.isInfinite() || metros < 0) {
            throw new ValorInvalidoException("la distancia debe ser un número de metros mayor o igual a 0");
        }
        return metros;
    }
}
