package knn.model.classification.metrics;

/**
 * Interfaz para definir diferentes métricas de distancia entre instancias.
 * Cada implementación debe calcular una medida de distancia específica.
 *
 * @author Daniel Alejandro Álvarez Casablanca
 * @version 1.0
 */
public interface DistanceMetric {

    /**
     * Calcula la distancia entre dos vectores de valores.
     *
     * @param a Primera instancia (valores)
     * @param b Segunda instancia (valores)
     * @param classIndices Índices de los atributos de clase (excluidos del cálculo)
     * @param weights Pesos para cada atributo
     * @return Valor de distancia
     */
    double calculate(double[] a, double[] b, int[] classIndices, double[] weights);

    /**
     * Obtiene el nombre descriptivo de la métrica.
     *
     * @return Nombre de la métrica
     */
    String getName();
}