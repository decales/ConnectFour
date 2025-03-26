package com.example.a3_1;

import com.example.a3_1.model.Model;
import com.example.a3_1.view.bottomBar.BottomBar;
import com.example.a3_1.view.gameBoard.GameBoard;
import com.example.a3_1.view.topBar.TopBar;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {
  @Override
  public void start(Stage stage) {

    double displayHeight = Screen.getPrimary().getBounds().getHeight();
    double displayRatio = 0.8;
    double displaySize = displayHeight * displayRatio;

    // Initialize model
    Model model = new Model(displaySize);

    // Initialize controller
    Controller controller = new Controller(model);

    // // Initialize UI components
    VBox root = new VBox();
    root.setAlignment(Pos.CENTER);
    root.setStyle("-fx-background-color: #20232F");
    double p = displaySize * 0.015;
    root.paddingProperty().set(new Insets(p, p * 3, p, p * 3));

    TopBar scoreBar = new TopBar();
    GameBoard gameBoard = new GameBoard(controller);
    BottomBar bottomBar = new BottomBar(controller);

    model.addSubscribers(scoreBar, gameBoard, bottomBar);
    root.getChildren().addAll(scoreBar, gameBoard, bottomBar);

    Scene scene = new Scene(root);
    stage.setTitle("Negamax Connect Four");
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
