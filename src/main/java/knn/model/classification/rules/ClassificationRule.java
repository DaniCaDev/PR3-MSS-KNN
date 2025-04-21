package knn.model.classification.rules;

import java.util.Map;

/**
 * Interfaz para definir diferentes reglas de clasificación.
 * Cada implementación debe decidir la clase asignada en función de los votos.
 *
 * @author Daniel Alejandro Álvarez Casablanca
 * @version 1.0
 */
public interface ClassificationRule {

    /**
     * Decide la clase a asignar según los votos.
     *
     * @param votes Mapa de clases a sus votos acumulados
     * @return Clase elegida
     */
    String decide(Map<String, Double> votes);

    /**
     * Obtiene el nombre descriptivo de la regla.
     *
     * @return Nombre de la regla
     */
    String getName();
}