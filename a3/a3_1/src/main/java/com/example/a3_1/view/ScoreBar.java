package com.example.a3_1.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import com.example.a3_1.model.AppState;
import com.example.a3_1.model.BoardState;
import com.example.a3_1.model.PublishSubscribe;

public class ScoreBar extends HBox implements PublishSubscribe {

  private Label playerScoreLabel;
  private Label computerScoreLabel;
  
  public ScoreBar() {
    setAlignment(Pos.CENTER);

    playerScoreLabel = new Label();
    computerScoreLabel = new Label();
    getChildren().addAll(playerScoreLabel, computerScoreLabel);
  }


  public void update(AppState appState, BoardState boardState) {
    
    playerScoreLabel.setText("Player: " + appState.playerWinCount);
    computerScoreLabel.setText("Computer: " + appState.computerWinCount);
  }
}
