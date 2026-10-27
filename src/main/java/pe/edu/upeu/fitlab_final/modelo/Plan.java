package pe.edu.upeu.fitlab_final.modelo;

import javafx.beans.property.*;

public class Plan {
    private final IntegerProperty idplan = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final DoubleProperty precio = new SimpleDoubleProperty();
    private final IntegerProperty duracionDias = new SimpleIntegerProperty();

    public Plan() {
    }

    public Plan(int idplan, String nombre, double precio, int duracionDias) {
        setIdplan(idplan);
        setNombre(nombre);
        setPrecio(precio);
        setDuracionDias(duracionDias);
    }

    public final int getIdplan() {
        return idplan.get();
    }

    public final void setIdplan(int value) {
        idplan.set(value);
    }

    public final IntegerProperty idplanProperty() {
        return idplan;
    }

    public final String getNombre() {
        return nombre.get();
    }

    public final void setNombre(String value) {
        nombre.set(value);
    }

    public final StringProperty nombreProperty() {
        return nombre;
    }

    public final double getPrecio() {
        return precio.get();
    }

    public final void setPrecio(double value) {
        precio.set(value);
    }

    public final DoubleProperty precioProperty() {
        return precio;
    }

    public final int getDuracionDias() {
        return duracionDias.get();
    }

    public final void setDuracionDias(int value) {
        duracionDias.set(value);
    }

    public final IntegerProperty duracionDiasProperty() {
        return duracionDias;
    }

    @Override
    public String toString() {
        return getNombre() + " - S/ " + String.format("%.2f", getPrecio());
    }
}

