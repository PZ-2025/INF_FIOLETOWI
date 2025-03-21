module org.fioletowi.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires com.fasterxml.jackson.databind;


    opens org.fioletowi.frontend to javafx.fxml;
    requires java.net.http;
    requires com.google.gson;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.prefs;
    exports org.fioletowi.frontend;
    exports org.fioletowi.frontend.auth;
    exports org.fioletowi.frontend.admin;
    opens org.fioletowi.frontend.auth to javafx.fxml, com.google.gson;
    opens org.fioletowi.frontend.admin to javafx.fxml;
    exports org.fioletowi.frontend.model;
    opens org.fioletowi.frontend.model to javafx.fxml, com.google.gson, com.fasterxml.jackson.databind;
    exports org.fioletowi.frontend.user_profile;
    opens org.fioletowi.frontend.user_profile to com.google.gson, javafx.fxml;
}
