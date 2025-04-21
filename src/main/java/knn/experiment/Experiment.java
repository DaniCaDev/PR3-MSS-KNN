package knn.experiment;

import knn.model.classification.KNNClassifier;
import knn.model.dataset.Dataset;
import knn.model.dataset.Instance;
import knn.model.dataset.attributes.Attribute;
import knn.model.dataset.attributes.CategoricalAttribute;

import java.io.IOException;

/**
 * Clase para gestionar experimentos de clasificación con k-nn.
 * Permite dividir datasets, ejecutar experimentos y evaluar resultados.
 *
 * @author Daniel Alejandro Álvarez Casablanca
 * @version 1.0
 */
public class Experiment {
    /** Dataset de entrenamiento */
    private Dataset trainingSet;

    /** Dataset de prueba */
    private Dataset testSet;

    /** Clasificador k-nn configurado */
    private KNNClassifier classifier;

    /**
     * Constructor para usar conjuntos de entrenamiento y prueba ya existentes.
     *
     * @param classifier Clasificador a utilizar
     * @param trainingSet Dataset de entrenamiento
     * @param testSet Dataset de prueba
     */
    public Experiment(KNNClassifier classifier, Dataset trainingSet, Dataset testSet) {
        this.classifier = classifier;
        this.trainingSet = trainingSet;
        this.testSet = testSet;
    }

    /**
     * Constructor que crea los conjuntos de entrenamiento y prueba a partir de un dataset completo.
     *
     * @param classifier Clasificador a utilizar
     * @param dataset Dataset completo
     * @param testPercentage Porcentaje para pruebas
     * @param random Si la división es aleatoria
     * @param seed Semilla para generación aleatoria
     */
    public Experiment(KNNClassifier classifier, Dataset dataset, double testPercentage, boolean random, long seed) {
        this.classifier = classifier;

        // Dividir el dataset
        Dataset[] splits = dataset.split(testPercentage, random, seed);
        this.trainingSet = splits[0];
        this.testSet = splits[1];
    }

    /**
     * Ejecuta el experimento clasificando todas las instancias del conjunto de prueba.
     *
     * @return Resultado del experimento
     */
    public ExperimentResult run() {
        if (testSet.getNumInstances() == 0) {
            throw new IllegalStateException("El conjunto de prueba está vacío");
        }

        // Almacenar las clases predichas y reales
        String[] predictedClasses = new String[testSet.getNumInstances()];
        String[] actualClasses = new String[testSet.getNumInstances()];

        // Índice de la clase principal
        int classIndex = classifier.getMainClassIndex();

        // Obtener atributo de clase
        Attribute classAttr = testSet.getAttribute(classIndex);
        if (!(classAttr instanceof CategoricalAttribute)) {
            throw new IllegalStateException("El atributo de clase debe ser categórico");
        }
        CategoricalAttribute catClassAttr = (CategoricalAttribute) classAttr;

        // Clasificar cada instancia del conjunto de prueba
        for (int i = 0; i < testSet.getNumInstances(); i++) {
            Instance instance = testSet.getInstance(i);

            // Obtener clase predicha
            String predicted = classifier.classify(instance);
            predictedClasses[i] = predicted;

            // Obtener clase real
            int actualCode = (int) instance.getValue(classIndex);
            String actual = catClassAttr.getValue(actualCode);
            actualClasses[i] = actual;
        }

        // Crear resultado del experimento
        return new ExperimentResult(predictedClasses, actualClasses, catClassAttr.getUniqueValues());
    }

    /**
     * Guarda el conjunto de entrenamiento en un archivo.
     *
     * @param filename Nombre del archivo
     * @throws IOException Si hay errores de escritura
     */
    public void saveTrainingSet(String filename) throws IOException {
        trainingSet.saveToFile(filename, false);
    }

    /**
     * Guarda el conjunto de prueba en un archivo.
     *
     * @param filename Nombre del archivo
     * @throws IOException Si hay errores de escritura
     */
    public void saveTestSet(String filename) throws IOException {
        testSet.saveToFile(filename, false);
    }

    /**
     * Obtiene el conjunto de entrenamiento.
     *
     * @return Dataset de entrenamiento
     */
    public Dataset getTrainingSet() {
        return trainingSet;
    }

    /**
     * Obtiene el conjunto de prueba.
     *
     * @return Dataset de prueba
     */
    public Dataset getTestSet() {
        return testSet;
    }
}