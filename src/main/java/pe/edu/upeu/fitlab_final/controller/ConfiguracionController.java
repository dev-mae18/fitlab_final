package pe.edu.upeu.fitlab_final.controller;

import javafx.beans.property.SimpleStringProperty;
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
import pe.edu.upeu.fitlab_final.implementacion.PlanDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IPlanDao;
import pe.edu.upeu.fitlab_final.modelo.Plan;
import pe.edu.upeu.fitlab_final.modelo.UserSession;
import pe.edu.upeu.fitlab_final.utils.UtilAlerts;
import pe.edu.upeu.fitlab_final.utils.UtilTablas;
import java.io.IOException;
import java.util.Locale;

public class ConfiguracionController {
    @FXML private TextField txtBuscar;
    @FXML private Label lblTotalPlanes;
    @FXML private Label lblPromociones;
    @FXML private Label lblResultados;
    @FXML private TableView<Plan> tablePlanes;
    @FXML private TableColumn<Plan, Number> colNumero;
    @FXML private TableColumn<Plan, String> colNombre;
    @FXML private TableColumn<Plan, Boolean> colTipo;
    @FXML private TableColumn<Plan, String> colDuracion;
    @FXML private TableColumn<Plan, String> colPrecio;
    @FXML private TableColumn<Plan, String> colEquivalente;
    private final IPlanDao planDao = new PlanDaoImpl();
    private ObservableList<Plan> listaPlanes;

    @FXML
    public void initialize() {
        colNumero.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colTipo.setCellValueFactory(c -> c.getValue().promocionProperty());
        colTipo.setCellFactory(columna -> new TableCell<>() {
            private final Label tipo = new Label();

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                tipo.setText(item ? "Promoción" : "Mensual");
                tipo.getStyleClass().setAll("label", "configuracion-tipo", item ? "configuracion-promocion" : "configuracion-mensual");
                setGraphic(tipo);
            }
        });
        colDuracion.setCellValueFactory(c -> {
            Plan plan = c.getValue();
            int meses = plan.isPromocion() ? plan.getMeses() : 1;
            return new SimpleStringProperty((meses == 1 ? "1 mes" : meses + " meses") + " · " + (meses * plan.getDuracionDias()) + " días");
        });
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty(String.format("S/ %.2f", c.getValue().getPrecio()) + (c.getValue().isPromocion() ? " / paquete" : " / mes")));
        colEquivalente.setCellValueFactory(c -> new SimpleStringProperty(String.format("S/ %.2f / mes", c.getValue().getPrecioMensual())));
        UtilTablas.ajustarTexto(colNombre);
        UtilTablas.ajustarTexto(colDuracion);
        UtilTablas.ajustarColumnas(tablePlanes);
        cargarDatos();
    }

    private void cargarDatos() {
        listaPlanes = FXCollections.observableArrayList(planDao.listAll());
        int promociones = 0;
        for (Plan plan : listaPlanes) {
            if (plan.isPromocion()) promociones++;
        }
        lblTotalPlanes.setText(String.valueOf(listaPlanes.size()));
        lblPromociones.setText(String.valueOf(promociones));
        handleSearch();
    }

    @FXML
    private void handleSearch() {
        String filtro = txtBuscar.getText().trim().toLowerCase(Locale.ROOT);
        ObservableList<Plan> filtrados = FXCollections.observableArrayList();
        for (Plan plan : listaPlanes) {
            String tipo = plan.isPromocion() ? "promoción promocion paquete" : "mensual";
            if (plan.getNombre().toLowerCase(Locale.ROOT).contains(filtro) || tipo.contains(filtro)) filtrados.add(plan);
        }
        tablePlanes.setItems(filtrados);
        lblResultados.setText(filtrados.size() + (filtrados.size() == 1 ? " plan en la lista" : " planes en la lista"));
    }

    @FXML private void handleNew() { abrirFormulario(null); }

    @FXML
    private void handleEdit() {
        Plan seleccionado = tablePlanes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilAlerts.mostrarAlerts("Selección requerida", "Seleccione un plan de la tabla para editarlo.", "WARNING").showAndWait();
            return;
        }
        Plan actual = planDao.searchById(seleccionado.getIdplan());
        if (actual == null) {
            UtilAlerts.mostrarAlerts("Plan no disponible", "No se pudo recuperar el plan seleccionado. La lista se actualizará.", "WARNING").showAndWait();
            cargarDatos();
            return;
        }
        abrirFormulario(actual);
    }

    private void abrirFormulario(Plan entidad) {
        if (UserSession.getInstance().getUserLogin() == null) {
            UtilAlerts.mostrarAlerts("Sesión requerida", "Inicie sesión para administrar los planes.", "WARNING").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pe/edu/upeu/fitlab_final/views/plan-form.fxml"));
            Parent root = loader.load();
            PlanFormController controller = loader.getController();
            controller.setPlan(entidad);
            Stage ventana = new Stage();
            ventana.setTitle(entidad == null ? "Nuevo plan" : "Editar plan");
            ventana.initOwner(tablePlanes.getScene().getWindow());
            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.setScene(new Scene(root));
            ventana.setResizable(false);
            ventana.showAndWait();
            if (controller.isSaved()) cargarDatos();
        } catch (IOException e) {
            UtilAlerts.mostrarAlerts("Error", "No se pudo abrir el formulario de plan: " + e.getMessage(), "ERROR").showAndWait();
        }
    }
}
