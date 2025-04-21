package knn.model.classification.metrics;

/**
 * Implementación de la distancia euclídea entre dos instancias.
 * La distancia euclídea se calcula como la raíz cuadrada de la suma
 * de los cuadrados de las diferencias entre los valores de los atributos.
 * Por eficiencia, no se calcula la raíz cuadrada (como recomendó el profesor).
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class EuclideanDistance implements DistanceMetric {

    /**
     * Calcula la distancia euclídea (sin raíz cuadrada para optimización).
     *
     * @param a Primera instancia (valores)
     * @param b Segunda instancia (valores)
     * @param classIndices Índices de los atributos de clase (excluidos del cálculo)
     * @param weights Pesos para cada atributo
     * @return Valor de distancia euclídea al cuadrado
     */
    @Override
    public double calculate(double[] a, double[] b, int[] classIndices, double[] weights) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Los vectores deben tener la misma dimensión");
        }

        double sumSquared = 0.0;

        for (int i = 0; i < a.length; i++) {
            // Ignorar atributos de clase
            if (!isClassAttribute(i, classIndices)) {
                double diff = a[i] - b[i];
                // Aplicar peso del atributo
                double weightedDiff = weights[i] * diff;
                sumSquared += weightedDiff * weightedDiff;
            }
        }

        // Según indicó el profesor, no calculamos la raíz para optimizar
        return sumSquared;
    }

    /**
     * Obtiene el nombre descriptivo de la métrica.
     *
     * @return Nombre de la métrica
     */
    @Override
    public String getName() {
        return "Distancia Euclídea";
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
