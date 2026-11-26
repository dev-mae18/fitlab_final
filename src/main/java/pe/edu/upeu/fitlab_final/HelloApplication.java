package pe.edu.upeu.fitlab_final;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import pe.edu.upeu.fitlab_final.utils.InstanciaUnica;
import pe.edu.upeu.fitlab_final.utils.UtilAlerts;

import java.io.IOException;

public class HelloApplication extends Application {
    private InstanciaUnica instancia;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            instancia = InstanciaUnica.obtener();
        } catch (IOException | RuntimeException e) {
            UtilAlerts.mostrarAlerts("Error al abrir FitLab", "No se pudo iniciar FitLab: " + e.getMessage(), "ERROR").showAndWait();
            Platform.exit();
            return;
        }
        if (instancia == null) {
            UtilAlerts.mostrarAlerts("FitLab ya está abierto", "Ya hay una instancia de FitLab abierta. Revisa la ventana que está en ejecución.", "INFORMATION").showAndWait();
            Platform.exit();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/pe/edu/upeu/fitlab_final/views/login-view.fxml"));
            Scene scene = new Scene(loader.load());
            stage.getIcons().add(new Image(HelloApplication.class.getResourceAsStream("/pe/edu/upeu/fitlab_final/img/logo.png")));
            stage.setTitle("FitLab - Gestión de gimnasio");
            stage.setScene(scene);
            stage.setMinWidth(600);
            stage.setMinHeight(520);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException | RuntimeException e) {
            try {
                instancia.close();
            } catch (IOException cierre) {
                e.addSuppressed(cierre);
            }
            instancia = null;
            throw e;
        }
    }

    @Override
    public void stop() throws IOException {
        if (instancia != null) {
            try {
                instancia.close();
            } finally {
                instancia = null;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
