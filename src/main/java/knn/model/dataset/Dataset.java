package knn.model.dataset;

import knn.model.dataset.attributes.Attribute;
import knn.model.dataset.attributes.CategoricalAttribute;
import knn.model.dataset.attributes.NumericAttribute;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Clase que representa un conjunto de datos (dataset) para clasificación
 * utilizando el método k-nn, con funcionalidades extendidas.
 *
 * @author Daniel Alejandro Álvarez Casablanca
 * @version 1.0
 */
public class Dataset {
    /** Lista de instancias del dataset */
    private List<Instance> instances;

    /** Lista de atributos */
    private List<Attribute> attributes;

    /** Índices de los atributos de clase */
    private int[] classAttributeIndices;

    /** Tipo de preprocesamiento aplicado */
    private PreprocessingType preprocessingType;

    /** Instancias originales (antes del preprocesamiento) */
    private List<Instance> originalInstances;

    /**
     * Constructor por defecto que inicializa un dataset vacío.
     */
    public Dataset() {
        instances = new ArrayList<>();
        attributes = new ArrayList<>();
        preprocessingType = PreprocessingType.NONE;
    }

    /**
     * Carga un dataset desde un archivo CSV.
     *
     * @param filename Nombre del archivo
     * @param hasHeader Si el archivo tiene cabecera con nombres de atributos
     * @param delimiter Delimitador utilizado en el archivo
     * @param classIndices Índices de los atributos de clase (pueden ser varios)
     * @throws IOException Si hay errores de lectura
     */
    public void loadFromFile(String filename, boolean hasHeader, String delimiter, int[] classIndices) throws IOException {
        instances.clear();
        attributes.clear();

        if (classIndices != null && classIndices.length > 0) {
            this.classAttributeIndices = classIndices.clone();
        } else {
            throw new IllegalArgumentException("Debe especificar al menos un índice de clase");
        }

        // Intentar abrir el archivo de diferentes maneras
        BufferedReader br = null;
        try {
            // 1. Intentar como ruta de archivo directa
            File file = new File(filename);
            if (file.exists()) {
                br = new BufferedReader(new FileReader(file));
            } else {
                // 2. Intentar cargarlo como recurso del classpath
                InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
                if (is != null) {
                    br = new BufferedReader(new InputStreamReader(is));
                } else {
                    // 3. Probar con una ruta relativa a resources
                    String resourcePath = "src/main/resources/" + filename;
                    File resourceFile = new File(resourcePath);
                    if (resourceFile.exists()) {
                        br = new BufferedReader(new FileReader(resourceFile));
                    } else {
                        throw new IOException("No se pudo encontrar el archivo: " + filename);
                    }
                }
            }

            String line;
            int lineNumber = 0;

            // Leer la primera línea (cabecera si existe)
            if ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                int numAttributes = values.length;

                // Inicializar atributos
                if (hasHeader) {
                    for (int i = 0; i < numAttributes; i++) {
                        // Determinar si es un atributo de clase
                        boolean isClass = isClassAttribute(i);

                        // Por defecto, todos los atributos son numéricos excepto los de clase
                        if (isClass) {
                            attributes.add(new CategoricalAttribute(values[i], i, true));
                        } else {
                            attributes.add(new NumericAttribute(values[i], i));
                        }
                    }
                    lineNumber++;
                } else {
                    // Generar nombres automáticos si no hay cabecera
                    for (int i = 0; i < numAttributes; i++) {
                        boolean isClass = isClassAttribute(i);
                        if (isClass) {
                            attributes.add(new CategoricalAttribute("Attribute_" + i, i, true));
                        } else {
                            attributes.add(new NumericAttribute("Attribute_" + i, i));
                        }
                    }

                    // Procesar la primera línea como una instancia
                    processInstanceLine(values, delimiter);
                }
            }

            // Leer el resto de líneas (instancias)
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (!line.trim().isEmpty()) {
                    String[] values = line.split(delimiter);
                    processInstanceLine(values, delimiter);
                }
            }

            // Calcular estadísticas después de cargar todas las instancias
            calculateStatistics();

            // Guardar las instancias originales
            saveOriginalInstances();
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    /**
     * Procesa una línea y la convierte en una instancia.
     *
     * @param values Valores de los atributos en formato String
     * @param delimiter Delimitador usado en el archivo
     */
    private void processInstanceLine(String[] values, String delimiter) {
        if (values.length != attributes.size()) {
            throw new IllegalArgumentException("El número de valores no coincide con el número de atributos");
        }

        double[] instanceValues = new double[values.length];

        for (int i = 0; i < values.length; i++) {
            Attribute attr = attributes.get(i);

            if (attr instanceof NumericAttribute) {
                // Atributo numérico
                try {
                    instanceValues[i] = Double.parseDouble(values[i].trim());

                    // Actualizar estadísticas del atributo
                    NumericAttribute numAttr = (NumericAttribute) attr;
                    numAttr.updateValue(instanceValues[i]);
                } catch (NumberFormatException e) {
                    // Si no se puede parsear como número, convertir el atributo a categórico
                    CategoricalAttribute catAttr = new CategoricalAttribute(attr.getName(), i, isClassAttribute(i));
                    attributes.set(i, catAttr);

                    // Procesar como categórico
                    instanceValues[i] = catAttr.addValue(values[i].trim());
                }
            } else {
                // Atributo categórico
                CategoricalAttribute catAttr = (CategoricalAttribute) attr;
                instanceValues[i] = catAttr.addValue(values[i].trim());
            }
        }

        // Añadir la instancia al dataset
        instances.add(new Instance(instanceValues));
    }

    /**
     * Guarda una copia de las instancias originales (sin preprocesar).
     */
    private void saveOriginalInstances() {
        originalInstances = new ArrayList<>(instances.size());
        for (Instance inst : instances) {
            double[] values = inst.getValues().clone();
            originalInstances.add(new Instance(values));
        }
    }

    /**
     * Calcula estadísticas para todos los atributos.
     */
    private void calculateStatistics() {
        // Actualizar estadísticas para atributos numéricos
        for (Attribute attr : attributes) {
            if (attr instanceof NumericAttribute) {
                NumericAttribute numAttr = (NumericAttribute) attr;
                numAttr.finalizeStats(instances.size());
            }
        }

        // Actualizar frecuencias para atributos categóricos
        for (Attribute attr : attributes) {
            if (attr instanceof CategoricalAttribute) {
                CategoricalAttribute catAttr = (CategoricalAttribute) attr;
                catAttr.updateFrequencies(instances);
            }
        }
    }

    /**
     * Verifica si un atributo es de clase.
     *
     * @param attributeIndex Índice del atributo
     * @return true si es un atributo de clase
     */
    public boolean isClassAttribute(int attributeIndex) {
        if (classAttributeIndices != null) {
            for (int classIdx : classAttributeIndices) {
                if (classIdx == attributeIndex) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Normaliza los atributos numéricos según el tipo especificado.
     *
     * @param type Tipo de preprocesamiento
     */
    public void normalize(PreprocessingType type) {
        // Si ya está en el tipo deseado, no hacer nada
        if (this.preprocessingType == type) {
            return;
        }

        // Si ya se aplicó un preprocesamiento, restaurar las instancias originales
        if (preprocessingType != PreprocessingType.NONE && originalInstances != null) {
            restoreOriginalInstances();
        }

        this.preprocessingType = type;

        // Si es NONE, simplemente salimos
        if (type == PreprocessingType.NONE) {
            return;
        }

        // Aplicar la normalización a cada instancia
        for (Instance inst : instances) {
            double[] values = inst.getValues();

            for (int i = 0; i < values.length; i++) {
                Attribute attr = attributes.get(i);

                // Solo normalizar atributos numéricos
                if (attr instanceof NumericAttribute && !isClassAttribute(i)) {
                    NumericAttribute numAttr = (NumericAttribute) attr;
                    values[i] = numAttr.normalize(values[i], type);
                }
            }
        }
    }

    /**
     * Restaura las instancias originales (sin preprocesamiento).
     */
    private void restoreOriginalInstances() {
        instances.clear();
        for (Instance inst : originalInstances) {
            double[] values = inst.getValues().clone();
            instances.add(new Instance(values));
        }
    }

    /**
     * Establece los pesos de los atributos numéricos para el cálculo de distancias.
     *
     * @param weights Array de pesos
     */
    public void setAttributeWeights(double[] weights) {
        if (weights.length != attributes.size()) {
            throw new IllegalArgumentException("El número de pesos debe coincidir con el número de atributos");
        }

        for (int i = 0; i < attributes.size(); i++) {
            Attribute attr = attributes.get(i);
            if (attr instanceof NumericAttribute) {
                ((NumericAttribute) attr).setWeight(weights[i]);
            }
        }
    }

    /**
     * Guarda el dataset en un archivo CSV.
     *
     * @param filename Nombre del archivo
     * @param saveNormalized Si se guardan los valores normalizados o los originales
     * @throws IOException Si hay errores de escritura
     */
    public void saveToFile(String filename, boolean saveNormalized) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // Escribir cabecera con nombres de atributos
            for (int i = 0; i < attributes.size(); i++) {
                bw.write(attributes.get(i).getName());
                if (i < attributes.size() - 1) {
                    bw.write(",");
                }
            }
            bw.newLine();

            // Determinar qué conjunto de instancias guardar
            List<Instance> instancesToSave = saveNormalized ? instances : originalInstances;
            if (instancesToSave == null) {
                instancesToSave = instances; // Si no hay originales, usar las actuales
            }

            // Escribir instancias
            for (Instance inst : instancesToSave) {
                double[] values = inst.getValues();

                for (int i = 0; i < values.length; i++) {
                    Attribute attr = attributes.get(i);

                    if (attr instanceof CategoricalAttribute) {
                        // Para atributos categóricos, escribir el valor original
                        CategoricalAttribute catAttr = (CategoricalAttribute) attr;
                        bw.write(catAttr.getValue((int) values[i]));
                    } else {
                        // Para atributos numéricos, escribir el valor numérico
                        bw.write(String.valueOf(values[i]));
                    }

                    if (i < values.length - 1) {
                        bw.write(",");
                    }
                }
                bw.newLine();
            }
        }
    }

    /**
     * Divide el dataset en conjuntos de entrenamiento y prueba.
     *
     * @param testPercentage Porcentaje para el conjunto de pruebas
     * @param randomize Si la división es aleatoria
     * @param seed Semilla para la generación aleatoria
     * @return Array con dos datasets: [0]=entrenamiento, [1]=prueba
     */
    public Dataset[] split(double testPercentage, boolean randomize, long seed) {
        if (testPercentage <= 0 || testPercentage >= 1) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 1");
        }

        int numInstances = instances.size();
        int numTestInstances = (int) Math.round(numInstances * testPercentage);

        // Crear datasets para entrenamiento y prueba
        Dataset trainDataset = new Dataset();
        Dataset testDataset = new Dataset();

        // Copiar metadatos (atributos)
        for (Attribute attr : attributes) {
            if (attr instanceof NumericAttribute) {
                NumericAttribute numAttr = (NumericAttribute) attr;
                NumericAttribute trainAttr = new NumericAttribute(numAttr.getName(), numAttr.getIndex());
                NumericAttribute testAttr = new NumericAttribute(numAttr.getName(), numAttr.getIndex());

                trainAttr.setMin(numAttr.getMin());
                trainAttr.setMax(numAttr.getMax());
                trainAttr.setMean(numAttr.getMean());
                trainAttr.setStdDev(numAttr.getStdDev());
                trainAttr.setWeight(numAttr.getWeight());

                testAttr.setMin(numAttr.getMin());
                testAttr.setMax(numAttr.getMax());
                testAttr.setMean(numAttr.getMean());
                testAttr.setStdDev(numAttr.getStdDev());
                testAttr.setWeight(numAttr.getWeight());

                trainDataset.attributes.add(trainAttr);
                testDataset.attributes.add(testAttr);
            } else if (attr instanceof CategoricalAttribute) {
                CategoricalAttribute catAttr = (CategoricalAttribute) attr;
                CategoricalAttribute trainAttr = new CategoricalAttribute(catAttr.getName(), catAttr.getIndex(), catAttr.isClass());
                CategoricalAttribute testAttr = new CategoricalAttribute(catAttr.getName(), catAttr.getIndex(), catAttr.isClass());

                // Copiar mapeos y valores únicos
                for (String value : catAttr.getUniqueValues()) {
                    int code = catAttr.getCode(value);
                    trainAttr.addValueWithCode(value, code);
                    testAttr.addValueWithCode(value, code);
                }

                trainDataset.attributes.add(trainAttr);
                testDataset.attributes.add(testAttr);
            }
        }

        // Copiar índices de clase
        trainDataset.classAttributeIndices = classAttributeIndices.clone();
        testDataset.classAttributeIndices = classAttributeIndices.clone();

        // Copiar tipo de preprocesamiento
        trainDataset.preprocessingType = this.preprocessingType;
        testDataset.preprocessingType = this.preprocessingType;

        // Determinar qué instancias van a cada conjunto
        boolean[] isTestInstance = new boolean[numInstances];

        if (randomize) {
            // Selección aleatoria de instancias para prueba
            Random rand = new Random(seed);
            Set<Integer> testIndices = new HashSet<>();

            while (testIndices.size() < numTestInstances) {
                int idx = rand.nextInt(numInstances);
                if (!testIndices.contains(idx)) {
                    testIndices.add(idx);
                    isTestInstance[idx] = true;
                }
            }
        } else {
            // Las últimas instancias van para prueba
            for (int i = numInstances - numTestInstances; i < numInstances; i++) {
                isTestInstance[i] = true;
            }
        }

        // Poblar los datasets de entrenamiento y prueba
        for (int i = 0; i < numInstances; i++) {
            Instance instance = instances.get(i);

            // Clonamos los valores para evitar referencias compartidas
            double[] values = instance.getValues().clone();
            Instance newInstance = new Instance(values);

            if (isTestInstance[i]) {
                testDataset.instances.add(newInstance);
            } else {
                trainDataset.instances.add(newInstance);
            }

            // También guardar copias originales si están disponibles
            if (originalInstances != null && i < originalInstances.size()) {
                Instance origInstance = originalInstances.get(i);
                double[] origValues = origInstance.getValues().clone();
                Instance newOrigInstance = new Instance(origValues);

                if (isTestInstance[i]) {
                    if (testDataset.originalInstances == null) {
                        testDataset.originalInstances = new ArrayList<>();
                    }
                    testDataset.originalInstances.add(newOrigInstance);
                } else {
                    if (trainDataset.originalInstances == null) {
                        trainDataset.originalInstances = new ArrayList<>();
                    }
                    trainDataset.originalInstances.add(newOrigInstance);
                }
            }
        }

        // Actualizar estadísticas para los nuevos datasets
        trainDataset.calculateStatistics();
        testDataset.calculateStatistics();

        return new Dataset[] { trainDataset, testDataset };
    }

    /**
     * Obtiene información textual sobre el dataset.
     *
     * @return String con información del dataset
     */
    public String getDatasetInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== Dataset Info ===").append("\n");
        sb.append("Número de instancias: ").append(instances.size()).append("\n");
        sb.append("Número de atributos: ").append(attributes.size()).append("\n");

        sb.append("Atributos de clase: ");
        for (int i = 0; i < classAttributeIndices.length; i++) {
            sb.append(attributes.get(classAttributeIndices[i]).getName());
            if (i < classAttributeIndices.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("\n");

        sb.append("Tipo de preprocesamiento: ").append(preprocessingType).append("\n");

        return sb.toString();
    }

    /**
     * Obtiene información textual sobre los atributos.
     *
     * @return String con información de los atributos
     */
    public String getAttributesInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== Attributes Info ===").append("\n");

        for (Attribute attr : attributes) {
            sb.append(attr.getStatistics()).append("\n\n");
        }

        return sb.toString();
    }

    /**
     * Obtiene una instancia del dataset por su índice.
     *
     * @param index Índice de la instancia
     * @return Instancia en la posición especificada
     */
    public Instance getInstance(int index) {
        return instances.get(index);
    }

    /**
     * Obtiene todas las instancias del dataset.
     *
     * @return Lista de instancias
     */
    public List<Instance> getInstances() {
        return instances;
    }

    /**
     * Obtiene un atributo del dataset por su índice.
     *
     * @param index Índice del atributo
     * @return Atributo en la posición especificada
     */
    public Attribute getAttribute(int index) {
        return attributes.get(index);
    }

    /**
     * Obtiene todos los atributos del dataset.
     *
     * @return Lista de atributos
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Obtiene el número de instancias en el dataset.
     *
     * @return Número de instancias
     */
    public int getNumInstances() {
        return instances.size();
    }

    /**
     * Obtiene el número de atributos en el dataset.
     *
     * @return Número de atributos
     */
    public int getNumAttributes() {
        return attributes.size();
    }

    /**
     * Obtiene los índices de los atributos de clase.
     *
     * @return Array con los índices de clase
     */
    public int[] getClassAttributeIndices() {
        return classAttributeIndices;
    }

    /**
     * Obtiene el tipo de preprocesamiento aplicado.
     *
     * @return Tipo de preprocesamiento
     */
    public PreprocessingType getPreprocessingType() {
        return preprocessingType;
    }

    /**
     * Obtiene el conjunto de valores de clase únicos para un determinado atributo de clase.
     *
     * @param classIndex Índice del atributo de clase
     * @return Set con los valores únicos de clase
     */
    public Set<String> getUniqueClassValues(int classIndex) {
        if (!isClassAttribute(classIndex)) {
            throw new IllegalArgumentException("El índice especificado no es un atributo de clase");
        }

        Attribute attr = attributes.get(classIndex);
        if (attr instanceof CategoricalAttribute) {
            return ((CategoricalAttribute) attr).getUniqueValues();
        } else {
            throw new IllegalStateException("El atributo de clase no es categórico");
        }
    }
}