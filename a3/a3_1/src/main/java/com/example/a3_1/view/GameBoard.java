package com.example.a3_1.view;

import com.example.a3_1.model.PublishSubscribe;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameBoard extends GridPane implements PublishSubscribe {

  public GameBoard() {

    setGridLinesVisible(true);
    setStyle("-fx-background-color: lightgrey");

  }

  public void update(double displaySize, int[][] gameBoard) {

    for (int i = 0; i < gameBoard.length; i++) {
      for (int j = 0; j < gameBoard[0].length; j++) {

        Color pieceColor = null;
        switch (gameBoard[i][j]) {
          case 0 -> pieceColor = Color.TRANSPARENT; // No piece 
          case 1 -> pieceColor = Color.BLACK; // CPU piece
          case 2 -> pieceColor = Color.WHITE; // Player piece
        }

        Circle tilePiece = new Circle(displaySize * 0.05);
        tilePiece.setFill(pieceColor);
        setMargin(tilePiece, new Insets(displaySize * 0.01));
        add(tilePiece, j, i);
      }
    }
  }
}
