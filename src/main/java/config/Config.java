package config;

/**
 * Configuración global: URL de la API y datos de conexión a MySQL.
 * Centralizarlo aquí evita tener "números mágicos" repartidos por el código.
 */
public final class Config {

    // ----- API -----
    public static final String API_BASE_URL = "https://anapioficeandfire.com/api";

    // ----- Base de datos -----
    public static final String DB_URL =
            "jdbc:mysql://localhost:3306/got_db";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "admin";   // la que pusiste al crear el usuario

    private Config() {
    }
}
