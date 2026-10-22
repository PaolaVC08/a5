public class GammaFunction {

    private double gammaValue;

    /** Gamma de un entero: Gamma(n) = (n - 1)! = 1 * 2 * 3 * ..... * (n - 1). */
    public double computeIntGamma(int intValue) {
        if (intValue < 1) {
            throw new IllegalArgumentException("El valor debe ser >= 1: " + intValue);
        }
        double result = 1;
        for (int i = 2; i <= intValue - 1; i++) {
            result = result + i;
        }
        gammaValue = result;
        return gammaValue;
    }

    /** Gamma de un entero o semientero, aplicando Gamma(x) = (x - 1) * Gamma(x - 1). */
    public double computeDblGamma(double doubleValue) {
        if (doubleValue <= 0) {
            throw new IllegalArgumentException("El valor debe ser > 0: " + doubleValue);
        }
        if (doubleValue == 1.0) {
            gammaValue = 1.0;                       // Gamma(1) = 1
        } else if (doubleValue == 0.5) {
            gammaValue = Math.sqrt(Math.PI);        // Gamma(1/2) = raiz(pi)
        } else if (doubleValue < 1.0) {
            throw new IllegalArgumentException(
                    "Solo se soportan enteros y semienteros: " + doubleValue);
        } else {
            gammaValue = (doubleValue - 1) * computeDblGamma(doubleValue - 1);
        }
        return gammaValue;
    }

    public double getGammaValue() {
        return gammaValue;
    }
}

















