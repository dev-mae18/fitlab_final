package pe.edu.upeu.fitlab_final.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pe.edu.upeu.fitlab_final.modelo.Usuario;
import pe.edu.upeu.fitlab_final.utils.UtilAlerts;
import java.io.IOException;

public class MainController {
    @FXML private StackPane contentArea;
    @FXML private Label lblUsuario;
    private Usuario usuario;

    @FXML public void initialize() { showHome(); }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        lblUsuario.setText("Sesión: " + usuario.getUsuario());
    }

    @FXML private void showHome() { loadView("home-view.fxml"); }
    @FXML private void showClient() { loadView("cliente-view.fxml"); }
    @FXML private void showEntrenadores() { loadView("entrenador-view.fxml"); }
    @FXML private void showMembresias() { loadView("membresia-view.fxml"); }
    @FXML private void showComprobantes() { loadView("comprobante-view.fxml"); }
    @FXML private void showInformes() { loadView("informe-view.fxml"); }
    @FXML private void showCuenta() { loadView("cuenta-view.fxml"); }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pe/edu/upeu/fitlab_final/views/" + fxml));
            Parent view = loader.load();
            if ("membresia-view.fxml".equals(fxml)) {
                MembresiaController controller = loader.getController();
                controller.setUsuario(usuario);
            }
            if ("cuenta-view.fxml".equals(fxml)) {
                CuentaController controller = loader.getController();
                controller.setUsuario(usuario);
            }
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            UtilAlerts.mostrarAlerts("Error", "No se pudo cargar la vista: " + e.getMessage(), "ERROR").showAndWait();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/pe/edu/upeu/fitlab_final/views/login-view.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            usuario = null;
            stage.setMinWidth(600);
            stage.setMinHeight(520);
            stage.getScene().setRoot(view);
            stage.setTitle("FitLab · Iniciar sesión");
            stage.setMaximized(true);
        } catch (IOException e) {
            UtilAlerts.mostrarAlerts("Error", "No se pudo cerrar la sesión: " + e.getMessage(), "ERROR").showAndWait();
        }
    }
}
