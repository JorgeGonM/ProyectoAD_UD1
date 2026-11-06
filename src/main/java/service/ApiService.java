package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.CharacterGoT;
import model.HouseGoT;
import repository.CharactersRepository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiService {

    private static final String API_BASE_URL = "https://anapioficeandfire.com/api";
    private static final int MAX_PAGE_SIZE = 50;

    private final CharactersRepository characterRepository = new CharactersRepository();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public void descargarYGuardarPersonajes() {
        try {
            int total = sincronizarPersonajes();
            System.out.println("¡" + total + " personajes sincronizados con MySQL!");
        } catch (IOException e) {
            System.err.println("Error al descargar o guardar personajes: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al guardar personajes en la base de datos: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Se interrumpió la descarga de personajes: " + e.getMessage());
        }
    }

    public int sincronizarPersonajes() throws IOException, InterruptedException, SQLException {
        List<CharacterGoT> characters = fetchAllPages("characters",
                new TypeReference<>() {});
        Map<String, String> houseNames = fetchHouseNames();

        for (CharacterGoT character : characters) {
            if (character.getId() <= 0) {
                throw new IOException("La API devolvió un personaje sin un ID válido.");
            }
            List<String> allegiances = character.getAllegiances() == null
                    ? List.of()
                    : character.getAllegiances();
            List<String> houses = allegiances.stream()
                    .map(houseNames::get)
                    .filter(name -> name != null && !name.isBlank())
                    .distinct()
                    .toList();
            character.setHouse(houses.isEmpty()
                    ? "Sin casa"
                    : houses.stream().collect(Collectors.joining(", ")));
        }

        characterRepository.saveAll(characters);
        return characters.size();
    }

    private Map<String, String> fetchHouseNames() throws IOException, InterruptedException {
        List<HouseGoT> houses = fetchAllPages("houses", new TypeReference<>() {});
        Map<String, String> namesByUrl = new HashMap<>();
        for (HouseGoT house : houses) {
            if (house.getUrl() != null && house.getName() != null && !house.getName().isBlank()) {
                namesByUrl.put(house.getUrl(), house.getName());
            }
        }
        return namesByUrl;
    }

    private <T> List<T> fetchAllPages(String resource, TypeReference<List<T>> type)
            throws IOException, InterruptedException {
        List<T> results = new ArrayList<>();
        for (int page = 1; ; page++) {
            String url = API_BASE_URL + "/" + resource
                    + "?page=" + page + "&pageSize=" + MAX_PAGE_SIZE;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("La API devolvió HTTP " + response.statusCode()
                        + " al consultar " + resource + " (página " + page + ").");
            }

            List<T> currentPage = mapper.readValue(response.body(), type);
            results.addAll(currentPage);
            if (currentPage.size() < MAX_PAGE_SIZE) {
                return results;
            }
        }
    }
}
