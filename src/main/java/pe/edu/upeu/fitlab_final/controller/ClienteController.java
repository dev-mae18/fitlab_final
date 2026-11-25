package pe.edu.upeu.fitlab_final.controller;

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
import pe.edu.upeu.fitlab_final.implementacion.ClienteDaoImpl;
import pe.edu.upeu.fitlab_final.implementacion.ComprobanteDaoImpl;
import pe.edu.upeu.fitlab_final.implementacion.MembresiaDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IClienteDao;
import pe.edu.upeu.fitlab_final.interfaces.IComprobanteDao;
import pe.edu.upeu.fitlab_final.interfaces.IMembresiaDao;
import pe.edu.upeu.fitlab_final.modelo.Cliente;
import pe.edu.upeu.fitlab_final.utils.UtilAlerts;
import java.io.IOException;

public class ClienteController {
    @FXML private TableView<Cliente> tableCliente;
    @FXML private TableColumn<Cliente, Number> colNumero;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colApellidos;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, Number> colEdad;
    @FXML private TableColumn<Cliente, String> colGenero;
    @FXML private TableColumn<Cliente, Boolean> colEstudiante;
    @FXML private TextField txtBuscar;
    private final IClienteDao clienteDao = new ClienteDaoImpl();
    private final IMembresiaDao membresiaDao = new MembresiaDaoImpl();
    private final IComprobanteDao comprobanteDao = new ComprobanteDaoImpl();
    private ObservableList<Cliente> listaSocios;

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
        colEdad.setCellValueFactory(c -> c.getValue().edadProperty());
        colGenero.setCellValueFactory(c -> c.getValue().generoProperty());
        colEstudiante.setCellValueFactory(c -> c.getValue().estudianteProperty());
        colEstudiante.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item ? "Sí" : "No");
            }
        });
        cargarDatos();
    }

    private void cargarDatos() {
        listaSocios = FXCollections.observableArrayList(clienteDao.listAll());
        handleSearch();
    }

    @FXML
    private void handleSearch() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        ObservableList<Cliente> filtrados = FXCollections.observableArrayList();
        for (Cliente cliente : listaSocios) {
            if (cliente.getDni().contains(filtro) || (cliente.getNombre() + " " + cliente.getApellidos()).toLowerCase().contains(filtro)) {
                filtrados.add(cliente);
            }
        }
        tableCliente.setItems(filtrados);
    }

    @FXML private void handleNew() { abrirFormulario(null); }

    @FXML
    private void handleVerDatos() {
        Cliente seleccionado = seleccionar();
        if (seleccionado == null) return;
        Cliente cliente = clienteDao.searchById(seleccionado.getIdcliente());
        if (cliente == null) {
            UtilAlerts.mostrarAlerts("Socio no disponible", "No se pudo recuperar el socio seleccionado.", "WARNING").showAndWait();
            cargarDatos();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pe/edu/upeu/fitlab_final/views/cliente-datos.fxml"));
            Parent root = loader.load();
            ClienteDatosController controller = loader.getController();
            controller.setDatos(cliente, membresiaDao.searchByCliente(cliente.getIdcliente()), comprobanteDao.searchByDni(cliente.getDni()));
            Stage ventana = new Stage();
            ventana.setTitle("Datos de " + cliente.getNombre() + " " + cliente.getApellidos());
            ventana.initOwner(tableCliente.getScene().getWindow());
            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.setScene(new Scene(root));
            ventana.setMinWidth(700);
            ventana.setMinHeight(500);
            ventana.showAndWait();
        } catch (IOException e) {
            UtilAlerts.mostrarAlerts("Error", "No se pudieron abrir los datos del socio: " + e.getMessage(), "ERROR").showAndWait();
        }
    }

    @FXML
    private void handleEdit() {
        Cliente seleccionado = seleccionar();
        if (seleccionado == null) return;
        Cliente cliente = clienteDao.searchById(seleccionado.getIdcliente());
        if (cliente == null) {
            UtilAlerts.mostrarAlerts("Cliente no disponible", "No se pudo recuperar el cliente seleccionado. La lista se actualizará.", "WARNING").showAndWait();
            cargarDatos();
            return;
        }
        abrirFormulario(cliente);
    }

    @FXML
    private void handleDelete() {
        Cliente seleccionado = seleccionar();
        if (seleccionado == null) return;
        if (UtilAlerts.mostrarAlerts("Confirmar eliminación", "¿Eliminar a " + seleccionado.getNombre() + " " + seleccionado.getApellidos() + "?", "CONFIRMATION").showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        if (!clienteDao.delete(seleccionado)) {
            UtilAlerts.mostrarAlerts("No se pudo eliminar", "El registro puede tener membresías o comprobantes asociados.", "WARNING").showAndWait();
        }
        cargarDatos();
    }

    private Cliente seleccionar() {
        Cliente seleccionado = tableCliente.getSelectionModel().getSelectedItem();
        if (seleccionado == null) UtilAlerts.mostrarAlerts("Selección requerida", "Seleccione un registro de la tabla.", "WARNING").showAndWait();
        return seleccionado;
    }

    private void abrirFormulario(Cliente entidad) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pe/edu/upeu/fitlab_final/views/cliente-form.fxml"));
            Parent root = loader.load();
            ClienteFormController controller = loader.getController();
            controller.setCliente(entidad);
            Stage ventana = new Stage();
            ventana.setTitle(entidad == null ? "Nuevo cliente" : "Editar cliente");
            ventana.initOwner(tableCliente.getScene().getWindow());
            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.setScene(new Scene(root));
            ventana.setResizable(false);
            ventana.showAndWait();
            if (controller.isSaved()) {
                Cliente clienteProcesado = controller.getCliente();
                boolean guardado;
                if (entidad == null) {
                    guardado = clienteDao.insert(clienteProcesado);
                } else {
                    guardado = clienteDao.update(clienteProcesado);
                }
                if (!guardado) UtilAlerts.mostrarAlerts("Error al guardar", "No se pudo guardar el registro. Compruebe que el DNI no esté registrado.", "ERROR").showAndWait();
                cargarDatos();
            }
        } catch (IOException e) {
            UtilAlerts.mostrarAlerts("Error", "No se pudo abrir el formulario: " + e.getMessage(), "ERROR").showAndWait();
        }
    }
}
