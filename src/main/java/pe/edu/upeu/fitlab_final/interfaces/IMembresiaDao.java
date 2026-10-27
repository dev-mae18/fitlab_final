package pe.edu.upeu.fitlab_final.interfaces;

import pe.edu.upeu.fitlab_final.modelo.Membresia;
import pe.edu.upeu.fitlab_final.modelo.Plan;
import java.util.List;

public interface IMembresiaDao {
    public boolean insert(Membresia e);
    public boolean update(Membresia e);
    public Membresia searchById(int id);
    public Membresia searchByCliente(int idcliente);
    public List<Membresia> listAll();
    public boolean cancelar(Membresia e);
    public boolean reactivar(Membresia e, boolean conservarVigencia);
    public boolean nuevaMembresia(Membresia e);
    public boolean renovar(Membresia e);
    public boolean migrarPlan(Membresia e, Plan plan);
}
