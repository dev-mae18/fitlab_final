package pe.edu.upeu.fitlab_final.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pe.edu.upeu.fitlab_final.implementacion.PlanDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IPlanDao;
import pe.edu.upeu.fitlab_final.modelo.Plan;
import pe.edu.upeu.fitlab_final.modelo.UserSession;
import java.util.Locale;

public class PlanFormController {
    @FXML private Label lblTitulo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private Label lblPrecio;
    @FXML private CheckBox chkPromocion;
    @FXML private ComboBox<Integer> cbxMeses;
    @FXML private Label lblDuracion;
    @FXML private Label lblResumen;
    @FXML private Label lblNota;
    @FXML private Label lblMensaje;
    @FXML private Button btnCancelar;
    private final IPlanDao planDao = new PlanDaoImpl();
    private Plan plan;
    private boolean saved;
    private boolean enUso;

    @FXML
    public void initialize() {
        for (int mes = 2; mes <= 12; mes++) cbxMeses.getItems().add(mes);
        cbxMeses.setValue(3);
        txtPrecio.textProperty().addListener((observable, anterior, actual) -> actualizarResumen());
        handlePromocion();
    }

    public boolean isSaved() { return saved; }
    public Plan getPlan() { return plan; }

    public void setPlan(Plan actual) {
        saved = false;
        plan = actual;
        enUso = actual != null && planDao.estaEnUso(actual.getIdplan());
        lblTitulo.setText(actual == null ? "Nuevo plan" : "Editar plan");
        txtNombre.setText(actual == null ? "" : actual.getNombre());
        txtPrecio.setText(actual == null ? "" : String.format(Locale.ROOT, "%.2f", actual.getPrecio()));
        chkPromocion.setSelected(actual != null && actual.isPromocion());
        cbxMeses.setValue(actual != null && actual.isPromocion() ? actual.getMeses() : 3);
        chkPromocion.setDisable(enUso);
        lblMensaje.setText("");
        handlePromocion();
    }

    @FXML
    private void handlePromocion() {
        boolean promocion = chkPromocion.isSelected();
        lblPrecio.setText(promocion ? "Precio TOTAL del paquete (S/)" : "Precio mensual (S/)");
        cbxMeses.setVisible(promocion);
        cbxMeses.setManaged(promocion);
        cbxMeses.setDisable(enUso || !promocion);
        if (enUso) {
            lblNota.setText("Este plan ya tiene membresías. Puede cambiar el nombre y el precio; el tipo y la duración se conservan. Las membresías registradas mantienen su importe pactado.");
        } else {
            int diasPorMes = plan == null ? 30 : plan.getDuracionDias();
            lblNota.setText(promocion ? "El precio cubre el paquete completo. Al elegir esta promoción, la membresía usa la duración del paquete." : "El precio se aplica por cada mes contratado. Cada mes equivale a " + diasPorMes + " días.");
        }
        actualizarResumen();
    }

    @FXML
    private void actualizarResumen() {
        boolean promocion = chkPromocion.isSelected();
        Integer seleccion = cbxMeses.getValue();
        int meses = promocion && seleccion != null ? seleccion : 1;
        int diasPorMes = plan == null ? 30 : plan.getDuracionDias();
        lblDuracion.setText(promocion ? (meses * diasPorMes) + " días de vigencia" : "1 mes · " + diasPorMes + " días");
        String texto = txtPrecio.getText().trim();
        if (!texto.matches("[0-9]+([.,][0-9]{1,2})?")) {
            lblResumen.setText(promocion ? "Paquete de " + meses + " meses. Ingrese el precio total para ver su equivalente mensual." : "Ingrese el precio mensual del plan.");
            return;
        }
        double precio = Double.parseDouble(texto.replace(',', '.'));
        if (!Double.isFinite(precio) || precio <= 0) {
            lblResumen.setText("El precio debe ser mayor que cero.");
            return;
        }
        lblResumen.setText(promocion
                ? String.format("Total del paquete: S/ %.2f · %d meses%nEquivalente mensual: S/ %.2f", precio, meses, precio / meses)
                : String.format("S/ %.2f por mes · %d días por mes contratado", precio, diasPorMes));
    }

    private boolean validate() {
        if (txtNombre.getText().trim().isEmpty()) {
            lblMensaje.setText("Ingrese el nombre del plan.");
            return false;
        }
        String precio = txtPrecio.getText().trim();
        if (!precio.matches("[0-9]+([.,][0-9]{1,2})?")) {
            lblMensaje.setText("Ingrese un precio válido con hasta 2 decimales. Use coma o punto como separador.");
            return false;
        }
        double importe = Double.parseDouble(precio.replace(',', '.'));
        if (!Double.isFinite(importe) || importe <= 0) {
            lblMensaje.setText("El precio debe ser un número mayor que cero.");
            return false;
        }
        if (chkPromocion.isSelected() && (cbxMeses.getValue() == null || cbxMeses.getValue() < 2 || cbxMeses.getValue() > 12)) {
            lblMensaje.setText("Seleccione una duración de 2 a 12 meses para la promoción.");
            return false;
        }
        Plan existente = planDao.searchByNombre(txtNombre.getText().trim());
        if (existente != null && (plan == null || existente.getIdplan() != plan.getIdplan())) {
            lblMensaje.setText("Ya existe un plan con ese nombre. Ingrese otro nombre.");
            return false;
        }
        return true;
    }

    @FXML
    private void handleSave() {
        if (saved) return;
        lblMensaje.setText("");
        if (UserSession.getInstance().getUserLogin() == null) {
            lblMensaje.setText("Inicie sesión antes de guardar el plan.");
            return;
        }
        if (!validate()) return;
        Plan procesado = new Plan();
        if (plan != null) procesado.setIdplan(plan.getIdplan());
        procesado.setNombre(txtNombre.getText().trim());
        procesado.setPrecio(Double.parseDouble(txtPrecio.getText().trim().replace(',', '.')));
        boolean promocion = enUso ? plan.isPromocion() : chkPromocion.isSelected();
        procesado.setPromocion(promocion);
        procesado.setMeses(enUso ? plan.getMeses() : promocion ? cbxMeses.getValue() : 1);
        procesado.setDuracionDias(plan == null ? 30 : plan.getDuracionDias());
        boolean guardado = plan == null ? planDao.insert(procesado) : planDao.update(procesado);
        if (!guardado) {
            lblMensaje.setText("No se pudo guardar. Compruebe el nombre y los datos; si el plan ya tiene membresías, conserve su tipo y duración. Puede corregirlos y volver a intentarlo.");
            return;
        }
        plan = procesado;
        saved = true;
        cerrar();
    }

    @FXML private void handleCancel() { cerrar(); }
    private void cerrar() { ((Stage) btnCancelar.getScene().getWindow()).close(); }
}
