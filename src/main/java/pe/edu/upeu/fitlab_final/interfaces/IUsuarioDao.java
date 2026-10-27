package pe.edu.upeu.fitlab_final.interfaces;

import pe.edu.upeu.fitlab_final.modelo.Usuario;

public interface IUsuarioDao {
    public Usuario autenticar(String usuario, String contrasena);
    public boolean cambiarContrasena(int idusuario, String actual, String nueva);
}
