package com.example.a3_1.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import com.example.a3_1.model.BoardStateNode;
import com.example.a3_1.model.PublishSubscribe;
import com.example.a3_1.model.Model.GameState;

public class ScoreBar extends HBox implements PublishSubscribe {

  private Label playerScoreLabel;
  private Label computerScoreLabel;
  private Button ResetButton;
  
  public ScoreBar() {

    playerScoreLabel = new Label();
    computerScoreLabel = new Label();
    getChildren().addAll(playerScoreLabel, computerScoreLabel);

    setAlignment(Pos.CENTER);
  }

  public void update(double displaySize, BoardStateNode boardState, GameState gameState, int playerWinCount, int computerWinCount) {
    
    playerScoreLabel.setText("Player: " + playerWinCount);
    computerScoreLabel.setText("Computer: " + computerWinCount);

  }

  
}
