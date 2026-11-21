package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.MappedSuperclass;
import py.edu.uc.lp3.interfaces.Avatar;
import py.edu.uc.lp3.interfaces.Cotizable;
import py.edu.uc.lp3.interfaces.Posicion;
import py.edu.uc.lp3.interfaces.VideoJuegoPosicionable;

/**
 * Los campos son private y los setters protected: fuera del paquete domain
 * solo las clases hijas pueden cambiar el precio; un controller o un
 * servicio no. En las armas y el chaleco el precio sale del catálogo del
 * juego.
 *
 * Es @MappedSuperclass: no tiene tabla propia. {@code precio} y
 * {@code descripcion} se guardan como columnas de la tabla de cada entidad
 * que la extiende (arma y chaleco).
 */
@MappedSuperclass
public class Vendible  implements VideoJuegoPosicionable, Cotizable {
    private Long precio;
    private String descripcion;

    public Long getPrecio() {
        return precio;
    }

    protected void setPrecio(Long precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    protected void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    @JsonIgnore
    public Posicion getUbicacion() {
        return null;
    }

    @Override
    @JsonIgnore
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
