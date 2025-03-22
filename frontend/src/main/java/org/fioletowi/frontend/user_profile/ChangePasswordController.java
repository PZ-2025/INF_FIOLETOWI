package org.fioletowi.frontend.user_profile;


import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import org.fioletowi.frontend.auth.TokenManager;
import org.fioletowi.frontend.config.ConfigLoader;
import org.fioletowi.frontend.config.JacksonConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ChangePasswordController {

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new JacksonConfig().createObjectMapper();
    private final String apiUrl = ConfigLoader.getProperty("api.url");

    public void changePassword() {
        if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
            messageLabel.setText("Nowe hasła nie są identyczne!");
            return;
        }

        try {
            String token = TokenManager.getToken();

            ChangePasswordRequest requestPayload = new ChangePasswordRequest(
                    oldPasswordField.getText(),
                    newPasswordField.getText()
            );

            String requestBody = objectMapper.writeValueAsString(requestPayload);
            System.out.println(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl + "/auth/change-password"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                messageLabel.setText("Hasło zmienione!");
            } else {
                messageLabel.setText("Błąd zmiany hasła: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
