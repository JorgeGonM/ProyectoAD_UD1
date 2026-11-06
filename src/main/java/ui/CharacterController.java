package ui;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import model.CharacterGoT;
import repository.CharactersRepository;
import service.ApiService;
import storage.FileExporter;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CharacterController {

    @FXML
    private TableView<CharacterGoT> charactersTable;
    @FXML
    private TableColumn<CharacterGoT, Number> idColumn;
    @FXML
    private TableColumn<CharacterGoT, String> nameColumn;
    @FXML
    private TableColumn<CharacterGoT, String> houseColumn;
    @FXML
    private TableColumn<CharacterGoT, String> genderColumn;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> genderFilter;
    @FXML
    private Button refreshButton;
    @FXML
    private Button exportButton;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField houseField;
    @FXML
    private TextField genderField;
    @FXML
    private Label countLabel;
    @FXML
    private Label statusLabel;

    private final CharactersRepository repository = new CharactersRepository();
    private final ObservableList<CharacterGoT> characters = FXCollections.observableArrayList();
    private final FilteredList<CharacterGoT> filteredCharacters =
            new FilteredList<>(characters, character -> true);

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cell ->
                new ReadOnlyIntegerWrapper(cell.getValue().getId()));
        nameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(displayValue(cell.getValue().getName())));
        houseColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(displayValue(cell.getValue().getHouse())));
        genderColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(displayValue(cell.getValue().getGender())));

        charactersTable.setItems(filteredCharacters);

        genderFilter.setItems(FXCollections.observableArrayList(
                "Todos", "Masculino", "Femenino"
        ));
        genderFilter.getSelectionModel().selectFirst();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applySearch());
        genderFilter.valueProperty().addListener((observable, oldValue, newValue) -> applySearch());
        filteredCharacters.addListener(
                (ListChangeListener<CharacterGoT>) change -> updateCount()
        );
        charactersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldCharacter, selectedCharacter) -> showCharacter(selectedCharacter)
        );
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        loadCharactersOnStartup();
    }

    @FXML
    private void refreshCharacters() {
        // La sincronización con la API solo se ejecuta cuando el usuario la solicita.
        synchronizeCharacters(false);
    }

    @FXML
    private void exportCharacters() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar personajes");
        chooser.setInitialFileName("personajes_exportados");
        FileChooser.ExtensionFilter csvFilter =
                new FileChooser.ExtensionFilter("Archivo CSV (*.csv)", "*.csv");
        FileChooser.ExtensionFilter jsonFilter =
                new FileChooser.ExtensionFilter("Archivo JSON (*.json)", "*.json");
        chooser.getExtensionFilters().addAll(csvFilter, jsonFilter);
        chooser.setSelectedExtensionFilter(csvFilter);

        File file = chooser.showSaveDialog(charactersTable.getScene().getWindow());
        if (file == null) {
            return;
        }

        String fileName = file.getName().toLowerCase(Locale.ROOT);
        boolean exportJson = fileName.endsWith(".json")
                || (!fileName.endsWith(".csv")
                && chooser.getSelectedExtensionFilter() == jsonFilter);
        String extension = exportJson ? ".json" : ".csv";
        if (!fileName.endsWith(".json") && !fileName.endsWith(".csv")) {
            file = new File(file.getParentFile(), file.getName() + extension);
        }

        File destination = file;
        List<CharacterGoT> charactersToExport = new ArrayList<>(characters);
        setCrudControlsDisabled(true);
        statusLabel.setText("Exportando personajes...");

        Task<Void> exportTask = new Task<>() {
            @Override
            protected Void call() throws IOException {
                if (exportJson) {
                    FileExporter.exportToJSON(charactersToExport, destination.getAbsolutePath());
                } else {
                    FileExporter.exportToCSV(charactersToExport, destination.getAbsolutePath());
                }
                return null;
            }
        };
        exportTask.setOnSucceeded(event -> {
            statusLabel.setText("Exportación completada: " + destination.getAbsolutePath());
            setCrudControlsDisabled(false);
        });
        exportTask.setOnFailed(event -> {
            Throwable error = exportTask.getException();
            statusLabel.setText("No se pudo exportar el fichero: "
                    + (error == null ? "error desconocido" : error.getMessage()));
            setCrudControlsDisabled(false);
        });
        Thread worker = new Thread(exportTask, "characters-export");
        worker.setDaemon(true);
        worker.start();
    }

    private void loadCharactersOnStartup() {
        statusLabel.setText("Cargando personajes guardados...");
        setCrudControlsDisabled(true);

        Task<List<CharacterGoT>> localLoadTask = new Task<>() {
            @Override
            protected List<CharacterGoT> call() throws Exception {
                return repository.findAllOrThrow();
            }
        };

        localLoadTask.setOnSucceeded(event -> {
            characters.setAll(localLoadTask.getValue());
            updateCount();
            if (characters.isEmpty()) {
                // La primera carga obtiene los datos online; los siguientes arranques respetan la BD.
                statusLabel.setText("La base de datos está vacía. Descargando datos iniciales...");
                synchronizeCharacters(true);
            } else {
                statusLabel.setText(characters.size() + " personajes cargados desde la base de datos.");
                setCrudControlsDisabled(false);
            }
        });
        localLoadTask.setOnFailed(event -> {
            Throwable error = localLoadTask.getException();
            statusLabel.setText("No se pudo leer la base de datos: "
                    + (error == null ? "error desconocido" : error.getMessage()));
            refreshButton.setDisable(false);
            setCrudControlsDisabled(false);
        });

        Thread loader = new Thread(localLoadTask, "characters-local-loader");
        loader.setDaemon(true);
        loader.start();
    }

    private void synchronizeCharacters(boolean initialLoad) {
        statusLabel.setText(initialLoad
                ? "Descargando los datos iniciales desde la API..."
                : "Actualizando datos desde la API...");
        setCrudControlsDisabled(true);

        Task<List<CharacterGoT>> syncTask = new Task<>() {
            @Override
            protected List<CharacterGoT> call() throws Exception {
                int downloaded = new ApiService().sincronizarPersonajes();
                updateMessage(downloaded + " personajes descargados. Actualizando tabla...");
                return repository.findAllOrThrow();
            }
        };

        syncTask.messageProperty().addListener((observable, oldValue, newValue) ->
                statusLabel.setText(newValue));
        syncTask.setOnSucceeded(event -> {
            characters.setAll(syncTask.getValue());
            updateCount();
            statusLabel.setText(initialLoad
                    ? characters.size() + " personajes cargados desde la API y guardados en la base de datos."
                    : characters.size() + " personajes actualizados desde la API.");
            refreshButton.setDisable(false);
            setCrudControlsDisabled(false);
        });
        syncTask.setOnFailed(event -> {
            Throwable error = syncTask.getException();
            String reason = error == null ? "error desconocido" : error.getMessage();
            statusLabel.setText(initialLoad
                    ? "No se pudieron cargar los datos iniciales: " + reason
                    : "Se mantienen los datos guardados; falló la actualización: " + reason);
            refreshButton.setDisable(false);
            setCrudControlsDisabled(false);
        });

        Thread loader = new Thread(syncTask, "characters-api-sync");
        loader.setDaemon(true);
        loader.start();
    }

    @FXML
    private void addCharacter() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            statusLabel.setText("El nombre es obligatorio para crear un personaje.");
            return;
        }

        CharacterGoT character = new CharacterGoT();
        try {
            String id = idField.getText().trim();
            if (!id.isEmpty()) {
                int parsedId = Integer.parseInt(id);
                if (parsedId <= 0) {
                    statusLabel.setText("El ID debe ser un número positivo.");
                    return;
                }
                character.setId(parsedId);
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("El ID debe ser un número entero válido.");
            return;
        }
        character.setName(name);
        character.setHouse(emptyToNull(houseField.getText()));
        character.setGender(emptyToNull(genderField.getText()));

        runCrudOperation("Personaje creado correctamente.", () -> repository.save(character));
    }

    @FXML
    private void updateCharacter() {
        CharacterGoT selected = charactersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Selecciona un personaje de la tabla para modificarlo.");
            return;
        }

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            statusLabel.setText("El nombre es obligatorio.");
            return;
        }

        CharacterGoT character = new CharacterGoT();
        character.setId(selected.getId());
        character.setName(name);
        character.setCulture(selected.getCulture());
        character.setHouse(emptyToNull(houseField.getText()));
        character.setGender(emptyToNull(genderField.getText()));
        runCrudOperation("Personaje actualizado correctamente.", () -> repository.update(character));
    }

    @FXML
    private void deleteCharacter() {
        CharacterGoT selected = charactersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Selecciona un personaje de la tabla para borrarlo.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Borrar personaje");
        confirmation.setHeaderText("¿Quieres borrar a " + displayValue(selected.getName()) + "?");
        confirmation.setContentText("Esta acción no se puede deshacer.");
        Optional<ButtonType> answer = confirmation.showAndWait();
        if (answer.isPresent() && answer.get() == ButtonType.OK) {
            runCrudOperation("Personaje borrado correctamente.",
                    () -> repository.delete((long) selected.getId()));
        }
    }

    @FXML
    private void clearEditor() {
        charactersTable.getSelectionModel().clearSelection();
        idField.clear();
        nameField.clear();
        houseField.clear();
        genderField.clear();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        statusLabel.setText("Formulario limpio. El ID puede dejarse vacío para generarlo automáticamente.");
    }

    private void runCrudOperation(String successMessage, DatabaseAction action) {
        setCrudControlsDisabled(true);
        statusLabel.setText("Procesando cambios...");

        Task<List<CharacterGoT>> operationTask = new Task<>() {
            @Override
            protected List<CharacterGoT> call() throws SQLException {
                action.run();
                return repository.findAllOrThrow();
            }
        };
        operationTask.setOnSucceeded(event -> {
            characters.setAll(operationTask.getValue());
            updateCount();
            clearEditor();
            statusLabel.setText(successMessage);
            setCrudControlsDisabled(false);
        });
        operationTask.setOnFailed(event -> {
            Throwable error = operationTask.getException();
            statusLabel.setText("No se pudo completar la operación: "
                    + (error == null ? "error desconocido" : error.getMessage()));
            setCrudControlsDisabled(false);
        });

        Thread worker = new Thread(operationTask, "character-crud-operation");
        worker.setDaemon(true);
        worker.start();
    }

    private void showCharacter(CharacterGoT character) {
        if (character == null) {
            return;
        }
        idField.setText(String.valueOf(character.getId()));
        nameField.setText(character.getName() == null ? "" : character.getName());
        houseField.setText(character.getHouse() == null ? "" : character.getHouse());
        genderField.setText(character.getGender() == null ? "" : character.getGender());
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    private void setCrudControlsDisabled(boolean disabled) {
        addButton.setDisable(disabled);
        updateButton.setDisable(disabled
                || charactersTable.getSelectionModel().getSelectedItem() == null);
        deleteButton.setDisable(disabled
                || charactersTable.getSelectionModel().getSelectedItem() == null);
        clearButton.setDisable(disabled);
        idField.setDisable(disabled);
        nameField.setDisable(disabled);
        houseField.setDisable(disabled);
        genderField.setDisable(disabled);
        refreshButton.setDisable(disabled);
        exportButton.setDisable(disabled);
    }

    private void applySearch() {
        String query = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String selectedGender = genderFilter.getValue();

        filteredCharacters.setPredicate(character -> {
            boolean matchesSearch = query.isEmpty()
                    || contains(character.getName(), query)
                    || contains(character.getHouse(), query);
            boolean matchesGender = switch (selectedGender == null ? "Todos" : selectedGender) {
                case "Masculino" -> "Male".equalsIgnoreCase(character.getGender());
                case "Femenino" -> "Female".equalsIgnoreCase(character.getGender());
                default -> true;
            };
            return matchesSearch && matchesGender;
        });
        updateCount();
    }

    private void updateCount() {
        int count = filteredCharacters.size();
        countLabel.setText(count + (count == 1 ? " personaje" : " personajes"));
    }

    private static boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private static String displayValue(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    private static String emptyToNull(String value) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @FunctionalInterface
    private interface DatabaseAction {
        void run() throws SQLException;
    }
}
