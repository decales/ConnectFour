module com.example.ConnectFour {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.ConnectFour to javafx.fxml;
    exports com.example.ConnectFour;

    exports com.example.ConnectFour.model;
    opens com.example.ConnectFour.model to javafx.fxml;

    exports com.example.ConnectFour.view.topBar;
    opens com.example.ConnectFour.view.topBar to javafx.fxml;

    exports com.example.ConnectFour.view.gameBoard;
    opens com.example.ConnectFour.view.gameBoard to javafx.fxml;

    exports com.example.ConnectFour.view.bottomBar;
    opens com.example.ConnectFour.view.bottomBar to javafx.fxml;
}
