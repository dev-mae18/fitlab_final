package pe.edu.upeu.fitlab_final.implementacion;

import pe.edu.upeu.fitlab_final.interfaces.IComprobanteDao;
import pe.edu.upeu.fitlab_final.modelo.Comprobante;
import pe.edu.upeu.fitlab_final.modelo.Cliente;
import pe.edu.upeu.fitlab_final.utils.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComprobanteDaoImpl implements IComprobanteDao {
    @Override
    public List<Comprobante> listAll() {
        List<Comprobante> lista = new ArrayList<>();
        String query = "SELECT r.*,c.nombre,c.apellidos,c.dni,c.edad,c.genero,c.estudiante,c.alergias FROM comprobantes r JOIN clientes c ON c.idcliente=r.idcliente ORDER BY r.idcomprobante DESC";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdcliente(rs.getInt("idcliente"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellidos(rs.getString("apellidos"));
                cliente.setDni(rs.getString("dni"));
                cliente.setEdad(rs.getInt("edad"));
                cliente.setGenero(rs.getString("genero"));
                cliente.setEstudiante(rs.getBoolean("estudiante"));
                cliente.setAlergias(rs.getString("alergias"));
                Comprobante e = new Comprobante();
                e.setIdcomprobante(rs.getInt("idcomprobante"));
                e.setCodigo(rs.getString("codigo"));
                e.setCliente(cliente);
                e.setTipo(rs.getString("tipo"));
                e.setEstado(rs.getString("estado"));
                e.setSubtotal(rs.getDouble("subtotal"));
                e.setDescuento(rs.getDouble("descuento"));
                e.setTotal(rs.getDouble("total"));
                e.setFecha(rs.getString("fecha"));
                e.setIdUsuario(rs.getInt("idusuario"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar comprobantes: " + ex.getMessage());
        }
        return lista;
    }

    @Override
    public List<Comprobante> searchByDni(String dni) {
        List<Comprobante> lista = new ArrayList<>();
        String query = "SELECT r.*,c.nombre,c.apellidos,c.dni,c.edad,c.genero,c.estudiante,c.alergias FROM comprobantes r JOIN clientes c ON c.idcliente=r.idcliente WHERE c.dni=? ORDER BY r.idcomprobante DESC";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, dni);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setIdcliente(rs.getInt("idcliente"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setApellidos(rs.getString("apellidos"));
                    cliente.setDni(rs.getString("dni"));
                    cliente.setEdad(rs.getInt("edad"));
                    cliente.setGenero(rs.getString("genero"));
                    cliente.setEstudiante(rs.getBoolean("estudiante"));
                    cliente.setAlergias(rs.getString("alergias"));
                    Comprobante e = new Comprobante();
                    e.setIdcomprobante(rs.getInt("idcomprobante"));
                    e.setCodigo(rs.getString("codigo"));
                    e.setCliente(cliente);
                    e.setTipo(rs.getString("tipo"));
                    e.setEstado(rs.getString("estado"));
                    e.setSubtotal(rs.getDouble("subtotal"));
                    e.setDescuento(rs.getDouble("descuento"));
                    e.setTotal(rs.getDouble("total"));
                    e.setFecha(rs.getString("fecha"));
                    e.setIdUsuario(rs.getInt("idusuario"));
                    lista.add(e);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar comprobantes por DNI: " + ex.getMessage());
        }
        return lista;
    }

    @Override
    public Comprobante searchByCodigo(String codigo) {
        Comprobante e = null;
        String query = "SELECT r.*,c.nombre,c.apellidos,c.dni,c.edad,c.genero,c.estudiante,c.alergias FROM comprobantes r JOIN clientes c ON c.idcliente=r.idcliente WHERE r.codigo=? COLLATE NOCASE";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, codigo);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setIdcliente(rs.getInt("idcliente"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setApellidos(rs.getString("apellidos"));
                    cliente.setDni(rs.getString("dni"));
                    cliente.setEdad(rs.getInt("edad"));
                    cliente.setGenero(rs.getString("genero"));
                    cliente.setEstudiante(rs.getBoolean("estudiante"));
                    cliente.setAlergias(rs.getString("alergias"));
                    e = new Comprobante();
                    e.setIdcomprobante(rs.getInt("idcomprobante"));
                    e.setCodigo(rs.getString("codigo"));
                    e.setCliente(cliente);
                    e.setTipo(rs.getString("tipo"));
                    e.setEstado(rs.getString("estado"));
                    e.setSubtotal(rs.getDouble("subtotal"));
                    e.setDescuento(rs.getDouble("descuento"));
                    e.setTotal(rs.getDouble("total"));
                    e.setFecha(rs.getString("fecha"));
                    e.setIdUsuario(rs.getInt("idusuario"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar comprobante por código: " + ex.getMessage());
        }
        return e;
    }
}
