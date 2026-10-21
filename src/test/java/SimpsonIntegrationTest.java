import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SimpsonIntegrationTest {

    @Test
    void firstPassOfKitExample() {
        // Kit, Tabla 2: num_seg = 10, x = 1.1, dof = 9 -> 0.3500589
        SimpsonIntegration s = new SimpsonIntegration();
        assertEquals(0.3500589, s.integrate(10, 0.0, 1.1, 9), 1e-6);
        assertEquals(10, s.getIntNumSeg());
        assertEquals(0.11, s.getDblW(), 1e-12);
        assertEquals(0.388035, s.getDblCoeff(), 1e-6);
        assertEquals(-5.0, s.getDblExponent(), 1e-12);
        assertEquals(s.getDblFinalValue(), s.integrate(10, 0.0, 1.1, 9), 1e-15);
    }

    @Test
    void secondPassOfKitExample() {
        // Kit: num_seg = 20 -> 0.35005864
        assertEquals(0.35005864, new SimpsonIntegration().integrate(20, 0.0, 1.1, 9), 1e-8);
    }

    @Test
    void intermediateArraysMatchKitTable2() {
        SimpsonIntegration s = new SimpsonIntegration();
        s.integrate(10, 0.0, 1.1, 9);
        assertEquals(11, s.getDblTotXi().length);
        assertEquals(0.0, s.getDblTotXi()[0], 1e-12);
        assertEquals(1.1, s.getDblTotXi()[10], 1e-12);
        assertEquals(1.00134, s.getDblFirstBaseTerms()[1], 1e-5);
        assertEquals(0.9933, s.getDblPowerTerms()[1], 1e-4);
        assertEquals(0.38803, s.getDblFxi()[0], 1e-5);
        assertEquals(0.38544, s.getDblFxi()[1], 1e-5);
        assertEquals(0.01423, s.getDblFinalTerms()[0], 1e-5);
        assertEquals(0.05653, s.getDblFinalTerms()[1], 1e-5);
        assertEquals(0.00757, s.getDblFinalTerms()[10], 1e-5);
        assertEquals(1.1, s.getDblX(), 1e-12);
        assertEquals(9, s.getIntDOF());
        assertEquals(0.0, s.getDblE(), 0.0);
    }

    @Test
    void multipliersFollowOneFourTwoPattern() {
        SimpsonIntegration s = new SimpsonIntegration();
        s.integrate(6, 0.0, 1.0, 5);
        assertArrayEquals(new int[] {1, 4, 2, 4, 2, 4, 1}, s.getIntMultipliers());
    }

    @Test
    void integralToLargeXApproachesHalf() {
        // La densidad t es simetrica: la integral de 0 a infinito vale 0.5.
        assertEquals(0.5, new SimpsonIntegration().integrate(2000, 0.0, 40.0, 5), 1e-3);
    }

    @Test
    void negativeXGivesNegativeIntegral() {
        SimpsonIntegration s = new SimpsonIntegration();
        assertEquals(-s.integrate(20, 0.0, 1.1, 9), s.integrate(20, 0.0, -1.1, 9), 1e-12);
    }

    @Test
    void nonZeroLowerLimitUsesAPlusIW() {
        SimpsonIntegration s = new SimpsonIntegration();
        s.integrate(4, 1.0, 3.0, 9);
        assertEquals(0.5, s.getDblW(), 1e-12);
        assertEquals(1.0, s.getDblLower(), 1e-12);
        assertArrayEquals(new double[] {1.0, 1.5, 2.0, 2.5, 3.0}, s.getDblTotXi(), 1e-12);
    }

    @Test
    void integralBetweenTwoLimitsIsDifferenceOfIntegralsFromZero() {
        SimpsonIntegration s = new SimpsonIntegration();
        // Misma malla (W = 0.05) en los tres calculos, por eso la resta coincide
        double from1to2 = s.integrate(20, 1.0, 2.0, 9);
        double zeroTo2 = s.integrate(40, 0.0, 2.0, 9);
        double zeroTo1 = s.integrate(20, 0.0, 1.0, 9);
        assertEquals(zeroTo2 - zeroTo1, from1to2, 1e-9);
    }

    @Test
    void symmetricIntervalIsTwiceTheHalf() {
        SimpsonIntegration s = new SimpsonIntegration();
        // Misma malla (W = 0.055) en ambos calculos
        double half = s.integrate(20, 0.0, 1.1, 9);
        assertEquals(2 * half, s.integrate(40, -1.1, 1.1, 9), 1e-9);
    }

    @Test
    void reversedLimitsGiveNegativeArea() {
        SimpsonIntegration s = new SimpsonIntegration();
        assertEquals(-s.integrate(20, 0.0, 1.1, 9), s.integrate(20, 1.1, 0.0, 9), 1e-12);
    }

    @Test
    void xZeroGivesZero() {
        assertEquals(0.0, new SimpsonIntegration().integrate(10, 0.0, 0.0, 9), 1e-15);
    }

    @Test
    void rejectsInvalidInputs() {
        SimpsonIntegration s = new SimpsonIntegration();
        assertThrows(IllegalArgumentException.class, () -> s.integrate(3, 0.0, 1.0, 9));
        assertThrows(IllegalArgumentException.class, () -> s.integrate(0, 0.0, 1.0, 9));
        assertThrows(IllegalArgumentException.class, () -> s.integrate(10, 0.0, 1.0, 0));
    }

    @Test
    void individualStepsWorkIndependently() {
        SimpsonIntegration s = new SimpsonIntegration();
        s.computeW(4, 0.0, 4.0);
        s.computeXi(4);
        assertArrayEquals(new double[] {0, 1, 2, 3, 4}, s.getDblTotXi(), 1e-12);
    }

    @Test
    void procedureShowsTableForSmallNumSeg() {
        SimpsonIntegration s = new SimpsonIntegration();
        s.integrate(10, 0.0, 1.1, 9);
        String text = s.getProcedure();
        assertTrue(text.contains("W = (b - a) / num_seg"));
       // assertTrue(text.contains("W = x / num_seg"));
        assertTrue(text.contains("termino"));
        assertTrue(text.contains("p = suma de los terminos = 0.35005890"));
    }

    @Test
    void procedureOmitsTableForManySegments() {
        SimpsonIntegration s = new SimpsonIntegration();
        s.integrate(SimpsonIntegration.MAX_ROWS_IN_TABLE + 2, 0.0, 1.1, 9);
        assertTrue(s.getProcedure().contains("tabla omitida"));
    }
}
