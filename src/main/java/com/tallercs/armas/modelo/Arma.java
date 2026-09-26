package com.tallercs.armas.modelo;

/*
 Clase base de toda la jerarquía de armas de CS2.

 Concentra lo que es comun a CUALQUIER arma (nombre, precio, equipo, peso,
 daño base) y el contrato uniforme que el inventario necesita para tratar
 cualquier instancia sin preguntar de qué tipo concreto es:
   - disparar()      comportamiento propio de cada rama (fuego vs granada)
   - mostrarEnTienda() igual para todas, por eso no es abstracto acá
   - comprar()  regla de negocio unica, por eso es final

 Todos los campos son privados: nadie fuera de la jerarquía puede dejar
 un arma en un estado imposible (p. ej. un precio negativo puesto desde afuera).
*/
public abstract class Arma {

    private final String nombre;
    private final double precio;
    private final String equipo; // "CT" o "TT"
    private final double peso;
    private final int dano; // daño base; cada rama decide cómo usarlo

    protected Arma(String nombre, double precio, String equipo, double peso, int dano) {
        if (precio < 0 || peso < 0 || dano < 0) {
            throw new IllegalArgumentException("precio, peso y daño no pueden ser negativos");
        }
        this.nombre = nombre;
        this.precio = precio;
        this.equipo = equipo;
        this.peso = peso;
        this.dano = dano;
    }

    /**
     * Regla de compra única para toda la jerarquía. final: ninguna subclase
     * puede alterar cómo se valida o registra una compra.
     */
    public final String comprar(double dineroDisponible) {
        if (dineroDisponible < precio) {
            return "No alcanza el dinero para comprar " + nombre + " (cuesta " + precio + ")";
        }
        return nombre + " comprada por " + precio + " (equipo " + equipo + ")";
    }

    /**
     * Contrato uniforme: el inventario llama esto sobre CUALQUIER Arma
     * (ArmaFuego o Granada) sin saber el tipo concreto.
     */
    public abstract String disparar();

    public abstract String inspeccionar();

    /**
     * Implementación única porque alcanza con los atributos comunes;
     * las subclases pueden sobreescribirla si quieren agregar detalle,
     * pero no están obligadas a hacerlo.
     */
    public String mostrarEnTienda() {
        return String.format("%s | %.2f$ | equipo %s | %.2fkg", nombre, precio, equipo, peso);
    }

    protected int getDano() {
        return dano;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public String getEquipo() {
        return equipo;
    }

    public double getPeso() {
        return peso;
    }
}
