package ui;

import database.DatabaseService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        try {
            new DatabaseService().initializeDatabase();
        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de base de datos");
            alert.setHeaderText("No se pudo preparar la base de datos.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            stage.close();
            return;
        }

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/main-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(
                MainApp.class.getResource("/styles/main.css").toExternalForm()
        );

        stage.setTitle("Crónicas de Poniente | Personajes");
        stage.setMinWidth(820);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
