import database.DatabaseService;
import model.CharacterGoT;
import repository.CharactersRepository;
import service.ApiService;
import service.CharacterService;
import storage.FileExporter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("INICIANDO APLICACIÓN JUEGO DE TRONOS (PROYECTO AD)");

        // 1. Preparar la estructura y comprobar la conexión a MySQL.
        DatabaseService dbService = new DatabaseService();
        try {
            dbService.initializeDatabase();
            System.out.println("Base de datos y tablas inicializadas correctamente.");

            try (var conn = dbService.getConnection()) {
                if (conn == null || conn.isClosed()) {
                    System.err.println("No se pudo establecer conexión con la base de datos 'got_db'.");
                    return;
                }
                System.out.println("Conexión a la base de datos 'got_db' establecida con éxito.");
            }
        } catch (Exception e) {
            System.err.println("No se pudo preparar o conectar con la base de datos: " + e.getMessage());
            return;
        }

        CharactersRepository charRepo = new CharactersRepository();
        ApiService apiService = new ApiService();
        List<CharacterGoT> personajes;

        try {
            // 2. La BD es la fuente de las consultas. La API solo se usa si aún no hay datos.
            personajes = charRepo.findAllOrThrow();
            if (personajes.isEmpty()) {
                System.out.println("\nPASO 1: Consumo de API y persistencia en BD");
                System.out.println("\nLa base de datos está vacía: descargando los datos iniciales desde la API...");
                int descargados = apiService.sincronizarPersonajes();
                System.out.println("Descarga inicial completada: " + descargados
                        + " personajes guardados en la base de datos.");

                // Vuelve a leer la BD para que todas las consultas trabajen con datos persistidos.
                personajes = charRepo.findAllOrThrow();
            } else {
                System.out.println("\nPASO 1: Consumo de los datos en la BD");
                System.out.println("\nYa hay datos guardados. Se omite la API y se conservan los cambios de la BD.");
            }
        } catch (IOException e) {
            System.err.println("Error al descargar los datos iniciales desde la API: " + e.getMessage());
            return;
        } catch (SQLException e) {
            System.err.println("Error al leer o guardar datos en la base de datos: " + e.getMessage());
            return;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Se interrumpió la descarga inicial desde la API.");
            return;
        }

        System.out.println("\nConsultas y exportaciones basadas en los datos leídos de la BD.");
        // 3. Leer los registros desde el repositorio JDBC para las consultas y exportaciones.
        System.out.println("Total de registros leídos de 'got_character': " + personajes.size());

        if (!personajes.isEmpty()) {
            // 4. Demostrar búsquedas, filtros, ordenaciones y agrupaciones con Streams sobre la BD.
            System.out.println("\nPASO 2: Ejecutando consultas con Java Streams");
            CharacterService streamService = new CharacterService();

            // Contar por género
            long totalMasculinos = streamService.contarPorGenero(personajes, "Male");
            long totalFemeninos = streamService.contarPorGenero(personajes, "Female");
            System.out.println(" · Personajes masculinos: " + totalMasculinos);
            System.out.println(" · Personajes femeninos: " + totalFemeninos);

            // Filtrar por una cultura disponible y ordenar sus personajes por nombre
            String cultura = personajes.stream()
                    .map(CharacterGoT::getCulture)
                    .filter(valor -> valor != null && !valor.isBlank())
                    .findFirst()
                    .orElse(null);
            if (cultura != null) {
                List<CharacterGoT> personajesPorCultura =
                        streamService.filtrarYOrdenarPorCultura(personajes, cultura);
                System.out.println(" · Personajes de la cultura '" + cultura
                        + "' (ordenadors por nombre): "
                        + personajesPorCultura.size());
                personajesPorCultura.stream()
                        .limit(15)
                        .forEach(personaje -> System.out.println("     - " + personaje.getName()));
            } else {
                System.out.println(" · No hay culturas disponibles para filtrar.");
            }

            // Obtener nombres únicos (map, distinct, collect)
            List<String> nombresUnicos = streamService.obtenerNombresUnicos(personajes);
            System.out.println(" · Muestra de nombres únicos (primeros 5):");
            nombresUnicos.stream().limit(5).forEach(nombre -> System.out.println("     - " + nombre));

            // Buscar el primer personaje cuyo nombre contenga el texto indicado
            var personajeEncontrado = streamService.buscarPrimerPersonajeConNombre(personajes, "Jon");
            personajeEncontrado.ifPresentOrElse(
                    personaje -> System.out.println(" · Primer personaje cuyo nombre contiene 'Jon': "
                            + personaje.getName()),
                    () -> System.out.println(" · No se encontró ningún personaje cuyo nombre contenga 'Jon'.")
            );

            // Agrupar por cultura (groupingBy)
            var agrupadosPorCultura = streamService.agruparPersonajesPorCultura(personajes);
            System.out.println(" · Culturas distintas encontradas: "
                    + agrupadosPorCultura.size());

            // 5. Exportar también el contenido actual de la BD a ficheros JSON y CSV.
            System.out.println("\nPASO 3: Exportando datos a ficheros");
            try {
                FileExporter.exportToJSON(personajes, "personajes_exportados.json");
                FileExporter.exportToCSV(personajes, "personajes_exportados.csv");
                System.out.println("Ficheros JSON y CSV exportados correctamente en la raíz del proyecto.");
            } catch (IOException e) {
                System.err.println("Detalle en la exportación de ficheros: " + e.getMessage());
            }

        } else {
            System.out.println("No hay personajes disponibles en la base de datos para procesar.");
        }

        System.out.println("\nEJECUCIÓN FINALIZADA CON ÉXITO");
    }
}