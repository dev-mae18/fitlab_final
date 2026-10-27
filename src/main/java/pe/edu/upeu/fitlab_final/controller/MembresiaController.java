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
import pe.edu.upeu.fitlab_final.implementacion.ComprobanteDaoImpl;
import pe.edu.upeu.fitlab_final.implementacion.MembresiaDaoImpl;
import pe.edu.upeu.fitlab_final.implementacion.PlanDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IComprobanteDao;
import pe.edu.upeu.fitlab_final.interfaces.IMembresiaDao;
import pe.edu.upeu.fitlab_final.interfaces.IPlanDao;
import pe.edu.upeu.fitlab_final.modelo.Comprobante;
import pe.edu.upeu.fitlab_final.modelo.Entrenador;
import pe.edu.upeu.fitlab_final.modelo.Membresia;
import pe.edu.upeu.fitlab_final.modelo.Plan;
import pe.edu.upeu.fitlab_final.modelo.Usuario;
import pe.edu.upeu.fitlab_final.utils.UtilAlerts;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MembresiaController {
    @FXML private TableView<Membresia> tableMembresia;
    @FXML private TableColumn<Membresia, Number> colNumero;
    @FXML private TableColumn<Membresia, String> colCliente;
    @FXML private TableColumn<Membresia, String> colDni;
    @FXML private TableColumn<Membresia, String> colPlan;
    @FXML private TableColumn<Membresia, String> colPrecio;
    @FXML private TableColumn<Membresia, String> colHorario;
    @FXML private TableColumn<Membresia, String> colDias;
    @FXML private TableColumn<Membresia, String> colEntrenador;
    @FXML private TableColumn<Membresia, LocalDate> colVencimiento;
    @FXML private TableColumn<Membresia, String> colEstado;
    @FXML private TextField txtBuscar;
    private final IMembresiaDao membresiaDao = new MembresiaDaoImpl();
    private final IPlanDao planDao = new PlanDaoImpl();
    private final IComprobanteDao comprobanteDao = new ComprobanteDaoImpl();
    private ObservableList<Membresia> listaMembresias;
    private Usuario usuario;

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    @FXML
    public void initialize() {
        colNumero.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else setText(String.valueOf(getIndex() + 1));
            }
        });
        colCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCliente().getNombre() + " " + c.getValue().getCliente().getApellidos()));
        colDni.setCellValueFactory(c -> c.getValue().getCliente().dniProperty());
        colPlan.setCellValueFactory(c -> c.getValue().getPlan().nombreProperty());
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty(String.format("S/ %.2f", c.getValue().getPlan().getPrecio())));
        colHorario.setCellValueFactory(c -> new SimpleStringProperty(Entrenador.describirTurno(c.getValue().getHorario())));
        colDias.setCellValueFactory(c -> new SimpleStringProperty(Membresia.describirDias(c.getValue().getTipoDias())));
        colEntrenador.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEntrenador() == null ? "Sin entrenador" : c.getValue().getEntrenador().getNombre() + " " + c.getValue().getEntrenador().getApellidos()));
        colVencimiento.setCellValueFactory(c -> c.getValue().fechaVencimientoProperty());
        colEstado.setCellValueFactory(c -> {
            String estado = "ACTIVA";
            if (!c.getValue().isActiva()) {
                estado = "INACTIVA";
            } else if (c.getValue().getFechaVencimiento().isBefore(LocalDate.now())) {
                estado = "VENCIDA";
            }
            return new SimpleStringProperty(estado);
        });
        cargarDatos();
    }

    private void cargarDatos() {
        listaMembresias = FXCollections.observableArrayList(membresiaDao.listAll());
        handleSearch();
    }

    @FXML
    private void handleSearch() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        ObservableList<Membresia> filtradas = FXCollections.observableArrayList();
        for (Membresia membresia : listaMembresias) {
            if (membresia.getCliente().getDni().contains(filtro) || (membresia.getCliente().getNombre() + " " + membresia.getCliente().getApellidos()).toLowerCase().contains(filtro)) {
                filtradas.add(membresia);
            }
        }
        tableMembresia.setItems(filtradas);
    }

    private Membresia seleccionar() {
        Membresia seleccionado = tableMembresia.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilAlerts.mostrarAlerts("Selección requerida", "Seleccione una membresía de la tabla antes de realizar esta acción.", "WARNING").showAndWait();
            return null;
        }
        Membresia actual = membresiaDao.searchById(seleccionado.getIdmembresia());
        if (actual == null) {
            UtilAlerts.mostrarAlerts("Membresía no disponible", "La membresía seleccionada ya no está disponible.", "WARNING").showAndWait();
            cargarDatos();
            return null;
        }
        if (usuario != null) actual.setIdUsuario(usuario.getIdusuario());
        return actual;
    }

    @FXML private void handleNew() { abrirFormulario(null, false); }
    @FXML private void handleEdit() {
        Membresia seleccionada = seleccionar();
        if (seleccionada != null) abrirFormulario(seleccionada, false);
    }

    private void abrirFormulario(Membresia membresia, boolean nuevaAdquisicion) {
        if (usuario == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pe/edu/upeu/fitlab_final/views/membresia-form.fxml"));
            Parent root = loader.load();
            MembresiaFormController controller = loader.getController();
            if (nuevaAdquisicion) controller.setNuevaMembresia(membresia);
            else controller.setMembresia(membresia);
            Stage ventana = new Stage();
            ventana.setTitle(membresia == null || nuevaAdquisicion ? "Nueva membresía" : "Editar horario de membresía");
            ventana.initOwner(tableMembresia.getScene().getWindow());
            ventana.initModality(Modality.WINDOW_MODAL);
            ventana.setScene(new Scene(root));
            ventana.setResizable(false);
            ventana.showAndWait();
            if (controller.isSaved()) {
                Membresia procesada = controller.getMembresia();
                procesada.setIdUsuario(usuario.getIdusuario());
                boolean guardado;
                boolean registroExistente = membresia == null && procesada.getIdmembresia() > 0;
                if (nuevaAdquisicion || registroExistente) {
                    guardado = membresiaDao.nuevaMembresia(procesada);
                } else if (membresia == null) {
                    guardado = membresiaDao.insert(procesada);
                } else {
                    guardado = membresiaDao.update(procesada);
                }
                String mensaje = (nuevaAdquisicion || registroExistente) && guardado ? "Nueva membresía registrada." + detalleComprobante(procesada) : "Membresía guardada.";
                resultado(guardado, mensaje, "No se pudo guardar la membresía. Compruebe los datos, los cupos del horario y el estado actual del socio.");
            }
        } catch (IOException e) {
            UtilAlerts.mostrarAlerts("Error", "No se pudo abrir el formulario: " + e.getMessage(), "ERROR").showAndWait();
        }
    }

    private boolean confirmar(String accion, Membresia m) {
        String mensaje = "¿Desea " + accion.toLowerCase() + " la membresía de " + m.getCliente().getNombre() + " " + m.getCliente().getApellidos() + "?";
        if ("Cancelar".equals(accion)) mensaje += "\n\nSe anulará el último comprobante de pago asociado, si existe." + detalleComprobante(m);
        if ("Migrar".equals(accion) && m.getFechaVencimiento().isBefore(LocalDate.now())) mensaje += "\n\nEl cambio de plan no renueva la vigencia. Use Renovar para iniciar un nuevo periodo.";
        return UtilAlerts.mostrarAlerts("Confirmar " + accion, mensaje, "CONFIRMATION").showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    @FXML
    private void handleCancelar() {
        Membresia m = seleccionar();
        if (m == null) return;
        if (!m.isActiva()) {
            informar("Membresía inactiva", "La membresía ya se encuentra inactiva." + detalleComprobante(m));
            return;
        }
        if (!confirmar("Cancelar", m)) return;
        boolean cancelada = membresiaDao.cancelar(m);
        resultado(cancelada, cancelada ? "Membresía cancelada." + detalleComprobante(m) : "", "No se pudo completar la cancelación. La membresía y su comprobante conservan su estado anterior. Vuelva a intentarlo.");
    }

    @FXML
    private void handleReactivar() {
        Membresia m = seleccionar();
        if (m == null) return;
        if (m.isActiva()) {
            if (m.getFechaVencimiento().isBefore(LocalDate.now())) {
                informar("Membresía vencida", "La membresía está vencida. Use Renovar para iniciar un nuevo periodo.");
            } else {
                informar("Membresía activa", "La membresía ya se encuentra activa.");
            }
            return;
        }
        ButtonType nueva = new ButtonType("Nueva membresía", ButtonBar.ButtonData.OTHER);
        ButtonType solo = new ButtonType("Solo reactivación", ButtonBar.ButtonData.OK_DONE);
        Dialog<ButtonType> opciones = new Dialog<>();
        opciones.initOwner(tableMembresia.getScene().getWindow());
        opciones.setTitle("Reactivar membresía");
        opciones.setHeaderText("¿Cómo desea continuar con la membresía de " + m.getCliente().getNombre() + "?");
        Label explicacion = new Label("Nueva membresía permite elegir otro plan y horario e inicia un periodo desde hoy. Solo reactivación conserva el plan y horario actuales y permite elegir la vigencia.");
        explicacion.setWrapText(true);
        explicacion.setMaxWidth(480);
        opciones.getDialogPane().setContent(explicacion);
        opciones.getDialogPane().getButtonTypes().setAll(nueva, solo, ButtonType.CANCEL);
        ButtonType elegida = opciones.showAndWait().orElse(ButtonType.CANCEL);
        if (elegida == nueva) {
            abrirFormulario(m, true);
            return;
        }
        if (elegida != solo) return;
        Boolean conservarVigencia = elegirVigencia(m);
        if (conservarVigencia == null) return;
        LocalDate inicio = conservarVigencia ? m.getFechaInicio() : LocalDate.now();
        LocalDate vencimiento = conservarVigencia ? m.getFechaVencimiento() : inicio.plusDays(m.getPlan().getDuracionDias());
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        double total = m.getPlan().getPrecio() - m.getCliente().calcularDescuento(m.getPlan().getPrecio());
        String mensaje = "¿Reactivar la membresía de " + m.getCliente().getNombre() + " " + m.getCliente().getApellidos() + "?\n\nVigencia: " + inicio.format(formato) + " – " + vencimiento.format(formato) + ".\nPlan: " + m.getPlan().getNombre() + String.format(".\nNuevo pago: S/ %.2f.", total) + "\nSe generará un nuevo comprobante de pago en estado Enviado.";
        Alert confirmacion = UtilAlerts.mostrarAlerts("Confirmar reactivación", mensaje, "CONFIRMATION");
        confirmacion.initOwner(tableMembresia.getScene().getWindow());
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        boolean reactivada = membresiaDao.reactivar(m, conservarVigencia);
        resultado(reactivada, reactivada ? "Membresía reactivada." + detalleComprobante(m) : "", "No se pudo reactivar la membresía. Compruebe los cupos de sus días de asistencia y el turno del entrenador, y vuelva a intentarlo.");
    }

    private Boolean elegirVigencia(Membresia m) {
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ButtonType conservar = new ButtonType("Conservar vigencia original", ButtonBar.ButtonData.OTHER);
        ButtonType desdeHoy = new ButtonType("Iniciar desde hoy", ButtonBar.ButtonData.OK_DONE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(tableMembresia.getScene().getWindow());
        dialog.setTitle("Vigencia de reactivación");
        dialog.setHeaderText("Elija la vigencia de la membresía");
        String texto = "Vigencia original: " + m.getFechaInicio().format(formato) + " – " + m.getFechaVencimiento().format(formato) + ".\nDesde hoy: " + hoy.format(formato) + " – " + hoy.plusDays(m.getPlan().getDuracionDias()).format(formato) + ".";
        boolean vencida = m.getFechaVencimiento().isBefore(hoy);
        if (vencida) texto += "\n\nLa vigencia original ya venció. Puede iniciar desde hoy o cancelar para elegir una nueva membresía.";
        Label fechas = new Label(texto);
        fechas.setWrapText(true);
        fechas.setMaxWidth(480);
        dialog.getDialogPane().setContent(fechas);
        dialog.getDialogPane().getButtonTypes().setAll(conservar, desdeHoy, ButtonType.CANCEL);
        dialog.getDialogPane().lookupButton(conservar).setDisable(vencida);
        ButtonType elegida = dialog.showAndWait().orElse(ButtonType.CANCEL);
        if (elegida == conservar && !vencida) return true;
        if (elegida == desdeHoy) return false;
        return null;
    }

    @FXML
    private void handleRenovar() {
        Membresia m = seleccionar();
        if (m == null) return;
        if (!m.isActiva()) {
            informar("Membresía inactiva", "Use la opción Reactivar para volver a poner vigente esta membresía.");
            return;
        }
        if (m.getFechaVencimiento().isAfter(LocalDate.now().plusDays(3))) {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String mensaje = "La membresía vence el " + m.getFechaVencimiento().format(formato) + ". La renovación estará disponible desde el " + m.getFechaVencimiento().minusDays(3).format(formato) + " (3 días antes del vencimiento).";
            informar("Renovación no disponible", mensaje);
            return;
        }
        if (!confirmar("Renovar", m)) return;
        boolean renovada = membresiaDao.renovar(m);
        resultado(renovada, renovada ? "Membresía renovada." + detalleComprobante(m) : "", "No se pudo renovar la membresía. Compruebe la disponibilidad del horario y vuelva a intentarlo.");
    }

    @FXML
    private void handleMigrar() {
        Membresia m = seleccionar();
        if (m == null) return;
        if (!m.isActiva()) {
            informar("Membresía inactiva", "Use la opción Reactivar para volver a poner vigente esta membresía.");
            return;
        }
        List<Plan> planes = new ArrayList<>();
        for (Plan plan : planDao.listAll()) {
            if (plan.getIdplan() != m.getPlan().getIdplan()) {
                planes.add(plan);
            }
        }
        if (planes.isEmpty()) {
            informar("Planes disponibles", "No hay otros planes disponibles para esta membresía.");
            return;
        }
        ChoiceDialog<Plan> dialog = new ChoiceDialog<>(planes.getFirst(), planes);
        dialog.initOwner(tableMembresia.getScene().getWindow());
        dialog.setTitle("Migrar plan");
        dialog.setHeaderText("Seleccione el nuevo plan de " + m.getCliente().getNombre());
        dialog.setContentText("Plan:");
        Optional<Plan> elegido = dialog.showAndWait();
        if (elegido.isPresent()) {
            Plan plan = elegido.get();
            if (!confirmar("Migrar", m)) return;
            boolean migrada = membresiaDao.migrarPlan(m, plan);
            resultado(migrada, migrada ? "Plan actualizado a " + plan.getNombre() + "." + detalleComprobante(m) : "", "No se pudo cambiar el plan. Compruebe que siga disponible y vuelva a intentarlo.");
        }
    }

    private String detalleComprobante(Membresia membresia) {
        List<Comprobante> comprobantes = comprobanteDao.searchByDni(membresia.getCliente().getDni());
        if (comprobantes.isEmpty()) return "\nNo tiene comprobantes de pago asociados.";
        Comprobante ultimo = comprobantes.getFirst();
        return "\nComprobante de pago " + ultimo.getCodigo() + ": " + ultimo.getEstado() + ".";
    }

    private void informar(String titulo, String mensaje) {
        UtilAlerts.mostrarAlerts(titulo, mensaje, "INFORMATION").showAndWait();
    }

    private void resultado(boolean correcto, String mensaje, String error) {
        UtilAlerts.mostrarAlerts(correcto ? "Operación realizada" : "No se pudo realizar", correcto ? mensaje : error, correcto ? "INFORMATION" : "WARNING").showAndWait();
        cargarDatos();
    }
}
