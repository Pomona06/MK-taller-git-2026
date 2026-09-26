package com.example.restservice;

/**
 * El profesor dejó esta clase vacía (public class Armas extends Vendible {}).
 * Acá se completa con lo que es común a cualquier arma de la jerarquía, y con
 * el contrato uniforme que pide el taller: disparar() e inspeccionar() son
 * abstractos para que cualquier hija (arma de fuego o granada) responda al
 * mismo mensaje sin que nadie pregunte de qué tipo concreto se trata.
 *
 * precio y descripcion ya vienen de Vendible, así que no se repiten acá.
 */
public abstract class Armas extends Vendible {

    private final String nombre;
    private final String equipo; // "CT" o "TT"
    private final double peso;
    private final int dano; // daño base; cada rama decide cómo usarlo

    protected Armas(String nombre, long precio, String equipo, double peso, int dano) {
        if (precio < 0 || peso < 0 || dano < 0) {
            throw new IllegalArgumentException("precio, peso y daño no pueden ser negativos");
        }
        this.nombre = nombre;
        this.equipo = equipo;
        this.peso = peso;
        this.dano = dano;
        setPrecio(precio);
    }

    /**
     * Regla de compra única para toda la jerarquía. final: ninguna subclase
     * puede alterar cómo se valida o registra una compra.
     */
    public final String comprar(double dineroDisponible) {
        if (dineroDisponible < getPrecio()) {
            return "No alcanza el dinero para comprar " + nombre + ", cuesta " + getPrecio();
        }
        return nombre + " comprada por " + getPrecio() + ", equipo " + equipo;
    }

    /**
     * Contrato uniforme: el inventario llama esto sobre CUALQUIER Armas
     * (de fuego o granada) sin saber el tipo concreto.
     */
    public abstract String disparar();

    public abstract String inspeccionar();

    /**
     * Implementación única porque alcanza con los atributos comunes;
     * las subclases pueden sobreescribirla si quieren agregar detalle,
     * pero no están obligadas a hacerlo.
     */
    public String mostrarEnTienda() {
        return String.format("%s | %d$ | equipo %s | %.2fkg", nombre, getPrecio(), equipo, peso);
    }

    protected int getDano() {
        return dano;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEquipo() {
        return equipo;
    }

    public double getPeso() {
        return peso;
    }
}
