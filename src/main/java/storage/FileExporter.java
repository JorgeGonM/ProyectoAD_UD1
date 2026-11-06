package storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import model.CharacterGoT;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class FileExporter {

    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static void exportToJSON(List<CharacterGoT> characters, String fileName) throws IOException {
        if (!fileName.toLowerCase(Locale.ROOT).endsWith(".json")) fileName += ".json";
        mapper.writeValue(Path.of(fileName).toFile(), characters);
    }

    public static void exportToCSV(List<CharacterGoT> characters, String fileName) throws IOException {
        if (!fileName.toLowerCase(Locale.ROOT).endsWith(".csv")) fileName += ".csv";
        try (BufferedWriter writer = Files.newBufferedWriter(
                Path.of(fileName), StandardCharsets.UTF_8)) {
            writer.write("ID,Name,Culture,Gender,House\n");
            for (CharacterGoT c : characters) {
                writer.write(csvValue(c.getId() != 0 ? String.valueOf(c.getId()) : ""));
                writer.write(',');
                writer.write(csvValue(c.getName()));
                writer.write(',');
                writer.write(csvValue(c.getCulture()));
                writer.write(',');
                writer.write(csvValue(c.getGender()));
                writer.write(',');
                writer.write(csvValue(c.getHouse()));
                writer.newLine();
            }
        }
    }

    private static String csvValue(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")
                || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}