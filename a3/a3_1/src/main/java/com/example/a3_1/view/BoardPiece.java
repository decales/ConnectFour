package com.example.a3_1.view;

import java.util.Stack;

import com.example.a3_1.model.Model.PieceType;

import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BoardPiece extends StackPane {

  protected Circle pieceCircle;
  protected PieceType pieceType;
  protected int row, col;

  public BoardPiece(int row, int col) {

    pieceCircle = new Circle();
    pieceCircle.setMouseTransparent(true);
    getChildren().add(pieceCircle);
    
    setType(PieceType.None, false);
    this.row = row;
    this.col = col;
  }

  public void setSize(double size) {
    pieceCircle.setRadius(size);
    pieceCircle.setStrokeWidth(size * 0.15);
    setPadding(new Insets(size * 0.2));
  }

  public void setType(PieceType pieceType, boolean inSequence) {
    if (this.pieceType != pieceType || inSequence) { // only update piece sprite if it needs to be

      // update circle sprite
      Color pieceColour = 
        (pieceType == PieceType.Computer) ? Color.web("#151515") :
        (pieceType == PieceType.None) ? Color.TRANSPARENT
        : Color.web("#4a526d");

      Color borderColour = 
        (pieceType == PieceType.None) ? Color.TRANSPARENT 
        : Color.BLACK;

      Color glowColour = 
        (pieceType == PieceType.Preview) ? Color.CYAN :
        (inSequence) ? Color.LIME
        : Color.TRANSPARENT;

      pieceCircle.setFill(pieceColour);
      pieceCircle.setStroke(borderColour);
      pieceCircle.setEffect(new DropShadow(pieceCircle.getRadius() * 0.2, glowColour));
      
      this.pieceType = pieceType; // update type 
    }
  }

  public int getColumn() {
    return col;
  }
}
