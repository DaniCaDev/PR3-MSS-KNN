package knn.model.classification.metrics;

/**
 * Implementación de la distancia de Chebyshev entre dos instancias.
 * La distancia de Chebyshev es el máximo de los valores absolutos de
 * las diferencias entre los valores de los atributos.
 *
 * @author Daniel Alejandro Álvarez Casablanca
 * @version 1.0
 */
public class ChebyshevDistance implements DistanceMetric {

    /**
     * Calcula la distancia de Chebyshev entre dos instancias.
     *
     * @param a Primera instancia (valores)
     * @param b Segunda instancia (valores)
     * @param classIndices Índices de los atributos de clase (excluidos del cálculo)
     * @param weights Pesos para cada atributo
     * @return Valor de distancia de Chebyshev
     */
    @Override
    public double calculate(double[] a, double[] b, int[] classIndices, double[] weights) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Los vectores deben tener la misma dimensión");
        }

        double maxDiff = 0.0;

        for (int i = 0; i < a.length; i++) {
            // Ignorar atributos de clase
            if (!isClassAttribute(i, classIndices)) {
                // Aplicar peso del atributo
                double weightedDiff = weights[i] * Math.abs(a[i] - b[i]);
                if (weightedDiff > maxDiff) {
                    maxDiff = weightedDiff;
                }
            }
        }

        return maxDiff;
    }

    /**
     * Obtiene el nombre descriptivo de la métrica.
     *
     * @return Nombre de la métrica
     */
    @Override
    public String getName() {
        return "Distancia de Chebyshev";
    }

    /**
     * Verifica si un atributo es de clase.
     *
     * @param attributeIndex Índice del atributo
     * @param classIndices Índices de atributos de clase
     * @return true si es atributo de clase
     */
    private boolean isClassAttribute(int attributeIndex, int[] classIndices) {
        for (int classIdx : classIndices) {
            if (classIdx == attributeIndex) {
                return true;
            }
        }
        return false;
    }
}