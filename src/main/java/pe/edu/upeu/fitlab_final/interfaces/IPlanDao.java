package pe.edu.upeu.fitlab_final.interfaces;

import pe.edu.upeu.fitlab_final.modelo.Plan;
import java.util.List;

public interface IPlanDao {
    public boolean insert(Plan e);
    public boolean update(Plan e);
    public Plan searchById(int id);
    public Plan searchByNombre(String nombre);
    public List<Plan> listAll();
    public boolean estaEnUso(int idplan);
}
