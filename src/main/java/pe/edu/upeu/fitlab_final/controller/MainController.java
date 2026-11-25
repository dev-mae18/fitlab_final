package pe.edu.upeu.fitlab_final.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pe.edu.upeu.fitlab_final.modelo.UserSession;
import pe.edu.upeu.fitlab_final.modelo.Usuario;
import pe.edu.upeu.fitlab_final.utils.UtilAlerts;
import java.io.IOException;

public class MainController {
    @FXML private StackPane contentArea;
    @FXML private Label lblUsuario;
    @FXML
    public void initialize() {
        Usuario usuario = UserSession.getInstance().getUserLogin();
        lblUsuario.setText(usuario == null ? "Sesión no iniciada" : "Sesión: " + usuario.getUsuario());
        showHome();
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
            UserSession.getInstance().cleanSession();
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
