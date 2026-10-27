package pe.edu.upeu.fitlab_final.modelo;

import javafx.beans.property.*;

public class Comprobante {
    private final IntegerProperty idcomprobante = new SimpleIntegerProperty();
    private final StringProperty codigo = new SimpleStringProperty();
    private final ObjectProperty<Cliente> cliente = new SimpleObjectProperty<>();
    private final StringProperty tipo = new SimpleStringProperty();
    private final StringProperty estado = new SimpleStringProperty();
    private final DoubleProperty subtotal = new SimpleDoubleProperty();
    private final DoubleProperty descuento = new SimpleDoubleProperty();
    private final DoubleProperty total = new SimpleDoubleProperty();
    private final StringProperty fecha = new SimpleStringProperty();
    private final IntegerProperty idUsuario = new SimpleIntegerProperty();

    public Comprobante() {
    }

    public Comprobante(int idcomprobante, String codigo, Cliente cliente, String tipo, String estado, double subtotal, double descuento, double total, String fecha, int idUsuario) {
        setIdcomprobante(idcomprobante);
        setCodigo(codigo);
        setCliente(cliente);
        setTipo(tipo);
        setEstado(estado);
        setSubtotal(subtotal);
        setDescuento(descuento);
        setTotal(total);
        setFecha(fecha);
        setIdUsuario(idUsuario);
    }

    public final int getIdcomprobante() {
        return idcomprobante.get();
    }

    public final void setIdcomprobante(int value) {
        idcomprobante.set(value);
    }

    public final IntegerProperty idcomprobanteProperty() {
        return idcomprobante;
    }

    public final String getCodigo() {
        return codigo.get();
    }

    public final void setCodigo(String value) {
        codigo.set(value);
    }

    public final StringProperty codigoProperty() {
        return codigo;
    }

    public final Cliente getCliente() {
        return cliente.get();
    }

    public final void setCliente(Cliente value) {
        cliente.set(value);
    }

    public final ObjectProperty<Cliente> clienteProperty() {
        return cliente;
    }

    public final String getTipo() {
        return tipo.get();
    }

    public final void setTipo(String value) {
        tipo.set(value);
    }

    public final StringProperty tipoProperty() {
        return tipo;
    }

    public final String getEstado() {
        return estado.get();
    }

    public final void setEstado(String value) {
        estado.set(value);
    }

    public final StringProperty estadoProperty() {
        return estado;
    }

    public final double getSubtotal() {
        return subtotal.get();
    }

    public final void setSubtotal(double value) {
        subtotal.set(value);
    }

    public final DoubleProperty subtotalProperty() {
        return subtotal;
    }

    public final double getDescuento() {
        return descuento.get();
    }

    public final void setDescuento(double value) {
        descuento.set(value);
    }

    public final DoubleProperty descuentoProperty() {
        return descuento;
    }

    public final double getTotal() {
        return total.get();
    }

    public final void setTotal(double value) {
        total.set(value);
    }

    public final DoubleProperty totalProperty() {
        return total;
    }

    public final String getFecha() {
        return fecha.get();
    }

    public final void setFecha(String value) {
        fecha.set(value);
    }

    public final StringProperty fechaProperty() {
        return fecha;
    }

    public final int getIdUsuario() {
        return idUsuario.get();
    }

    public final void setIdUsuario(int value) {
        idUsuario.set(value);
    }

    public final IntegerProperty idUsuarioProperty() {
        return idUsuario;
    }

}

