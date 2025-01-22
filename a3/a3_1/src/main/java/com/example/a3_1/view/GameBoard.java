package com.example.a3_1.view;

import com.example.a3_1.Controller;
import com.example.a3_1.model.BoardStateNode;
import com.example.a3_1.model.PublishSubscribe;

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
    setStyle("-fx-background-color: lightgrey");
  }

  public void update(double displaySize, BoardStateNode boardState) {
    for (Node child : getChildren()) {
      if (child instanceof BoardPiece piece) {
        // update each piece on the board based on the data from the model
        piece.setType(boardState.board[piece.row][piece.col], false);
        piece.setRadius(displaySize * 0.05);
        setMargin(piece, new Insets(displaySize * 0.01));
      }
    }
  }
}
