import java.util.Locale;

/**
 * Regla de Simpson aplicada a la distribucion t, del limite inferior (a) al superior (b).
 *
 *   p = (W/3) * [ F(a) + 4F(a+1W) + 2F(a+2W) + 4F(a+3W) + ... + 4F(b-W) + F(b) ]
 *
 * Los pasos van en el mismo orden que las columnas del Excel (hoja t-student):
 *
 *   1. W       = (b - a) / num_seg
 *   2. xi      = a + i * W                          (i = 0 .. num_seg)
 *   3. p1      = 1 + xi^2 / dof                     (primera base)
 *   4. p2      = p1 ^ exponente                     (exponente = -(dof+1)/2)
 *   5. cons    = Gamma((dof+1)/2) / ( raiz(dof*pi) * Gamma(dof/2) )
 *   6. F(xi)   = p2 * cons
 *   7. mult    = 1, 4, 2, 4, ..., 4, 1
 *   8. termino = (W/3) * mult * F(xi)
 *   9. p       = suma de todos los terminos
 */
public class SimpsonIntegration {

    /** Si hay mas segmentos que esto, el procedimiento no imprime la tabla completa. */
    static final int MAX_ROWS_IN_TABLE = 40;

    private int intNumSeg;
    private double dblW;
    private double dblE;
    private int intDOF;
    private double dblLower;              // limite inferior (a)
    private double dblX;                  // limite superior (b)
    private double[] dblTotXi;
    private double[] dblFirstBaseTerms;   // p1
    private double dblExponent;
    private double dblCoeff;              // cons
    private double[] dblFxi;
    private double[] dblFinalTerms;
    private double dblFinalValue;

    // Datos extra para poder mostrar el procedimiento
    private double[] dblPowerTerms;       // p2
    private int[] intMultipliers;
    private double dblGammaTop;           // Gamma((dof+1)/2)
    private double dblGammaBottom;        // Gamma(dof/2)

    private final GammaFunction gamma = new GammaFunction();

    /** Ejecuta los pasos 1 a 9 en orden para un numero de segmentos dado. */
    public double integrate(int numSeg, double lower, double upper, int dof) {
        if (numSeg < 2 || numSeg % 2 != 0) {
            throw new IllegalArgumentException("numSeg debe ser par y >= 2: " + numSeg);
        }
        if (dof < 1) {
            throw new IllegalArgumentException("dof debe ser >= 1: " + dof);
        }
        intNumSeg = numSeg;
        intDOF = dof;

        computeW(numSeg, lower, upper);                                // paso 1
        computeXi(numSeg);                                     // paso 2
        computeFirstBaseTerms(numSeg, dblTotXi, dof);          // paso 3
        computeExponent(dof);                                  // (para el paso 4)
        computeCoefficient(dof);                               // paso 5
        computeFxi(numSeg);                                    // pasos 4 y 6
        computeFinalTerms(numSeg);                             // pasos 7 y 8
        computeFinalValue(numSeg);                             // paso 9
        return dblFinalValue;
    }

    /** Paso 1: W = (b - a) / num_seg. */
    public void computeW(int numSeg, double lower, double upper) {
        dblLower = lower;
        dblX = upper;
        dblW = (upper - lower) / numSeg;
    }

    /** Paso 2: xi = a + i * W, para i = 0, 1, ..., num_seg. */
    public void computeXi(int numSeg) {
        dblTotXi = new double[numSeg + 1];
        for (int i = 0; i <= numSeg; i++) {
            dblTotXi[i] = dblLower + i * dblW;
        }
    }

    /** Paso 3: p1 = 1 + xi^2 / dof. */
    public void computeFirstBaseTerms(int numSeg, double[] totXi, int dof) {
        dblFirstBaseTerms = new double[numSeg + 1];
        for (int i = 0; i <= numSeg; i++) {
            dblFirstBaseTerms[i] = 1 + (totXi[i] * totXi[i]) / dof;
        }
    }

    /** Exponente = -(dof + 1) / 2. */
    public void computeExponent(int dof) {
        dblExponent = -(dof + 1) / 2.0;
    }

    /** Paso 5: cons = Gamma((dof+1)/2) / ( raiz(dof*pi) * Gamma(dof/2) ). */
    public void computeCoefficient(int dof) {
        dblGammaTop = gamma.computeDblGamma((dof + 1) / 2.0);
        dblGammaBottom = gamma.computeDblGamma(dof / 2.0);
        double sqrtPart = Math.sqrt(dof * Math.PI);
        dblCoeff = dblGammaTop / (sqrtPart * dblGammaBottom);
    }

    /** Pasos 4 y 6: p2 = p1 ^ exponente, y F(xi) = p2 * cons. */
    public void computeFxi(int numSeg) {
        dblPowerTerms = new double[numSeg + 1];
        dblFxi = new double[numSeg + 1];
        for (int i = 0; i <= numSeg; i++) {
            dblPowerTerms[i] = Math.pow(dblFirstBaseTerms[i], dblExponent);
            dblFxi[i] = dblPowerTerms[i] * dblCoeff;
        }
    }

    /**
     * Pasos 7 y 8: multiplicador (1 en los extremos, 4 en los impares, 2 en los pares)
     * y termino = (W/3) * multiplicador * F(xi).
     */
    public void computeFinalTerms(int numSeg) {
        intMultipliers = new int[numSeg + 1];
        dblFinalTerms = new double[numSeg + 1];
        for (int i = 0; i <= numSeg; i++) {
            if (i == 0 || i == numSeg) {
                intMultipliers[i] = 1;
            } else if (i % 2 == 1) {
                intMultipliers[i] = 4;
            } else {
                intMultipliers[i] = 2;
            }
            dblFinalTerms[i] = (dblW / 3) * intMultipliers[i] * dblFxi[i];
        }
    }

    /** Paso 9: p = suma de todos los terminos. */
    public void computeFinalValue(int numSeg) {
        dblFinalValue = 0;
        for (int i = 0; i <= numSeg; i++) {
            dblFinalValue = dblFinalValue + dblFinalTerms[i];
        }
    }

    /** Texto con el procedimiento de la ultima integracion, como la hoja del Excel. */
    public String getProcedure() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US, "W = (b - a) / num_seg = (%s - %s) / %d = %.6f%n",
                dblX, dblLower, intNumSeg, dblW));
        sb.append(String.format(Locale.US, "W/3 = %.6f%n", dblW / 3));
        sb.append(String.format(Locale.US, "exponente = -(dof+1)/2 = -(%d+1)/2 = %s%n",
                intDOF, dblExponent));
        sb.append(String.format(Locale.US,
                "cons = Gamma((dof+1)/2) / ( raiz(dof*pi) * Gamma(dof/2) )%n"
                + "     = %.6f / ( %.6f * %.6f ) = %.6f%n",
                dblGammaTop, Math.sqrt(intDOF * Math.PI), dblGammaBottom, dblCoeff));

        if (intNumSeg > MAX_ROWS_IN_TABLE) {
            sb.append("(tabla omitida: hay mas de " + MAX_ROWS_IN_TABLE + " segmentos)\n");
        } else {
            sb.append(String.format("%3s %9s %10s %10s %10s %5s %10s%n",
                    "i", "xi", "p1", "p2", "F(xi)", "mult", "termino"));
            for (int i = 0; i <= intNumSeg; i++) {
                sb.append(String.format(Locale.US, "%3d %9.5f %10.6f %10.6f %10.6f %5d %10.6f%n",
                        i, dblTotXi[i], dblFirstBaseTerms[i], dblPowerTerms[i],
                        dblFxi[i], intMultipliers[i], dblFinalTerms[i]));
            }
        }
        sb.append(String.format(Locale.US, "p = suma de los terminos = %.8f%n", dblFinalValue));
        return sb.toString();
    }

    public int getIntNumSeg() { return intNumSeg; }
    public double getDblW() { return dblW; }
    public double getDblE() { return dblE; }
    public int getIntDOF() { return intDOF; }
    public double getDblLower() { return dblLower; }
    public double getDblX() { return dblX; }
    public double[] getDblTotXi() { return dblTotXi; }
    public double[] getDblFirstBaseTerms() { return dblFirstBaseTerms; }
    public double[] getDblPowerTerms() { return dblPowerTerms; }
    public double getDblExponent() { return dblExponent; }
    public double getDblCoeff() { return dblCoeff; }
    public double[] getDblFxi() { return dblFxi; }
    public int[] getIntMultipliers() { return intMultipliers; }
    public double[] getDblFinalTerms() { return dblFinalTerms; }
    public double getDblFinalValue() { return dblFinalValue; }
}
