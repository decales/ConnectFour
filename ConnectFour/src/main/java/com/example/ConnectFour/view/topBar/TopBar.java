package com.example.ConnectFour.view.topBar;

import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import com.example.ConnectFour.model.AppState;
import com.example.ConnectFour.model.BoardState;
import com.example.ConnectFour.model.PublishSubscribe;
import com.example.ConnectFour.model.Model.PieceType;

public class TopBar extends StackPane implements PublishSubscribe {

  private ScoreLabel playerScoreLabel;
  private ScoreLabel computerScoreLabel;
  private StateLabel stateLabel;
  
  public TopBar() {

    playerScoreLabel = new ScoreLabel(PieceType.Player);
    computerScoreLabel = new ScoreLabel(PieceType.Computer);
    stateLabel = new StateLabel();
    
    getChildren().addAll(playerScoreLabel, stateLabel, computerScoreLabel);
  }


  public void update(AppState appState, BoardState boardState) {
    double p = appState.displaySize * 0.015;
    setPadding(new Insets(0, p * 4, p, p * 4));

    playerScoreLabel.update(appState.displaySize, appState.playerScore);
    computerScoreLabel.update(appState.displaySize, appState.computerScore);
    stateLabel.update(appState.displaySize, appState.state, boardState.numberPiecesMoved);
  }
}
