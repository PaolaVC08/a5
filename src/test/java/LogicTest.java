import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LogicTest {

    private static final double E = 0.00001;

    private static double run(double x, int dof) {
        Logic logic = new Logic(10, E, dof, 0.0, x);
        logic.logic1a();
        return logic.getDblResult();
    }

    // ---- Tabla 1 del kit (5 decimales) ----

    @Test
    void test1() {
        assertEquals(0.35006, run(1.1, 9), 0.000005 + 1e-12);
    }

    @Test
    void test2() {
        assertEquals(0.36757, run(1.1812, 10), 0.000005 + 1e-12);
    }

    @Test
    void test3() {
        assertEquals(0.49500, run(2.750, 30), 0.000005 + 1e-12);
    }

    @Test
    void kitExampleStopsAtTwentySegments() {
        Logic logic = new Logic(10, E, 9, 0.0, 1.1);
        logic.logic1a();
        assertEquals(20, logic.getIntFinalNumSeg());
        assertEquals(0.35005864, logic.getDblResult(), 1e-8);
    }

    @Test
    void procedureShowsIterationsAndComparison() {
        Logic logic = new Logic(10, E, 9, 0.0, 1.1);
        logic.logic1a();
        String text = logic.getProcedure();
        assertTrue(text.contains("Iteracion 1: num_seg = 10"));
        assertTrue(text.contains("Iteracion 2: num_seg = 20"));
        assertTrue(text.contains("terminamos"));
    }

    @Test
    void procedureShowsDoublingWhenErrorIsTight() {
        Logic logic = new Logic(2, 1e-9, 9, 0.0, 1.1);
        logic.logic1a();
        assertTrue(logic.getProcedure().contains("duplicamos num_seg"));
    }

    @Test
    void integralBetweenTwoLimits() {
        Logic logic = new Logic(10, E, 9, 1.0, 2.0);
        logic.logic1a();
        Logic to2 = new Logic(10, E, 9, 0.0, 2.0);
        to2.logic1a();
        Logic to1 = new Logic(10, E, 9, 0.0, 1.0);
        to1.logic1a();
        assertEquals(to2.getDblResult() - to1.getDblResult(), logic.getDblResult(), 2 * E);
    }

    @Test
    void reportLineContainsData() {
        Logic logic = new Logic(10, E, 9, 0.0, 1.1);
        logic.logic1a();
        String line = logic.getReportLine();
        assertTrue(line.contains("a = 0.0, b = 1.1"));
        assertTrue(line.contains("dof = 9"));
        assertTrue(line.contains("0.35005864"));
    }

    @Test
    void saveReportWritesFile(@TempDir Path dir) throws Exception {
        Logic logic = new Logic(10, E, 9, 0.0, 1.1);
        Path file = dir.resolve("sub").resolve("out.txt");
        logic.saveReport(file.toString(), "hola");
        assertEquals("hola", Files.readString(file));
    }

    @Test
    void failsToConvergeWhenErrorIsImpossible() {
        Logic logic = new Logic(10, 1e-300, 9, 0.0, 1.1);
        assertThrows(IllegalStateException.class, logic::logic1a);
    }

    @Test
    void rejectsInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> new Logic(9, E, 9, 0.0, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new Logic(0, E, 9, 0.0, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new Logic(10, 0.0, 9, 0.0, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new Logic(10, E, 0, 0.0, 1.0));
    }
}
