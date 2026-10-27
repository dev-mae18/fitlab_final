package pe.edu.upeu.fitlab_final.test;

import pe.edu.upeu.fitlab_final.implementacion.EntrenadorDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IEntrenadorDao;
import pe.edu.upeu.fitlab_final.modelo.Entrenador;
import java.util.List;

public class TestEntrenador {
    private static IEntrenadorDao entrenadorDao = new EntrenadorDaoImpl();
    private static Entrenador entrenadorPrueba;

    public static void listar() {
        List<Entrenador> lista = entrenadorDao.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Entrenador entrenador : lista) {
                mostrar(entrenador);
            }
        } else {
            System.out.println("No hay entrenadores registrados");
        }
    }

    public static void searchById() {
        List<Entrenador> lista = entrenadorDao.listAll();
        if (lista != null && !lista.isEmpty()) {
            Entrenador entrenador = entrenadorDao.searchById(lista.get(0).getIdentrenador());
            if (entrenador != null) mostrar(entrenador);
        }
    }

    public static void searchByDni() {
        List<Entrenador> lista = entrenadorDao.listAll();
        if (lista != null && !lista.isEmpty()) {
            Entrenador entrenador = entrenadorDao.searchByDni(lista.get(0).getDni());
            if (entrenador != null) mostrar(entrenador);
        }
    }

    public static void insertar() {
        if (entrenadorDao.searchByDni("99000002") != null) {
            System.out.println("El DNI de prueba 99000002 ya existe");
            return;
        }
        entrenadorPrueba = new Entrenador();
        entrenadorPrueba.setNombre("Entrenador");
        entrenadorPrueba.setApellidos("Prueba FitLab");
        entrenadorPrueba.setDni("99000002");
        entrenadorPrueba.setEspecialidad("Prueba");
        entrenadorPrueba.setTurno("MANANA");
        boolean result = entrenadorDao.insert(entrenadorPrueba);
        if (result) {
            System.out.println("INSERT ENTRENADOR CORRECTO");
        } else {
            System.out.println("ERROR AL INSERTAR ENTRENADOR");
        }
    }

    public static void actualizar() {
        if (entrenadorPrueba != null && entrenadorPrueba.getIdentrenador() > 0) {
            entrenadorPrueba.setEspecialidad("Prueba actualizada");
            boolean result = entrenadorDao.update(entrenadorPrueba);
            if (result) {
                System.out.println("UPDATE ENTRENADOR CORRECTO");
            } else {
                System.out.println("ERROR AL ACTUALIZAR ENTRENADOR");
            }
        }
    }

    public static void eliminar() {
        if (entrenadorPrueba != null && entrenadorPrueba.getIdentrenador() > 0) {
            boolean result = entrenadorDao.delete(entrenadorPrueba);
            if (result) {
                System.out.println("DELETE ENTRENADOR CORRECTO");
            } else {
                System.out.println("ERROR AL ELIMINAR ENTRENADOR");
            }
        }
    }

    private static void mostrar(Entrenador entrenador) {
        System.out.println("ID_ENTRENADOR: " + entrenador.getIdentrenador());
        System.out.println("NOMBRE: " + entrenador.getNombre());
        System.out.println("APELLIDOS: " + entrenador.getApellidos());
        System.out.println("DNI: " + entrenador.getDni());
        System.out.println("ESPECIALIDAD: " + entrenador.getEspecialidad());
        System.out.println("TURNO: " + Entrenador.describirTurno(entrenador.getTurno()));
        System.out.println("************");
    }

    public static void main(String[] args) {
        listar();
        searchById();
        searchByDni();
        insertar();
        actualizar();
        eliminar();
    }
}
