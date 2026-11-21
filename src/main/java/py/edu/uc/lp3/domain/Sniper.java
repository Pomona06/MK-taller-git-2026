package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import py.edu.uc.lp3.constants.CatalogoArmas;

@Entity
@DiscriminatorValue("sniper")
public class Sniper extends ArmasDeFuego {

    private boolean conMira;

    /** Constructor simple: el AWP, que lo compran los dos equipos; acá, del equipo CT. */
    public Sniper() {
        this(CatalogoArmas.AWP);
    }

    /** Sobrecarga: otro francotirador del catálogo, del equipo CT. */
    public Sniper(CatalogoArmas modelo) {
        this(modelo, modelo.equipoUnico().orElse("CT"));
    }

    /** Constructor completo: es el único que llama a super(...). */
    public Sniper(CatalogoArmas modelo, String equipo) {
        super(modelo, equipo);
        this.conMira = false;
    }

    /**
     * Apuntar con la mira. En CS2 cambia la precisión, no el daño; el modelo
     * no simula la precisión, así que solo se guarda el estado.
     */
    public void apuntarConMira() {
        this.conMira = true;
    }

    public boolean isConMira() {
        return conMira;
    }

    @Override
    public int balasPorDisparo() {
        return 1;
    }

    /** El AWP y el SSG 08 bajan la mira después de cada disparo (m_bUnzoomsAfterShot). */
    @Override
    protected void despuesDeDisparar() {
        this.conMira = false;
    }

    @Override
    public String ranura() {
        return "principal";
    }
}
