package org.fioletowi.frontend.auth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class RegistrationSuccessController {

    @FXML
    private Text user_email;

    /*@FXML
    public void initialize() {
        user_email.setText("[email@gmail.com]");
    }*/
    public void setUserEmail(String email) {
        user_email.setText(email);
    }

    @FXML
    private void backToLoginOnAction(ActionEvent event) {
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
