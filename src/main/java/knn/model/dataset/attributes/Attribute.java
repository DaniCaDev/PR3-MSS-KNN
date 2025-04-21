package knn.model.dataset.attributes;

/**
 * Clase abstracta que representa un atributo del dataset.
 * Sirve como base para los diferentes tipos de atributos (numéricos y categóricos).
 *
 * @author Tu Nombre
 * @version 1.0
 */
public abstract class Attribute {
    /** Nombre del atributo */
    private String name;

    /** Índice del atributo en el dataset */
    private int index;

    /**
     * Constructor que crea un atributo con el nombre e índice especificados.
     *
     * @param name Nombre del atributo
     * @param index Índice del atributo en el dataset
     */
    public Attribute(String name, int index) {
        this.name = name;
        this.index = index;
    }

    /**
     * Obtiene el nombre del atributo.
     *
     * @return Nombre del atributo
     */
    public String getName() {
        return name;
    }

    /**
     * Obtiene el índice del atributo.
     *
     * @return Índice del atributo
     */
    public int getIndex() {
        return index;
    }

    /**
     * Indica si el atributo es un atributo de clase.
     *
     * @return true si es un atributo de clase, false en caso contrario
     */
    public abstract boolean isClass();

    /**
     * Obtiene información estadística sobre el atributo.
     *
     * @return String con estadísticas del atributo
     */
    public abstract String getStatistics();
}