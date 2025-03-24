package com.example.a3_1.view;

import com.example.a3_1.model.BoardPosition;
import com.example.a3_1.model.Model.PieceType;

import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BoardPiece extends StackPane {

  protected Circle pieceCircle;
  protected PieceType pieceType;
  protected boolean isPreview;
  protected BoardPosition position;

  public BoardPiece(int row, int col) {

    pieceCircle = new Circle();
    pieceCircle.setMouseTransparent(true);
    getChildren().add(pieceCircle);
    
    setType(PieceType.None, false, false);
    position = new BoardPosition(row, col);
  }


  public void setSize(double size) {
    pieceCircle.setRadius(size);
    pieceCircle.setStrokeWidth(size * 0.15);
    setPadding(new Insets(size * 0.2));
  }


  public void setType(PieceType pieceType, boolean isPreview, boolean inSequence) {
    // only update piece sprite if it needs to be updated (piece is placed, preview should be shown/removed, piece is part of sequence)
    if (this.pieceType != pieceType || (this.isPreview ^ isPreview) || inSequence) { 

      // update circle sprite
      Color pieceColour = 
        (pieceType == PieceType.Computer) ? Color.web("#151515") :
        (pieceType == PieceType.None && !isPreview) ? Color.TRANSPARENT
        : Color.web("#4a526d");

      Color borderColour = 
        (pieceType == PieceType.None && !isPreview) ? Color.TRANSPARENT 
        : Color.BLACK;

      Color glowColour = 
        (isPreview) ? Color.CYAN :
        (inSequence) ? Color.LIME
        : Color.TRANSPARENT;

      pieceCircle.setFill(pieceColour);
      pieceCircle.setStroke(borderColour);
      pieceCircle.setEffect(new DropShadow(pieceCircle.getRadius() * 0.2, glowColour));

      // keep track to remember whether piece requires change on next update
      this.pieceType = pieceType; 
      this.isPreview = isPreview;
    }
  }


  public int getColumn() {
    return position.col;
  }
}
