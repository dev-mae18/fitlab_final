package pe.edu.upeu.fitlab_final.test;

import pe.edu.upeu.fitlab_final.implementacion.MembresiaDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IMembresiaDao;
import pe.edu.upeu.fitlab_final.modelo.Entrenador;
import pe.edu.upeu.fitlab_final.modelo.Membresia;
import java.util.List;

public class TestMembresia {
    private static IMembresiaDao membresiaDao = new MembresiaDaoImpl();

    public static void listar() {
        List<Membresia> lista = membresiaDao.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Membresia membresia : lista) {
                mostrar(membresia);
            }
        } else {
            System.out.println("No hay membresias registradas");
        }
    }

    public static void searchById() {
        List<Membresia> lista = membresiaDao.listAll();
        if (lista != null && !lista.isEmpty()) {
            Membresia membresia = membresiaDao.searchById(lista.get(0).getIdmembresia());
            if (membresia != null) mostrar(membresia);
        }
    }

    public static void searchByCliente() {
        List<Membresia> lista = membresiaDao.listAll();
        if (lista != null && !lista.isEmpty()) {
            Membresia membresia = membresiaDao.searchByCliente(lista.get(0).getCliente().getIdcliente());
            if (membresia != null) mostrar(membresia);
        }
    }

    private static void mostrar(Membresia membresia) {
        System.out.println("ID_MEMBRESIA: " + membresia.getIdmembresia());
        System.out.println("CLIENTE: " + membresia.getCliente().getNombre() + " " + membresia.getCliente().getApellidos());
        System.out.println("PLAN: " + membresia.getPlan().getNombre());
        System.out.println("HORARIO: " + Entrenador.describirTurno(membresia.getHorario()));
        System.out.println("DIAS: " + Membresia.describirDias(membresia.getTipoDias()));
        if (membresia.getEntrenador() != null) {
            System.out.println("ENTRENADOR: " + membresia.getEntrenador().getNombre() + " " + membresia.getEntrenador().getApellidos());
        } else {
            System.out.println("ENTRENADOR: Sin entrenador");
        }
        System.out.println("INICIO: " + membresia.getFechaInicio());
        System.out.println("VENCIMIENTO: " + membresia.getFechaVencimiento());
        System.out.println("TIEMPO: " + membresia.getMeses() + (membresia.getMeses() == 1 ? " MES" : " MESES"));
        System.out.println("ACTIVA: " + membresia.isActiva());
        System.out.println("RENOVACIONES: " + membresia.getRenovaciones());
        System.out.println("************");
    }

    public static void main(String[] args) {
        listar();
        searchById();
        searchByCliente();
    }
}
