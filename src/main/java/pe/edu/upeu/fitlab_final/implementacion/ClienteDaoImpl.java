package pe.edu.upeu.fitlab_final.implementacion;

import pe.edu.upeu.fitlab_final.interfaces.IClienteDao;
import pe.edu.upeu.fitlab_final.modelo.Cliente;
import pe.edu.upeu.fitlab_final.utils.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDaoImpl implements IClienteDao {
    @Override
    public boolean insert(Cliente e) {
        boolean flag = false;
        String query = "INSERT INTO clientes(nombre,apellidos,dni,edad,genero,estudiante,alergias) VALUES(?,?,?,?,?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            int cantidad = 0;
            try (Statement limite = cn.createStatement();
                 ResultSet rs = limite.executeQuery("SELECT COUNT(*) FROM clientes")) {
                if (rs.next()) cantidad = rs.getInt(1);
            }
            if (cantidad < 750) {
                st.setString(1, e.getNombre());
                st.setString(2, e.getApellidos());
                st.setString(3, e.getDni());
                st.setInt(4, e.getEdad());
                st.setString(5, e.getGenero());
                st.setBoolean(6, e.isEstudiante());
                st.setString(7, e.getAlergias());
                st.executeUpdate();
                flag = true;
                try (Statement id = cn.createStatement();
                     ResultSet rs = id.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) e.setIdcliente(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            flag = false;
            System.out.println("Error al insertar cliente: " + ex.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Cliente e) {
        boolean flag = false;
        String query = "UPDATE clientes SET nombre=?,apellidos=?,dni=?,edad=?,genero=?,estudiante=?,alergias=? WHERE idcliente=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, e.getNombre());
            st.setString(2, e.getApellidos());
            st.setString(3, e.getDni());
            st.setInt(4, e.getEdad());
            st.setString(5, e.getGenero());
            st.setBoolean(6, e.isEstudiante());
            st.setString(7, e.getAlergias());
            st.setInt(8, e.getIdcliente());
            st.executeUpdate();
            flag = true;
        } catch (SQLException ex) {
            flag = false;
            System.out.println("Error al actualizar cliente: " + ex.getMessage());
        }
        return flag;
    }

    @Override
    public boolean delete(Cliente e) {
        boolean flag = false;
        String query = "DELETE FROM clientes WHERE idcliente=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, e.getIdcliente());
            st.executeUpdate();
            flag = true;
        } catch (SQLException ex) {
            flag = false;
            System.out.println("Error al eliminar cliente: " + ex.getMessage());
        }
        return flag;
    }

    @Override
    public Cliente searchById(int id) {
        Cliente e = null;
        String query = "SELECT * FROM clientes WHERE idcliente=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    e = new Cliente();
                    e.setIdcliente(rs.getInt("idcliente"));
                    e.setNombre(rs.getString("nombre"));
                    e.setApellidos(rs.getString("apellidos"));
                    e.setDni(rs.getString("dni"));
                    e.setEdad(rs.getInt("edad"));
                    e.setGenero(rs.getString("genero"));
                    e.setEstudiante(rs.getBoolean("estudiante"));
                    e.setAlergias(rs.getString("alergias"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar cliente por ID: " + ex.getMessage());
        }
        return e;
    }

    @Override
    public Cliente searchByDni(String dni) {
        Cliente e = null;
        String query = "SELECT * FROM clientes WHERE dni=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, dni);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    e = new Cliente();
                    e.setIdcliente(rs.getInt("idcliente"));
                    e.setNombre(rs.getString("nombre"));
                    e.setApellidos(rs.getString("apellidos"));
                    e.setDni(rs.getString("dni"));
                    e.setEdad(rs.getInt("edad"));
                    e.setGenero(rs.getString("genero"));
                    e.setEstudiante(rs.getBoolean("estudiante"));
                    e.setAlergias(rs.getString("alergias"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar cliente por DNI: " + ex.getMessage());
        }
        return e;
    }

    @Override
    public List<Cliente> listAll() {
        List<Cliente> lista = new ArrayList<>();
        String query = "SELECT * FROM clientes ORDER BY idcliente";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Cliente e = new Cliente();
                e.setIdcliente(rs.getInt("idcliente"));
                e.setNombre(rs.getString("nombre"));
                e.setApellidos(rs.getString("apellidos"));
                e.setDni(rs.getString("dni"));
                e.setEdad(rs.getInt("edad"));
                e.setGenero(rs.getString("genero"));
                e.setEstudiante(rs.getBoolean("estudiante"));
                e.setAlergias(rs.getString("alergias"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar clientes: " + ex.getMessage());
        }
        return lista;
    }
}
