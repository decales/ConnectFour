package com.example.ConnectFour.view.gameBoard;

import com.example.ConnectFour.Controller;
import com.example.ConnectFour.model.AppState;
import com.example.ConnectFour.model.BoardState;
import com.example.ConnectFour.model.PublishSubscribe;
import com.example.ConnectFour.model.AppState.GameState;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class GameBoard extends GridPane implements PublishSubscribe {

  public GameBoard(Controller controller) {

    // setup event handlers
    setOnMouseClicked(controller::handleMouseClicked);
    setOnMouseMoved(controller::handleMouseMoved);
    setOnMouseExited(controller::handleMouseExited);

    setAlignment(Pos.CENTER);
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


  public void update(AppState appState, BoardState boardState) {

    setStyle(String.format(
          "-fx-background-color: #2a2e3d;"+ 
          "-fx-border-color: #000000;"+
          "-fx-border-width: %fpx;"+
          "-fx-border-radius: %fpx;"+
          "-fx-background-radius: %fpx;", 
          appState.displaySize * 0.00667, appState.displaySize * 0.015, appState.displaySize * 0.03));

    // initialize the board when app opens or when board dimensions are toggled
    if (getChildren().size() != boardState.board.length * boardState.board[0].length) {
      initializeBoard(boardState.board.length, boardState.board[0].length);
    }

    for (Node child : getChildren()) {
      if (child instanceof BoardPiece piece) {

        // if a player has won, check if a piece is part of the game winning sequence
        boolean inSequence = (appState.state == GameState.PlayerWin || appState.state == GameState.ComputerWin) 
        ? boardState.winningSequence.contains(piece.position)
        : false;

        // check if the position is where the move preview indicator should be
        boolean isPreview = (appState.state == GameState.InProgress && boardState.movePosition != null) 
        ? piece.position.equals(boardState.movePosition) 
        : false; 
        
        // update each piece on the board based on the data from the model
        piece.update(
          (appState.displaySize * 0.3) / (boardState.board.length), 
          boardState.board[piece.position.row][piece.position.col], 
          isPreview, inSequence);
      }
    }
  }
}
