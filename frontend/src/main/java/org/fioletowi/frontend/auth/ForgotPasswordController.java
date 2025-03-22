package com.example.farm_registration.auth;

import com.example.farm_registration.config.ConfigLoader;
import com.google.gson.Gson;
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

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField codeField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String apiUrl = ConfigLoader.getProperty("api.url");

    /**
     * Wysyła kod resetujący na podany adres e-mail.
     */
    public void sendCodeOnAction(ActionEvent event) {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            messageLabel.setText("Wprowadź adres e-mail.");
            return;
        }

        ForgotPasswordRequest requestPayload = ForgotPasswordRequest.builder()
                .email(email)
                .build();

        String requestBody = gson.toJson(requestPayload);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl + "/auth/forgot-password"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                messageLabel.setText("Kod został wysłany na e-mail.");
            } else {
                messageLabel.setText("Błąd: " + response.body());
            }
        } catch (Exception e) {
            messageLabel.setText("Błąd połączenia.");
            e.printStackTrace();
        }
    }

    /**
     * Weryfikuje kod i pozwala na zmianę hasła.
     */
    public void changePasswordOnAction(ActionEvent event) {
        String email = emailField.getText().trim();
        String code = codeField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (email.isEmpty() || code.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Wypełnij wszystkie pola.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("Hasła nie są identyczne.");
            return;
        }

        ResetPasswordRequest requestPayload = ResetPasswordRequest.builder()
                .email(email)
                .token(code)
                .newPassword(newPassword)
                .build();

        String requestBody = gson.toJson(requestPayload);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl + "/auth/reset-password"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                messageLabel.setText("Hasło zostało zmienione.");
            } else {
                messageLabel.setText("Błąd: " + response.body());
            }
        } catch (Exception e) {
            messageLabel.setText("Błąd połączenia.");
            e.printStackTrace();
        }
    }

    // Metoda przełączająca widok na "Forgot Password New"
    public void goToResetPasswordViewOnAction(MouseEvent event) {
        try {
            // Ładujemy widok forgot_password_new.fxml – upewnij się, że ścieżka jest poprawna
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/farm_registration/forgot_password_new.fxml"));
            Parent resetPasswordRoot = loader.load();

            // Pobieramy aktualną scenę i ustawiamy nowy root
            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(resetPasswordRoot);

            // Opcjonalnie ustaw tytuł okna
            Stage stage = (Stage) currentScene.getWindow();
            stage.setTitle("Reset Password");
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Nie udało się przejść do widoku resetowania hasła.");
        }
    }
    public void goBackLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/farm_registration/login.fxml"));
            Parent loginView = loader.load();

            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(loginView);

            Stage stage = (Stage) currentScene.getWindow();
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goBackLoginOnMouseClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/farm_registration/login.fxml"));
            Parent loginView = loader.load();

            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(loginView);

            Stage stage = (Stage) currentScene.getWindow();
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
