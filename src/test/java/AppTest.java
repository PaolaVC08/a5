import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AppTest {

    @Test
    void noArgsRunsAllTable1Cases(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("res.txt");
        assertEquals(0, App.run(new String[] {}, file.toString()));
        String text = Files.readString(file);
        assertTrue(text.contains("0.35005864"));
        assertTrue(text.contains("0.36757341"));
        assertTrue(text.contains("0.49499986"));
        assertFalse(text.contains("FALLO"));
    }

    @Test
    void lowerUpperDofComputeIntegral(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("res.txt");
        assertEquals(0, App.run(new String[] {"0", "1.1", "9"}, file.toString()));
        String text = Files.readString(file);
        assertTrue(text.contains("RESULTADO: p = 0.35005864"));
        assertTrue(text.contains("num_seg final = 20"));
        assertTrue(text.contains("limite inferior (a) = 0.0"));
    }

    @Test
    void nonZeroLowerLimitWorks(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("res.txt");
        assertEquals(0, App.run(new String[] {"1", "2", "9"}, file.toString()));
        assertTrue(Files.readString(file).contains("RESULTADO"));
    }

    @Test
    void wrongNumberOfArgsReturnsError(@TempDir Path dir) {
        String out = dir.resolve("r.txt").toString();
        assertEquals(1, App.run(new String[] {"1.1", "9"}, out));
        assertEquals(1, App.run(new String[] {"0", "1.1", "9", "0.00001"}, out));
    }

    @Test
    void nonNumericArgsReturnError(@TempDir Path dir) {
        String out = dir.resolve("r.txt").toString();
        assertEquals(1, App.run(new String[] {"abc", "1.1", "9"}, out));
        assertEquals(1, App.run(new String[] {"0", "1.1", "9.5"}, out));
    }

    @Test
    void invalidDofReturnsError(@TempDir Path dir) {
        assertEquals(1, App.run(new String[] {"0", "1.1", "0"}, dir.resolve("r.txt").toString()));
    }

    @Test
    void mainWithValidArgsWritesDefaultFile() {
        App.main(new String[] {"0", "1.1", "9"});
        assertTrue(Files.exists(Path.of(App.DEFAULT_OUT_FILE)));
    }
}
