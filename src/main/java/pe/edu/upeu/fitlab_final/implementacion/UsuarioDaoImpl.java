package pe.edu.upeu.fitlab_final.implementacion;

import pe.edu.upeu.fitlab_final.interfaces.IUsuarioDao;
import pe.edu.upeu.fitlab_final.modelo.Usuario;
import pe.edu.upeu.fitlab_final.utils.ConexionDB;
import java.sql.*;

public class UsuarioDaoImpl implements IUsuarioDao {
    @Override
    public Usuario autenticar(String usuario, String contrasena) {
        Usuario e = null;
        String query = "SELECT idusuario,usuario FROM usuarios WHERE usuario=? AND contrasena=? AND activo=1";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, usuario);
            st.setString(2, contrasena);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    e = new Usuario();
                    e.setIdusuario(rs.getInt("idusuario"));
                    e.setUsuario(rs.getString("usuario"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al iniciar sesión: " + ex.getMessage());
        }
        return e;
    }

    @Override
    public boolean cambiarContrasena(int idusuario, String actual, String nueva) {
        boolean flag = false;
        if (actual == null || nueva == null || nueva.isBlank()) return flag;
        String query = "SELECT contrasena FROM usuarios WHERE idusuario=? AND activo=1";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idusuario);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next() || !actual.equals(rs.getString("contrasena"))) return flag;
            }
            try (PreparedStatement cambio = cn.prepareStatement("UPDATE usuarios SET contrasena=? WHERE idusuario=?")) {
                cambio.setString(1, nueva);
                cambio.setInt(2, idusuario);
                cambio.executeUpdate();
                flag = true;
            }
        } catch (SQLException ex) {
            flag = false;
            System.out.println("Error al cambiar contraseña: " + ex.getMessage());
        }
        return flag;
    }
}
