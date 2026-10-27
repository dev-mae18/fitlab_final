package pe.edu.upeu.fitlab_final.interfaces;

import pe.edu.upeu.fitlab_final.modelo.Comprobante;
import java.util.List;

public interface IComprobanteDao {
    public List<Comprobante> listAll();
    public List<Comprobante> searchByDni(String dni);
    public Comprobante searchByCodigo(String codigo);
}
