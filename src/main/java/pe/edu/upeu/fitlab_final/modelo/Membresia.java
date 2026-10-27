package pe.edu.upeu.fitlab_final.modelo;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Membresia {
    private final IntegerProperty idmembresia = new SimpleIntegerProperty();
    private final ObjectProperty<Cliente> cliente = new SimpleObjectProperty<>();
    private final ObjectProperty<Plan> plan = new SimpleObjectProperty<>();
    private final ObjectProperty<Entrenador> entrenador = new SimpleObjectProperty<>();
    private final StringProperty horario = new SimpleStringProperty();
    private final StringProperty tipoDias = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> fechaInicio = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> fechaVencimiento = new SimpleObjectProperty<>();
    private final BooleanProperty activa = new SimpleBooleanProperty();
    private final IntegerProperty renovaciones = new SimpleIntegerProperty();
    private final IntegerProperty meses = new SimpleIntegerProperty(1);
    private final IntegerProperty idUsuario = new SimpleIntegerProperty();

    public Membresia() {
    }

    public Membresia(int idmembresia, Cliente cliente, Plan plan, Entrenador entrenador, String horario, String tipoDias, LocalDate fechaInicio, LocalDate fechaVencimiento, boolean activa, int renovaciones, int meses, int idUsuario) {
        setIdmembresia(idmembresia);
        setCliente(cliente);
        setPlan(plan);
        setEntrenador(entrenador);
        setHorario(horario);
        setTipoDias(tipoDias);
        setFechaInicio(fechaInicio);
        setFechaVencimiento(fechaVencimiento);
        setActiva(activa);
        setRenovaciones(renovaciones);
        setMeses(meses);
        setIdUsuario(idUsuario);
    }

    public final int getIdmembresia() {
        return idmembresia.get();
    }

    public final void setIdmembresia(int value) {
        idmembresia.set(value);
    }

    public final IntegerProperty idmembresiaProperty() {
        return idmembresia;
    }

    public final Cliente getCliente() {
        return cliente.get();
    }

    public final void setCliente(Cliente value) {
        cliente.set(value);
    }

    public final ObjectProperty<Cliente> clienteProperty() {
        return cliente;
    }

    public final Plan getPlan() {
        return plan.get();
    }

    public final void setPlan(Plan value) {
        plan.set(value);
    }

    public final ObjectProperty<Plan> planProperty() {
        return plan;
    }

    public final Entrenador getEntrenador() {
        return entrenador.get();
    }

    public final void setEntrenador(Entrenador value) {
        entrenador.set(value);
    }

    public final ObjectProperty<Entrenador> entrenadorProperty() {
        return entrenador;
    }

    public final String getHorario() {
        return horario.get();
    }

    public final void setHorario(String value) {
        horario.set(value);
    }

    public final StringProperty horarioProperty() {
        return horario;
    }

    public final String getTipoDias() {
        return tipoDias.get();
    }

    public final void setTipoDias(String value) {
        tipoDias.set(value);
    }

    public final StringProperty tipoDiasProperty() {
        return tipoDias;
    }

    public List<String> getDiasAsistencia() {
        String dias = getTipoDias();
        List<String> lista = new ArrayList<>();
        if ("TODOS".equals(dias)) {
            lista.add("LUNES");
            lista.add("MARTES");
            lista.add("MIERCOLES");
            lista.add("JUEVES");
            lista.add("VIERNES");
            lista.add("SABADO");
            lista.add("DOMINGO");
        }
        if ("LUNES-MIERCOLES-VIERNES".equals(dias) || "MARTES-JUEVES-SABADO".equals(dias)) {
            String[] separados = dias.split("-");
            for (String dia : separados) {
                lista.add(dia);
            }
        }
        return lista;
    }

    public static String describirDias(String dias) {
        if ("TODOS".equals(dias)) {
            return "Todos los días";
        }
        if ("LUNES-MIERCOLES-VIERNES".equals(dias)) {
            return "Lunes, miércoles y viernes";
        }
        if ("MARTES-JUEVES-SABADO".equals(dias)) {
            return "Martes, jueves y sábado";
        }
        if (dias == null) return "";
        return dias;
    }

    public final LocalDate getFechaInicio() {
        return fechaInicio.get();
    }

    public final void setFechaInicio(LocalDate value) {
        fechaInicio.set(value);
    }

    public final ObjectProperty<LocalDate> fechaInicioProperty() {
        return fechaInicio;
    }

    public final LocalDate getFechaVencimiento() {
        return fechaVencimiento.get();
    }

    public final void setFechaVencimiento(LocalDate value) {
        fechaVencimiento.set(value);
    }

    public final ObjectProperty<LocalDate> fechaVencimientoProperty() {
        return fechaVencimiento;
    }

    public final boolean isActiva() {
        return activa.get();
    }

    public final void setActiva(boolean value) {
        activa.set(value);
    }

    public final BooleanProperty activaProperty() {
        return activa;
    }

    public final int getRenovaciones() {
        return renovaciones.get();
    }

    public final void setRenovaciones(int value) {
        renovaciones.set(value);
    }

    public final IntegerProperty renovacionesProperty() {
        return renovaciones;
    }

    public final int getMeses() {
        return meses.get();
    }

    public final void setMeses(int value) {
        meses.set(value);
    }

    public final IntegerProperty mesesProperty() {
        return meses;
    }

    public long getDiasRestantes() {
        if (getFechaVencimiento() == null) return 0;
        long dias = getFechaVencimiento().toEpochDay() - LocalDate.now().toEpochDay();
        if (dias < 0) dias = 0;
        return dias;
    }

    public double calcularImporteMigracion(Plan nuevoPlan) {
        if (getPlan() == null || nuevoPlan == null || getPlan().getDuracionDias() <= 0) return 0;
        double diferencia = nuevoPlan.getPrecio() - getPlan().getPrecio();
        if (diferencia < 0) diferencia = 0;
        return diferencia * getDiasRestantes() / getPlan().getDuracionDias();
    }

    public final int getIdUsuario() {
        return idUsuario.get();
    }

    public final void setIdUsuario(int value) {
        idUsuario.set(value);
    }

    public final IntegerProperty idUsuarioProperty() {
        return idUsuario;
    }

}

