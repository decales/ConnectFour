package com.example.a3_1.view;

import com.example.a3_1.Controller;
import javafx.scene.control.Button;

public class UndoButton extends Button {

  public UndoButton(Controller controller) {
    setText("Undo");
    setOnAction(controller::handleAction);
  }


  public void update(double displaySize) {
    setStyle(String.format("-fx-text-fill: black; -fx-font-size: %f", displaySize * 0.0175));
  }
}
