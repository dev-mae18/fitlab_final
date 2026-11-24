package pe.edu.upeu.fitlab_final.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class ConexionDB {
    private static Connection connection;
    private static Path rutaConexion;
    private static final Set<Path> basesInicializadas = new HashSet<>();

    public static Path getDatabasePath() {
        String rutaConfigurada = System.getProperty("fitlab.db.path");
        Path ruta = rutaConfigurada == null || rutaConfigurada.isBlank()
                ? Path.of(System.getProperty("user.home"), "FitLab", "fitlab.db")
                : Path.of(rutaConfigurada);
        return ruta.toAbsolutePath().normalize();
    }

    public static synchronized Connection getConnection() {
        try {
            Path ruta = getDatabasePath();
            if (connection != null && !connection.isClosed() && ruta.equals(rutaConexion)) {
                return connection;
            }
            if (connection != null) {
                connection.close();
                connection = null;
            }
            Class.forName("org.sqlite.JDBC");
            Files.createDirectories(ruta.getParent());
            String rutaConfigurada = System.getProperty("fitlab.db.path");
            if ((rutaConfigurada == null || rutaConfigurada.isBlank()) && !Files.exists(ruta)) {
                migrarBaseAnterior(Path.of("C:/sqlite/db/fitlab.db"), ruta);
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + ruta);
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
            if (!basesInicializadas.contains(ruta)) {
                inicializarBase(connection);
                basesInicializadas.add(ruta);
            }
            rutaConexion = ruta;
            return connection;
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException cierre) {
                    e.addSuppressed(cierre);
                }
            }
            connection = null;
            rutaConexion = null;
            throw new RuntimeException("Error al conectar a la BD: " + e.getMessage(), e);
        }
    }

    private static void inicializarBase(Connection cn) throws SQLException, IOException {
        cn.setAutoCommit(false);
        try {
            try (InputStream archivo = ConexionDB.class.getResourceAsStream("/pe/edu/upeu/fitlab_final/database/schema.sql")) {
                if (archivo == null) throw new IOException("No se encontró el esquema de la base de datos.");
                String esquema = new String(archivo.readAllBytes(), StandardCharsets.UTF_8);
                try (Statement st = cn.createStatement()) {
                    for (String instruccion : esquema.split(";")) {
                        if (!instruccion.isBlank()) st.execute(instruccion);
                    }
                    st.executeUpdate("INSERT INTO usuarios(usuario, contrasena, activo) "
                            + "SELECT 'admin', 'admin123', 1 WHERE NOT EXISTS (SELECT 1 FROM usuarios)");
                    boolean sinPlanes;
                    try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM planes")) {
                        sinPlanes = rs.next() && rs.getInt(1) == 0;
                    }
                    if (sinPlanes) {
                        st.executeUpdate("INSERT INTO planes(nombre, precio, duracionDias) VALUES "
                                + "('BASICO', 50, 30), ('PREMIUM', 80, 30), ('VIP', 120, 30)");
                    }
                }
            }
            cn.commit();
            cn.setAutoCommit(true);
        } catch (SQLException | IOException e) {
            try {
                cn.rollback();
            } catch (SQLException rollback) {
                e.addSuppressed(rollback);
            }
            throw e;
        }
    }

    private static void migrarBaseAnterior(Path origen, Path destino) throws SQLException, IOException {
        if (!Files.isRegularFile(origen) || Files.exists(destino)) return;
        Path temporal = Files.createTempFile(destino.getParent(), "fitlab-migracion-", ".db");
        try {
            String url = "jdbc:sqlite:" + origen.toAbsolutePath().toUri() + "?mode=ro";
            try (Connection anterior = DriverManager.getConnection(url);
                 PreparedStatement copia = anterior.prepareStatement("VACUUM INTO ?")) {
                copia.setString(1, temporal.toAbsolutePath().toString());
                copia.execute();
            }
            try {
                Files.move(temporal, destino);
            } catch (FileAlreadyExistsException e) {
                if (!Files.isRegularFile(destino)) throw e;
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }
}
