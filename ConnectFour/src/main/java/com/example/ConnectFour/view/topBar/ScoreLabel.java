package com.example.ConnectFour.view.topBar;

import com.example.ConnectFour.model.Model.PieceType;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class ScoreLabel extends HBox {

  private Label playerLabel;
  private Label scoreLabel;
  private Color color;

  public ScoreLabel(PieceType type) {

    playerLabel = new Label();
    scoreLabel = new Label();

    switch(type) {
      case Player -> {
        playerLabel.setText("Player");
        color = Color.web("#31FFFF");
        setAlignment(Pos.CENTER_LEFT);
      }
      case Computer -> {
        playerLabel.setText("Computer");
        color = Color.web("#FF65C8");
        setAlignment(Pos.CENTER_RIGHT);
      }
    }
    getChildren().addAll(playerLabel, scoreLabel);
  }


  public void update(double displaySize, int score) {
    setSpacing(displaySize * 0.02);
    scoreLabel.setText(Integer.toString(score));
    scoreLabel.setStyle(String.format("-fx-text-fill: #7781A3; -fx-font-size: %f", displaySize * 0.035));
    playerLabel.setStyle(String.format(
          "-fx-effect: dropshadow(gaussian, #000000, %f, %f, 0, 0);"+
          "-fx-text-fill: %s; -fx-font-size: %f", 
          displaySize * 0.005 ,displaySize * 0.005, color.toString().replace("0x","#"), displaySize * 0.0333));
  }
}
