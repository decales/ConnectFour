package com.example.a3_1.model;

public class AppState {
// class to encapsulate game/app data for convenience in publish-subscribe mechanism 

  public enum GameState { InProgress, ComputerWin, PlayerWin, Draw };

  public double displaySize;
  public GameState state;
  public int playerScore, computerScore;
  public int maxDepth;
  public boolean canMove;
  public boolean canUndo;
}
