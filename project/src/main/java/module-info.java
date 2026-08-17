module com.mursu {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mursu to javafx.fxml;
    exports com.mursu;
}
