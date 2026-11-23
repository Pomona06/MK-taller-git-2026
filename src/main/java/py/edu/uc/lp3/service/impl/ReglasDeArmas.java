package py.edu.uc.lp3.service.impl;

import py.edu.uc.lp3.constants.CatalogoArmas;
import py.edu.uc.lp3.constants.Categoria;
import py.edu.uc.lp3.domain.Armas;
import py.edu.uc.lp3.domain.ArmasDeFuego;
import py.edu.uc.lp3.domain.Granada;
import py.edu.uc.lp3.domain.Pistola;
import py.edu.uc.lp3.domain.Rifle;
import py.edu.uc.lp3.domain.Sniper;
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
            // long: con un int, 3 balas × 715 millones de ráfagas se desborda y da un número negativo.
            long necesarias = (long) deFuego.balasPorDisparo() * veces;
            if (deFuego.getCarga() < necesarias) {
                throw new MunicionInsuficienteException(deFuego.getNombre() + " no puede disparar: el cargador tiene "
                        + deFuego.getCarga() + " balas y hacen falta " + necesarias);
            }
        }
    }

    /**
     * Regla de alcance: la bala de un arma de fuego no llega más lejos que el
     * alcance de su modelo (m_flRange: 208 m los rifles y francotiradores,
     * 104 m las pistolas). Las granadas no tiran balas: no se controla.
     */
    static void verificarAlcance(CatalogoArmas modelo, double metros) {
        if (modelo.getCategoria() != Categoria.GRANADA && metros > modelo.getAlcanceMetros()) {
            throw new ValorInvalidoException("en CS2 la bala de " + modelo.getNombre() + " llega hasta "
                    + (int) modelo.getAlcanceMetros() + " m: a " + enMetros(metros) + " m no le pega a nadie");
        }
    }

    /** 300.0 se escribe 300; 40.5 queda 40.5. */
    private static String enMetros(double metros) {
        return metros == Math.rint(metros) ? String.valueOf((long) metros) : String.valueOf(metros);
    }

    /** Fábrica: el modelo del catálogo decide la clase hija. Quien llama ya validó modelo y equipo. */
    static Armas construir(CatalogoArmas modelo, String equipo) {
        return switch (modelo.getCategoria()) {
            case RIFLE -> new Rifle(modelo, equipo);
            case PISTOLA -> new Pistola(modelo, equipo);
            case SNIPER -> new Sniper(modelo, equipo);
            case GRANADA -> new Granada(modelo, equipo);
        };
    }

    /** El equipo de un arma o de un jugador: CT o TT, sin importar mayúsculas ni espacios. */
    static String equipo(String texto) {
        String equipo = texto == null ? "" : texto.strip().toUpperCase();
        if (!"CT".equals(equipo) && !"TT".equals(equipo)) {
            throw new ValorInvalidoException("el equipo debe ser CT o TT (recibido: " + texto + ")");
        }
        return equipo;
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
