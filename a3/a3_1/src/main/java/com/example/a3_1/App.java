package com.example.a3_1;

import com.example.a3_1.model.Model;
import com.example.a3_1.view.GameBoard;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {

        
      double displayHeight = Screen.getPrimary().getBounds().getHeight();
      double displayRatio = 0.8;
      double rootPaddingRatio = 0.05;        

      // Initialize model
      Model model = new Model(displayHeight * displayRatio);

      // Initialize controller
      Controller controller = new Controller(model);

      // // Initialize UI components
      HBox root = new HBox();
      // root.paddingProperty().set(new Insets(rootPadding));
      // root.spacingProperty().set(rootPadding);
      // root.setStyle("-fx-background-color: lightgrey");

      GameBoard gameBoard = new GameBoard(6, 7, controller);

      model.addSubscribers(gameBoard);
      root.getChildren().addAll(gameBoard);

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
