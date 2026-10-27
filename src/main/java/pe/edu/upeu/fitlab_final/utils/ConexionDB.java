package pe.edu.upeu.fitlab_final.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConexionDB {
    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                String url = "jdbc:sqlite:C:/sqlite/db/fitlab.db";
                connection = DriverManager.getConnection(url);
                connection.setAutoCommit(true);
                try (Statement st = connection.createStatement()) {
                    st.execute("PRAGMA foreign_keys = ON");
                }
            }
            return connection;
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar a la BD: " + e.getMessage());
        }
    }
}
