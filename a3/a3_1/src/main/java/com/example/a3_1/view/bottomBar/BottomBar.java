package com.example.a3_1.view.bottomBar;

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
    
    resetButton = new ResetButton(controller);
    undoButton = new UndoButton(controller);
    depthSelector = new DepthSelector(controller);
    dimensionsToggle = new DimensionsToggle(controller);

    getChildren().addAll(resetButton, undoButton, depthSelector, dimensionsToggle);
  }


  public void update(AppState appState, BoardState boardState) {
    double p = appState.displaySize * 0.015;
    setPadding(new Insets(p, p * 3, 0, p * 3));

    setSpacing(appState.displaySize * 0.075);

    resetButton.update(appState.displaySize);
    undoButton.update(appState.displaySize, appState.canUndo);
    depthSelector.update(appState.displaySize);
    dimensionsToggle.update(appState.displaySize);
  }
}
