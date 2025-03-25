package com.example.a3_1.view;

import com.example.a3_1.Controller;
import com.example.a3_1.model.BoardPosition;
import com.example.a3_1.model.BoardState;
import com.example.a3_1.model.PublishSubscribe;
import com.example.a3_1.model.Model.GameState;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class BottomBar extends HBox implements PublishSubscribe {

  private ResetButton resetButton;
  private UndoButton undoButton;
  private DepthSelector depthSelector;
  private DimensionsToggle dimensionsToggle;

  public BottomBar(Controller controller) {
    setAlignment(Pos.CENTER);
    
    // button to reset game
    resetButton = new ResetButton(controller);

    // button to undo the last player move
    undoButton = new UndoButton(controller);

    // drop-down box to select minimax depth cutoff
    depthSelector = new DepthSelector(controller);

    // radio buttons to select dimensions of game board
    dimensionsToggle = new DimensionsToggle(controller);

    getChildren().addAll(resetButton, undoButton, depthSelector, dimensionsToggle);
  }


  public void update(
      double displaySize,
      GameState gameState,
      BoardState boardState,
      BoardPosition previewPosition,
      int playerWinCount,
      int computerWinCount) {

    setPadding(new Insets(displaySize * 0.001));
    setSpacing(displaySize * 0.075);

    resetButton.update(displaySize);
    undoButton.update(displaySize);
    depthSelector.update(displaySize);
    dimensionsToggle.update(displaySize);
  }
}
