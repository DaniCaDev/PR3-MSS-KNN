package knn.model.classification.weights;

import knn.model.classification.Neighbor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación de estrategia de peso basada en la distancia.
 * Los vecinos tienen un peso inversamente proporcional a su distancia.
 *
 * @author Daniel Alejandro Álvarez Casablanca
 * @version 1.0
 */
public class DistanceBasedWeight implements CaseWeightStrategy {

    /**
     * Calcula los pesos para cada vecino, inversamente proporcionales a su distancia.
     *
     * @param neighbors Lista de vecinos ordenados por distancia
     * @param k Número de vecinos a considerar
     * @return Mapa de índices a pesos
     */
    @Override
    public Map<Integer, Double> calculateWeights(List<Neighbor> neighbors, int k) {
        Map<Integer, Double> weights = new HashMap<>();

        // Asignar pesos inversamente proporcionales a la distancia
        for (int i = 0; i < Math.min(k, neighbors.size()); i++) {
            double distance = neighbors.get(i).getDistance();

            // Para evitar división por cero
            if (distance == 0) {
                weights.put(i, 1000.0); // Valor grande para distancia cero
            } else {
                weights.put(i, 1.0 / distance);
            }
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
        return "Cercanía";
    }
}