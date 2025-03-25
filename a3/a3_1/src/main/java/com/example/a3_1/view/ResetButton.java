package com.example.a3_1.view;

import com.example.a3_1.Controller;
import javafx.scene.control.Button;

public class ResetButton extends Button {

  public ResetButton(Controller controller) {
    setText("Reset");
    setOnAction(controller::handleAction);
  }


  public void update(double displaySize) {
    setStyle(String.format("-fx-text-fill: black; -fx-font-size: %f", displaySize * 0.0175));
  }
}
