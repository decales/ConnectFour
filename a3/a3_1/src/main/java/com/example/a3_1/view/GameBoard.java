package com.example.a3_1.view;

import com.example.a3_1.Controller;
import com.example.a3_1.model.BoardPosition;
import com.example.a3_1.model.BoardStateNode;
import com.example.a3_1.model.PublishSubscribe;
import com.example.a3_1.model.Model.GameState;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class GameBoard extends GridPane implements PublishSubscribe {

  public GameBoard(int numRows, int numCols, Controller controller) {

    // initialize BoardPiece children and add them to the grid
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        add(new BoardPiece(i, j), j, i);
      }
    }
    // setup event handlers
    setOnMouseClicked(controller::handleMouseClicked);
    setOnMouseMoved(controller::handleMouseMoved);
    setOnMouseExited(controller::handleMouseExited);

    setGridLinesVisible(true);
    setStyle("-fx-background-color: #2a2e3d");
  }

  public void update(double displaySize, BoardStateNode boardState, GameState gameState, int playerWinCount, int computerWinCount) {
    for (Node child : getChildren()) {
      if (child instanceof BoardPiece piece) {
        
        // if the game has ended, check if a piece is part of the game winning sequence
        boolean inSequence = (gameState != GameState.InProgress) ? boardState.winningSequence.contains(new BoardPosition(piece.row, piece.col)) : false;
        
        // update each piece on the board based on the data from the model
        piece.setSize(displaySize * 0.05);
        piece.setType(boardState.board[piece.row][piece.col], inSequence);
      }
    }
  }
}
