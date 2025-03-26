module com.example.a3_1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.a3_1 to javafx.fxml;
    exports com.example.a3_1;

    exports com.example.a3_1.model;
    opens com.example.a3_1.model to javafx.fxml;

    exports com.example.a3_1.view.topBar;
    opens com.example.a3_1.view.topBar to javafx.fxml;

    exports com.example.a3_1.view.gameBoard;
    opens com.example.a3_1.view.gameBoard to javafx.fxml;

    exports com.example.a3_1.view.bottomBar;
    opens com.example.a3_1.view.bottomBar to javafx.fxml;
}
