package service;

import model.CharacterGoT;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CharacterService {

    // 1. Filtrar y ordenar (Intermedios: filter, sorted | Terminal: collect)
    public List<CharacterGoT> filtrarYOrdenarPorCultura(List<CharacterGoT> personajes, String culturaBuscada) {
        return personajes.stream()
                .filter(c -> c.getCulture() != null && c.getCulture().equalsIgnoreCase(culturaBuscada))
                .sorted(Comparator.comparing(CharacterGoT::getName))
                .collect(Collectors.toList());
    }

    // 2. Mapear y quitar duplicados (Intermedios: map, distinct, filter | Terminal: collect)
    public List<String> obtenerNombresUnicos(List<CharacterGoT> personajes) {
        return personajes.stream()
                .map(CharacterGoT::getName)
                .filter(name -> name != null && !name.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    // 3. Contar elementos (Intermedio: filter | Terminal: count)
    public long contarPorGenero(List<CharacterGoT> personajes, String genero) {
        return personajes.stream()
                .filter(c -> c.getGender() != null && c.getGender().equalsIgnoreCase(genero))
                .count();
    }

    // 4. Búsqueda específica (Intermedio: filter | Terminal: findFirst)
    public Optional<CharacterGoT> buscarPrimerPersonajeConNombre(List<CharacterGoT> personajes, String textoBuscado) {
        return personajes.stream()
                .filter(c -> c.getName() != null && c.getName().toLowerCase().contains(textoBuscado.toLowerCase()))
                .findFirst();
    }

    // 5. Agrupación (Terminal: collect con groupingBy)
    public Map<String, List<CharacterGoT>> agruparPersonajesPorCultura(List<CharacterGoT> personajes) {
        return personajes.stream()
                .filter(c -> c.getCulture() != null && !c.getCulture().isBlank())
                .collect(Collectors.groupingBy(CharacterGoT::getCulture));
    }
}