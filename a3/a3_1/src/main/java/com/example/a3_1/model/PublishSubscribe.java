package com.example.a3_1.model;

import com.example.a3_1.model.Model.GameState;

public interface PublishSubscribe {
  void update(double displaySize, BoardState boardState, GameState gameState, int playerWinCount, int computerWinCount);
}
