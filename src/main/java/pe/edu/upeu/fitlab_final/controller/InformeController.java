package pe.edu.upeu.fitlab_final.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import pe.edu.upeu.fitlab_final.implementacion.MembresiaDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IMembresiaDao;
import pe.edu.upeu.fitlab_final.modelo.Membresia;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class InformeController {
    @FXML private Label lblActivas;
    @FXML private Label lblInactivas;
    @FXML private Label lblPorVencer;
    @FXML private Label lblOcupacionMaxima;
    @FXML private Label lblTurnoMaximo;
    @FXML private Label lblActualizado;
    @FXML private GridPane gridAforo;
    private final IMembresiaDao membresiaDao = new MembresiaDaoImpl();
    private static final int AFORO_MAXIMO = 50;
    private static final String[] DIAS = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};
    private static final String[] NOMBRES_DIAS = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
    private static final String[] TURNOS = {"MANANA", "TARDE", "NOCHE"};
    private static final String[] NOMBRES_TURNOS = {"Mañana", "Tarde", "Noche"};

    @FXML public void initialize() { cargarDatos(); }

    private void cargarDatos() {
        List<Membresia> membresias = membresiaDao.listAll();
        List<Membresia> activas = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        int porVencer = 0;
        for (Membresia membresia : membresias) {
            if (membresia.isActiva()) {
                activas.add(membresia);
                if (!membresia.getFechaVencimiento().isBefore(hoy) && !membresia.getFechaVencimiento().isAfter(hoy.plusDays(3))) porVencer++;
            }
        }
        lblActivas.setText(String.valueOf(activas.size()));
        lblInactivas.setText(String.valueOf(membresias.size() - activas.size()));
        lblPorVencer.setText(String.valueOf(porVencer));
        cargarAforo(activas);
        lblActualizado.setText("Actualizado a las " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private void cargarAforo(List<Membresia> activas) {
        gridAforo.getChildren().clear();
        agregarEncabezado("Día", 0);
        for (int turno = 0; turno < TURNOS.length; turno++) agregarEncabezado(NOMBRES_TURNOS[turno], turno + 1);
        long mayor = 0;
        String turnoMaximo = AFORO_MAXIMO + " plazas disponibles por turno";
        for (int dia = 0; dia < DIAS.length; dia++) {
            Label nombre = new Label(NOMBRES_DIAS[dia]);
            nombre.getStyleClass().add("aforo-dia");
            gridAforo.add(nombre, 0, dia + 1);
            for (int turno = 0; turno < TURNOS.length; turno++) {
                String diaActual = DIAS[dia], turnoActual = TURNOS[turno];
                int cantidad = 0;
                for (Membresia membresia : activas) {
                    if (membresia.getDiasAsistencia().contains(diaActual) && turnoActual.equals(membresia.getHorario())) cantidad++;
                }
                String descripcion = NOMBRES_DIAS[dia] + " · " + NOMBRES_TURNOS[turno];
                VBox celda = crearCeldaAforo(cantidad, descripcion);
                celda.setId("aforo-" + diaActual + "-" + turnoActual);
                gridAforo.add(celda, turno + 1, dia + 1);
                if (cantidad > mayor) {
                    mayor = cantidad;
                    turnoMaximo = descripcion + " · " + cantidad + " / " + AFORO_MAXIMO;
                }
            }
        }
        lblOcupacionMaxima.setText(Math.round(mayor * 100.0 / AFORO_MAXIMO) + " %");
        lblTurnoMaximo.setText(turnoMaximo);
    }

    private void agregarEncabezado(String texto, int columna) {
        Label encabezado = new Label(texto);
        encabezado.getStyleClass().add("aforo-header");
        gridAforo.add(encabezado, columna, 0);
    }

    private VBox crearCeldaAforo(long cantidad, String descripcion) {
        double ocupacion = cantidad / (double) AFORO_MAXIMO;
        String estado = "disponible";
        if (ocupacion >= 1) {
            estado = "lleno";
        } else if (ocupacion >= 0.75) {
            estado = "alto";
        } else if (ocupacion >= 0.50) {
            estado = "medio";
        }
        Label valor = new Label(cantidad + " / " + AFORO_MAXIMO);
        valor.getStyleClass().add("aforo-valor");
        ProgressBar progreso = new ProgressBar(Math.min(1, ocupacion));
        progreso.getStyleClass().add("aforo-progress");
        VBox celda = new VBox(valor, progreso);
        celda.getStyleClass().addAll("aforo-cell", "aforo-" + estado);
        String detalle = descripcion + ": " + cantidad + " plazas ocupadas y " + Math.max(0, AFORO_MAXIMO - cantidad) + " disponibles.";
        celda.setAccessibleText(detalle);
        return celda;
    }
}
