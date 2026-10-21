import java.util.Locale;

/**
 * Repite la regla de Simpson duplicando el numero de segmentos hasta que la diferencia
 * entre dos resultados consecutivos sea menor que el error aceptable E
 * (en el Excel: iter 1, iter 2 y la celda Error).
 */
public class Logic {

    /** Limite de seguridad para no agotar memoria si no converge. */
    static final int MAX_SEGMENTS = 1 << 20;

    private int intNumSeg;
    private double dblE;
    private int intDOF;
    private double dblLower;
    private double dblX;

    private double dblResult;
    private int intFinalNumSeg;
    private String procedure = "";

    private final SimpsonIntegration simpson = new SimpsonIntegration();
    private final Output output = new Output();

    public Logic(int intNumSeg, double dblE, int intDOF, double dblLower, double dblX) {
        if (intNumSeg < 2 || intNumSeg % 2 != 0) {
            throw new IllegalArgumentException("num_seg debe ser par y >= 2: " + intNumSeg);
        }
        if (dblE <= 0) {
            throw new IllegalArgumentException("E debe ser > 0: " + dblE);
        }
        if (intDOF < 1) {
            throw new IllegalArgumentException("dof debe ser >= 1: " + intDOF);
        }
        this.intNumSeg = intNumSeg;
        this.dblE = dblE;
        this.intDOF = intDOF;
        this.dblLower = dblLower;
        this.dblX = dblX;
    }

    /** Ejecuta el algoritmo completo y deja el resultado en getDblResult(). */
    public void logic1a() {
        StringBuilder text = new StringBuilder();
        text.append(String.format(Locale.US,
                "DATOS: limite inferior (a) = %s, limite superior (b) = %s, dof = %d, "
                + "E = %s, num_seg inicial = %d%n%n",
                dblLower, dblX, intDOF, dblE, intNumSeg));

        int numSeg = intNumSeg;
        int iteration = 1;
        double previous = simpson.integrate(numSeg, dblLower, dblX, intDOF);
        text.append(String.format("=== Iteracion %d: num_seg = %d ===%n", iteration, numSeg));
        text.append(simpson.getProcedure()).append(System.lineSeparator());

        boolean finished = false;
        double current = previous;
        while (!finished) {
            numSeg = numSeg * 2;
            if (numSeg > MAX_SEGMENTS) {
                throw new IllegalStateException("No convergio con " + MAX_SEGMENTS + " segmentos");
            }
            iteration++;
            current = simpson.integrate(numSeg, dblLower, dblX, intDOF);
            text.append(String.format("=== Iteracion %d: num_seg = %d ===%n", iteration, numSeg));
            text.append(simpson.getProcedure());

            double difference = Math.abs(current - previous);
            text.append(String.format(Locale.US, "|%.8f - %.8f| = %.8f ", current, previous, difference));
            if (difference < dblE) {
                text.append(String.format(Locale.US, "< E (%s): terminamos.%n", dblE));
                finished = true;
            } else {
                text.append(String.format(Locale.US, ">= E (%s): duplicamos num_seg.%n%n", dblE));
                previous = current;
            }
        }
        dblResult = current;
        intFinalNumSeg = numSeg;
        procedure = text.toString();
    }

    /** Linea de reporte con los datos de entrada y el resultado. */
    public String getReportLine() {
        return String.format(Locale.US,
                "a = %s, b = %s | dof = %d | E = %s | num_seg final = %d | p = %.8f",
                dblLower, dblX, intDOF, dblE, intFinalNumSeg, dblResult);
    }

    public void saveReport(String outFile, String text) {
        output.writeData(outFile, text);
    }

    /** Texto con todo el procedimiento (iteraciones, tablas y comparaciones). */
    public String getProcedure() { return procedure; }
    public double getDblResult() { return dblResult; }
    public int getIntFinalNumSeg() { return intFinalNumSeg; }
}
