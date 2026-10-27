package pe.edu.upeu.fitlab_final.implementacion;

import pe.edu.upeu.fitlab_final.interfaces.IEntrenadorDao;
import pe.edu.upeu.fitlab_final.modelo.Entrenador;
import pe.edu.upeu.fitlab_final.utils.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntrenadorDaoImpl implements IEntrenadorDao {
    @Override
    public boolean insert(Entrenador e) {
        boolean flag = false;
        String query = "INSERT INTO entrenadores(nombre,apellidos,dni,especialidad,turno) VALUES(?,?,?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, e.getNombre());
            st.setString(2, e.getApellidos());
            st.setString(3, e.getDni());
            st.setString(4, e.getEspecialidad());
            st.setString(5, e.getTurno());
            st.executeUpdate();
            flag = true;
            try (Statement id = cn.createStatement();
                 ResultSet rs = id.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) e.setIdentrenador(rs.getInt(1));
            }
        } catch (SQLException ex) {
            flag = false;
            System.out.println("Error al insertar entrenador: " + ex.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Entrenador e) {
        boolean flag = false;
        String query = "UPDATE entrenadores SET nombre=?,apellidos=?,dni=?,especialidad=?,turno=? WHERE identrenador=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            String consulta = "SELECT COUNT(*) FROM membresias WHERE identrenador=? AND horario<>?";
            try (PreparedStatement asignaciones = cn.prepareStatement(consulta)) {
                asignaciones.setInt(1, e.getIdentrenador());
                asignaciones.setString(2, e.getTurno());
                try (ResultSet rs = asignaciones.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) return flag;
                }
            }
            st.setString(1, e.getNombre());
            st.setString(2, e.getApellidos());
            st.setString(3, e.getDni());
            st.setString(4, e.getEspecialidad());
            st.setString(5, e.getTurno());
            st.setInt(6, e.getIdentrenador());
            st.executeUpdate();
            flag = true;
        } catch (SQLException ex) {
            flag = false;
            System.out.println("Error al actualizar entrenador: " + ex.getMessage());
        }
        return flag;
    }

    @Override
    public boolean delete(Entrenador e) {
        boolean flag = false;
        String query = "DELETE FROM entrenadores WHERE identrenador=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, e.getIdentrenador());
            st.executeUpdate();
            flag = true;
        } catch (SQLException ex) {
            flag = false;
            System.out.println("Error al eliminar entrenador: " + ex.getMessage());
        }
        return flag;
    }

    @Override
    public Entrenador searchById(int id) {
        Entrenador e = null;
        String query = "SELECT * FROM entrenadores WHERE identrenador=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    e = new Entrenador();
                    e.setIdentrenador(rs.getInt("identrenador"));
                    e.setNombre(rs.getString("nombre"));
                    e.setApellidos(rs.getString("apellidos"));
                    e.setDni(rs.getString("dni"));
                    e.setEspecialidad(rs.getString("especialidad"));
                    e.setTurno(rs.getString("turno"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar entrenador por ID: " + ex.getMessage());
        }
        return e;
    }

    @Override
    public Entrenador searchByDni(String dni) {
        Entrenador e = null;
        String query = "SELECT * FROM entrenadores WHERE dni=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, dni);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    e = new Entrenador();
                    e.setIdentrenador(rs.getInt("identrenador"));
                    e.setNombre(rs.getString("nombre"));
                    e.setApellidos(rs.getString("apellidos"));
                    e.setDni(rs.getString("dni"));
                    e.setEspecialidad(rs.getString("especialidad"));
                    e.setTurno(rs.getString("turno"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar entrenador por DNI: " + ex.getMessage());
        }
        return e;
    }

    @Override
    public List<Entrenador> listAll() {
        List<Entrenador> lista = new ArrayList<>();
        String query = "SELECT * FROM entrenadores ORDER BY identrenador";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Entrenador e = new Entrenador();
                e.setIdentrenador(rs.getInt("identrenador"));
                e.setNombre(rs.getString("nombre"));
                e.setApellidos(rs.getString("apellidos"));
                e.setDni(rs.getString("dni"));
                e.setEspecialidad(rs.getString("especialidad"));
                e.setTurno(rs.getString("turno"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar entrenadores: " + ex.getMessage());
        }
        return lista;
    }
}
