package knn.model.classification.rules;

import java.util.Map;

/**
 * Implementación de regla de clasificación por umbral.
 * La clase elegida debe tener una proporción de votos superior al umbral definido.
 * Si ninguna clase supera el umbral, se elige la de mayor votación.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class ThresholdRule implements ClassificationRule {
    /** Umbral de votos (0-1) */
    private double threshold;

    /**
     * Constructor que establece el umbral para la regla.
     *
     * @param threshold Umbral (0-1)
     */
    public ThresholdRule(double threshold) {
        if (threshold <= 0 || threshold > 1) {
            throw new IllegalArgumentException("El umbral debe estar entre 0 y 1");
        }
        this.threshold = threshold;
    }

    /**
     * Decide la clase a asignar según la proporción de votos.
     *
     * @param votes Mapa de clases a sus votos acumulados
     * @return Clase elegida que supere el umbral, o la de mayor votación
     */
    @Override
    public String decide(Map<String, Double> votes) {
        String bestClass = null;
        double maxVotes = -1;
        double totalVotes = 0;

        // Calcular el total de votos
        for (double vote : votes.values()) {
            totalVotes += vote;
        }

        // Buscar la clase con más votos
        for (Map.Entry<String, Double> entry : votes.entrySet()) {
            // Normalizar a proporción (0-1)
            double proportion = entry.getValue() / totalVotes;

            if (proportion > maxVotes) {
                maxVotes = proportion;
                bestClass = entry.getKey();
            }
        }

        // Verificar si supera el umbral
        if (maxVotes >= threshold) {
            return bestClass;
        } else {
            // Si ninguna clase supera el umbral, devolver la más votada
            return bestClass;
        }
    }

    /**
     * Obtiene el nombre descriptivo de la regla.
     *
     * @return Nombre de la regla
     */
    @Override
    public String getName() {
        return "Umbral " + String.format("%.2f", threshold);
    }

    /**
     * Obtiene el umbral configurado.
     *
     * @return Valor del umbral
     */
    public double getThreshold() {
        return threshold;
    }
}