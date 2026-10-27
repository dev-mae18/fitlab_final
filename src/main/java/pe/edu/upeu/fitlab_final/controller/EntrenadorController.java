package pe.edu.upeu.fitlab_final.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.fitlab_final.implementacion.EntrenadorDaoImpl;
import pe.edu.upeu.fitlab_final.implementacion.MembresiaDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IEntrenadorDao;
import pe.edu.upeu.fitlab_final.interfaces.IMembresiaDao;
import pe.edu.upeu.fitlab_final.modelo.Entrenador;
import pe.edu.upeu.fitlab_final.modelo.Membresia;
import pe.edu.upeu.fitlab_final.utils.UtilAlerts;
import java.io.IOException;
import java.time.LocalDate;

public class EntrenadorController {
    @FXML private TableView<Entrenador> tableEntrenador;
    @FXML private TableColumn<Entrenador, Number> colNumero;
    @FXML private TableColumn<Entrenador, String> colNombre;
    @FXML private TableColumn<Entrenador, String> colApellidos;
    @FXML private TableColumn<Entrenador, String> colDni;
    @FXML private TableColumn<Entrenador, String> colEspecialidad;
    @FXML private TableColumn<Entrenador, String> colTurno;
    @FXML private TextField txtBuscar;
    private final IEntrenadorDao entrenadorDao = new EntrenadorDaoImpl();
    private final IMembresiaDao membresiaDao = new MembresiaDaoImpl();
    private ObservableList<Entrenador> listaEntrenadores;

    @FXML
    public void initialize() {
        colNumero.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else setText(String.valueOf(getIndex() + 1));
            }
        });
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colApellidos.setCellValueFactory(c -> c.getValue().apellidosProperty());
        colDni.setCellValueFactory(c -> c.getValue().dniProperty());
        colEspecialidad.setCellValueFactory(c -> c.getValue().especialidadProperty());
        colTurno.setCellValueFactory(c -> new SimpleStringProperty(Entrenador.describirTurno(c.getValue().getTurno())));
        cargarDatos();
    }

    private void cargarDatos() {
        listaEntrenadores = FXCollections.observableArrayList(entrenadorDao.listAll());
        handleSearch();
    }

    @FXML
    private void handleSearch() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        ObservableList<Entrenador> filtrados = FXCollections.observableArrayList();
        for (Entrenador entrenador : listaEntrenadores) {
            if (entrenador.getDni().contains(filtro) || (entrenador.getNombre() + " " + entrenador.getApellidos()).toLowerCase().contains(filtro)) {
                filtrados.add(entrenador);
            }
        }
        tableEntrenador.setItems(filtrados);
    }

    @FXML private void handleNew() { abrirFormulario(null); }

    @FXML
    private void handleVerHorario() {
        Entrenador seleccionado = seleccionar();
        if (seleccionado == null) return;
        Entrenador entrenador = entrenadorDao.searchById(seleccionado.getIdentrenador());
        if (entrenador == null) {
            UtilAlerts.mostrarAlerts("Entrenador no disponible", "No se pudo recuperar el entrenador seleccionado.", "WARNING").showAndWait();
            cargarDatos();
            return;
        }
        String detalle = "DATOS DEL ENTRENADOR\n\n";
        detalle += "Entrenador: " + entrenador.getNombre() + " " + entrenador.getApellidos() + "\n";
        detalle += "DNI: " + entrenador.getDni() + "\n";
        detalle += "Especialidad: " + entrenador.getEspecialidad() + "\n";
        detalle += "Turno: " + Entrenador.describirTurno(entrenador.getTurno()) + "\n\n";
        detalle += "SOCIOS ASIGNADOS\n\n";
        int cantidad = 0;
        for (Membresia membresia : membresiaDao.listAll()) {
            if (membresia.getEntrenador() != null && membresia.getEntrenador().getIdentrenador() == entrenador.getIdentrenador()) {
                String estado = "ACTIVA";
                if (!membresia.isActiva()) estado = "INACTIVA";
                else if (membresia.getFechaVencimiento().isBefore(LocalDate.now())) estado = "VENCIDA";
                detalle += membresia.getCliente().getNombre() + " " + membresia.getCliente().getApellidos() + " - " + membresia.getCliente().getDni() + "\n";
                detalle += "Plan: " + membresia.getPlan().getNombre() + "\n";
                detalle += "Días: " + Membresia.describirDias(membresia.getTipoDias()) + "\n";
                detalle += "Turno: " + Entrenador.describirTurno(membresia.getHorario()) + "\n";
                detalle += "Estado: " + estado + "\n\n";
                cantidad++;
            }
        }
        if (cantidad == 0) detalle += "El entrenador no tiene socios asignados.";
        Alert alerta = UtilAlerts.mostrarAlerts("Horario de " + entrenador.getNombre() + " " + entrenador.getApellidos(), detalle, "INFORMATION");
        alerta.getDialogPane().setPrefWidth(620);
        alerta.setResizable(true);
        alerta.showAndWait();
    }

    @FXML
    private void handleEdit() {
        Entrenador seleccionado = seleccionar();
        if (seleccionado == null) return;
        Entrenador entrenador = entrenadorDao.searchById(seleccionado.getIdentrenador());
        if (entrenador == null) {
            UtilAlerts.mostrarAlerts("Entrenador no disponible", "No se pudo recuperar el entrenador seleccionado. La lista se actualizará.", "WARNING").showAndWait();
            cargarDatos();
            return;
        }
        abrirFormulario(entrenador);
    }

    @FXML
    private void handleDelete() {
        Entrenador seleccionado = seleccionar();
        if (seleccionado == null) return;
        if (UtilAlerts.mostrarAlerts("Confirmar eliminación", "¿Eliminar a " + seleccionado.getNombre() + " " + seleccionado.getApellidos() + "?", "CONFIRMATION").showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        if (!entrenadorDao.delete(seleccionado)) {
            UtilAlerts.mostrarAlerts("No se pudo eliminar", "El registro puede tener membresías o comprobantes asociados.", "WARNING").showAndWait();
        }
        cargarDatos();
    }

    private Entrenador seleccionar() {
        Entrenador seleccionado = tableEntrenador.getSelectionModel().getSelectedItem();
        if (seleccionado == null) UtilAlerts.mostrarAlerts("Selección requerida", "Seleccione un registro de la tabla.", "WARNING").showAndWait();
        return seleccionado;
    }

    private void abrirFormulario(Entrenador entidad) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pe/edu/upeu/fitlab_final/views/entrenador-form.fxml"));
            Parent root = loader.load();
            EntrenadorFormController controller = loader.getController();
            controller.setEntrenador(entidad);
            Stage ventana = new Stage();
            ventana.setTitle(entidad == null ? "Nuevo entrenador" : "Editar entrenador");
            ventana.initOwner(tableEntrenador.getScene().getWindow());
            ventana.initModality(Modality.WINDOW_MODAL);
            ventana.setScene(new Scene(root));
            ventana.setResizable(false);
            ventana.showAndWait();
            if (controller.isSaved()) {
                Entrenador entrenadorProcesado = controller.getEntrenador();
                boolean guardado;
                if (entidad == null) {
                    guardado = entrenadorDao.insert(entrenadorProcesado);
                } else {
                    guardado = entrenadorDao.update(entrenadorProcesado);
                }
                if (!guardado) UtilAlerts.mostrarAlerts("Error al guardar", "No se pudo guardar el registro. Compruebe que el DNI no esté registrado.", "ERROR").showAndWait();
                cargarDatos();
            }
        } catch (IOException e) {
            UtilAlerts.mostrarAlerts("Error", "No se pudo abrir el formulario: " + e.getMessage(), "ERROR").showAndWait();
        }
    }
}
