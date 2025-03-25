package com.example.a3_1.view;

import com.example.a3_1.Controller;
import com.example.a3_1.model.BoardPosition;
import com.example.a3_1.model.BoardState;
import com.example.a3_1.model.PublishSubscribe;
import com.example.a3_1.model.Model.GameState;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class BottomBar extends HBox implements PublishSubscribe {

  private Button resetButton;
  private ComboBox<Integer> depthSelector;
  private DimensionsToggle dimensionsToggle;

  public BottomBar(Controller controller) {

    // button to reset game
    resetButton = new Button("Reset");
    resetButton.setOnAction(controller::handleAction);
    
    // drop-down box to select minimax depth cutoff
    depthSelector = new ComboBox<>();
    depthSelector.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    depthSelector.setValue(4); // default value
    depthSelector.setOnAction(controller::handleAction);

    dimensionsToggle = new DimensionsToggle(controller);

    getChildren().addAll(resetButton, depthSelector, dimensionsToggle);
  }


  public void update(
      double displaySize,
      GameState gameState,
      BoardState boardState,
      BoardPosition previewPosition,
      int playerWinCount,
      int computerWinCount) {

    dimensionsToggle.update(displaySize);
    
  }
}
