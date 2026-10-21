import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class OutputTest {

    @Test
    void writesAndOverwrites(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("a.txt");
        Output out = new Output();
        out.writeData(file.toString(), "uno");
        out.writeData(file.toString(), "dos");
        assertEquals("dos", Files.readString(file));
    }

    @Test
    void failsWhenPathIsADirectory(@TempDir Path dir) {
        assertThrows(UncheckedIOException.class,
                () -> new Output().writeData(dir.toString(), "x"));
    }
}
