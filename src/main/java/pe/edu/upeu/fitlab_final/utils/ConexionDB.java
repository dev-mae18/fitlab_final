package pe.edu.upeu.fitlab_final.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {
    private static Connection connection;
    private static Path rutaConexion;

    public static Path getDatabasePath() {
        String rutaConfigurada = System.getProperty("fitlab.db.path");
        File archivo;
        if (rutaConfigurada == null || rutaConfigurada.isBlank()) {
            String userHome = System.getProperty("user.home");
            File directory = new File(userHome, "FitLab");
            archivo = new File(directory, "fitlab.db");
        } else {
            archivo = new File(rutaConfigurada);
        }
        return archivo.toPath().toAbsolutePath().normalize();
    }

    public static Connection getConnection() {
        try {
            Path ruta = getDatabasePath();
            if (connection == null || connection.isClosed() || !ruta.equals(rutaConexion)) {
                if (connection != null) {
                    connection.close();
                    connection = null;
                }
                File directory = ruta.toFile().getParentFile();
                if (!directory.isDirectory() && !directory.mkdirs() && !directory.isDirectory()) {
                    throw new IOException("No se pudo crear la carpeta de la base de datos: " + directory);
                }
                Class.forName("org.sqlite.JDBC");
                String rutaConfigurada = System.getProperty("fitlab.db.path");
                if ((rutaConfigurada == null || rutaConfigurada.isBlank()) && !Files.exists(ruta)) {
                    migrarBaseAnterior(Path.of("C:/sqlite/db/fitlab.db"), ruta);
                }
                String url = "jdbc:sqlite:" + ruta;
                connection = DriverManager.getConnection(url);
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON");
                }
                initDatabase();
                rutaConexion = ruta;
            }
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

    private static void initDatabase() throws SQLException {
        String sqlUsuario = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "idusuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario TEXT NOT NULL UNIQUE CHECK (length(trim(usuario)) > 0), " +
                "contrasena TEXT NOT NULL, " +
                "activo INTEGER NOT NULL DEFAULT 1 CHECK (activo IN (0, 1)));";

        String sqlCliente = "CREATE TABLE IF NOT EXISTS clientes (" +
                "idcliente INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0), " +
                "apellidos TEXT NOT NULL CHECK (length(trim(apellidos)) > 0), " +
                "dni TEXT NOT NULL UNIQUE CHECK (length(dni) = 8 AND dni NOT GLOB '*[^0-9]*'), " +
                "edad INTEGER NOT NULL CHECK (edad BETWEEN 1 AND 120), " +
                "genero TEXT NOT NULL CHECK (genero IN ('M', 'F')), " +
                "estudiante INTEGER NOT NULL DEFAULT 0 CHECK (estudiante IN (0, 1)), " +
                "alergias TEXT NOT NULL DEFAULT 'Ninguna');";

        String sqlEntrenador = "CREATE TABLE IF NOT EXISTS entrenadores (" +
                "identrenador INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0), " +
                "apellidos TEXT NOT NULL CHECK (length(trim(apellidos)) > 0), " +
                "dni TEXT NOT NULL UNIQUE CHECK (length(dni) = 8 AND dni NOT GLOB '*[^0-9]*'), " +
                "especialidad TEXT NOT NULL CHECK (length(trim(especialidad)) > 0), " +
                "turno TEXT NOT NULL CHECK (turno IN ('MANANA', 'TARDE', 'NOCHE')));";

        String sqlPlan = "CREATE TABLE IF NOT EXISTS planes (" +
                "idplan INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL UNIQUE CHECK (length(trim(nombre)) > 0), " +
                "precio REAL NOT NULL CHECK (precio > 0), " +
                "duracionDias INTEGER NOT NULL DEFAULT 30 CHECK (duracionDias > 0));";

        String sqlMembresia = "CREATE TABLE IF NOT EXISTS membresias (" +
                "idmembresia INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idcliente INTEGER NOT NULL UNIQUE, " +
                "idplan INTEGER NOT NULL, " +
                "identrenador INTEGER, " +
                "horario TEXT NOT NULL CHECK (horario IN ('MANANA', 'TARDE', 'NOCHE')), " +
                "tipoDias TEXT NOT NULL CHECK (tipoDias IN ('TODOS', 'LUNES-MIERCOLES-VIERNES', 'MARTES-JUEVES-SABADO')), " +
                "fechaInicio TEXT NOT NULL CHECK (length(fechaInicio) = 10), " +
                "fechaVencimiento TEXT NOT NULL CHECK (length(fechaVencimiento) = 10 AND fechaVencimiento >= fechaInicio), " +
                "activa INTEGER NOT NULL DEFAULT 1 CHECK (activa IN (0, 1)), " +
                "renovaciones INTEGER NOT NULL DEFAULT 0 CHECK (renovaciones >= 0), " +
                "meses INTEGER NOT NULL DEFAULT 1 CHECK (meses BETWEEN 1 AND 12), " +
                "idusuario INTEGER NOT NULL, " +
                "FOREIGN KEY (idcliente) REFERENCES clientes(idcliente) ON DELETE RESTRICT, " +
                "FOREIGN KEY (idplan) REFERENCES planes(idplan) ON DELETE RESTRICT, " +
                "FOREIGN KEY (identrenador) REFERENCES entrenadores(identrenador) ON DELETE RESTRICT, " +
                "FOREIGN KEY (idusuario) REFERENCES usuarios(idusuario) ON DELETE RESTRICT);";

        String sqlComprobante = "CREATE TABLE IF NOT EXISTS comprobantes (" +
                "idcomprobante INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "codigo TEXT NOT NULL UNIQUE, " +
                "idcliente INTEGER NOT NULL, " +
                "tipo TEXT NOT NULL CHECK (tipo IN ('REGISTRO', 'REACTIVACION', 'RENOVACION', 'MIGRACION')), " +
                "estado TEXT NOT NULL DEFAULT 'Enviado' CHECK (estado IN ('Enviado', 'Anulado')), " +
                "subtotal REAL NOT NULL CHECK (subtotal >= 0), " +
                "descuento REAL NOT NULL CHECK (descuento >= 0 AND descuento <= subtotal), " +
                "total REAL NOT NULL CHECK (total >= 0), " +
                "fecha TEXT NOT NULL, " +
                "idusuario INTEGER NOT NULL, " +
                "FOREIGN KEY (idcliente) REFERENCES clientes(idcliente) ON DELETE RESTRICT, " +
                "FOREIGN KEY (idusuario) REFERENCES usuarios(idusuario) ON DELETE RESTRICT);";

        String sqlIndiceAforo = "CREATE INDEX IF NOT EXISTS idx_membresias_aforo " +
                "ON membresias(activa, horario, fechaVencimiento);";
        String sqlIndiceComprobantes = "CREATE INDEX IF NOT EXISTS idx_comprobantes_cliente ON comprobantes(idcliente);";

        connection.setAutoCommit(false);
        try {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sqlUsuario);
                stmt.execute(sqlCliente);
                stmt.execute(sqlEntrenador);
                stmt.execute(sqlPlan);
                stmt.execute(sqlMembresia);
                stmt.execute(sqlComprobante);
                stmt.execute(sqlIndiceAforo);
                stmt.execute(sqlIndiceComprobantes);

                stmt.executeUpdate("INSERT INTO usuarios(usuario, contrasena, activo) " +
                        "SELECT 'admin', 'admin123', 1 WHERE NOT EXISTS (SELECT 1 FROM usuarios)");
                boolean sinPlanes;
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM planes")) {
                    sinPlanes = rs.next() && rs.getInt(1) == 0;
                }
                if (sinPlanes) {
                    stmt.executeUpdate("INSERT INTO planes(nombre, precio, duracionDias) VALUES " +
                            "('BASICO', 50, 30), ('PREMIUM', 80, 30), ('VIP', 120, 30)");
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
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
