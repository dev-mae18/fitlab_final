package pe.edu.upeu.fitlab_final.interfaces;

import pe.edu.upeu.fitlab_final.modelo.Entrenador;
import java.util.List;

public interface IEntrenadorDao {
    public boolean insert(Entrenador e);
    public boolean update(Entrenador e);
    public Entrenador searchById(int id);
    public boolean delete(Entrenador e);
    public List<Entrenador> listAll();
    public Entrenador searchByDni(String dni);
}
