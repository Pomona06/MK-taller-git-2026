package py.edu.uc.lp3.domain;

import py.edu.uc.lp3.interfaces.Avatar;
import py.edu.uc.lp3.interfaces.Cotizable;
import py.edu.uc.lp3.interfaces.Posicion;
import py.edu.uc.lp3.interfaces.VideoJuegoPosicionable;

/**
 * Los campos pasan a private y los setters a protected: antes cualquier
 * clase (por ejemplo un controller) podía hacer arma.setPrecio(-500L) y
 * dejar el objeto en un estado imposible. Ahora solo la jerarquía puede
 * fijar el precio, y siempre pasa por la validación.
 */
public class Vendible  implements VideoJuegoPosicionable, Cotizable {
    private Long precio;
    private String descripcion;

    public Long getPrecio() {
        return precio;
    }

    protected void setPrecio(Long precio) {
        if (precio == null || precio < 0) {
            throw new IllegalArgumentException("el precio no puede ser nulo ni negativo");
        }
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    protected void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public Posicion getUbicacion() {
        return null;
    }

    @Override
    public Avatar getAvatar() {
        return null;
    }

    @Override
    public Long getPrecio(String identificador) {
        return 0l;
    }

    @Override
    public Double getPrecioUSD(String identificador) {
        return 0.0;
    }
}
