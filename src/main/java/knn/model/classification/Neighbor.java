package knn.model.classification;

import knn.model.dataset.Instance;

/**
 * Clase que representa un vecino para el algoritmo k-nn.
 * Almacena una instancia junto con su distancia al caso a clasificar
 * y su índice en el dataset original.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class Neighbor implements Comparable<Neighbor> {
    /** Instancia vecina */
    private Instance instance;

    /** Distancia al caso a clasificar */
    private double distance;

    /** Índice en el dataset original */
    private int index;

    /**
     * Constructor que crea un vecino con los datos especificados.
     *
     * @param instance Instancia vecina
     * @param distance Distancia
     * @param index Índice original
     */
    public Neighbor(Instance instance, double distance, int index) {
        this.instance = instance;
        this.distance = distance;
        this.index = index;
    }

    /**
     * Obtiene la instancia vecina.
     *
     * @return Instancia
     */
    public Instance getInstance() {
        return instance;
    }

    /**
     * Obtiene la distancia al caso a clasificar.
     *
     * @return Distancia
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Obtiene el índice original en el dataset.
     *
     * @return Índice
     */
    public int getIndex() {
        return index;
    }

    /**
     * Compara este vecino con otro para ordenarlos por distancia.
     *
     * @param other Otro vecino
     * @return Resultado de la comparación
     */
    @Override
    public int compareTo(Neighbor other) {
        return Double.compare(this.distance, other.distance);
    }

    /**
     * Representación en texto del vecino.
     *
     * @return Cadena con información del vecino
     */
    @Override
    public String toString() {
        return String.format("Neighbor[distance=%.4f, index=%d]", distance, index);
    }
}