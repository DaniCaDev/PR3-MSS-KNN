package knn.model.dataset.attributes;

import knn.model.dataset.PreprocessingType;

/**
 * Clase que representa un atributo numérico del dataset.
 * Almacena estadísticas como valor mínimo, máximo, media y desviación típica.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class NumericAttribute extends Attribute {
    /** Valor mínimo */
    private double min = Double.MAX_VALUE;

    /** Valor máximo */
    private double max = Double.MIN_VALUE;

    /** Media */
    private double mean = 0.0;

    /** Desviación típica */
    private double stdDev = 0.0;

    /** Peso para el cálculo de distancias */
    private double weight = 1.0;

    /** Suma de valores (para cálculo de media) */
    private double sum = 0.0;

    /** Suma de cuadrados (para cálculo de desviación) */
    private double sumSquared = 0.0;

    /** Contador de valores actualizados */
    private int count = 0;

    /**
     * Constructor que crea un atributo numérico con el nombre e índice especificados.
     *
     * @param name Nombre del atributo
     * @param index Índice del atributo
     */
    public NumericAttribute(String name, int index) {
        super(name, index);
    }

    /**
     * Actualiza las estadísticas con un nuevo valor.
     *
     * @param value Valor a añadir
     */
    public void updateValue(double value) {
        if (value < min) {
            min = value;
        }
        if (value > max) {
            max = value;
        }

        sum += value;
        sumSquared += value * value;
        count++;
    }

    /**
     * Finaliza el cálculo de estadísticas después de procesar todos los valores.
     *
     * @param totalInstances Número total de instancias en el dataset
     */
    public void finalizeStats(int totalInstances) {
        if (count > 0) {
            mean = sum / count;

            if (count > 1) {
                // Fórmula eficiente para la varianza: E[X²] - (E[X])²
                double variance = (sumSquared / count) - (mean * mean);
                stdDev = Math.sqrt(variance * count / (count - 1)); // Corrección para muestras
            }
        }
    }

    /**
     * Normaliza un valor según el tipo de preprocesamiento especificado.
     *
     * @param value Valor a normalizar
     * @param type Tipo de preprocesamiento
     * @return Valor normalizado
     */
    public double normalize(double value, PreprocessingType type) {
        if (type == PreprocessingType.RANGE_0_1) {
            // Normalización en rango [0,1]
            if (max > min) {
                return (value - min) / (max - min);
            } else {
                return 0.5; // Si min=max, asignar 0.5
            }
        } else if (type == PreprocessingType.STANDARDIZE) {
            // Estandarización (z-score)
            if (stdDev > 0) {
                return (value - mean) / stdDev;
            } else {
                return 0; // Si stdDev=0, asignar 0
            }
        } else {
            return value; // Sin normalización
        }
    }

    /**
     * Indica si este atributo es un atributo de clase.
     * Los atributos numéricos no son de clase por defecto.
     *
     * @return false (los atributos numéricos no son de clase por defecto)
     */
    @Override
    public boolean isClass() {
        return false;
    }

    /**
     * Obtiene información estadística sobre el atributo.
     *
     * @return String con estadísticas del atributo
     */
    @Override
    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("Atributo: ").append(getName()).append(" (Numérico)\n");
        sb.append("  Mínimo: ").append(min).append("\n");
        sb.append("  Máximo: ").append(max).append("\n");
        sb.append("  Media: ").append(mean).append("\n");
        sb.append("  Desviación típica: ").append(stdDev).append("\n");
        sb.append("  Peso: ").append(weight);
        return sb.toString();
    }

    /**
     * Obtiene el valor mínimo.
     *
     * @return Valor mínimo
     */
    public double getMin() {
        return min;
    }

    /**
     * Establece el valor mínimo.
     *
     * @param min Nuevo valor mínimo
     */
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * Obtiene el valor máximo.
     *
     * @return Valor máximo
     */
    public double getMax() {
        return max;
    }

    /**
     * Establece el valor máximo.
     *
     * @param max Nuevo valor máximo
     */
    public void setMax(double max) {
        this.max = max;
    }

    /**
     * Obtiene la media.
     *
     * @return Media
     */
    public double getMean() {
        return mean;
    }

    /**
     * Establece la media.
     *
     * @param mean Nueva media
     */
    public void setMean(double mean) {
        this.mean = mean;
    }

    /**
     * Obtiene la desviación típica.
     *
     * @return Desviación típica
     */
    public double getStdDev() {
        return stdDev;
    }

    /**
     * Establece la desviación típica.
     *
     * @param stdDev Nueva desviación típica
     */
    public void setStdDev(double stdDev) {
        this.stdDev = stdDev;
    }

    /**
     * Obtiene el peso del atributo para cálculos de distancia.
     *
     * @return Peso del atributo
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Establece el peso del atributo para cálculos de distancia.
     *
     * @param weight Nuevo peso
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }
}