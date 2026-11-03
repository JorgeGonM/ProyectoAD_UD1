package repository;

import model.CharacterGoT;
import database.DatabaseService; // O la clase que uséis para la conexión

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CharactersRepository implements CrudRepository<CharacterGoT> {

    // Dependencia de la conexión a la base de datos
    private final DatabaseService dbService = new DatabaseService();

    @Override
    public List<CharacterGoT> findAll() {
        List<CharacterGoT> characters = new ArrayList<>();
        String query = "SELECT * FROM characters";

        try (Connection conn = dbService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                CharacterGoT c = new CharacterGoT();
                c.setId(rs.getLong("id"));
                c.setName(rs.getString("name"));
                c.setCulture(rs.getString("culture"));
                c.setGender(rs.getString("gender"));
                characters.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los personajes: " + e.getMessage());
        }
        return characters;
    }


    @Override
    public Optional<CharacterGoT> findById(Long id) {
        String query = "SELECT * FROM characters WHERE id = ?";
        try (Connection conn = dbService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    CharacterGoT c = new CharacterGoT();
                    c.setId(rs.getLong("id"));
                    c.setName(rs.getString("name"));
                    c.setCulture(rs.getString("culture"));
                    c.setGender(rs.getString("gender"));
                    return Optional.of(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void save(CharacterGoT entity) {
        String query = "INSERT INTO characters (id, name, culture, gender) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, entity.getId());
            pstmt.setString(2, entity.getName());
            pstmt.setString(3, entity.getCulture());
            pstmt.setString(4, entity.getGender());
            pstmt.executeUpdate();
            System.out.println("Personaje guardado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al guardar personaje: " + e.getMessage());
        }
    }

    @Override
    public void update(CharacterGoT entity) {
        String query = "UPDATE characters SET name = ?, culture = ?, gender = ? WHERE id = ?";
        try (Connection conn = dbService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getCulture());
            pstmt.setString(3, entity.getGender());
            pstmt.setLong(4, entity.getId());
            pstmt.executeUpdate();
            System.out.println("Personaje actualizado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al actualizar personaje: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM characters WHERE id = ?";
        try (Connection conn = dbService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            System.out.println("Personaje borrado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al borrar personaje: " + e.getMessage());
        }
    }
}