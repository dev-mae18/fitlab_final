package pe.edu.upeu.fitlab_final.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pe.edu.upeu.fitlab_final.implementacion.EntrenadorDaoImpl;
import pe.edu.upeu.fitlab_final.implementacion.MembresiaDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IEntrenadorDao;
import pe.edu.upeu.fitlab_final.interfaces.IMembresiaDao;
import pe.edu.upeu.fitlab_final.modelo.Entrenador;
import pe.edu.upeu.fitlab_final.modelo.Membresia;
import pe.edu.upeu.fitlab_final.utils.UtilAlerts;

public class EntrenadorFormController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDni;
    @FXML private TextField txtEspecialidad;
    @FXML private ComboBox<String> cbxTurno;
    @FXML private Button btnCancelar;
    private final IEntrenadorDao entrenadorDao = new EntrenadorDaoImpl();
    private final IMembresiaDao membresiaDao = new MembresiaDaoImpl();
    private Entrenador entrenador;
    private boolean saved = false;

    @FXML public void initialize() { cbxTurno.getItems().addAll("MAÑANA", "TARDE", "NOCHE"); }
    public boolean isSaved() { return saved; }
    public Entrenador getEntrenador() { return entrenador; }

    public void setEntrenador(Entrenador e) {
        saved = false;
        this.entrenador = e;
        if (e != null) {
            txtNombre.setText(e.getNombre());
            txtApellidos.setText(e.getApellidos());
            txtDni.setText(e.getDni());
            txtEspecialidad.setText(e.getEspecialidad());
            cbxTurno.setValue(Entrenador.describirTurno(e.getTurno()));
        }
    }

    private boolean validate() {
        if (txtNombre.getText().isBlank() || txtApellidos.getText().isBlank() || !txtDni.getText().trim().matches("[0-9]{8}") || txtEspecialidad.getText().isBlank() || cbxTurno.getValue() == null) {
            UtilAlerts.mostrarAlerts("Validación", "Complete todos los campos y un DNI de 8 dígitos.", "WARNING").showAndWait();
            return false;
        }
        Entrenador existente = entrenadorDao.searchByDni(txtDni.getText().trim());
        if (existente != null && (entrenador == null || existente.getIdentrenador() != entrenador.getIdentrenador())) {
            UtilAlerts.mostrarAlerts("DNI registrado", "Ya existe un entrenador con este DNI.", "WARNING").showAndWait();
            return false;
        }
        if (entrenador != null && !cbxTurno.getValue().equals(Entrenador.describirTurno(entrenador.getTurno()))) {
            for (Membresia membresia : membresiaDao.listAll()) {
                Entrenador asignado = membresia.getEntrenador();
                if (asignado != null && asignado.getIdentrenador() == entrenador.getIdentrenador() && !cbxTurno.getValue().equals(Entrenador.describirTurno(membresia.getHorario()))) {
                    UtilAlerts.mostrarAlerts("Cambio de turno no disponible", "Este entrenador tiene membresías asignadas en otro horario. Reasigne el entrenador o ajuste primero el horario de esas membresías.", "WARNING").showAndWait();
                    return false;
                }
            }
        }
        return true;
    }

    @FXML
    private void handleSave() {
        if (validate()) {
            if (entrenador == null) entrenador = new Entrenador();
            entrenador.setNombre(txtNombre.getText().trim());
            entrenador.setApellidos(txtApellidos.getText().trim());
            entrenador.setDni(txtDni.getText().trim());
            entrenador.setEspecialidad(txtEspecialidad.getText().trim());
            String turno = cbxTurno.getValue();
            if ("MAÑANA".equals(turno)) turno = "MANANA";
            entrenador.setTurno(turno);
            saved = true;
            cerrar();
        }
    }

    @FXML private void handleCancel() { cerrar(); }
    private void cerrar() { ((Stage) btnCancelar.getScene().getWindow()).close(); }
}
