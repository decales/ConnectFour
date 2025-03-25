package com.example.a3_1.view;

import com.example.a3_1.Controller;
import com.example.a3_1.model.AppState;
import com.example.a3_1.model.BoardState;
import com.example.a3_1.model.PublishSubscribe;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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


  public void update(AppState appState, BoardState boardState) {

    setPadding(new Insets(appState.displaySize * 0.001));
    setSpacing(appState.displaySize * 0.075);

    resetButton.update(appState.displaySize);
    undoButton.update(appState.displaySize, appState.canUndo);
    depthSelector.update(appState.displaySize);
    dimensionsToggle.update(appState.displaySize);
  }
}
