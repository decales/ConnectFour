package com.example.a3_1;

import com.example.a3_1.model.Model;
import com.example.a3_1.view.BoardPiece;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    model.clearPreview();
  }


  public void handleAction(ActionEvent e) {
    if (e.getTarget() instanceof Button) {
      model.initializeGame();
    }
    else if (e.getTarget() instanceof ComboBox box) {
      model.setMinimaxDepth((int)box.getValue());
    }
  }
}
