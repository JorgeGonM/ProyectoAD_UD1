package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DatabaseService implements DatabaseManager {

    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
    private static final String URL = "jdbc:mysql://localhost:3306/got_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    public void initializeDatabase() throws SQLException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Conecta al servidor sin seleccionar got_db, porque puede que aún no exista.
            try (Connection connection = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
                 InputStream schema = DatabaseService.class.getResourceAsStream("/tablas.sql")) {
                if (schema == null) {
                    throw new IOException("No se encontró el recurso /tablas.sql");
                }

                // Lee el esquema incluido en resources y quita los comentarios SQL de una línea.
                String sql = new String(schema.readAllBytes(), StandardCharsets.UTF_8);
                sql = sql.replaceAll("(?m)^\\s*--.*$", "");

                // Ejecuta cada sentencia por separado, en el orden definido en tablas.sql.
                try (Statement statement = connection.createStatement()) {
                    for (String command : sql.split(";")) {
                        if (!command.isBlank()) {
                            statement.execute(command);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de MySQL", e);
        }
    }

    @Override
    public Connection getConnection() {
        try {
            // Asegura que el driver de MySQL está cargado
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver JDBC de MySQL: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error de conexión a MySQL: " + e.getMessage());
        }
        return null;
    }
}