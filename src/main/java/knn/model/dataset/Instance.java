package knn.model.dataset;

/**
 * Clase que representa una instancia (fila) del dataset.
 * Contiene los valores de los atributos para un caso concreto.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class Instance {
    /** Valores de los atributos */
    private final double[] values;

    /**
     * Constructor que crea una instancia con los valores especificados.
     *
     * @param values Array con los valores de los atributos
     */
    public Instance(double[] values) {
        this.values = values;
    }

    /**
     * Obtiene el valor de un atributo específico.
     *
     * @param index Índice del atributo
     * @return Valor del atributo
     */
    public double getValue(int index) {
        return values[index];
    }

    /**
     * Establece el valor de un atributo específico.
     *
     * @param index Índice del atributo
     * @param value Nuevo valor
     */
    public void setValue(int index, double value) {
        values[index] = value;
    }

    /**
     * Obtiene todos los valores de la instancia.
     *
     * @return Array con los valores de todos los atributos
     */
    public double[] getValues() {
        return values;
    }

    /**
     * Representación en texto de la instancia.
     *
     * @return Cadena con los valores de la instancia
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i]);
            if (i < values.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}