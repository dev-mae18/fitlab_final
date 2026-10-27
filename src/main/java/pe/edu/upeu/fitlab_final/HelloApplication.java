package pe.edu.upeu.fitlab_final;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/pe/edu/upeu/fitlab_final/views/login-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("FitLab - Gestión de gimnasio");
        stage.setScene(scene);
        stage.setMinWidth(600);
        stage.setMinHeight(520);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
