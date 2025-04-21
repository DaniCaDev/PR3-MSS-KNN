package knn.model.classification.weights;

import knn.model.classification.Neighbor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación de estrategia de peso fijo según el orden.
 * Los vecinos tienen pesos predefinidos según su posición en la lista ordenada.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class FixedOrderWeight implements CaseWeightStrategy {
    /** Pesos predefinidos */
    private double[] weights;

    /**
     * Constructor con pesos predefinidos.
     *
     * @param weights Array de pesos
     */
    public FixedOrderWeight(double[] weights) {
        this.weights = weights;
    }

    /**
     * Constructor por defecto (k, k-1, ..., 1).
     *
     * @param k Número de vecinos
     */
    public FixedOrderWeight(int k) {
        this.weights = new double[k];
        for (int i = 0; i < k; i++) {
            weights[i] = k - i;
        }
    }

    /**
     * Calcula los pesos para cada vecino según su posición.
     *
     * @param neighbors Lista de vecinos ordenados por distancia
     * @param k Número de vecinos a considerar
     * @return Mapa de índices a pesos
     */
    @Override
    public Map<Integer, Double> calculateWeights(List<Neighbor> neighbors, int k) {
        Map<Integer, Double> result = new HashMap<>();

        // Asignar pesos según la posición
        for (int i = 0; i < Math.min(k, neighbors.size()); i++) {
            if (i < weights.length) {
                result.put(i, weights[i]);
            } else {
                result.put(i, 1.0); // Por defecto 1.0 si no hay peso definido
            }
        }

        return result;
    }

    /**
     * Obtiene el nombre descriptivo de la estrategia.
     *
     * @return Nombre de la estrategia
     */
    @Override
    public String getName() {
        return "Voto fijo según orden: " + Arrays.toString(weights);
    }
}