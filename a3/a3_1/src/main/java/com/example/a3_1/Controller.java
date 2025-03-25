package com.example.a3_1;

import com.example.a3_1.model.Model;
import com.example.a3_1.view.BoardPiece;
import com.example.a3_1.view.ResetButton;
import com.example.a3_1.view.UndoButton;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;


public class Controller {

private Model model;

  public Controller(Model model) {
    this.model = model;
  }


  public void handleMouseClicked(MouseEvent e) {
    if (e.getTarget() instanceof BoardPiece) {
      model.playMove();
    }
  }


  public void handleMouseMoved(MouseEvent e) {
    if (e.getTarget() instanceof BoardPiece piece) {
      model.previewMove(piece.getColumn());
    }
  }


  public void handleMouseExited(MouseEvent e) {
    model.previewMove(-1); // sets previewPosition to null
  }


  public void handleAction(ActionEvent e) {
    switch(e.getTarget()) {
      case ResetButton rButton -> model.initializeGame();
      case UndoButton uButton -> model.undoTurn();
      case ComboBox box -> model.setDepth((int) box.getValue());
      case RadioButton radioButton -> { //  this is bad and I'm not sorry
        String[] dimensionStrs = radioButton.getText().split(" x ");
        model.initializeGame(Integer.parseInt(dimensionStrs[0]), Integer.parseInt(dimensionStrs[1]));
      }
      default -> {}
    }
  }
}
