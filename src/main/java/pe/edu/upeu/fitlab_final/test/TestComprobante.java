package pe.edu.upeu.fitlab_final.test;

import pe.edu.upeu.fitlab_final.implementacion.ComprobanteDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IComprobanteDao;
import pe.edu.upeu.fitlab_final.modelo.Comprobante;
import java.util.List;

public class TestComprobante {
    private static IComprobanteDao comprobanteDao = new ComprobanteDaoImpl();

    public static void listar() {
        List<Comprobante> lista = comprobanteDao.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Comprobante comprobante : lista) {
                mostrar(comprobante);
            }
        } else {
            System.out.println("No hay comprobantes registrados");
        }
    }

    public static void searchByCodigo() {
        List<Comprobante> lista = comprobanteDao.listAll();
        if (lista != null && !lista.isEmpty()) {
            Comprobante comprobante = comprobanteDao.searchByCodigo(lista.get(0).getCodigo());
            if (comprobante != null) mostrar(comprobante);
        }
    }

    public static void searchByDni() {
        List<Comprobante> lista = comprobanteDao.listAll();
        if (lista != null && !lista.isEmpty()) {
            List<Comprobante> resultado = comprobanteDao.searchByDni(lista.get(0).getCliente().getDni());
            for (Comprobante comprobante : resultado) {
                mostrar(comprobante);
            }
        }
    }

    private static void mostrar(Comprobante comprobante) {
        System.out.println("ID_COMPROBANTE: " + comprobante.getIdcomprobante());
        System.out.println("CODIGO: " + comprobante.getCodigo());
        System.out.println("CLIENTE: " + comprobante.getCliente().getNombre() + " " + comprobante.getCliente().getApellidos());
        System.out.println("TIPO: " + comprobante.getTipo());
        System.out.println("ESTADO: " + comprobante.getEstado());
        System.out.println("SUBTOTAL: " + comprobante.getSubtotal());
        System.out.println("DESCUENTO: " + comprobante.getDescuento());
        System.out.println("TOTAL: " + comprobante.getTotal());
        System.out.println("FECHA: " + comprobante.getFecha());
        System.out.println("************");
    }

    public static void main(String[] args) {
        listar();
        searchByCodigo();
        searchByDni();
    }
}
