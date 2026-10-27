package pe.edu.upeu.fitlab_final.modelo;

import javafx.beans.property.*;

public class Cliente extends Persona {
    private final IntegerProperty idcliente = new SimpleIntegerProperty();
    private final IntegerProperty edad = new SimpleIntegerProperty();
    private final StringProperty genero = new SimpleStringProperty();
    private final BooleanProperty estudiante = new SimpleBooleanProperty();
    private final StringProperty alergias = new SimpleStringProperty();
    private final StringProperty estadoMembresia = new SimpleStringProperty();

    public Cliente() {
    }

    public Cliente(String nombre, String apellidos, String dni, int idcliente, int edad, String genero, boolean estudiante, String alergias) {
        super(nombre, apellidos, dni);
        setIdcliente(idcliente);
        setEdad(edad);
        setGenero(genero);
        setEstudiante(estudiante);
        setAlergias(alergias);
    }

    public final int getIdcliente() {
        return idcliente.get();
    }

    public final void setIdcliente(int value) {
        idcliente.set(value);
    }

    public final IntegerProperty idclienteProperty() {
        return idcliente;
    }

    public final int getEdad() {
        return edad.get();
    }

    public final void setEdad(int value) {
        edad.set(value);
    }

    public final IntegerProperty edadProperty() {
        return edad;
    }

    public final String getGenero() {
        return genero.get();
    }

    public final void setGenero(String value) {
        genero.set(value);
    }

    public final StringProperty generoProperty() {
        return genero;
    }

    public final boolean isEstudiante() {
        return estudiante.get();
    }

    public final void setEstudiante(boolean value) {
        estudiante.set(value);
    }

    public final BooleanProperty estudianteProperty() {
        return estudiante;
    }

    public final String getAlergias() {
        return alergias.get();
    }

    public final void setAlergias(String value) {
        alergias.set(value);
    }

    public final StringProperty alergiasProperty() {
        return alergias;
    }

    public double calcularDescuento(double importe) {
        double descuento = 0;
        if (isEstudiante()) {
            descuento = importe * 0.10;
        }
        return descuento;
    }

    public final String getEstadoMembresia() {
        return estadoMembresia.get();
    }

    public final void setEstadoMembresia(String value) {
        estadoMembresia.set(value);
    }

    public final StringProperty estadoMembresiaProperty() {
        return estadoMembresia;
    }

    @Override
    public String toString() {
        String texto = super.toString();
        if (getEstadoMembresia() != null && !getEstadoMembresia().isBlank()) texto += " - " + getEstadoMembresia();
        return texto;
    }

}

