package pe.edu.upeu.fitlab_final.test;

import pe.edu.upeu.fitlab_final.implementacion.UsuarioDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IUsuarioDao;
import pe.edu.upeu.fitlab_final.modelo.Usuario;

public class TestUsuario {
    private static IUsuarioDao usuarioDao = new UsuarioDaoImpl();

    public static void validarLogin() {
        Usuario usuario = usuarioDao.login("admin", "admin123");
        if (usuario != null) {
            System.out.println("LOGIN CORRECTO");
            System.out.println("ID_USUARIO: " + usuario.getIdusuario());
            System.out.println("USUARIO: " + usuario.getUsuario());
        } else {
            System.out.println("Usuario o contraseña incorrectos");
        }
    }

    public static void main(String[] args) {
        validarLogin();
    }
}
