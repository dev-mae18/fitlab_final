package pe.edu.upeu.fitlab_final.modelo;

import javafx.beans.property.*;

public class Usuario {
    private final IntegerProperty idusuario = new SimpleIntegerProperty();
    private final StringProperty usuario = new SimpleStringProperty();

    public Usuario() {
    }

    public Usuario(int idusuario, String usuario) {
        setIdusuario(idusuario);
        setUsuario(usuario);
    }

    public final int getIdusuario() {
        return idusuario.get();
    }

    public final void setIdusuario(int value) {
        idusuario.set(value);
    }

    public final IntegerProperty idusuarioProperty() {
        return idusuario;
    }

    public final String getUsuario() {
        return usuario.get();
    }

    public final void setUsuario(String value) {
        usuario.set(value);
    }

    public final StringProperty usuarioProperty() {
        return usuario;
    }

}

