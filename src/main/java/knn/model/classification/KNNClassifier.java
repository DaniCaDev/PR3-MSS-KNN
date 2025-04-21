package knn.model.classification;

import knn.model.classification.metrics.DistanceMetric;
import knn.model.classification.metrics.EuclideanDistance;
import knn.model.classification.rules.ClassificationRule;
import knn.model.classification.rules.MajorityRule;
import knn.model.classification.weights.CaseWeightStrategy;
import knn.model.classification.weights.EqualityWeight;
import knn.model.dataset.Dataset;
import knn.model.dataset.Instance;
import knn.model.dataset.attributes.Attribute;
import knn.model.dataset.attributes.CategoricalAttribute;
import knn.model.dataset.attributes.NumericAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación del clasificador k-nn (k vecinos más cercanos).
 * Permite configurar diferentes métricas, estrategias de peso y reglas de clasificación.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class KNNClassifier {
    /** Dataset de entrenamiento */
    private Dataset trainingSet;

    /** Número de vecinos a considerar */
    private int k;

    /** Métrica de distancia */
    private DistanceMetric metric;

    /** Estrategia de peso de casos */
    private CaseWeightStrategy weightStrategy;

    /** Regla de clasificación */
    private ClassificationRule classificationRule;

    /** Índice del atributo de clase principal (para clasificación) */
    private int mainClassIndex;

    /**
     * Constructor que crea un clasificador con configuración por defecto.
     *
     * @param trainingSet Dataset de entrenamiento
     * @param k Número de vecinos
     */
    public KNNClassifier(Dataset trainingSet, int k) {
        this.trainingSet = trainingSet;
        this.k = k;
        this.metric = new EuclideanDistance(); // Por defecto Euclídea
        this.weightStrategy = new EqualityWeight(); // Por defecto igualdad
        this.classificationRule = new MajorityRule(); // Por defecto mayoría simple

        // Usar el primer índice de clase como principal
        if (trainingSet.getClassAttributeIndices().length > 0) {
            this.mainClassIndex = trainingSet.getClassAttributeIndices()[0];
        } else {
            throw new IllegalArgumentException("El dataset de entrenamiento no tiene atributos de clase");
        }
    }

    /**
     * Establece la métrica de distancia a utilizar.
     *
     * @param metric Métrica a utilizar
     */
    public void setMetric(DistanceMetric metric) {
        this.metric = metric;
    }

    /**
     * Establece la estrategia de peso de casos a utilizar.
     *
     * @param strategy Estrategia a utilizar
     */
    public void setWeightStrategy(CaseWeightStrategy strategy) {
        this.weightStrategy = strategy;
    }

    /**
     * Establece la regla de clasificación a utilizar.
     *
     * @param rule Regla a utilizar
     */
    public void setClassificationRule(ClassificationRule rule) {
        this.classificationRule = rule;
    }

    /**
     * Establece el índice del atributo de clase principal.
     *
     * @param index Índice del atributo
     */
    public void setMainClassIndex(int index) {
        if (!trainingSet.isClassAttribute(index)) {
            throw new IllegalArgumentException("El índice no corresponde a un atributo de clase");
        }
        this.mainClassIndex = index;
    }

    /**
     * Obtiene el índice del atributo de clase principal.
     *
     * @return Índice del atributo de clase principal
     */
    public int getMainClassIndex() {
        return mainClassIndex;
    }

    /**
     * Clasifica una instancia utilizando el método k-nn.
     *
     * @param instance Instancia a clasificar
     * @return Clase predicha
     */
    public String classify(Instance instance) {
        // Verificar dimensiones
        if (instance.getValues().length != trainingSet.getNumAttributes()) {
            throw new IllegalArgumentException("La instancia no tiene la dimensión correcta");
        }

        // Lista de vecinos ordenados por distancia
        List<Neighbor> neighbors = findNeighbors(instance);

        // Obtener los k vecinos más cercanos
        neighbors = neighbors.subList(0, Math.min(k, neighbors.size()));

        // Calcular pesos para cada vecino
        Map<Integer, Double> neighborWeights = weightStrategy.calculateWeights(neighbors, k);

        // Contar votos por clase
        Map<String, Double> votes = countVotes(neighbors, neighborWeights);

        // Aplicar regla de clasificación
        return classificationRule.decide(votes);
    }

    /**
     * Encuentra todos los vecinos ordenados por distancia.
     *
     * @param instance Instancia a clasificar
     * @return Lista de vecinos ordenada
     */
    private List<Neighbor> findNeighbors(Instance instance) {
        List<Neighbor> neighbors = new ArrayList<>();

        // Calcular distancias a todas las instancias del conjunto de entrenamiento
        for (int i = 0; i < trainingSet.getNumInstances(); i++) {
            Instance trainInstance = trainingSet.getInstance(i);

            // Calcular distancia
            double distance = calculateDistance(instance, trainInstance);

            // Añadir a la lista de vecinos
            neighbors.add(new Neighbor(trainInstance, distance, i));
        }

        // Ordenar por distancia (menor a mayor)
        Collections.sort(neighbors);

        return neighbors;
    }

    /**
     * Calcula la distancia entre dos instancias.
     *
     * @param instance1 Primera instancia
     * @param instance2 Segunda instancia
     * @return Valor de distancia
     */
    private double calculateDistance(Instance instance1, Instance instance2) {
        // Preparar pesos de atributos
        double[] weights = new double[trainingSet.getNumAttributes()];
        for (int i = 0; i < weights.length; i++) {
            Attribute attr = trainingSet.getAttribute(i);
            if (attr instanceof NumericAttribute) {
                weights[i] = ((NumericAttribute) attr).getWeight();
            } else {
                weights[i] = 1.0; // Peso por defecto para atributos categóricos
            }
        }

        // Calcular distancia usando la métrica seleccionada
        return metric.calculate(
                instance1.getValues(),
                instance2.getValues(),
                trainingSet.getClassAttributeIndices(),
                weights
        );
    }

    /**
     * Cuenta votos por clase según los pesos asignados a cada vecino.
     *
     * @param neighbors Lista de vecinos
     * @param weights Pesos de cada vecino
     * @return Mapa de clases a votos
     */
    private Map<String, Double> countVotes(List<Neighbor> neighbors, Map<Integer, Double> weights) {
        Map<String, Double> votes = new HashMap<>();

        // Obtener atributo de clase
        Attribute classAttr = trainingSet.getAttribute(mainClassIndex);

        if (!(classAttr instanceof CategoricalAttribute)) {
            throw new IllegalStateException("El atributo de clase debe ser categórico");
        }

        CategoricalAttribute catClassAttr = (CategoricalAttribute) classAttr;

        // Contar votos ponderados
        for (int i = 0; i < neighbors.size(); i++) {
            Neighbor neighbor = neighbors.get(i);
            double weight = weights.get(i);

            // Obtener valor de clase
            int classCode = (int) neighbor.getInstance().getValue(mainClassIndex);
            String className = catClassAttr.getValue(classCode);

            // Acumular voto ponderado
            votes.put(className, votes.getOrDefault(className, 0.0) + weight);
        }

        return votes;
    }

    /**
     * Obtiene información sobre la configuración del clasificador.
     *
     * @return String con la configuración
     */
    public String getConfiguration() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== Configuración del Clasificador K-NN ===\n");
        sb.append("Valor de k: ").append(k).append("\n");
        sb.append("Métrica: ").append(metric.getName()).append("\n");
        sb.append("Estrategia de peso: ").append(weightStrategy.getName()).append("\n");
        sb.append("Regla de clasificación: ").append(classificationRule.getName()).append("\n");
        sb.append("Atributo de clase principal: ").append(trainingSet.getAttribute(mainClassIndex).getName());

        return sb.toString();
    }
}