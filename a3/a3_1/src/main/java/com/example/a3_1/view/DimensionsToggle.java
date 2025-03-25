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

    mainLabel = new Label("Board dimensions");
    mainLabel.setAlignment(Pos.CENTER);

    radioButtonBox = new HBox();
    radioButtons = new ToggleGroup();

    for (int [] dimensions : new int[][] {{6, 7}, {8, 9}, {10, 11}, {12, 13}}) {
      RadioButton button = new RadioButton(String.format("%d x %d", dimensions[0], dimensions[1]));
      button.setToggleGroup(radioButtons);
      button.setOnAction(controller::handleAction);
      radioButtonBox.getChildren().add(button);
    }
    radioButtons.getToggles().getFirst().setSelected(true);
    // radioButtons.selectedToggleProperty().addListener((o, oldVal, newVal) -> controller.handleAction(this));
    getChildren().addAll(mainLabel, radioButtonBox);
  }

  public int getSelectionValue() {
    RadioButton button = (RadioButton) radioButtons.getSelectedToggle();
    return Integer.parseInt(button.getText().split(" ")[0]); // Defensive programming? Never heard of it.
  }

  public void update(double displaySize) {

    mainLabel.setStyle(String.format("-fx-text-fill: black; -fx-font-size: %f", displaySize * 0.015));

    radioButtonBox.setSpacing(displaySize * 0.02);
    for (Node node : radioButtonBox.getChildren()) {
      RadioButton button = (RadioButton) node;
      button.setStyle(String.format("-fx-text-fill: black; -fx-font-size: %f", displaySize * 0.015));
    }
    setSpacing(displaySize * 0.0075);
  }
}
