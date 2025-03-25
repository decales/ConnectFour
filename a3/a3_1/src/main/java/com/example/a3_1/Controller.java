package com.example.a3_1;

import com.example.a3_1.model.Model;
import com.example.a3_1.view.BoardPiece;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
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
      model.playTurn();
    }
  }


  public void handleMouseMoved(MouseEvent e) {
    if (e.getTarget() instanceof BoardPiece piece) {
      model.previewTurn(piece.getColumn());
    }
  }


  public void handleMouseExited(MouseEvent e) {
    model.previewTurn(-1); // sets previewPosition to null
  }


  public void handleAction(ActionEvent e) {

    switch(e.getTarget()) {
      case Button button -> model.initializeGame();
      case ComboBox box -> model.setDepth((int) box.getValue());
      case RadioButton rbutton -> {
        //  what is defensive programming???
        String[] dimensionStrs = rbutton.getText().split(" x ");
        model.initializeGame(Integer.parseInt(dimensionStrs[0]), Integer.parseInt(dimensionStrs[1]));
      }
      default -> {}
    }
  }
}
