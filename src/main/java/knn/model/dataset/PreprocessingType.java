package knn.model.dataset;

/**
 * Enumeración que define los diferentes tipos de preprocesamiento
 * que se pueden aplicar a los atributos numéricos del dataset.
 *
 * @author Daniel Alejandro Álvarez Casablanca
 * @version 1.0
 */
public enum PreprocessingType {
    /**
     * Sin preprocesamiento (datos crudos)
     */
    NONE,

    /**
     * Normalización en rango [0,1]
     * Fórmula: x̂ = (x - min) / (max - min)
     */
    RANGE_0_1,

    /**
     * Estandarización (z-score)
     * Fórmula: x̂ = (x - μ) / σ
     * donde μ es la media y σ es la desviación estándar
     */
    STANDARDIZE
}