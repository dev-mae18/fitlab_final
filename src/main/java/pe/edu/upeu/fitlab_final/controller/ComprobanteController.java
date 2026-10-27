package pe.edu.upeu.fitlab_final.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.fitlab_final.implementacion.ComprobanteDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IComprobanteDao;
import pe.edu.upeu.fitlab_final.modelo.Comprobante;
import pe.edu.upeu.fitlab_final.utils.UtilAlerts;

public class ComprobanteController {
    @FXML private TableView<Comprobante> tableComprobante;
    @FXML private TableColumn<Comprobante, String> colCodigo;
    @FXML private TableColumn<Comprobante, String> colCliente;
    @FXML private TableColumn<Comprobante, String> colDni;
    @FXML private TableColumn<Comprobante, String> colTipo;
    @FXML private TableColumn<Comprobante, String> colEstado;
    @FXML private TableColumn<Comprobante, Number> colSubtotal;
    @FXML private TableColumn<Comprobante, Number> colDescuento;
    @FXML private TableColumn<Comprobante, Number> colTotal;
    @FXML private TableColumn<Comprobante, String> colFecha;
    @FXML private TextField txtBuscar;
    private final IComprobanteDao comprobanteDao = new ComprobanteDaoImpl();

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(c -> c.getValue().codigoProperty());
        colCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCliente().getNombre() + " " + c.getValue().getCliente().getApellidos()));
        colDni.setCellValueFactory(c -> c.getValue().getCliente().dniProperty());
        colTipo.setCellValueFactory(c -> c.getValue().tipoProperty());
        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());
        colEstado.setCellFactory(column -> new TableCell<>() {
            private final Label estado = new Label();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                boolean anulado = "ANULADO".equalsIgnoreCase(item);
                estado.setText(anulado ? "Anulado" : "Enviado");
                estado.getStyleClass().setAll("label", "comprobante-estado", anulado ? "comprobante-anulado" : "comprobante-enviado");
                setGraphic(estado);
            }
        });
        colSubtotal.setCellValueFactory(c -> c.getValue().subtotalProperty());
        colDescuento.setCellValueFactory(c -> c.getValue().descuentoProperty());
        colTotal.setCellValueFactory(c -> c.getValue().totalProperty());
        colFecha.setCellValueFactory(c -> c.getValue().fechaProperty());
        configurarMoneda(colSubtotal);
        configurarMoneda(colDescuento);
        configurarMoneda(colTotal);
        cargarDatos();
    }

    private void configurarMoneda(TableColumn<Comprobante, Number> columna) {
        columna.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("S/ %.2f", item.doubleValue()));
            }
        });
    }

    @FXML
    private void cargarDatos() {
        ObservableList<Comprobante> listaComprobantes = FXCollections.observableArrayList(comprobanteDao.listAll());
        tableComprobante.setItems(listaComprobantes);
        txtBuscar.clear();
    }

    @FXML
    private void handleSearch() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isBlank()) { cargarDatos(); return; }
        if (filtro.matches("[0-9]{8}")) {
            tableComprobante.setItems(FXCollections.observableArrayList(comprobanteDao.searchByDni(filtro)));
        } else {
            Comprobante comprobante = comprobanteDao.searchByCodigo(filtro);
            if (comprobante == null) {
                tableComprobante.setItems(FXCollections.observableArrayList());
            } else {
                tableComprobante.setItems(FXCollections.observableArrayList(comprobante));
            }
        }
    }

    @FXML
    private void handleDetalle() {
        Comprobante c = tableComprobante.getSelectionModel().getSelectedItem();
        if (c == null) {
            UtilAlerts.mostrarAlerts("Selección requerida", "Seleccione un comprobante de la tabla.", "WARNING").showAndWait();
            return;
        }
        String estado = "ANULADO".equalsIgnoreCase(c.getEstado()) ? "Anulado" : "Enviado";
        String detalle = String.format("FITLAB · %s%n%nSocio: %s %s%nDNI: %s%nOperación: %s%nEstado: %s%nFecha: %s%n%nSubtotal: S/ %.2f%nDescuento: S/ %.2f%nTotal: S/ %.2f", c.getCodigo(), c.getCliente().getNombre(), c.getCliente().getApellidos(), c.getCliente().getDni(), c.getTipo(), estado, c.getFecha(), c.getSubtotal(), c.getDescuento(), c.getTotal());
        UtilAlerts.mostrarAlerts("Comprobante " + c.getCodigo(), detalle, "INFORMATION").showAndWait();
    }
}
