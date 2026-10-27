package pe.edu.upeu.fitlab_final.modelo;

import javafx.beans.property.*;

public class Persona {
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty apellidos = new SimpleStringProperty();
    private final StringProperty dni = new SimpleStringProperty();

    public Persona() {
    }

    public Persona(String nombre, String apellidos, String dni) {
        setNombre(nombre);
        setApellidos(apellidos);
        setDni(dni);
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

    public final String getApellidos() {
        return apellidos.get();
    }

    public final void setApellidos(String value) {
        apellidos.set(value);
    }

    public final StringProperty apellidosProperty() {
        return apellidos;
    }

    public final String getDni() {
        return dni.get();
    }

    public final void setDni(String value) {
        dni.set(value);
    }

    public final StringProperty dniProperty() {
        return dni;
    }

    @Override
    public String toString() {
        return getNombre() + " " + getApellidos() + " - " + getDni();
    }

}

