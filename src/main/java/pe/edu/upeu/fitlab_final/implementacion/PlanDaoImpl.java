package pe.edu.upeu.fitlab_final.implementacion;

import pe.edu.upeu.fitlab_final.interfaces.IPlanDao;
import pe.edu.upeu.fitlab_final.modelo.Plan;
import pe.edu.upeu.fitlab_final.utils.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanDaoImpl implements IPlanDao {
    @Override
    public boolean insert(Plan e) {
        boolean flag = false;
        if (!validar(e)) return flag;
        String query = "INSERT INTO planes(nombre,precio,duracionDias,meses,promocion) " +
                "SELECT ?,?,?,?,? WHERE NOT EXISTS " +
                "(SELECT 1 FROM planes WHERE lower(trim(nombre))=lower(trim(?)))";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, e.getNombre().trim());
            st.setDouble(2, e.getPrecio());
            st.setInt(3, e.getDuracionDias());
            st.setInt(4, e.getMeses());
            st.setBoolean(5, e.isPromocion());
            st.setString(6, e.getNombre().trim());
            if (st.executeUpdate() > 0) {
                try (Statement id = cn.createStatement(); ResultSet rs = id.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) e.setIdplan(rs.getInt(1));
                }
                e.setNombre(e.getNombre().trim());
                flag = true;
            }
        } catch (SQLException ex) {
            System.out.println("Error al insertar plan: " + ex.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Plan e) {
        boolean flag = false;
        if (!validar(e) || e.getIdplan() <= 0) return flag;
        String query = "UPDATE planes SET nombre=?,precio=?,duracionDias=?,meses=?,promocion=? " +
                "WHERE idplan=? AND NOT EXISTS " +
                "(SELECT 1 FROM planes p WHERE p.idplan<>? AND lower(trim(p.nombre))=lower(trim(?))) " +
                "AND (NOT EXISTS (SELECT 1 FROM membresias m WHERE m.idplan=planes.idplan) " +
                "OR (promocion=? AND meses=? AND duracionDias=?))";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, e.getNombre().trim());
            st.setDouble(2, e.getPrecio());
            st.setInt(3, e.getDuracionDias());
            st.setInt(4, e.getMeses());
            st.setBoolean(5, e.isPromocion());
            st.setInt(6, e.getIdplan());
            st.setInt(7, e.getIdplan());
            st.setString(8, e.getNombre().trim());
            st.setBoolean(9, e.isPromocion());
            st.setInt(10, e.getMeses());
            st.setInt(11, e.getDuracionDias());
            flag = st.executeUpdate() > 0;
            if (flag) e.setNombre(e.getNombre().trim());
        } catch (SQLException ex) {
            System.out.println("Error al actualizar plan: " + ex.getMessage());
        }
        return flag;
    }

    private boolean validar(Plan e) {
        if (e == null || e.getNombre() == null || e.getNombre().trim().isEmpty()) return false;
        if (!Double.isFinite(e.getPrecio()) || e.getPrecio() <= 0 || e.getDuracionDias() <= 0) return false;
        if (e.isPromocion()) return e.getMeses() >= 2 && e.getMeses() <= 12;
        return e.getMeses() == 1;
    }

    @Override
    public Plan searchById(int id) {
        Plan e = null;
        String query = "SELECT idplan,nombre,precio,duracionDias,meses,promocion FROM planes WHERE idplan=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    e = new Plan();
                    e.setIdplan(rs.getInt("idplan"));
                    e.setNombre(rs.getString("nombre"));
                    e.setPrecio(rs.getDouble("precio"));
                    e.setDuracionDias(rs.getInt("duracionDias"));
                    e.setMeses(rs.getInt("meses"));
                    e.setPromocion(rs.getBoolean("promocion"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar plan: " + ex.getMessage());
        }
        return e;
    }

    @Override
    public Plan searchByNombre(String nombre) {
        Plan e = null;
        if (nombre == null || nombre.trim().isEmpty()) return e;
        String query = "SELECT idplan,nombre,precio,duracionDias,meses,promocion " +
                "FROM planes WHERE lower(trim(nombre))=lower(trim(?)) ORDER BY idplan LIMIT 1";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, nombre.trim());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    e = new Plan();
                    e.setIdplan(rs.getInt("idplan"));
                    e.setNombre(rs.getString("nombre"));
                    e.setPrecio(rs.getDouble("precio"));
                    e.setDuracionDias(rs.getInt("duracionDias"));
                    e.setMeses(rs.getInt("meses"));
                    e.setPromocion(rs.getBoolean("promocion"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar plan por nombre: " + ex.getMessage());
        }
        return e;
    }

    @Override
    public List<Plan> listAll() {
        List<Plan> lista = new ArrayList<>();
        String query = "SELECT idplan,nombre,precio,duracionDias,meses,promocion FROM planes ORDER BY idplan";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Plan e = new Plan();
                e.setIdplan(rs.getInt("idplan"));
                e.setNombre(rs.getString("nombre"));
                e.setPrecio(rs.getDouble("precio"));
                e.setDuracionDias(rs.getInt("duracionDias"));
                e.setMeses(rs.getInt("meses"));
                e.setPromocion(rs.getBoolean("promocion"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar planes: " + ex.getMessage());
        }
        return lista;
    }

    @Override
    public boolean estaEnUso(int idplan) {
        boolean flag = false;
        String query = "SELECT 1 FROM membresias WHERE idplan=? LIMIT 1";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idplan);
            try (ResultSet rs = st.executeQuery()) {
                flag = rs.next();
            }
        } catch (SQLException ex) {
            System.out.println("Error al consultar uso del plan: " + ex.getMessage());
        }
        return flag;
    }
}
