package com.example.a3_1.view;

import com.example.a3_1.Controller;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DimensionsToggle extends VBox {

  private Label mainLabel;
  private ToggleGroup radioButtons;
  private HBox radioButtonBox;
  
  public DimensionsToggle(Controller controller) {
    setAlignment(Pos.CENTER);

    mainLabel = new Label("Board dimensions");

    radioButtonBox = new HBox();
    radioButtons = new ToggleGroup();

    for (int [] dimensions : new int[][] {{6, 7}, {7, 8}, {8, 9}}) {
      RadioButton button = new RadioButton(String.format("%d x %d", dimensions[0], dimensions[1]));
      button.setToggleGroup(radioButtons);
      button.setOnAction(controller::handleAction);
      radioButtonBox.getChildren().add(button);
    }
    radioButtons.getToggles().getFirst().setSelected(true);
    getChildren().addAll(mainLabel, radioButtonBox);
  }


  public void update(double displaySize) {

    mainLabel.setStyle(String.format("-fx-text-fill: black; -fx-font-size: %f", displaySize * 0.0175));

    radioButtonBox.setSpacing(displaySize * 0.02);
    for (Node node : radioButtonBox.getChildren()) {
      RadioButton button = (RadioButton) node;
      button.setStyle(String.format("-fx-text-fill: black; -fx-font-size: %f", displaySize * 0.01333));
    }
    setSpacing(displaySize * 0.00333);
  }
}
