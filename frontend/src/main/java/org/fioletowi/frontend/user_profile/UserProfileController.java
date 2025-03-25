package org.fioletowi.frontend.user_profile;


import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import org.fioletowi.frontend.auth.TokenManager;
import org.fioletowi.frontend.auth.UserSession;
import org.fioletowi.frontend.config.ConfigLoader;
import org.fioletowi.frontend.config.JacksonConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserProfileController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label birthDateLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label addressLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = JacksonConfig.createObjectMapper();
    private final String apiUrl = ConfigLoader.getProperty("api.url");

    public void initialize() {
        loadUserProfile();
    }

    private void loadUserProfile() {
        UserResponse user = UserSession.getCurrentUser();

        if (user != null) {
            System.out.println("Załadowano dane z pamięci lokalnej.");
            updateUI(user);
        } else {
            System.out.println("Pobieranie danych użytkownika z API...");
            fetchUserProfileFromAPI();
        }
    }

    private void fetchUserProfileFromAPI() {
        try {
            String token = TokenManager.getToken();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl + "/user/me"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                UserResponse user = objectMapper.readValue(response.body(), UserResponse.class);

                // Zapisujemy w pamięci
                UserSession.setCurrentUser(user);

                // Aktualizujemy UI
                updateUI(user);
            } else {
                System.out.println("Błąd pobierania profilu: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI(UserResponse user) {
        nameLabel.setText(user.getFirstName() + " " + user.getLastName());
        emailLabel.setText(user.getEmail());
        birthDateLabel.setText(user.getBirthDate().toString());
        phoneLabel.setText(user.getPhoneNumber());
        addressLabel.setText(user.getAddress());
    }

    public void goToSettings(ActionEvent event) throws IOException {
        switchView(event, "/com/example/farm_registration/admin profile settings.fxml");
    }

    public void goToChangePassword(ActionEvent event) throws IOException {
        switchView(event, "/com/example/farm_registration/admin profile password.fxml");
    }

    public void logOut(ActionEvent event) throws IOException {
        TokenManager.clearToken();
        UserSession.clear();  // Czyścimy dane użytkownika
        switchView(event, "/com/example/farm_registration/login.fxml");
    }

    private void switchView(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent view = loader.load();
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(view);
    }
}
