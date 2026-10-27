package pe.edu.upeu.fitlab_final.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import pe.edu.upeu.fitlab_final.implementacion.UsuarioDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IUsuarioDao;
import pe.edu.upeu.fitlab_final.modelo.Usuario;
import pe.edu.upeu.fitlab_final.utils.UtilAlerts;

public class CuentaController {
    @FXML private Label lblUsuario;
    @FXML private PasswordField txtActual;
    @FXML private PasswordField txtNueva;
    @FXML private PasswordField txtConfirmacion;
    private final IUsuarioDao usuarioDao = new UsuarioDaoImpl();
    private Usuario usuario;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) lblUsuario.setText("Usuario: " + usuario.getUsuario());
    }

    @FXML
    private void handleGuardar() {
        if (usuario == null) return;
        if (txtActual.getText().isBlank() || txtNueva.getText().isBlank() || !txtNueva.getText().equals(txtConfirmacion.getText())) {
            UtilAlerts.mostrarAlerts("Validación", "Ingrese la contraseña actual y confirme la nueva contraseña.", "WARNING").showAndWait();
            return;
        }
        boolean guardado = usuarioDao.cambiarContrasena(usuario.getIdusuario(), txtActual.getText(), txtNueva.getText());
        UtilAlerts.mostrarAlerts(guardado ? "Contraseña actualizada" : "No se pudo actualizar", guardado ? "La contraseña se guardó correctamente." : "Compruebe la contraseña actual.", guardado ? "INFORMATION" : "WARNING").showAndWait();
        if (guardado) {
            txtActual.clear();
            txtNueva.clear();
            txtConfirmacion.clear();
        }
    }
}
