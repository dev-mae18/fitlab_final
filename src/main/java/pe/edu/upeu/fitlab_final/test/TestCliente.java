package pe.edu.upeu.fitlab_final.test;

import pe.edu.upeu.fitlab_final.implementacion.ClienteDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IClienteDao;
import pe.edu.upeu.fitlab_final.modelo.Cliente;
import java.util.List;

public class TestCliente {
    private static IClienteDao clienteDao = new ClienteDaoImpl();
    private static Cliente clientePrueba;

    public static void listar() {
        List<Cliente> lista = clienteDao.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Cliente cliente : lista) {
                mostrar(cliente);
            }
        } else {
            System.out.println("No hay clientes registrados");
        }
    }

    public static void searchById() {
        List<Cliente> lista = clienteDao.listAll();
        if (lista != null && !lista.isEmpty()) {
            Cliente cliente = clienteDao.searchById(lista.get(0).getIdcliente());
            if (cliente != null) mostrar(cliente);
        }
    }

    public static void searchByDni() {
        List<Cliente> lista = clienteDao.listAll();
        if (lista != null && !lista.isEmpty()) {
            Cliente cliente = clienteDao.searchByDni(lista.get(0).getDni());
            if (cliente != null) mostrar(cliente);
        }
    }

    public static void insertar() {
        if (clienteDao.searchByDni("99000001") != null) {
            System.out.println("El DNI de prueba 99000001 ya existe");
            return;
        }
        clientePrueba = new Cliente();
        clientePrueba.setNombre("Cliente");
        clientePrueba.setApellidos("Prueba FitLab");
        clientePrueba.setDni("99000001");
        clientePrueba.setEdad(25);
        clientePrueba.setGenero("M");
        clientePrueba.setEstudiante(false);
        clientePrueba.setAlergias("Ninguna");
        boolean result = clienteDao.insert(clientePrueba);
        if (result) {
            System.out.println("INSERT CLIENTE CORRECTO");
        } else {
            System.out.println("ERROR AL INSERTAR CLIENTE");
        }
    }

    public static void actualizar() {
        if (clientePrueba != null && clientePrueba.getIdcliente() > 0) {
            clientePrueba.setNombre("Cliente Actualizado");
            boolean result = clienteDao.update(clientePrueba);
            if (result) {
                System.out.println("UPDATE CLIENTE CORRECTO");
            } else {
                System.out.println("ERROR AL ACTUALIZAR CLIENTE");
            }
        }
    }

    public static void eliminar() {
        if (clientePrueba != null && clientePrueba.getIdcliente() > 0) {
            boolean result = clienteDao.delete(clientePrueba);
            if (result) {
                System.out.println("DELETE CLIENTE CORRECTO");
            } else {
                System.out.println("ERROR AL ELIMINAR CLIENTE");
            }
        }
    }

    private static void mostrar(Cliente cliente) {
        System.out.println("ID_CLIENTE: " + cliente.getIdcliente());
        System.out.println("NOMBRE: " + cliente.getNombre());
        System.out.println("APELLIDOS: " + cliente.getApellidos());
        System.out.println("DNI: " + cliente.getDni());
        System.out.println("EDAD: " + cliente.getEdad());
        System.out.println("GENERO: " + cliente.getGenero());
        System.out.println("ESTUDIANTE: " + cliente.isEstudiante());
        System.out.println("ALERGIAS: " + cliente.getAlergias());
        System.out.println("************");
    }

    public static void main(String[] args) {
        //Metodos ya configurados, para los test por dev mae
        listar();
        searchById();
        searchByDni();
        insertar();
        actualizar();
        eliminar();
    }
}
