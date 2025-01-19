package com.example.a3_1.model;

import java.util.ArrayList;

import com.example.a3_1.model.Model.PieceType;

public class GameStateNode {
  
  protected PieceType[][] board;
  protected int minimaxValue;
  protected PieceType lastToPlay;
  protected ArrayList<GameStateNode> children;

  public GameStateNode(PieceType[][] board, PieceType lastToPlay) {
    this.board = board;
    this.lastToPlay = lastToPlay;
    children = new ArrayList<>();
  }

  public void addChild(GameStateNode child) {
    children.add(child);
  }

  public void addMinimaxValue(int points) {
    minimaxValue += points;
  }

}
