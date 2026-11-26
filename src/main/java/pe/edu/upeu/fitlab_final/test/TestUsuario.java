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
            System.out.println("NOMBRE: " + usuario.getNombre());
            System.out.println("APELLIDOS: " + usuario.getApellidos());
            System.out.println("DNI: " + usuario.getDni());
        } else {
            System.out.println("Usuario o contraseña incorrectos");
        }
    }

    public static void main(String[] args) {
        validarLogin();
    }
}
