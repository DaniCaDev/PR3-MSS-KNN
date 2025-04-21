package knn.model.classification.metrics;

/**
 * Implementación de la distancia de Manhattan entre dos instancias.
 * La distancia de Manhattan es la suma de los valores absolutos de
 * las diferencias entre los valores de los atributos.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class ManhattanDistance implements DistanceMetric {

    /**
     * Calcula la distancia de Manhattan entre dos instancias.
     *
     * @param a Primera instancia (valores)
     * @param b Segunda instancia (valores)
     * @param classIndices Índices de los atributos de clase (excluidos del cálculo)
     * @param weights Pesos para cada atributo
     * @return Valor de distancia de Manhattan
     */
    @Override
    public double calculate(double[] a, double[] b, int[] classIndices, double[] weights) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Los vectores deben tener la misma dimensión");
        }

        double sum = 0.0;

        for (int i = 0; i < a.length; i++) {
            // Ignorar atributos de clase
            if (!isClassAttribute(i, classIndices)) {
                // Aplicar peso del atributo
                sum += weights[i] * Math.abs(a[i] - b[i]);
            }
        }

        return sum;
    }

    /**
     * Obtiene el nombre descriptivo de la métrica.
     *
     * @return Nombre de la métrica
     */
    @Override
    public String getName() {
        return "Distancia de Manhattan";
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