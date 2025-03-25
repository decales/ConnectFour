package com.example.a3_1;

import com.example.a3_1.model.Model;
import com.example.a3_1.view.BottomBar;
import com.example.a3_1.view.GameBoard;
import com.example.a3_1.view.ScoreBar;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {

      double displayHeight = Screen.getPrimary().getBounds().getHeight();
      double displayRatio = 0.8;
      double rootPadding = displayHeight * displayRatio * 0.025;        

      // Initialize model
      Model model = new Model(displayHeight * displayRatio);

      // Initialize controller
      Controller controller = new Controller(model);

      // // Initialize UI components
      VBox root = new VBox();
      root.paddingProperty().set(new Insets(rootPadding));
      // root.spacingProperty().set(rootPadding);
      // root.setStyle("-fx-background-color: lightgrey");
      //

      ScoreBar scoreBar = new ScoreBar();
      GameBoard gameBoard = new GameBoard(controller);
      BottomBar bottomBar = new BottomBar(controller);

      model.addSubscribers(scoreBar, gameBoard, bottomBar);
      root.getChildren().addAll(scoreBar, gameBoard, bottomBar);

      Scene scene = new Scene(root);
      stage.setTitle("");
      stage.setScene(scene);
      stage.setResizable(false);
      stage.show();
    }

    public static void main(String[] args) {
      launch();
    }
}
