package com.example.a3_1.view.topBar;

import com.example.a3_1.model.AppState.GameState;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class StateLabel extends StackPane {

  private Label mainLabel;

  public StateLabel() {
    mainLabel = new Label();
    getChildren().addAll(mainLabel);
  }


  public void update(double displaySize, GameState state, int moveCount) {

    String s = "";
    Color c = Color.TRANSPARENT;

    switch(state) {
      case PlayerWin -> { s = "Player wins"; c = Color.web("#31FFFF"); }
      case ComputerWin -> { s = "Computer wins"; c = Color.web("#FF65C8"); }
      case Draw -> s = "Draw";
      case InProgress -> s = String.format("Turn %d", moveCount / 2 + 1);
    }

    setEffect(new DropShadow(displaySize * 0.0333, c));

    mainLabel.setText(s);
    mainLabel.setStyle(String.format(
          "-fx-effect: dropshadow(gaussian, #000000, %f, %f, 0, 0);"+
          "-fx-text-fill: #7781A3;"+
          "-fx-font-size: %f", 
          displaySize * 0.004 ,displaySize * 0.004, displaySize * 0.02667));
  }
}
