package pe.edu.upeu.fitlab_final.utils;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class UtilTablas {
    public static void ajustarColumnas(TableView<?> tabla) {
        Runnable ajustar = () -> {
            if (tabla.getWidth() <= 0) return;
            double disponible = tabla.getWidth() - tabla.getInsets().getLeft() - tabla.getInsets().getRight();
            for (Node nodo : tabla.lookupAll(".scroll-bar")) {
                if (nodo instanceof ScrollBar barra && barra.getOrientation() == Orientation.VERTICAL && barra.isVisible()) {
                    disponible -= barra.prefWidth(-1);
                }
            }
            double minimo = 0;
            for (TableColumn<?, ?> columna : tabla.getVisibleLeafColumns()) {
                minimo += columna.getMinWidth();
            }
            var politica = disponible >= minimo
                    ? TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
                    : TableView.UNCONSTRAINED_RESIZE_POLICY;
            if (tabla.getColumnResizePolicy() != politica) tabla.setColumnResizePolicy(politica);
        };
        InvalidationListener reajustar = observable -> ajustar.run();
        Runnable observarBarras = () -> {
            for (Node nodo : tabla.lookupAll(".scroll-bar")) {
                if (nodo instanceof ScrollBar barra && barra.getOrientation() == Orientation.VERTICAL) {
                    barra.visibleProperty().addListener(reajustar);
                    barra.widthProperty().addListener(reajustar);
                }
            }
            ajustar.run();
        };
        tabla.widthProperty().addListener(reajustar);
        tabla.skinProperty().addListener(observable -> Platform.runLater(observarBarras));
        tabla.getVisibleLeafColumns().addListener(reajustar);
        if (tabla.getSkin() != null) Platform.runLater(observarBarras);
        ajustar.run();
    }

    public static <S> void ajustarTexto(TableColumn<S, String> columna) {
        columna.setCellFactory(c -> new TableCell<>() {
            private final Label etiqueta = new Label();

            {
                etiqueta.setWrapText(true);
                etiqueta.setMinHeight(Label.USE_PREF_SIZE);
                etiqueta.prefWidthProperty().bind(columna.widthProperty().subtract(16));
                prefHeightProperty().bind(Bindings.createDoubleBinding(
                        () -> Math.max(39, etiqueta.prefHeight(Math.max(1, columna.getWidth() - 16)) + 12),
                        etiqueta.textProperty(), etiqueta.fontProperty(), columna.widthProperty()));
            }

            @Override
            protected void updateItem(String texto, boolean empty) {
                super.updateItem(texto, empty);
                setText(null);
                etiqueta.setText(empty || texto == null ? null : texto);
                setGraphic(empty || texto == null ? null : etiqueta);
            }
        });
    }
}
