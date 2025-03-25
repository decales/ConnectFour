package com.example.a3_1.model;

import com.example.a3_1.model.Model.GameState;

public interface PublishSubscribe {
  void update(
      double displaySize,
      GameState gameState,
      BoardState boardState,
      BoardPosition previewPosition,
      int playerWinCount, int computerWinCount);
}
