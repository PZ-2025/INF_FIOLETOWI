package org.fioletowi.frontend.user_profile;


import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import org.fioletowi.frontend.Main;
import org.fioletowi.frontend.auth.TokenManager;
import org.fioletowi.frontend.auth.UserSession;
import org.fioletowi.frontend.config.ConfigLoader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.prefs.Preferences;

public class UserSettingsController {

    @FXML
    private ToggleButton notificationsToggle;

    @FXML
    private ToggleButton darkModeToggle;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiUrl = ConfigLoader.getProperty("api.url");

    private final Preferences preferences = Preferences.userNodeForPackage(UserSettingsController.class); // Pamięć lokalna

    public void initialize() {
        loadUserSettings();

        // Wczytujemy ustawienie Dark Mode z preferencji
        Preferences preferences = Main.getPreferences();
        boolean isDarkMode = preferences.getBoolean("dark_mode", false); // Domyślnie OFF

        // Ustawiamy stan przycisku Dark Mode na podstawie preferencji
        darkModeToggle.setSelected(isDarkMode); // Ustawiamy przycisk zgodnie z zapisaną wartością
        applyDarkMode(isDarkMode);  // Zastosuj Dark Mode

        // Ustawienie tekstu na przycisku
        updateToggleButtonText();
    }

    private void loadUserSettings() {
        UserResponse user = UserSession.getCurrentUser();

        if (user != null) {
            // Pobieramy ustawienia użytkownika z pamięci
            System.out.println("Załadowano ustawienia użytkownika z pamięci lokalnej.");
            notificationsToggle.setSelected(user.getAllowNotifications());
        } else {
            // Brak danych, pobieramy z API
            System.out.println("Pobieranie ustawień użytkownika z API...");
            fetchUserSettingsFromAPI();
        }

        // Sprawdzamy Dark Mode w pamięci lokalnej i ustawiamy wartość
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        darkModeToggle.setSelected(isDarkMode);
        applyDarkMode(isDarkMode);

        updateToggleButtonText();
    }

    private void fetchUserSettingsFromAPI() {
        try {
            String token = TokenManager.getToken();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl + "/user/settings"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                UserResponse user = objectMapper.readValue(response.body(), UserResponse.class);

                // Zapisujemy w pamięci lokalnej
                UserSession.setCurrentUser(user);

                // Aktualizujemy UI
                notificationsToggle.setSelected(user.getAllowNotifications());

                System.out.println("Ustawienia użytkownika pobrane i zapisane w pamięci.");
            } else {
                System.out.println("Błąd pobierania ustawień: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleNotifications() {
        boolean isSelected = notificationsToggle.isSelected();
        UserResponse currentUser = UserSession.getCurrentUser();
        if (currentUser != null) {
            currentUser.setAllowNotifications(isSelected);
            UserSession.setCurrentUser(currentUser);
        }
        saveSettings();
        updateToggleButtonText();
    }

    @FXML
    private void toggleDarkMode() {
        boolean isDarkMode = darkModeToggle.isSelected();

        // Zapisujemy ustawienie Dark Mode w preferencjach z Main
        Preferences preferences = Main.getPreferences();
        preferences.putBoolean("dark_mode", isDarkMode); // Zapisujemy jako true lub false

        // Zastosuj Dark Mode na scenie
        applyDarkMode(isDarkMode);
        updateToggleButtonText();
        System.out.println("Dark Mode: " + (isDarkMode ? "ON" : "OFF"));
    }

    private void applyDarkMode(boolean isDarkMode) {
        Scene scene = darkModeToggle.getScene();
        if (scene != null) {
            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("/com/example/styles/dark-mode.css").toExternalForm());
            } else {
                scene.getStylesheets().clear();  // Usuwamy styl dla ciemnego motywu, jeśli wyłączono
                scene.getStylesheets().add(getClass().getResource("/com/example/styles/light-mode.css").toExternalForm());
            }
        }
    }

    private void updateToggleButtonText() {
        notificationsToggle.setText(notificationsToggle.isSelected() ? "ON" : "OFF");
        darkModeToggle.setText(darkModeToggle.isSelected() ? "ON" : "OFF");
    }

    public void saveSettings() {
        try {
            String token = TokenManager.getToken();
            UserSettingsRequest settingsRequest = new UserSettingsRequest(notificationsToggle.isSelected());
            String requestBody = objectMapper.writeValueAsString(settingsRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl + "/user/settings"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Ustawienia zapisane!");

                UserResponse currentUser = UserSession.getCurrentUser();
                if (currentUser != null) {
                    currentUser.setAllowNotifications(notificationsToggle.isSelected());
                    UserSession.setCurrentUser(currentUser);
                    System.out.println("Pamięć lokalna zaktualizowana.");
                }
            } else {
                System.out.println("Błąd zapisywania ustawień: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
