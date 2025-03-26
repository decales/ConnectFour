package com.example.a3_1.view.bottomBar;

import com.example.a3_1.Controller;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class DepthSelector extends HBox {
  
  private Label label;
  private ComboBox<Integer> selector;
  private DropShadow mouseOverEffect;

  public DepthSelector(Controller controller) {
    setAlignment(Pos.CENTER);

    label = new Label("Depth");

    selector = new ComboBox<>();
    selector.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8);
    selector.setValue(4); // default value
    selector.setOnAction(controller::handleAction);

    mouseOverEffect = new DropShadow();
    selector.setEffect(mouseOverEffect);
    selector.setOnMouseEntered(e -> mouseOverEffect.setColor(Color.web("#31FFFF")));
    selector.setOnMouseExited(e -> mouseOverEffect.setColor(Color.TRANSPARENT));

    getChildren().addAll(label, selector);
  }

  public void update(double displaySize) {
    setSpacing(displaySize * 0.01);
    label.setStyle(String.format("-fx-text-fill: #7781A3; -fx-font-size: %f", displaySize * 0.02));
    selector.setStyle(String.format(
          "-fx-text-fill: #000000;"+
          "-fx-font-size: %f;"+
          "-fx-background-color: #7781A3;"+ 
          "-fx-background-radius: %fpx;", 
          displaySize * 0.0175, displaySize *0.0125));
  }
}
