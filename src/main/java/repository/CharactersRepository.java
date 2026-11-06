package repository;

import model.CharacterGoT;
import database.DatabaseService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CharactersRepository implements CrudRepository<CharacterGoT> {

    private final DatabaseService dbService = new DatabaseService();

//    @Override
//    public List<CharacterGoT> findAll() {
//        try {
//            return findAllOrThrow();
//        } catch (SQLException e) {
//            System.err.println("Error al obtener todos los personajes: " + e.getMessage());
//            return new ArrayList<>();
//        }
//    }

    public List<CharacterGoT> findAllOrThrow() throws SQLException {
        List<CharacterGoT> characters = new ArrayList<>();
        String query = "SELECT id, name, culture, gender, house FROM got_character";

        try (Connection conn = openConnection()) {
            ensureHouseColumn(conn);
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    CharacterGoT c = new CharacterGoT();
                    c.setId((int) rs.getLong("id"));
                    c.setName(rs.getString("name"));
                    c.setCulture(rs.getString("culture"));
                    c.setGender(rs.getString("gender"));
                    c.setHouse(rs.getString("house"));
                    characters.add(c);
                }
            }
        }
        return characters;
    }

    @Override
    public Optional<CharacterGoT> findById(Long id) throws SQLException {
        String query = "SELECT id, name, culture, gender, house FROM got_character WHERE id = ?";
        try (Connection conn = openConnection()) {
            ensureHouseColumn(conn);
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setLong(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        CharacterGoT c = new CharacterGoT();
                        c.setId((int) rs.getLong("id"));
                        c.setName(rs.getString("name"));
                        c.setCulture(rs.getString("culture"));
                        c.setGender(rs.getString("gender"));
                        c.setHouse(rs.getString("house"));
                        return Optional.of(c);
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void save(CharacterGoT entity) throws SQLException {
        boolean generateId = entity.getId() <= 0;
        String query = generateId
                ? "INSERT INTO got_character (name, culture, gender, house) VALUES (?, ?, ?, ?)"
                : "INSERT INTO got_character (id, name, culture, gender, house) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = openConnection()) {
            ensureHouseColumn(conn);
            try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                int parameter = 1;
                if (!generateId) {
                    pstmt.setLong(parameter++, entity.getId());
                }
                pstmt.setString(parameter++, entity.getName());
                pstmt.setString(parameter++, entity.getCulture());
                pstmt.setString(parameter++, entity.getGender());
                pstmt.setString(parameter, entity.getHouse());
                pstmt.executeUpdate();

                if (generateId) {
                    try (ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("No se pudo obtener el ID del personaje creado.");
                        }
                        entity.setId(keys.getInt(1));
                    }
                }
            }
        }
    }

    public void saveAll(List<CharacterGoT> characters) throws SQLException {
        String query = """
                INSERT INTO got_character (id, name, culture, gender, house)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    name = VALUES(name),
                    culture = VALUES(culture),
                    gender = VALUES(gender),
                    house = VALUES(house)
                """;
        try (Connection conn = openConnection()) {
            ensureHouseColumn(conn);
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {

                for (CharacterGoT character : characters) {
                    pstmt.setLong(1, character.getId());
                    pstmt.setString(2, character.getName());
                    pstmt.setString(3, character.getCulture());
                    pstmt.setString(4, character.getGender());
                    pstmt.setString(5, character.getHouse());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        }
    }

    @Override
    public void update(CharacterGoT entity) throws SQLException {
        String query = "UPDATE got_character SET name = ?, culture = ?, gender = ?, house = ? WHERE id = ?";
        try (Connection conn = openConnection()) {
            ensureHouseColumn(conn);
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, entity.getName());
                pstmt.setString(2, entity.getCulture());
                pstmt.setString(3, entity.getGender());
                pstmt.setString(4, entity.getHouse());
                pstmt.setLong(5, entity.getId());
                if (pstmt.executeUpdate() == 0 && !exists(conn, entity.getId())) {
                    throw new SQLException("No existe un personaje con ID " + entity.getId() + ".");
                }
            }
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        String query = "DELETE FROM got_character WHERE id = ?";
        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("No existe un personaje con ID " + id + ".");
            }
        }
    }

    private boolean exists(Connection conn, int id) throws SQLException {
        try (PreparedStatement statement =
                     conn.prepareStatement("SELECT 1 FROM got_character WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private void ensureHouseColumn(Connection conn) throws SQLException {
        try (ResultSet columns = conn.getMetaData().getColumns(
                conn.getCatalog(), null, "got_character", "house")) {
            if (columns.next()) {
                return;
            }
        }
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate("ALTER TABLE got_character ADD COLUMN house VARCHAR(512)");
        }
    }

    private Connection openConnection() throws SQLException {
        Connection connection = dbService.getConnection();
        if (connection == null) {
            throw new SQLException("No se pudo conectar con la base de datos.");
        }
        return connection;
    }
}