import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;

/** Escritura de resultados a archivo. */
public class Output {

    /** Escribe outText en outFile (lo crea o lo sobrescribe, y crea la carpeta si no existe). */
    public void writeData(String outFile, String outText) {
        File file = new File(outFile);
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(outText);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir " + outFile, e);
        }
    }
}
