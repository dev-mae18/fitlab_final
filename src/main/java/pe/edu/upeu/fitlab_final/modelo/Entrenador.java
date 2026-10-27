package pe.edu.upeu.fitlab_final.modelo;

import javafx.beans.property.*;

public class Entrenador extends Persona {
    private final IntegerProperty identrenador = new SimpleIntegerProperty();
    private final StringProperty especialidad = new SimpleStringProperty();
    private final StringProperty turno = new SimpleStringProperty();

    public Entrenador() {
    }

    public Entrenador(String nombre, String apellidos, String dni, int identrenador, String especialidad, String turno) {
        super(nombre, apellidos, dni);
        setIdentrenador(identrenador);
        setEspecialidad(especialidad);
        setTurno(turno);
    }

    public final int getIdentrenador() {
        return identrenador.get();
    }

    public final void setIdentrenador(int value) {
        identrenador.set(value);
    }

    public final IntegerProperty identrenadorProperty() {
        return identrenador;
    }

    public final String getEspecialidad() {
        return especialidad.get();
    }

    public final void setEspecialidad(String value) {
        especialidad.set(value);
    }

    public final StringProperty especialidadProperty() {
        return especialidad;
    }

    public final String getTurno() {
        return turno.get();
    }

    public final void setTurno(String value) {
        turno.set(value);
    }

    public final StringProperty turnoProperty() {
        return turno;
    }

    public static String describirTurno(String turno) {
        if ("MANANA".equals(turno)) return "MAÑANA";
        return turno;
    }

    @Override
    public String toString() {
        return getNombre() + " " + getApellidos() + " - " + getDni() + " - " + describirTurno(getTurno());
    }

}

