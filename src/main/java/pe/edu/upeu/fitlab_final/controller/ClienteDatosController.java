package pe.edu.upeu.fitlab_final.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pe.edu.upeu.fitlab_final.modelo.Cliente;
import pe.edu.upeu.fitlab_final.modelo.Comprobante;
import pe.edu.upeu.fitlab_final.modelo.Entrenador;
import pe.edu.upeu.fitlab_final.modelo.Membresia;
import pe.edu.upeu.fitlab_final.utils.UtilTablas;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClienteDatosController {
    @FXML private Label lblNombre;
    @FXML private Label lblNombres;
    @FXML private Label lblApellidos;
    @FXML private Label lblDni;
    @FXML private Label lblEdad;
    @FXML private Label lblGenero;
    @FXML private Label lblEstudiante;
    @FXML private Label lblAlergias;
    @FXML private Label lblEstado;
    @FXML private Label lblSinMembresia;
    @FXML private VBox boxMembresia;
    @FXML private Label lblPlan;
    @FXML private Label lblPrecio;
    @FXML private Label lblMeses;
    @FXML private Label lblHorario;
    @FXML private Label lblDias;
    @FXML private Label lblEntrenador;
    @FXML private Label lblInicio;
    @FXML private Label lblVencimiento;
    @FXML private Label lblRenovaciones;
    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuento;
    @FXML private Label lblTotal;
    @FXML private Label lblComprobantes;
    @FXML private TableView<Comprobante> tableComprobantes;
    @FXML private TableColumn<Comprobante, String> colCodigo;
    @FXML private TableColumn<Comprobante, String> colTipo;
    @FXML private TableColumn<Comprobante, String> colEstado;
    @FXML private TableColumn<Comprobante, String> colFecha;
    @FXML private TableColumn<Comprobante, Number> colTotal;
    @FXML private Button btnCerrar;

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(c -> c.getValue().codigoProperty());
        colTipo.setCellValueFactory(c -> c.getValue().tipoProperty());
        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());
        colFecha.setCellValueFactory(c -> c.getValue().fechaProperty());
        colTotal.setCellValueFactory(c -> c.getValue().totalProperty());
        colTotal.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("S/ %.2f", item.doubleValue()));
            }
        });
        UtilTablas.ajustarTexto(colTipo);
        UtilTablas.ajustarColumnas(tableComprobantes);
    }

    public void setDatos(Cliente cliente, Membresia membresia, List<Comprobante> comprobantes) {
        lblNombre.setText(cliente.getNombre() + " " + cliente.getApellidos());
        lblNombres.setText(cliente.getNombre());
        lblApellidos.setText(cliente.getApellidos());
        lblDni.setText(cliente.getDni());
        lblEdad.setText(cliente.getEdad() + " años");
        lblGenero.setText(cliente.getGenero());
        lblEstudiante.setText(cliente.isEstudiante() ? "Sí · Descuento del 10 %" : "No");
        lblAlergias.setText(cliente.getAlergias() == null || cliente.getAlergias().isBlank() ? "Sin alergias registradas" : cliente.getAlergias());

        boxMembresia.setVisible(membresia != null);
        boxMembresia.setManaged(membresia != null);
        lblSinMembresia.setVisible(membresia == null);
        lblSinMembresia.setManaged(membresia == null);
        lblEstado.getStyleClass().removeAll("detalle-activa", "detalle-inactiva", "detalle-vencida");
        if (membresia == null) {
            lblEstado.setText("Sin membresía");
            lblEstado.getStyleClass().add("detalle-inactiva");
        } else {
            String estado = "ACTIVA";
            String clase = "detalle-activa";
            if (!membresia.isActiva()) {
                estado = "INACTIVA";
                clase = "detalle-inactiva";
            } else if (membresia.getFechaVencimiento().isBefore(LocalDate.now())) {
                estado = "VENCIDA";
                clase = "detalle-vencida";
            }
            lblEstado.setText(estado);
            lblEstado.getStyleClass().add(clase);
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            double subtotal = membresia.calcularSubtotal();
            double descuento = cliente.calcularDescuento(subtotal);
            lblPlan.setText(membresia.getPlan().getNombre());
            lblPrecio.setText(String.format("S/ %.2f", membresia.getPrecioMensual()));
            lblMeses.setText(membresia.getMeses() == 1 ? "1 mes" : membresia.getMeses() + " meses");
            lblHorario.setText(Entrenador.describirTurno(membresia.getHorario()));
            lblDias.setText(Membresia.describirDias(membresia.getTipoDias()));
            Entrenador entrenador = membresia.getEntrenador();
            lblEntrenador.setText(entrenador == null ? "Sin entrenador" : entrenador.getNombre() + " " + entrenador.getApellidos() + " · " + entrenador.getDni());
            lblInicio.setText(membresia.getFechaInicio().format(formato));
            lblVencimiento.setText(membresia.getFechaVencimiento().format(formato));
            lblRenovaciones.setText(String.valueOf(membresia.getRenovaciones()));
            lblSubtotal.setText(String.format("S/ %.2f", subtotal));
            lblDescuento.setText(String.format("S/ %.2f", descuento));
            lblTotal.setText(String.format("S/ %.2f", subtotal - descuento));
        }
        lblComprobantes.setText(comprobantes.size() + (comprobantes.size() == 1 ? " comprobante registrado" : " comprobantes registrados"));
        tableComprobantes.setItems(FXCollections.observableArrayList(comprobantes));
    }

    @FXML
    private void handleCerrar() {
        ((Stage) btnCerrar.getScene().getWindow()).close();
    }
}
