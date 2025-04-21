package knn.model.dataset.attributes;

import knn.model.dataset.Instance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Clase que representa un atributo categórico (cualitativo) del dataset.
 * Gestiona el mapeo entre valores de texto y códigos numéricos,
 * y almacena información sobre frecuencias y valores únicos.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class CategoricalAttribute extends Attribute {
    /** Conjunto de valores únicos */
    private Set<String> uniqueValues;

    /** Mapeo de valores String a códigos numéricos */
    private Map<String, Integer> mapping;

    /** Mapeo inverso de códigos numéricos a valores String */
    private Map<Integer, String> reverseMapping;

    /** Frecuencias relativas de cada valor */
    private Map<String, Double> frequencies;

    /** Indica si es un atributo de clase */
    private boolean isClassAttribute;

    /**
     * Constructor que crea un atributo categórico.
     *
     * @param name Nombre del atributo
     * @param index Índice del atributo
     * @param isClass Si es un atributo de clase
     */
    public CategoricalAttribute(String name, int index, boolean isClass) {
        super(name, index);
        this.isClassAttribute = isClass;
        this.uniqueValues = new HashSet<>();
        this.mapping = new HashMap<>();
        this.reverseMapping = new HashMap<>();
        this.frequencies = new HashMap<>();
    }

    /**
     * Añade un nuevo valor categórico y devuelve su código numérico.
     * Si el valor ya existe, devuelve el código existente.
     *
     * @param value Valor categórico
     * @return Código numérico asignado
     */
    public int addValue(String value) {
        uniqueValues.add(value);

        if (!mapping.containsKey(value)) {
            int code = mapping.size();
            mapping.put(value, code);
            reverseMapping.put(code, value);
        }

        return mapping.get(value);
    }

    /**
     * Añade un valor categórico con un código específico.
     * Útil para sincronizar mapeos entre conjuntos de datos.
     *
     * @param value Valor categórico
     * @param code Código numérico
     */
    public void addValueWithCode(String value, int code) {
        uniqueValues.add(value);
        mapping.put(value, code);
        reverseMapping.put(code, value);
    }

    /**
     * Obtiene el valor categórico para un código numérico.
     *
     * @param code Código numérico
     * @return Valor categórico correspondiente
     */
    public String getValue(int code) {
        return reverseMapping.get(code);
    }

    /**
     * Obtiene el código numérico para un valor categórico.
     *
     * @param value Valor categórico
     * @return Código numérico correspondiente, o -1 si no existe
     */
    public int getCode(String value) {
        return mapping.getOrDefault(value, -1);
    }

    /**
     * Actualiza las frecuencias relativas de cada valor categórico.
     *
     * @param instances Lista de instancias para calcular frecuencias
     */
    public void updateFrequencies(List<Instance> instances) {
        // Resetear frecuencias
        frequencies.clear();

        // Contar ocurrencias
        Map<String, Integer> counts = new HashMap<>();
        for (Instance instance : instances) {
            int code = (int) instance.getValue(getIndex());
            String value = getValue(code);
            counts.put(value, counts.getOrDefault(value, 0) + 1);
        }

        // Calcular frecuencias relativas
        int total = instances.size();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            frequencies.put(entry.getKey(), (double) entry.getValue() / total);
        }
    }

    /**
     * Indica si este atributo es un atributo de clase.
     *
     * @return true si es un atributo de clase, false en caso contrario
     */
    @Override
    public boolean isClass() {
        return isClassAttribute;
    }

    /**
     * Obtiene información estadística sobre el atributo.
     *
     * @return String con estadísticas del atributo
     */
    @Override
    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("Atributo: ").append(getName()).append(" (Categórico");
        if (isClassAttribute) {
            sb.append(", Clase");
        }
        sb.append(")\n");
        sb.append("  Número de valores únicos: ").append(uniqueValues.size()).append("\n");
        sb.append("  Valores: ").append(uniqueValues).append("\n");
        sb.append("  Frecuencias: \n");
        for (Map.Entry<String, Double> entry : frequencies.entrySet()) {
            sb.append("    ").append(entry.getKey()).append(": ")
                    .append(String.format("%.2f", entry.getValue())).append("\n");
        }
        return sb.toString();
    }

    /**
     * Obtiene el conjunto de valores únicos.
     *
     * @return Set de valores únicos
     */
    public Set<String> getUniqueValues() {
        return uniqueValues;
    }

    /**
     * Obtiene las frecuencias relativas de cada valor.
     *
     * @return Mapa de frecuencias por valor
     */
    public Map<String, Double> getFrequencies() {
        return frequencies;
    }
}