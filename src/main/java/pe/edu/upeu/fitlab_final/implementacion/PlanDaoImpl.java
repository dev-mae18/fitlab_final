package pe.edu.upeu.fitlab_final.implementacion;

import pe.edu.upeu.fitlab_final.interfaces.IPlanDao;
import pe.edu.upeu.fitlab_final.modelo.Plan;
import pe.edu.upeu.fitlab_final.utils.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanDaoImpl implements IPlanDao {
    @Override
    public Plan searchById(int id) {
        Plan e = null;
        String query = "SELECT idplan,nombre,precio,duracionDias FROM planes WHERE idplan=?";
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
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar plan: " + ex.getMessage());
        }
        return e;
    }

    @Override
    public List<Plan> listAll() {
        List<Plan> lista = new ArrayList<>();
        String query = "SELECT idplan,nombre,precio,duracionDias FROM planes ORDER BY idplan";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Plan e = new Plan();
                e.setIdplan(rs.getInt("idplan"));
                e.setNombre(rs.getString("nombre"));
                e.setPrecio(rs.getDouble("precio"));
                e.setDuracionDias(rs.getInt("duracionDias"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar planes: " + ex.getMessage());
        }
        return lista;
    }
}
