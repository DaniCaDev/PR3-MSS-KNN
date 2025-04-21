package knn;

import knn.experiment.Experiment;
import knn.experiment.ExperimentResult;
import knn.model.classification.KNNClassifier;
import knn.model.classification.metrics.ChebyshevDistance;
import knn.model.classification.metrics.DistanceMetric;
import knn.model.classification.metrics.EuclideanDistance;
import knn.model.classification.metrics.ManhattanDistance;
import knn.model.classification.rules.ClassificationRule;
import knn.model.classification.rules.MajorityRule;
import knn.model.classification.rules.ThresholdRule;
import knn.model.classification.weights.CaseWeightStrategy;
import knn.model.classification.weights.DistanceBasedWeight;
import knn.model.classification.weights.EqualityWeight;
import knn.model.classification.weights.FixedOrderWeight;
import knn.model.dataset.Dataset;
import knn.model.dataset.Instance;
import knn.model.dataset.PreprocessingType;
import knn.model.dataset.attributes.Attribute;
import knn.model.dataset.attributes.CategoricalAttribute;
import knn.model.dataset.attributes.NumericAttribute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Clase principal que implementa la interfaz de usuario para el sistema de clasificación KNN extendido.
 * Proporciona menús interactivos para cargar datasets, configurar el clasificador y realizar experimentos.
 *
 * @author Daniel Alejandro Álvarez Casablanca
 * @version 1.0
 */
public class KNNSystemMain {
    /** Dataset completo cargado */
    private static Dataset dataset;

    /** Conjunto de entrenamiento para clasificación */
    private static Dataset trainingSet;

    /** Conjunto de prueba para evaluación */
    private static Dataset testSet;

    /** Clasificador KNN configurado */
    private static KNNClassifier classifier;

    /** Lector para entrada de usuario */
    private static BufferedReader reader;

    /**
     * Método principal que inicia la aplicación.
     *
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.println("=== Sistema de Clasificación KNN Extendido ===");
            mainMenu();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar el lector: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra el menú principal y procesa la selección del usuario.
     *
     * @throws IOException Si ocurre un error de lectura
     */
    private static void mainMenu() throws IOException {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== Menú Principal ===");
            System.out.println("1. Cargar dataset");
            System.out.println("2. Mostrar información del dataset");
            System.out.println("3. Preprocesar dataset");
            System.out.println("4. Configurar clasificador KNN");
            System.out.println("5. Experimentación");
            System.out.println("6. Clasificar nueva instancia");
            System.out.println("7. Guardar dataset");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            int option = Integer.parseInt(reader.readLine().trim());

            switch (option) {
                case 1:
                    loadDatasetMenu();
                    break;
                case 2:
                    showDatasetInfo();
                    break;
                case 3:
                    preprocessDataset();
                    break;
                case 4:
                    configureClassifier();
                    break;
                case 5:
                    experimentationMenu();
                    break;
                case 6:
                    classifyNewInstance();
                    break;
                case 7:
                    saveDataset();
                    break;
                case 0:
                    exit = true;
                    System.out.println("¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }

    /**
     * Muestra el menú para cargar datasets y procesa la selección del usuario.
     *
     * @throws IOException Si ocurre un error de lectura
     */
    private static void loadDatasetMenu() throws IOException {
        System.out.println("\n=== Cargar Dataset ===");
        System.out.println("1. Cargar dataset completo");
        System.out.println("2. Cargar conjuntos de entrenamiento y prueba");
        System.out.println("0. Volver al menú principal");
        System.out.print("Seleccione una opción: ");

        int option = Integer.parseInt(reader.readLine().trim());

        switch (option) {
            case 1:
                loadFullDataset();
                break;
            case 2:
                loadTrainingAndTestSets();
                break;
            case 0:
                // Volver al menú principal
                break;
            default:
                System.out.println("Opción no válida. Intente nuevamente.");
        }
    }

    /**
     * Carga un dataset completo desde un archivo especificado por el usuario.
     *
     * @throws IOException Si ocurre un error de lectura
     */
    private static void loadFullDataset() throws IOException {
        System.out.print("Nombre del archivo CSV: ");
        String filename = reader.readLine().trim();

        System.out.print("¿El archivo tiene cabecera? (s/n): ");
        boolean hasHeader = reader.readLine().trim().equalsIgnoreCase("s");

        System.out.print("Delimitador (por defecto ','): ");
        String delimiter = reader.readLine().trim();
        if (delimiter.isEmpty()) {
            delimiter = ",";
        }

        System.out.println("Índices de atributos de clase (separados por comas): ");
        String[] classIndicesStr = reader.readLine().trim().split(",");
        int[] classIndices = new int[classIndicesStr.length];

        for (int i = 0; i < classIndicesStr.length; i++) {
            classIndices[i] = Integer.parseInt(classIndicesStr[i].trim());
        }

        dataset = new Dataset();
        try {
            dataset.loadFromFile(filename, hasHeader, delimiter, classIndices);
            System.out.println("Dataset cargado correctamente con " + dataset.getNumInstances() + " instancias.");

            // Reset training and test sets
            trainingSet = null;
            testSet = null;
            classifier = null;
        } catch (Exception e) {
            System.err.println("Error al cargar el dataset: " + e.getMessage());
        }
    }

    /**
     * Carga conjuntos de entrenamiento y prueba por separado.
     *
     * @throws IOException Si ocurre un error de lectura
     */
    private static void loadTrainingAndTestSets() throws IOException {
        System.out.println("\n=== Cargar conjunto de entrenamiento ===");
        System.out.print("Nombre del archivo CSV: ");
        String trainingFile = reader.readLine().trim();

        System.out.print("¿El archivo tiene cabecera? (s/n): ");
        boolean hasHeader = reader.readLine().trim().equalsIgnoreCase("s");

        System.out.print("Delimitador (por defecto ','): ");
        String delimiter = reader.readLine().trim();
        if (delimiter.isEmpty()) {
            delimiter = ",";
        }

        System.out.println("Índices de atributos de clase (separados por comas): ");
        String[] classIndicesStr = reader.readLine().trim().split(",");
        int[] classIndices = new int[classIndicesStr.length];

        for (int i = 0; i < classIndicesStr.length; i++) {
            classIndices[i] = Integer.parseInt(classIndicesStr[i].trim());
        }

        trainingSet = new Dataset();
        try {
            trainingSet.loadFromFile(trainingFile, hasHeader, delimiter, classIndices);
            System.out.println("Conjunto de entrenamiento cargado con " + trainingSet.getNumInstances() + " instancias.");
        } catch (Exception e) {
            System.err.println("Error al cargar el conjunto de entrenamiento: " + e.getMessage());
            return;
        }

        System.out.println("\n=== Cargar conjunto de prueba ===");
        System.out.print("Nombre del archivo CSV: ");
        String testFile = reader.readLine().trim();

        testSet = new Dataset();
        try {
            testSet.loadFromFile(testFile, hasHeader, delimiter, classIndices);
            System.out.println("Conjunto de prueba cargado con " + testSet.getNumInstances() + " instancias.");
        } catch (Exception e) {
            System.err.println("Error al cargar el conjunto de prueba: " + e.getMessage());
            return;
        }

        // El dataset completo no está disponible
        dataset = null;
        classifier = null;
    }

    /**
     * Muestra información detallada sobre el dataset cargado.
     */
    private static void showDatasetInfo() {
        if (dataset != null) {
            System.out.println("\n=== Información del Dataset Completo ===");
            System.out.println(dataset.getDatasetInfo());

            System.out.println("\n¿Desea ver información detallada de los atributos? (s/n): ");
            try {
                String response = reader.readLine().trim();
                if (response.equalsIgnoreCase("s")) {
                    System.out.println(dataset.getAttributesInfo());
                }
            } catch (IOException e) {
                System.err.println("Error de lectura: " + e.getMessage());
            }
        } else if (trainingSet != null) {
            System.out.println("\n=== Información del Conjunto de Entrenamiento ===");
            System.out.println(trainingSet.getDatasetInfo());

            if (testSet != null) {
                System.out.println("\n=== Información del Conjunto de Prueba ===");
                System.out.println(testSet.getDatasetInfo());
            }

            System.out.println("\n¿Desea ver información detallada de los atributos? (s/n): ");
            try {
                String response = reader.readLine().trim();
                if (response.equalsIgnoreCase("s")) {
                    System.out.println(trainingSet.getAttributesInfo());
                }
            } catch (IOException e) {
                System.err.println("Error de lectura: " + e.getMessage());
            }
        } else {
            System.out.println("No hay dataset cargado.");
        }
    }

    /**
     * Permite al usuario aplicar opciones de preprocesamiento al dataset.
     *
     * @throws IOException Si ocurre un error de lectura
     */
    private static void preprocessDataset() throws IOException {
        if (dataset == null && trainingSet == null) {
            System.out.println("No hay dataset cargado.");
            return;
        }

        System.out.println("\n=== Preprocesamiento ===");
        System.out.println("1. Sin preprocesamiento (datos crudos)");
        System.out.println("2. Normalización rango [0,1]");
        System.out.println("3. Estandarización (z-score)");
        System.out.print("Seleccione el tipo de preprocesamiento: ");

        int option = Integer.parseInt(reader.readLine().trim());
        PreprocessingType type;

        switch (option) {
            case 1:
                type = PreprocessingType.NONE;
                break;
            case 2:
                type = PreprocessingType.RANGE_0_1;
                break;
            case 3:
                type = PreprocessingType.STANDARDIZE;
                break;
            default:
                System.out.println("Opción no válida. Se usará normalización [0,1].");
                type = PreprocessingType.RANGE_0_1;
        }

        // Aplicar preprocesamiento
        if (dataset != null) {
            dataset.normalize(type);
            System.out.println("Preprocesamiento aplicado al dataset completo.");
        }

        if (trainingSet != null) {
            trainingSet.normalize(type);
            System.out.println("Preprocesamiento aplicado al conjunto de entrenamiento.");
        }

        if (testSet != null) {
            testSet.normalize(type);
            System.out.println("Preprocesamiento aplicado al conjunto de prueba.");
        }

        // Configurar pesos de atributos
        System.out.print("¿Desea configurar pesos para los atributos? (s/n): ");
        String response = reader.readLine().trim();

        if (response.equalsIgnoreCase("s")) {
            Dataset activeDataset = dataset != null ? dataset : trainingSet;

            if (activeDataset != null) {
                System.out.println("\nAtributos disponibles:");
                for (int i = 0; i < activeDataset.getNumAttributes(); i++) {
                    Attribute attr = activeDataset.getAttribute(i);
                    if (attr instanceof NumericAttribute && !activeDataset.isClassAttribute(i)) {
                        System.out.println(i + ": " + attr.getName());
                    }
                }

                double[] weights = new double[activeDataset.getNumAttributes()];
                Arrays.fill(weights, 1.0); // Peso por defecto

                System.out.println("Ingrese los pesos para cada atributo (separados por comas):");
                System.out.println("Deje en blanco para usar el peso por defecto (1.0) para todos.");

                String weightsInput = reader.readLine().trim();
                if (!weightsInput.isEmpty()) {
                    String[] weightValues = weightsInput.split(",");
                    for (int i = 0; i < Math.min(weightValues.length, weights.length); i++) {
                        weights[i] = Double.parseDouble(weightValues[i].trim());
                    }
                }

                // Aplicar pesos
                if (dataset != null) {
                    dataset.setAttributeWeights(weights);
                }
                if (trainingSet != null) {
                    trainingSet.setAttributeWeights(weights);
                }

                System.out.println("Pesos de atributos configurados correctamente.");
            }
        }
    }

    /**
     * Permite al usuario configurar los parámetros del clasificador KNN.
     *
     * @throws IOException Si ocurre un error de lectura
     */
    private static void configureClassifier() throws IOException {
        Dataset activeDataset = dataset != null ? dataset : trainingSet;

        if (activeDataset == null) {
            System.out.println("No hay dataset cargado.");
            return;
        }

        System.out.println("\n=== Configuración del Clasificador KNN ===");

        // Configurar valor de k
        System.out.print("Valor de k (número de vecinos): ");
        int k = Integer.parseInt(reader.readLine().trim());

        // Crear clasificador
        classifier = new KNNClassifier(activeDataset, k);

        // Configurar métrica
        System.out.println("\nSeleccione la métrica de distancia:");
        System.out.println("1. Distancia Euclídea");
        System.out.println("2. Distancia de Manhattan");
        System.out.println("3. Distancia de Chebyshev");
        System.out.print("Opción: ");

        int metricOption = Integer.parseInt(reader.readLine().trim());
        DistanceMetric metric;

        switch (metricOption) {
            case 1:
                metric = new EuclideanDistance();
                break;
            case 2:
                metric = new ManhattanDistance();
                break;
            case 3:
                metric = new ChebyshevDistance();
                break;
            default:
                System.out.println("Opción no válida. Se usará Distancia Euclídea.");
                metric = new EuclideanDistance();
        }

        classifier.setMetric(metric);

        // Configurar estrategia de peso de casos
        System.out.println("\nSeleccione la estrategia de peso de casos:");
        System.out.println("1. Igualdad de votos");
        System.out.println("2. Cercanía (inversamente proporcional a la distancia)");
        System.out.println("3. Voto fijo según orden");
        System.out.print("Opción: ");

        int weightOption = Integer.parseInt(reader.readLine().trim());
        CaseWeightStrategy weightStrategy;

        switch (weightOption) {
            case 1:
                weightStrategy = new EqualityWeight();
                break;
            case 2:
                weightStrategy = new DistanceBasedWeight();
                break;
            case 3:
                System.out.println("Ingrese los pesos para cada vecino (separados por comas):");
                System.out.println("Deje en blanco para usar pesos por defecto (k, k-1, ..., 1)");

                String weightsInput = reader.readLine().trim();
                double[] weights;

                if (!weightsInput.isEmpty()) {
                    String[] weightValues = weightsInput.split(",");
                    weights = new double[weightValues.length];
                    for (int i = 0; i < weights.length; i++) {
                        weights[i] = Double.parseDouble(weightValues[i].trim());
                    }
                } else {
                    weights = new double[k];
                    for (int i = 0; i < k; i++) {
                        weights[i] = k - i;
                    }
                }

                weightStrategy = new FixedOrderWeight(weights);
                break;
            default:
                System.out.println("Opción no válida. Se usará Igualdad de votos.");
                weightStrategy = new EqualityWeight();
        }

        classifier.setWeightStrategy(weightStrategy);

        // Configurar regla de clasificación
        System.out.println("\nSeleccione la regla de clasificación:");
        System.out.println("1. Mayoría simple");
        System.out.println("2. Umbral (mayoría cualificada)");
        System.out.print("Opción: ");

        int ruleOption = Integer.parseInt(reader.readLine().trim());
        ClassificationRule classRule;

        switch (ruleOption) {
            case 1:
                classRule = new MajorityRule();
                break;
            case 2:
                System.out.print("Ingrese el umbral (0-1, por defecto 0.5): ");
                String thresholdInput = reader.readLine().trim();
                double threshold = 0.5; // Por defecto
                if (!thresholdInput.isEmpty()) {
                    threshold = Double.parseDouble(thresholdInput);
                }
                classRule = new ThresholdRule(threshold);
                break;
            default:
                System.out.println("Opción no válida. Se usará Mayoría simple.");
                classRule = new MajorityRule();
        }

        classifier.setClassificationRule(classRule);

        // Configurar atributo de clase principal
        int[] classIndices = activeDataset.getClassAttributeIndices();
        if (classIndices.length > 1) {
            System.out.println("\nSeleccione el atributo de clase principal para clasificación:");
            for (int i = 0; i < classIndices.length; i++) {
                System.out.println((i+1) + ": " + activeDataset.getAttribute(classIndices[i]).getName());
            }
            System.out.print("Opción: ");

            int classOption = Integer.parseInt(reader.readLine().trim());
            if (classOption >= 1 && classOption <= classIndices.length) {
                classifier.setMainClassIndex(classIndices[classOption-1]);
            }
        }

        System.out.println("\nConfiguración del clasificador completada:");
        System.out.println(classifier.getConfiguration());
    }

    /**
     * Menú de experimentación que permite realizar pruebas con el clasificador.
     *
     * @throws IOException Si ocurre un error de lectura
     */
    private static void experimentationMenu() throws IOException {
        if (classifier == null) {
            System.out.println("Primero debe configurar el clasificador.");
            return;
        }

        System.out.println("\n=== Experimentación ===");

        Experiment experiment;

        if (dataset != null && trainingSet == null && testSet == null) {
            // Generar conjuntos de entrenamiento y prueba automáticamente
            System.out.print("Porcentaje para conjunto de prueba (0-1): ");
            double testPercentage = Double.parseDouble(reader.readLine().trim());

            System.out.print("¿Generar conjunto de prueba aleatoriamente? (s/n): ");
            boolean random = reader.readLine().trim().equalsIgnoreCase("s");

            long seed = 1234; // Semilla por defecto
            if (random) {
                System.out.print("Semilla para generación aleatoria (por defecto 1234): ");
                String seedInput = reader.readLine().trim();
                if (!seedInput.isEmpty()) {
                    seed = Long.parseLong(seedInput);
                }
            }

            experiment = new Experiment(classifier, dataset, testPercentage, random, seed);
            trainingSet = experiment.getTrainingSet();
            testSet = experiment.getTestSet();

            System.out.println("Conjuntos de entrenamiento y prueba generados.");
            System.out.print("¿Desea guardar estos conjuntos? (s/n): ");
            if (reader.readLine().trim().equalsIgnoreCase("s")) {
                System.out.print("Nombre para archivo de entrenamiento: ");
                String trainFile = reader.readLine().trim();
                System.out.print("Nombre para archivo de prueba: ");
                String testFile = reader.readLine().trim();

                experiment.saveTrainingSet(trainFile);
                experiment.saveTestSet(testFile);
                System.out.println("Conjuntos guardados correctamente.");
            }
        } else if (trainingSet != null && testSet != null) {
            // Usar conjuntos ya cargados
            experiment = new Experiment(classifier, trainingSet, testSet);
        } else {
            System.out.println("Error: No se pueden realizar experimentos sin datos adecuados.");
            return;
        }

        // Ejecutar el experimento
        System.out.println("Ejecutando experimento...");
        ExperimentResult result = experiment.run();

        // Mostrar resultados
        System.out.println(result.getResultsInfo());
    }

    /**
     * Permite al usuario clasificar una nueva instancia.
     *
     * @throws IOException Si ocurre un error de lectura
     */
    private static void classifyNewInstance() throws IOException {
        if (classifier == null) {
            System.out.println("Primero debe configurar el clasificador.");
            return;
        }

        Dataset activeDataset = dataset != null ? dataset : trainingSet;

        if (activeDataset == null) {
            System.out.println("No hay dataset cargado.");
            return;
        }

        System.out.println("\n=== Clasificar Nueva Instancia ===");
        System.out.println("Ingrese los valores para cada atributo (separados por comas):");

        for (int i = 0; i < activeDataset.getNumAttributes(); i++) {
            Attribute attr = activeDataset.getAttribute(i);
            System.out.println(i + ": " + attr.getName() +
                    (activeDataset.isClassAttribute(i) ? " (clase)" : ""));
        }

        String valuesInput = reader.readLine().trim();
        String[] valueStrings = valuesInput.split(",");

        // Crear instancia
        double[] instanceValues = new double[activeDataset.getNumAttributes()];

        for (int i = 0; i < Math.min(valueStrings.length, instanceValues.length); i++) {
            if (activeDataset.isClassAttribute(i)) {
                // Para atributos de clase, intentar obtener código numérico
                Attribute attr = activeDataset.getAttribute(i);
                if (attr instanceof CategoricalAttribute) {
                    CategoricalAttribute catAttr = (CategoricalAttribute) attr;
                    int code = catAttr.getCode(valueStrings[i].trim());
                    instanceValues[i] = code;
                } else {
                    // Si no es categórico, tratar como numérico
                    instanceValues[i] = Double.parseDouble(valueStrings[i].trim());
                }
            } else {
                // Para atributos no de clase
                instanceValues[i] = Double.parseDouble(valueStrings[i].trim());
            }
        }

        // Normalizar si es necesario
        PreprocessingType type = activeDataset.getPreprocessingType();
        if (type != PreprocessingType.NONE) {
            for (int i = 0; i < instanceValues.length; i++) {
                if (!activeDataset.isClassAttribute(i)) {
                    Attribute attr = activeDataset.getAttribute(i);
                    if (attr instanceof NumericAttribute) {
                        NumericAttribute numAttr = (NumericAttribute) attr;
                        instanceValues[i] = numAttr.normalize(instanceValues[i], type);
                    }
                }
            }
        }

        Instance newInstance = new Instance(instanceValues);

        // Clasificar
        String predictedClass = classifier.classify(newInstance);
        System.out.println("Clase predicha: " + predictedClass);
    }

    /**
     * Permite guardar el dataset procesado en un archivo.
     *
     * @throws IOException Si ocurre un error de escritura
     */
    private static void saveDataset() throws IOException {
        if (dataset == null && trainingSet == null) {
            System.out.println("No hay dataset para guardar.");
            return;
        }

        System.out.println("\n=== Guardar Dataset ===");

        if (dataset != null) {
            System.out.print("Nombre para archivo de dataset completo: ");
            String filename = reader.readLine().trim();

            System.out.print("¿Guardar valores normalizados? (s/n): ");
            boolean normalized = reader.readLine().trim().equalsIgnoreCase("s");

            dataset.saveToFile(filename, normalized);
            System.out.println("Dataset guardado correctamente.");
        }

        if (trainingSet != null) {
            System.out.print("¿Desea guardar el conjunto de entrenamiento? (s/n): ");
            if (reader.readLine().trim().equalsIgnoreCase("s")) {
                System.out.print("Nombre para archivo de entrenamiento: ");
                String trainFile = reader.readLine().trim();

                System.out.print("¿Guardar valores normalizados? (s/n): ");
                boolean normalized = reader.readLine().trim().equalsIgnoreCase("s");

                trainingSet.saveToFile(trainFile, normalized);
                System.out.println("Conjunto de entrenamiento guardado correctamente.");
            }
        }

        if (testSet != null) {
            System.out.print("¿Desea guardar el conjunto de prueba? (s/n): ");
            if (reader.readLine().trim().equalsIgnoreCase("s")) {
                System.out.print("Nombre para archivo de prueba: ");
                String testFile = reader.readLine().trim();

                System.out.print("¿Guardar valores normalizados? (s/n): ");
                boolean normalized = reader.readLine().trim().equalsIgnoreCase("s");

                testSet.saveToFile(testFile, normalized);
                System.out.println("Conjunto de prueba guardado correctamente.");
            }
        }
    }
}