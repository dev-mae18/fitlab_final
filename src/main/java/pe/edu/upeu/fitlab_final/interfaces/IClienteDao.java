package pe.edu.upeu.fitlab_final.interfaces;

import pe.edu.upeu.fitlab_final.modelo.Cliente;
import java.util.List;

public interface IClienteDao {
    public boolean insert(Cliente e);
    public boolean update(Cliente e);
    public Cliente searchById(int id);
    public boolean delete(Cliente e);
    public List<Cliente> listAll();
    public Cliente searchByDni(String dni);
}
