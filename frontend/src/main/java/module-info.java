module org.fioletowi.frontend {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.fioletowi.frontend to javafx.fxml;
    exports org.fioletowi.frontend;
}