package storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import model.CharacterGoT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileExporter {

    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static void exportToJSON(List<CharacterGoT> characters, String fileName) {
        if (!fileName.endsWith(".json")) fileName += ".json";
        try {
            mapper.writeValue(new File(fileName), characters);
            System.out.println("Fichero JSON exportado correctamente: " + fileName);
        } catch (IOException e) {
            System.err.println("Error al exportar JSON: " + e.getMessage());
        }
    }

    public static void exportToCSV(List<CharacterGoT> characters, String fileName) {
        if (!fileName.endsWith(".csv")) fileName += ".csv";
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write("ID,Name,Culture,Gender\n");
            for (CharacterGoT c : characters) {
                writer.write(String.format("%s,%s,%s,%s\n",
                        c.getId() != null ? c.getId() : "",
                        c.getName() != null ? c.getName() : "",
                        c.getCulture() != null ? c.getCulture() : "",
                        c.getGender() != null ? c.getGender() : ""
                ));
            }
            System.out.println("Fichero CSV exportado correctamente: " + fileName);
        } catch (IOException e) {
            System.err.println("Error al exportar CSV: " + e.getMessage());
        }
    }
}