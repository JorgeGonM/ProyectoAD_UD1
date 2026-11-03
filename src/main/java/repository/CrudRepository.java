package repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    List<T> findAll();
    Optional<T> findById(Long id);
    void save(T entity);         // Crear o actualizar
    void update(T entity);       // Modificar explícitamente
    void delete(Long id);        // Borrar por ID
}