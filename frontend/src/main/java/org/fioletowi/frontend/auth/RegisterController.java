package com.example.farm_registration.auth;

import com.example.farm_registration.config.ConfigLoader;
import com.example.farm_registration.util.LocalDateTimeAdapter;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class RegisterController {

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private TextField firstname;

    @FXML
    private TextField surname;

    @FXML
    private TextField phone_number;

    @FXML
    private TextField address;

    @FXML
    private DatePicker date;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final Gson gson = new Gson().newBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();


    String apiUrl = ConfigLoader.getProperty("api.url");

    public void registerButtonOnAction(ActionEvent e) {
        //co najmniej 8 znakow
        if (password.getText().length() < 8) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("The password must be at least 8 characters long.");
            alert.showAndWait();
            return;
        }

        try {
            RegistrationRequest registerRequest = RegistrationRequest.builder()
                    .email(email.getText())
                    .password(password.getText())
                    .firstName(firstname.getText())
                    .lastName(surname.getText())
                    .phoneNumber(phone_number.getText())
                    .address(address.getText())
                    .birthDate(date.getValue().atStartOfDay())
                    .build();

            String requestBody = gson.toJson(registerRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl + "/auth/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 202) {
                System.out.println("User registered successfully.");
                switchToSuccessView(e, email.getText());
            } else {
                System.out.println("Error: " + response.statusCode() + " - " + response.body());
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    private void switchToSuccessView(ActionEvent event, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/farm_registration/registration_success.fxml"));
            Parent root = loader.load();

            RegistrationSuccessController controller = loader.getController();
            controller.setUserEmail(email);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
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

