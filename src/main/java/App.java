import java.util.Locale;

/**
 * Programa 5 (PSP2): integral de la distribucion t con la regla de Simpson.
 *
 * Uso:  java App                         -> corre los 3 casos de la Tabla 1 del kit
 *       java App inferior superior dof   -> calcula la integral de inferior a superior
 *
 *   inferior   limite inferior de la integral (usar punto decimal)
 *   superior   limite superior de la integral (usar punto decimal)
 *   dof        grados de libertad (entero >= 1)
 *
 * El error aceptable (E) y los segmentos iniciales van fijos en las constantes de abajo.
 */
public class App {

    /** Segmentos con los que empieza (debe ser par). */
    static final int INITIAL_SEGMENTS = 10;
    /** Error aceptable E. */
    static final double ERROR = 0.00001;
    static final String DEFAULT_OUT_FILE = "resultados.txt";

    static final String USAGE =
            "Uso: java App [inferior superior dof]%n"
            + "  inferior   limite inferior de la integral (ej. 0)%n"
            + "  superior   limite superior de la integral (ej. 1.1)%n"
            + "  dof        grados de libertad, entero >= 1 (ej. 9)%n"
            + "Sin parametros se ejecutan los 3 casos de la Tabla 1.%n";

    /** Casos de prueba de la Tabla 1: {x, dof, valor esperado}. El limite inferior es 0. */
    static final double[][] TESTS = {
        {1.1, 9, 0.35006},
        {1.1812, 10, 0.36757},
        {2.750, 30, 0.49500},
    };

    public static void main(String[] args) {
        int status = run(args, DEFAULT_OUT_FILE);
        if (status != 0) {
            System.exit(status);
        }
    }

    /** Ejecuta el programa y devuelve 0 si todo salio bien, 1 si hubo error de uso. */
    static int run(String[] args, String outFile) {
        try {
            if (args.length == 0) {
                runTable1(outFile);
            } else if (args.length == 3) {
                double lower = Double.parseDouble(args[0]);
                double upper = Double.parseDouble(args[1]);
                int dof = Integer.parseInt(args[2]);
                runSingle(lower, upper, dof, outFile);
            } else {
                System.err.printf(USAGE);
                return 1;
            }
            return 0;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.err.println("Error: " + ex.getMessage());
            System.err.printf(USAGE);
            return 1;
        }
    }

    /** Un solo calculo, mostrando todo el procedimiento. */
    private static void runSingle(double lower, double upper, int dof, String outFile) {
        Logic logic = new Logic(INITIAL_SEGMENTS, ERROR, dof, lower, upper);
        logic.logic1a();
        String text = logic.getProcedure()
                + String.format(Locale.US, "%nRESULTADO: p = %.8f (num_seg final = %d)%n",
                        logic.getDblResult(), logic.getIntFinalNumSeg());
        System.out.print(text);
        logic.saveReport(outFile, text);
        System.out.println("Resultados guardados en " + outFile);
    }

    /** Los 3 casos de la Tabla 1 con esperado, actual y estado. */
    private static void runTable1(String outFile) {
        StringBuilder text = new StringBuilder();
        text.append(String.format("%-10s %-5s %-15s %-15s %-8s%n",
                "0 a x", "dof", "Esperado", "Actual", "Estado"));
        Logic last = null;
        for (double[] t : TESTS) {
            Logic logic = new Logic(INITIAL_SEGMENTS, ERROR, (int) t[1], 0.0, t[0]);
            logic.logic1a();
            last = logic;
            double actual = logic.getDblResult();
            boolean ok = Math.abs(actual - t[2]) < 0.000005 + 1e-12;
            text.append(String.format(Locale.US, "%-10.4f %-5d %-15.5f %-15.8f %-8s%n",
                    t[0], (int) t[1], t[2], actual, ok ? "OK" : "FALLO"));
        }
        System.out.print(text);
        last.saveReport(outFile, text.toString());
        System.out.println("Resultados guardados en " + outFile);
    }
}
