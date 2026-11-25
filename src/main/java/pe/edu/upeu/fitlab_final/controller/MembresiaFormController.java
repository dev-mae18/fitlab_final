package pe.edu.upeu.fitlab_final.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pe.edu.upeu.fitlab_final.implementacion.ClienteDaoImpl;
import pe.edu.upeu.fitlab_final.implementacion.EntrenadorDaoImpl;
import pe.edu.upeu.fitlab_final.implementacion.MembresiaDaoImpl;
import pe.edu.upeu.fitlab_final.implementacion.PlanDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IClienteDao;
import pe.edu.upeu.fitlab_final.interfaces.IEntrenadorDao;
import pe.edu.upeu.fitlab_final.interfaces.IMembresiaDao;
import pe.edu.upeu.fitlab_final.interfaces.IPlanDao;
import pe.edu.upeu.fitlab_final.modelo.*;
import pe.edu.upeu.fitlab_final.utils.UtilAlerts;
import java.time.LocalDate;
import java.util.List;

public class MembresiaFormController {
    @FXML private ComboBox<Cliente> cbxCliente;
    @FXML private ComboBox<Plan> cbxPlan;
    @FXML private ComboBox<String> cbxMeses;
    @FXML private ComboBox<Entrenador> cbxEntrenador;
    @FXML private CheckBox chkEntrenador;
    @FXML private ComboBox<String> cbxHorario;
    @FXML private ComboBox<String> cbxTipoDias;
    @FXML private Label lblImporte;
    @FXML private Label lblNota;
    @FXML private Button btnCancelar;
    private final IClienteDao clienteDao = new ClienteDaoImpl();
    private final IPlanDao planDao = new PlanDaoImpl();
    private final IEntrenadorDao entrenadorDao = new EntrenadorDaoImpl();
    private final IMembresiaDao membresiaDao = new MembresiaDaoImpl();
    private Membresia membresia;
    private boolean saved = false;
    private boolean nuevoRegistro = true;
    private boolean nuevaAdquisicion;
    private boolean readquisicionGuardada;

    @FXML
    public void initialize() {
        cbxPlan.setItems(FXCollections.observableArrayList(planDao.listAll()));
        for (int i = 1; i <= 12; i++) {
            if (i == 1) cbxMeses.getItems().add("1 mes");
            else cbxMeses.getItems().add(i + " meses");
        }
        cbxMeses.setValue("1 mes");
        cbxEntrenador.setItems(FXCollections.observableArrayList(entrenadorDao.listAll()));
        cbxHorario.getItems().addAll("MAÑANA", "TARDE", "NOCHE");
        cbxTipoDias.getItems().addAll("Lunes, miércoles y viernes", "Martes, jueves y sábado", "Todos los días");
    }

    public boolean isSaved() { return saved; }
    public Membresia getMembresia() { return membresia; }
    public boolean isNuevaAdquisicion() { return nuevaAdquisicion || readquisicionGuardada; }

    public void setMembresia(Membresia m) {
        saved = false;
        nuevoRegistro = m == null;
        nuevaAdquisicion = false;
        readquisicionGuardada = false;
        this.membresia = m;
        cbxCliente.setDisable(false);
        cbxPlan.setDisable(false);
        cbxMeses.setDisable(false);
        if (m == null) {
            cbxCliente.setValue(null);
            cbxPlan.setValue(null);
            cbxMeses.setValue("1 mes");
            cbxHorario.setValue(null);
            cbxTipoDias.setValue(null);
            chkEntrenador.setSelected(false);
            cbxEntrenador.setValue(null);
            handleEntrenador();
            lblNota.setText("La nueva membresía inicia hoy. Los socios vencidos o inactivos aparecen identificados en la lista.");
            List<Membresia> registradas = membresiaDao.listAll();
            ObservableList<Cliente> disponibles = FXCollections.observableArrayList();
            for (Cliente cliente : clienteDao.listAll()) {
                Membresia registrada = null;
                for (Membresia actual : registradas) {
                    if (actual.getCliente().getIdcliente() == cliente.getIdcliente()) {
                        registrada = actual;
                        break;
                    }
                }
                if (registrada == null) {
                    disponibles.add(cliente);
                } else if (!registrada.isActiva()) {
                    cliente.setEstadoMembresia("INACTIVO");
                    disponibles.add(cliente);
                } else if (registrada.getFechaVencimiento().isBefore(LocalDate.now())) {
                    cliente.setEstadoMembresia("VENCIDO");
                    disponibles.add(cliente);
                }
            }
            cbxCliente.setItems(disponibles);
        } else {
            lblNota.setText("La edición actualiza horario, días y entrenador. Para cambiar de plan utilice Migrar plan.");
            cbxCliente.getItems().setAll(m.getCliente());
            cbxCliente.setValue(m.getCliente());
            Plan planSeleccionado = m.getPlan();
            for (Plan plan : cbxPlan.getItems()) {
                if (plan.getIdplan() == m.getPlan().getIdplan()) {
                    planSeleccionado = plan;
                    break;
                }
            }
            cbxPlan.setValue(planSeleccionado);
            cbxCliente.setDisable(true);
            cbxPlan.setDisable(true);
            if (m.getMeses() == 1) cbxMeses.setValue("1 mes");
            else cbxMeses.setValue(m.getMeses() + " meses");
            cbxMeses.setDisable(true);
            cbxHorario.setValue(Entrenador.describirTurno(m.getHorario()));
            cbxTipoDias.setValue(Membresia.describirDias(m.getTipoDias()));
            chkEntrenador.setSelected(m.getEntrenador() != null);
            handleEntrenador();
            if (m.getEntrenador() != null) {
                Entrenador entrenadorSeleccionado = m.getEntrenador();
                for (Entrenador entrenador : cbxEntrenador.getItems()) {
                    if (entrenador.getIdentrenador() == m.getEntrenador().getIdentrenador()) {
                        entrenadorSeleccionado = entrenador;
                        break;
                    }
                }
                cbxEntrenador.setValue(entrenadorSeleccionado);
            }
        }
        actualizarImporte();
    }

    public void setNuevaMembresia(Membresia anterior) {
        setMembresia(anterior);
        nuevaAdquisicion = true;
        cbxPlan.setDisable(false);
        cbxMeses.setDisable(false);
        lblNota.setText("La nueva membresía comienza hoy. Puede elegir el plan, horario y días de asistencia; se generará un nuevo comprobante de pago.");
    }

    @FXML
    private void handleEntrenador() {
        cbxEntrenador.setDisable(!chkEntrenador.isSelected());
    }

    @FXML
    private void actualizarImporte() {
        Cliente c = cbxCliente.getValue();
        Plan p = cbxPlan.getValue();
        if (c == null || p == null || cbxMeses.getValue() == null) {
            lblImporte.setText("Seleccione socio, plan y tiempo para ver el importe.");
            return;
        }
        int meses = Integer.parseInt(cbxMeses.getValue().split(" ")[0]);
        double subtotal = p.getPrecio() * meses;
        double descuento = c.calcularDescuento(subtotal);
        lblImporte.setText(String.format("Precio mensual: S/ %.2f · Tiempo: %s · Subtotal: S/ %.2f · Descuento: S/ %.2f · Total: S/ %.2f", p.getPrecio(), cbxMeses.getValue(), subtotal, descuento, subtotal - descuento));
    }

    private boolean validate() {
        if (cbxCliente.getValue() == null || cbxPlan.getValue() == null || cbxMeses.getValue() == null || cbxHorario.getValue() == null || cbxTipoDias.getValue() == null || (chkEntrenador.isSelected() && cbxEntrenador.getValue() == null)) {
            UtilAlerts.mostrarAlerts("Validación", "Seleccione socio, plan, tiempo, horario y días de asistencia; también entrenador si corresponde.", "WARNING").showAndWait();
            return false;
        }
        if (chkEntrenador.isSelected() && !cbxHorario.getValue().equals(Entrenador.describirTurno(cbxEntrenador.getValue().getTurno()))) {
            UtilAlerts.mostrarAlerts("Turno del entrenador", "Seleccione un entrenador del mismo turno que la membresía.", "WARNING").showAndWait();
            return false;
        }
        return true;
    }

    @FXML
    private void handleSave() {
        if (!validate()) return;
        Usuario usuario = UserSession.getInstance().getUserLogin();
        if (usuario == null) {
            UtilAlerts.mostrarAlerts("Sesión requerida", "Inicie sesión antes de guardar la membresía.", "WARNING").showAndWait();
            return;
        }
        Membresia procesada = membresia;
        if (nuevoRegistro) {
            procesada = membresiaDao.searchByCliente(cbxCliente.getValue().getIdcliente());
            if (procesada == null) procesada = new Membresia();
        }
        boolean readquisicion = nuevaAdquisicion || (nuevoRegistro && procesada.getIdmembresia() > 0);
        procesada.setCliente(cbxCliente.getValue());
        procesada.setPlan(cbxPlan.getValue());
        procesada.setMeses(Integer.parseInt(cbxMeses.getValue().split(" ")[0]));
        procesada.setEntrenador(chkEntrenador.isSelected() ? cbxEntrenador.getValue() : null);
        String horario = cbxHorario.getValue();
        if ("MAÑANA".equals(horario)) horario = "MANANA";
        procesada.setHorario(horario);
        String dias = cbxTipoDias.getValue();
        if ("Lunes, miércoles y viernes".equals(dias)) procesada.setTipoDias("LUNES-MIERCOLES-VIERNES");
        if ("Martes, jueves y sábado".equals(dias)) procesada.setTipoDias("MARTES-JUEVES-SABADO");
        if ("Todos los días".equals(dias)) procesada.setTipoDias("TODOS");
        procesada.setIdUsuario(usuario.getIdusuario());
        boolean guardado;
        if (readquisicion) {
            guardado = membresiaDao.nuevaMembresia(procesada);
        } else if (nuevoRegistro) {
            guardado = membresiaDao.insert(procesada);
        } else {
            guardado = membresiaDao.update(procesada);
        }
        if (!guardado) {
            UtilAlerts.mostrarAlerts("No se pudo guardar", "Compruebe los datos, los cupos del horario y el estado actual del socio. Puede corregirlos y volver a intentarlo.", "WARNING").showAndWait();
            return;
        }
        membresia = procesada;
        readquisicionGuardada = readquisicion;
        saved = true;
        cerrar();
    }

    @FXML private void handleCancel() { cerrar(); }
    private void cerrar() { ((Stage) btnCancelar.getScene().getWindow()).close(); }
}
