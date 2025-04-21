package knn.model.classification.weights;

import knn.model.classification.Neighbor;

import java.util.List;
import java.util.Map;

/**
 * Interfaz para definir diferentes estrategias de peso de casos.
 * Cada implementación debe asignar pesos a los vecinos según un criterio específico.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public interface CaseWeightStrategy {

    /**
     * Calcula los pesos para cada vecino.
     *
     * @param neighbors Lista de vecinos ordenados por distancia
     * @param k Número de vecinos a considerar
     * @return Mapa de índices a pesos
     */
    Map<Integer, Double> calculateWeights(List<Neighbor> neighbors, int k);

    /**
     * Obtiene el nombre descriptivo de la estrategia.
     *
     * @return Nombre de la estrategia
     */
    String getName();
}