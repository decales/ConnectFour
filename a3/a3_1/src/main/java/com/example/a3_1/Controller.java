package com.example.a3_1;

import com.example.a3_1.model.Model;
import com.example.a3_1.view.BoardPiece;

import javafx.scene.input.MouseEvent;


public class Controller {

private Model model;

  public Controller(Model model) {
    this.model = model;
  }

  public void handleMouseClicked(MouseEvent e) {
    model.playTurn();
  }

  public void handleMouseMoved(MouseEvent e) {
    if (e.getTarget() instanceof BoardPiece piece) {
      model.previewTurn(piece.getColumn());
    }
  }

  public void handleMouseExited(MouseEvent e) {
    model.clearPreview();
  }
}
