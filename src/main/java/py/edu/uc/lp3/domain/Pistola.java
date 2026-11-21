package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import py.edu.uc.lp3.constants.CatalogoArmas;
import py.edu.uc.lp3.constants.ReglasJuego;

@Entity
@DiscriminatorValue("pistola")
public class Pistola extends ArmasDeFuego {

    /** Constructor simple: la Glock-18 con la que empiezan los terroristas. */
    public Pistola() {
        this(CatalogoArmas.GLOCK_18);
    }

    /** Sobrecarga: otra pistola del catálogo, para el único equipo que la compra. */
    public Pistola(CatalogoArmas modelo) {
        this(modelo, modelo.equipoUnico().orElse("CT"));
    }

    /** Constructor completo: es el único que llama a super(...). */
    public Pistola(CatalogoArmas modelo, String equipo) {
        super(modelo, equipo);
    }

    /** La pistola con la que se empieza la partida: Glock-18 (TT) o USP-S (CT). */
    public boolean esInicial() {
        return getModelo() == CatalogoArmas.GLOCK_18 || getModelo() == CatalogoArmas.USP_S;
    }

    /** La Glock-18 en ráfaga gasta tres balas por disparo; el resto, una. */
    @Override
    public int balasPorDisparo() {
        return isModoRafaga() ? ReglasJuego.BALAS_POR_RAFAGA : 1;
    }

    @Override
    public String ranura() {
        return "secundaria";
    }
}
