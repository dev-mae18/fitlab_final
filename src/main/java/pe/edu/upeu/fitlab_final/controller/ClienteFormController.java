package pe.edu.upeu.fitlab_final.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pe.edu.upeu.fitlab_final.implementacion.ClienteDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IClienteDao;
import pe.edu.upeu.fitlab_final.modelo.Cliente;
import pe.edu.upeu.fitlab_final.utils.UtilAlerts;

public class ClienteFormController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDni;
    @FXML private TextField txtEdad;
    @FXML private ComboBox<String> cbxGenero;
    @FXML private CheckBox chkEstudiante;
    @FXML private TextField txtAlergias;
    @FXML private Button btnCancelar;
    private final IClienteDao clienteDao = new ClienteDaoImpl();
    private Cliente cliente;
    private boolean saved = false;

    @FXML
    public void initialize() {
        cbxGenero.getItems().addAll("M", "F");
    }

    public boolean isSaved() { return saved; }
    public Cliente getCliente() { return cliente; }

    public void setCliente(Cliente c) {
        saved = false;
        this.cliente = c;
        if (c != null) {
            txtNombre.setText(c.getNombre());
            txtApellidos.setText(c.getApellidos());
            txtDni.setText(c.getDni());
            txtEdad.setText(String.valueOf(c.getEdad()));
            cbxGenero.setValue(c.getGenero());
            chkEstudiante.setSelected(c.isEstudiante());
            txtAlergias.setText(c.getAlergias());
        }
    }

    private boolean validate() {
        if (txtNombre.getText().isBlank() || txtApellidos.getText().isBlank() || !txtDni.getText().trim().matches("[0-9]{8}") || !txtEdad.getText().trim().matches("[0-9]{1,3}") || cbxGenero.getValue() == null) {
            UtilAlerts.mostrarAlerts("Validación", "Complete nombres, apellidos, edad, género y un DNI de 8 dígitos.", "WARNING").showAndWait();
            return false;
        }
        int edad = Integer.parseInt(txtEdad.getText().trim());
        if (edad < 1 || edad > 120) {
            UtilAlerts.mostrarAlerts("Validación", "La edad debe estar entre 1 y 120 años.", "WARNING").showAndWait();
            return false;
        }
        Cliente existente = clienteDao.searchByDni(txtDni.getText().trim());
        if (existente != null && (cliente == null || existente.getIdcliente() != cliente.getIdcliente())) {
            UtilAlerts.mostrarAlerts("DNI registrado", "Ya existe un socio con este DNI.", "WARNING").showAndWait();
            return false;
        }
        return true;
    }

    @FXML
    private void handleSave() {
        if (validate()) {
            if (cliente == null) cliente = new Cliente();
            cliente.setNombre(txtNombre.getText().trim());
            cliente.setApellidos(txtApellidos.getText().trim());
            cliente.setDni(txtDni.getText().trim());
            cliente.setEdad(Integer.parseInt(txtEdad.getText().trim()));
            cliente.setGenero(cbxGenero.getValue());
            cliente.setEstudiante(chkEstudiante.isSelected());
            cliente.setAlergias(txtAlergias.getText().isBlank() ? "Ninguna" : txtAlergias.getText().trim());
            saved = true;
            cerrar();
        }
    }

    @FXML private void handleCancel() { cerrar(); }
    private void cerrar() { ((Stage) btnCancelar.getScene().getWindow()).close(); }
}
