package pe.edu.upeu.fitlab_final.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import pe.edu.upeu.fitlab_final.modelo.Entrenador;
import pe.edu.upeu.fitlab_final.modelo.Membresia;
import pe.edu.upeu.fitlab_final.utils.UtilTablas;
import java.time.LocalDate;
import java.util.List;

public class EntrenadorHorarioController {
    @FXML private Label lblNombre;
    @FXML private Label lblDni;
    @FXML private Label lblEspecialidad;
    @FXML private Label lblTurno;
    @FXML private Label lblCantidad;
    @FXML private TableView<Membresia> tableSocios;
    @FXML private TableColumn<Membresia, String> colSocio;
    @FXML private TableColumn<Membresia, String> colDni;
    @FXML private TableColumn<Membresia, String> colPlan;
    @FXML private TableColumn<Membresia, String> colDias;
    @FXML private TableColumn<Membresia, String> colTurno;
    @FXML private TableColumn<Membresia, String> colEstado;

    @FXML
    public void initialize() {
        colSocio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCliente().getNombre() + " " + c.getValue().getCliente().getApellidos()));
        colDni.setCellValueFactory(c -> c.getValue().getCliente().dniProperty());
        colPlan.setCellValueFactory(c -> c.getValue().getPlan().nombreProperty());
        colDias.setCellValueFactory(c -> new SimpleStringProperty(Membresia.describirDias(c.getValue().getTipoDias())));
        colTurno.setCellValueFactory(c -> new SimpleStringProperty(Entrenador.describirTurno(c.getValue().getHorario())));
        colEstado.setCellValueFactory(c -> {
            Membresia membresia = c.getValue();
            String estado = "ACTIVA";
            if (!membresia.isActiva()) estado = "INACTIVA";
            else if (membresia.getFechaVencimiento().isBefore(LocalDate.now())) estado = "VENCIDA";
            return new SimpleStringProperty(estado);
        });
        colEstado.setCellFactory(columna -> new TableCell<>() {
            private final Label etiqueta = new Label();

            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                setText(null);
                setAlignment(Pos.CENTER);
                if (empty || estado == null) {
                    setGraphic(null);
                } else {
                    etiqueta.setText(estado);
                    etiqueta.getStyleClass().setAll("detalle-badge", "detalle-" + estado.toLowerCase());
                    setGraphic(etiqueta);
                }
            }
        });
        UtilTablas.ajustarTexto(colSocio);
        UtilTablas.ajustarTexto(colPlan);
        UtilTablas.ajustarTexto(colDias);
        UtilTablas.ajustarColumnas(tableSocios);
    }

    public void setDatos(Entrenador entrenador, List<Membresia> asignados) {
        lblNombre.setText(entrenador.getNombre() + " " + entrenador.getApellidos());
        lblDni.setText(entrenador.getDni());
        lblEspecialidad.setText(entrenador.getEspecialidad());
        lblTurno.setText(Entrenador.describirTurno(entrenador.getTurno()));
        lblCantidad.setText(asignados.size() + (asignados.size() == 1 ? " socio" : " socios"));
        tableSocios.setItems(FXCollections.observableArrayList(asignados));
    }

    @FXML
    private void handleCerrar() {
        ((Stage) lblNombre.getScene().getWindow()).close();
    }
}
