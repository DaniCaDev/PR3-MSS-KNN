package knn.model.classification.rules;

import java.util.Map;

/**
 * Implementación de regla de clasificación por mayoría simple.
 * La clase elegida es la que tiene más votos acumulados.
 *
 * @author Daniel Alejandro Álvarez Casablanca
 * @version 1.0
 */
public class MajorityRule implements ClassificationRule {

    /**
     * Decide la clase a asignar según la mayoría simple de votos.
     *
     * @param votes Mapa de clases a sus votos acumulados
     * @return Clase elegida con más votos
     */
    @Override
    public String decide(Map<String, Double> votes) {
        String bestClass = null;
        double maxVotes = -1;

        for (Map.Entry<String, Double> entry : votes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                bestClass = entry.getKey();
            }
        }

        return bestClass;
    }

    /**
     * Obtiene el nombre descriptivo de la regla.
     *
     * @return Nombre de la regla
     */
    @Override
    public String getName() {
        return "Mayoría simple";
    }
}
