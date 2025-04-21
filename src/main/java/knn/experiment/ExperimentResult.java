package knn.experiment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Clase que representa el resultado de un experimento de clasificación.
 * Almacena información sobre precisión y matriz de confusión.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class ExperimentResult {
    /** Precisión predictiva */
    private double accuracy;

    /** Matriz de confusión */
    private int[][] confusionMatrix;

    /** Mapeo de nombres de clase a índices */
    private Map<String, Integer> classIndices;

    /** Nombres de las clases únicas */
    private String[] classNames;

    /**
     * Constructor que calcula los resultados del experimento.
     *
     * @param predicted Clases predichas
     * @param actual Clases reales
     * @param uniqueClasses Conjunto de clases únicas
     */
    public ExperimentResult(String[] predicted, String[] actual, Set<String> uniqueClasses) {
        if (predicted.length != actual.length) {
            throw new IllegalArgumentException("Los arrays deben tener el mismo tamaño");
        }

        // Inicializar mapeo de clases
        classIndices = new HashMap<>();
        classNames = uniqueClasses.toArray(new String[0]);

        for (int i = 0; i < classNames.length; i++) {
            classIndices.put(classNames[i], i);
        }

        // Crear matriz de confusión
        int numClasses = classNames.length;
        confusionMatrix = new int[numClasses][numClasses];

        // Calcular matriz de confusión
        int correctCount = 0;
        for (int i = 0; i < predicted.length; i++) {
            String pred = predicted[i];
            String act = actual[i];

            // Incrementar celda en matriz de confusión
            int predIdx = classIndices.getOrDefault(pred, -1);
            int actIdx = classIndices.getOrDefault(act, -1);

            if (predIdx >= 0 && actIdx >= 0) {
                confusionMatrix[actIdx][predIdx]++;

                // Contar predicciones correctas (diagonal)
                if (pred.equals(act)) {
                    correctCount++;
                }
            }
        }

        // Calcular precisión
        this.accuracy = (double) correctCount / predicted.length;
    }

    /**
     * Obtiene la precisión predictiva del experimento.
     *
     * @return Valor de precisión (0-1)
     */
    public double getAccuracy() {
        return accuracy;
    }

    /**
     * Obtiene la matriz de confusión del experimento.
     *
     * @return Matriz de confusión [clase real][clase predicha]
     */
    public int[][] getConfusionMatrix() {
        return confusionMatrix;
    }

    /**
     * Obtiene información textual detallada de los resultados.
     *
     * @return String con los resultados formateados
     */
    public String getResultsInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== Resultados del Experimento ===\n");
        sb.append("Precisión Predictiva: ").append(String.format("%.4f", accuracy)).append("\n\n");

        sb.append("Matriz de Confusión:\n");

        // Cabecera
        sb.append("            ");
        for (String className : classNames) {
            sb.append(String.format("%-12s", className));
        }
        sb.append("(Predicho)\n");

        // Filas
        for (int i = 0; i < confusionMatrix.length; i++) {
            sb.append(String.format("%-12s", classNames[i]));

            for (int j = 0; j < confusionMatrix[i].length; j++) {
                sb.append(String.format("%-12d", confusionMatrix[i][j]));
            }

            sb.append("\n");
        }

        sb.append("(Real)\n");

        return sb.toString();
    }
}