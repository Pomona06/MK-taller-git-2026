package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import py.edu.uc.lp3.constants.CatalogoArmas;
import py.edu.uc.lp3.constants.ReglasJuego;

@Entity
@DiscriminatorValue("rifle")
public class Rifle extends ArmasDeFuego {

    /**
     * Constructor simple: un AK-47 de los terroristas. Es también el
     * constructor sin argumentos que necesita JPA: deja un objeto completo y
     * después JPA le carga los valores de la fila.
     */
    public Rifle() {
        this(CatalogoArmas.AK_47);
    }

    /** Sobrecarga: otro rifle del catálogo, para el único equipo que lo compra. */
    public Rifle(CatalogoArmas modelo) {
        this(modelo, modelo.equipoUnico().orElse("CT"));
    }

    /** Constructor completo: es el único que llama a super(...). */
    public Rifle(CatalogoArmas modelo, String equipo) {
        super(modelo, equipo);
    }

    /** La FAMAS en ráfaga gasta tres balas por disparo; el resto, una. */
    @Override
    public int balasPorDisparo() {
        return isModoRafaga() ? ReglasJuego.BALAS_POR_RAFAGA : 1;
    }

    @Override
    public String ranura() {
        return "principal";
    }
}
