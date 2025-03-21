package org.fioletowi.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.prefs.Preferences;

public class Main extends Application {

    public static Preferences getPreferences() {
        return Preferences.userNodeForPackage(Main.class);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        // Sprawdzamy, czy użytkownik ma Dark Mode włączony
        Preferences preferences = getPreferences();
        boolean isDarkMode = preferences.getBoolean("dark_mode", false); // Domyślnie OF

        if (isDarkMode) {
            scene.getStylesheets().add(getClass().getResource("/com/example/styles/dark-mode.css").toExternalForm());
        }
        else {
            scene.getStylesheets().add(getClass().getResource("/com/example/styles/light-mode.css").toExternalForm());
        }

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
