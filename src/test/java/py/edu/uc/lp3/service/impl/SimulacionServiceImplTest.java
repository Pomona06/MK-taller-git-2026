package py.edu.uc.lp3.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/** La regla de vida: cómo se reparte el daño entre la vida y el blindaje. */
class SimulacionServiceImplTest {

    @Test
    void sinBlindajeTodoElDanoVaALaVida() {
        assertArrayEquals(new int[]{35, 0}, SimulacionServiceImpl.calcularImpacto(35, 1.55, 0, 100));
    }

    @Test
    void conBlindajeLaVidaRecibeSegunLaRelacionDeBlindaje() {
        // AK-47 (relación 1.55): 35 × 0.775 = 27 a la vida; (35 − 27.1) × 0.5 = 3 al chaleco.
        assertArrayEquals(new int[]{27, 3}, SimulacionServiceImpl.calcularImpacto(35, 1.55, 100, 100));
        // HE (relación 1.2): 99 × 0.6 = 59 a la vida; 19 al chaleco.
        assertArrayEquals(new int[]{59, 19}, SimulacionServiceImpl.calcularImpacto(99, 1.2, 97, 73));
    }

    @Test
    void siElChalecoNoAlcanzaElRestoPasaALaVida() {
        assertArrayEquals(new int[]{80, 10}, SimulacionServiceImpl.calcularImpacto(100, 1.0, 10, 100));
    }

    @Test
    void laVidaNuncaBajaDeCero() {
        assertArrayEquals(new int[]{100, 0}, SimulacionServiceImpl.calcularImpacto(114, 1.95, 0, 100));
    }
}
