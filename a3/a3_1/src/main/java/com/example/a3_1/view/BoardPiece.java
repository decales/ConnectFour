package com.example.a3_1.view;

import com.example.a3_1.model.Model.PieceType;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BoardPiece extends Circle {

  protected PieceType pieceType;
  protected int row, col;

  public BoardPiece(int row, int col) {

    setType(PieceType.None, false);
    this.row = row;
    this.col = col;
  }

  public void setType(PieceType pieceType, boolean inSequence) {
    if (this.pieceType != pieceType || inSequence) { // only update piece sprite if it needs to be

      this.pieceType = pieceType; // update type
      
      // update circle sprite
      Color pieceColour = null;
      Color borderColour = null;

      switch(this.pieceType) {
        case Player -> {
          pieceColour = Color.WHITE;
          borderColour = Color.BLACK;
        }
        case Computer -> {
          pieceColour = Color.BLACK;
          borderColour = Color.BLACK;
        }
        case None -> {
          pieceColour = Color.TRANSPARENT;
          borderColour = Color.TRANSPARENT;
        }
        case Preview -> {
          pieceColour = Color.WHITE;
          borderColour = Color.MAGENTA;
        }
      }
      setFill(pieceColour);
      setStroke(borderColour);
    }
  }

  public int getColumn() {
    return col;
  }
}
