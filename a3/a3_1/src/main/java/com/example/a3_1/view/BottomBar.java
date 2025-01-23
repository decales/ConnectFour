package com.example.a3_1.view;

import java.util.List;

import com.example.a3_1.Controller;
import com.example.a3_1.model.BoardStateNode;
import com.example.a3_1.model.PublishSubscribe;
import com.example.a3_1.model.Model.GameState;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class BottomBar extends HBox implements PublishSubscribe {

  private Button resetButton;
  private ComboBox<Integer> depthSelector;

  public BottomBar(Controller controller) {

    // button to reset game
    resetButton = new Button("Reset");
    resetButton.setOnAction(controller::handleAction);
    
    // drop-down box to select minimax depth cutoff
    depthSelector = new ComboBox<>();
    depthSelector.getItems().addAll(1, 2, 3, 4, 5, 6);
    depthSelector.setValue(4); // default value
    depthSelector.setOnAction(controller::handleAction);

    getChildren().addAll(resetButton, depthSelector);
  }

  public void update(double displaySize, BoardStateNode boardState, GameState gameState, int playerWinCount, int computerWinCount) {
    
  }
}
