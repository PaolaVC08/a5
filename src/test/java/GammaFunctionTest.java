import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GammaFunctionTest {

    private final GammaFunction gamma = new GammaFunction();

    @Test
    void intGammaOfOneAndFive() {
        assertEquals(1.0, gamma.computeIntGamma(1), 1e-12);
        assertEquals(1.0, gamma.computeIntGamma(2), 1e-12);
        assertEquals(24.0, gamma.computeIntGamma(5), 1e-12);
        assertEquals(24.0, gamma.getGammaValue(), 1e-12);
    }

    @Test
    void intGammaRejectsZero() {
        assertThrows(IllegalArgumentException.class, () -> gamma.computeIntGamma(0));
    }

    @Test
    void dblGammaHalfIsSqrtPi() {
        assertEquals(Math.sqrt(Math.PI), gamma.computeDblGamma(0.5), 1e-12);
    }

    @Test
    void dblGammaNineHalvesMatchesKit() {
        // Ejemplo del kit: Gamma(9/2) = 11.63173
        assertEquals(11.63173, gamma.computeDblGamma(4.5), 1e-5);
    }

    @Test
    void dblGammaOfWholeNumbersMatchesFactorial() {
        assertEquals(1.0, gamma.computeDblGamma(1.0), 1e-12);
        assertEquals(24.0, gamma.computeDblGamma(5.0), 1e-12);
    }

    @Test
    void dblGammaRejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(0.0));
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(-1.5));
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(2.3));
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(0.3));
    }
}
