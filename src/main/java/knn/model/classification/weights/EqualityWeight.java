package knn.model.classification.weights;

import knn.model.classification.Neighbor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación de estrategia de peso donde todos los vecinos tienen igual peso.
 * Todos los vecinos considerados tienen un peso de 1.0.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class EqualityWeight implements CaseWeightStrategy {

    /**
     * Calcula los pesos para cada vecino, asignando peso 1.0 a todos.
     *
     * @param neighbors Lista de vecinos ordenados por distancia
     * @param k Número de vecinos a considerar
     * @return Mapa de índices a pesos
     */
    @Override
    public Map<Integer, Double> calculateWeights(List<Neighbor> neighbors, int k) {
        Map<Integer, Double> weights = new HashMap<>();

        // Asignar peso 1.0 a cada vecino (todos iguales)
        for (int i = 0; i < Math.min(k, neighbors.size()); i++) {
            weights.put(i, 1.0);
        }

        return weights;
    }

    /**
     * Obtiene el nombre descriptivo de la estrategia.
     *
     * @return Nombre de la estrategia
     */
    @Override
    public String getName() {
        return "Igualdad de votos";
    }
}