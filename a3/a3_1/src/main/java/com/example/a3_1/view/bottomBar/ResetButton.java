package com.example.a3_1.view.bottomBar;

import com.example.a3_1.Controller;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class ResetButton extends Button {

  private DropShadow mouseOverEffect;

  public ResetButton(Controller controller) {
    setText("Reset");

    mouseOverEffect = new DropShadow();
    setEffect(mouseOverEffect);

    setOnAction(controller::handleAction);
    setOnMouseEntered(e -> mouseOverEffect.setColor(Color.web("#31FFFF")));
    setOnMouseExited(e -> mouseOverEffect.setColor(Color.TRANSPARENT));
  }


  public void update(double displaySize) {
    setStyle(String.format(
          "-fx-text-fill: #000000;"+
          "-fx-font-size: %f;"+
          "-fx-background-color: #7781A3;"+ 
          "-fx-background-radius: %fpx;", 
          displaySize * 0.02, displaySize *0.0125));
  }
}
