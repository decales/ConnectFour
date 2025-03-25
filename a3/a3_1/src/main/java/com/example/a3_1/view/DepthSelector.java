package com.example.a3_1.view;

import com.example.a3_1.Controller;

import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class DepthSelector extends HBox {
  
  private Label label;
  private ComboBox<Integer> selector;

  public DepthSelector(Controller controller) {
    setAlignment(Pos.CENTER);

    label = new Label("Max depth");

    selector = new ComboBox<>();
    selector.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8);
    selector.setValue(4); // default value
    selector.setOnAction(controller::handleAction);

    getChildren().addAll(label, selector);
  }

  public void update(double displaySize) {
    label.setStyle(String.format("-fx-text-fill: black; -fx-font-size: %f", displaySize * 0.0175));
    setSpacing(displaySize * 0.0075);
  }
}
