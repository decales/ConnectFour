package com.example.ConnectFour.view.bottomBar;

import com.example.ConnectFour.Controller;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class UndoButton extends Button {

  private DropShadow mouseOverEffect;

  public UndoButton(Controller controller) {
    setText("Undo");
    
    mouseOverEffect = new DropShadow();
    setEffect(mouseOverEffect);

    setOnAction(controller::handleAction);
    setOnMouseEntered(e -> mouseOverEffect.setColor(Color.web("#31FFFF")));
    setOnMouseExited(e -> mouseOverEffect.setColor(Color.TRANSPARENT));
  }


  public void update(double displaySize, boolean canUndo) {
    this.setDisable(!canUndo);

    setStyle(String.format(
          "-fx-text-fill: #000000;"+
          "-fx-font-size: %f;"+
          "-fx-background-color: #7781A3;"+ 
          "-fx-background-radius: %fpx;", 
          displaySize * 0.02, displaySize *0.0125));
  }
}
