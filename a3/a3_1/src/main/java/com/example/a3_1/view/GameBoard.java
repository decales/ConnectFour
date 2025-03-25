package com.example.a3_1.view;

import com.example.a3_1.Controller;
import com.example.a3_1.model.BoardPosition;
import com.example.a3_1.model.BoardState;
import com.example.a3_1.model.PublishSubscribe;
import com.example.a3_1.model.Model.GameState;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class GameBoard extends GridPane implements PublishSubscribe {

  public GameBoard(Controller controller) {

    // setup event handlers
    setOnMouseClicked(controller::handleMouseClicked);
    setOnMouseMoved(controller::handleMouseMoved);
    setOnMouseExited(controller::handleMouseExited);

    setGridLinesVisible(true);
    setStyle("-fx-background-color: #2a2e3d");
  }
  
  public void initializeBoard(int numRows, int numCols) {
    getChildren().clear();

    // initialize BoardPiece children and add them to the grid
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        add(new BoardPiece(i, j), j, i);
      }
    }
    // yes, this is necessary if you want the gridlines to reappear after clearing the children :)
    setGridLinesVisible(false); setGridLinesVisible(true);
  }


  public void update(
      double displaySize, 
      GameState gameState, 
      BoardState boardState, 
      BoardPosition previewPosition, 
      int playerWinCount, int computerWinCount) {

    // initialize the board when app opens or when board dimensions are toggled
    if (getChildren().size() != boardState.board.length * boardState.board[0].length) {
      initializeBoard(boardState.board.length, boardState.board[0].length);
    }

    for (Node child : getChildren()) {
      if (child instanceof BoardPiece piece) {

        // if a player has won, check if a piece is part of the game winning sequence
        boolean inSequence = (gameState == GameState.PlayerWin || gameState == GameState.ComputerWin) 
        ? boardState.winningSequence.contains(piece.position)
        : false;

        // check if the position is where the move preview indicator should be
        boolean isPreview = (gameState == GameState.InProgress) ? piece.position.equals(previewPosition) : false; 
        
        // update each piece on the board based on the data from the model
        piece.setSize((displaySize * 0.3) / (boardState.board.length));
        piece.setType(boardState.board[piece.position.row][piece.position.col], isPreview, inSequence);
      }
    }
  }
}
