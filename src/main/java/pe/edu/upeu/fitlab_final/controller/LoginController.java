package pe.edu.upeu.fitlab_final.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.fitlab_final.implementacion.UsuarioDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IUsuarioDao;
import pe.edu.upeu.fitlab_final.modelo.Usuario;
import pe.edu.upeu.fitlab_final.utils.UtilAlerts;
import java.io.IOException;

public class LoginController {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblMensaje;
    @FXML private Button btnIngresar;
    private final IUsuarioDao usuarioDao = new UsuarioDaoImpl();
    private int intentos;

    @FXML
    private void handleLogin() {
        if (intentos >= 3) return;
        if (txtUsuario.getText().isBlank() || txtContrasena.getText().isBlank()) {
            lblMensaje.setText("Ingrese usuario y contraseña.");
            return;
        }
        Usuario usuario = usuarioDao.autenticar(txtUsuario.getText().trim(), txtContrasena.getText());
        if (usuario == null) {
            intentos++;
            lblMensaje.setText(intentos >= 3 ? "Acceso bloqueado. Vuelva a abrir FitLab." : "Usuario o contraseña incorrectos. Intentos restantes: " + (3 - intentos));
            txtContrasena.clear();
            if (intentos >= 3) {
                txtUsuario.setDisable(true);
                txtContrasena.setDisable(true);
                btnIngresar.setDisable(true);
            }
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pe/edu/upeu/fitlab_final/views/main-view.fxml"));
            Parent view = loader.load();
            MainController controller = loader.getController();
            controller.setUsuario(usuario);
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.getScene().setRoot(view);
            stage.setTitle("FitLab · Gestión de gimnasio");
            stage.setMinWidth(1100);
            stage.setMinHeight(720);
            stage.setMaximized(true);
        } catch (IOException e) {
            UtilAlerts.mostrarAlerts("Error", "No se pudo abrir FitLab: " + e.getMessage(), "ERROR").showAndWait();
        }
    }
}
