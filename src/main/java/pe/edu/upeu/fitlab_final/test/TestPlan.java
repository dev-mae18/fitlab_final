package pe.edu.upeu.fitlab_final.test;

import pe.edu.upeu.fitlab_final.implementacion.PlanDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IPlanDao;
import pe.edu.upeu.fitlab_final.modelo.Plan;
import java.util.List;

public class TestPlan {
    private static IPlanDao planDao = new PlanDaoImpl();

    public static void listar() {
        List<Plan> lista = planDao.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Plan plan : lista) {
                mostrar(plan);
            }
        } else {
            System.out.println("No hay planes registrados");
        }
    }

    public static void searchById() {
        List<Plan> lista = planDao.listAll();
        if (lista != null && !lista.isEmpty()) {
            Plan plan = planDao.searchById(lista.get(0).getIdplan());
            if (plan != null) mostrar(plan);
        }
    }

    private static void mostrar(Plan plan) {
        System.out.println("ID_PLAN: " + plan.getIdplan());
        System.out.println("NOMBRE: " + plan.getNombre());
        System.out.println("PRECIO: " + plan.getPrecio());
        System.out.println("DURACION: " + plan.getDuracionDias() + " dias");
        System.out.println("************");
    }

    public static void main(String[] args) {
        listar();
        searchById();
    }
}
