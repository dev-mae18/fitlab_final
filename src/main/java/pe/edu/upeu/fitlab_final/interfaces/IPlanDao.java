package pe.edu.upeu.fitlab_final.interfaces;

import pe.edu.upeu.fitlab_final.modelo.Plan;
import java.util.List;

public interface IPlanDao {
    public Plan searchById(int id);
    public List<Plan> listAll();
}
