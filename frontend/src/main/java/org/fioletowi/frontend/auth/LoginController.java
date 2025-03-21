package org.fioletowi.frontend.auth;

import org.fioletowi.frontend.auth.admin.AdminTeamsController;
import org.fioletowi.frontend.config.ConfigLoader;
import org.fioletowi.frontend.config.JacksonConfig;
import org.fioletowi.frontend.model.TokenManager;
import org.fioletowi.frontend.user_profile.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class LoginController {

    @FXML
    private Label login_message;

    @FXML
    private TextField email;

    @FXML
    private Label forgot_password;

    @FXML
    private PasswordField password;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = JacksonConfig.createObjectMapper();
    private final String apiUrl = ConfigLoader.getProperty("api.url");

    public void loginButtonOnAction(ActionEvent e) {
        try {
            AuthRequest authRequest = AuthRequest.builder()
                    .email(email.getText())
                    .password(password.getText())
                    .build();

            String requestBody = objectMapper.writeValueAsString(authRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl + "/auth/authenticate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                AuthResponse authResponse = objectMapper.readValue(response.body(), AuthResponse.class);

                login_message.setText("Login successful!");
                System.out.println("Token: " + authResponse.getToken());

                TokenManager.setToken(authResponse.getToken());

                loadUserProfile(authResponse.getToken());

                switchToAdminTeamsView(e);
            } else {
                login_message.setText("Invalid credentials. Try again.");
                System.out.println("Error: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void loadUserProfile(String token) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl + "/user/me"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                UserResponse user = objectMapper.readValue(response.body(), UserResponse.class);

                UserSession.setCurrentUser(user);

                System.out.println("Dane użytkownika pobrane: " + user.getFirstName() + " " + user.getLastName());
            } else {
                System.out.println("Błąd pobierania profilu: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchToAdminTeamsView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/farm_registration/admin profile.fxml"));
            Parent adminTeamsView = loader.load();

            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(adminTeamsView);

            Stage stage = (Stage) scene.getWindow();
            stage.setTitle("Admin Teams");
        } catch (IOException ex) {
            ex.printStackTrace();
            login_message.setText("Error loading admin teams view.");
        }
    }

    public void forgotPasswordButtonOnAction(MouseEvent event) {
        switchView(event, "/com/example/farm_registration/forgot_password.fxml", "Resetowanie hasła");
    }

    public void registerButtonOnAction(ActionEvent event) {
        switchView(event, "/com/example/farm_registration/registration.fxml", "Registration");
    }

    private void switchView(javafx.event.Event event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(view);

            Stage stage = (Stage) scene.getWindow();
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            login_message.setText("Błąd podczas ładowania widoku: " + title);
        }
    }
}
