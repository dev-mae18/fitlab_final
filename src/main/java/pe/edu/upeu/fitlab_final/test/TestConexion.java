package pe.edu.upeu.fitlab_final.test;

import pe.edu.upeu.fitlab_final.utils.ConexionDB;
import java.sql.Connection;

public class TestConexion {
    public static void testConexion() {
        try {
            Connection connection = ConexionDB.getConnection();
            if (connection != null && !connection.isClosed()) {
                System.out.println("Conexion correcta a fitlab.db");
            } else {
                System.out.println("Error de conexion");
            }
        } catch (Exception e) {
            System.out.println("Error de conexion: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        testConexion();
    }
}
