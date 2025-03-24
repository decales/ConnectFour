package com.example.a3_1.model;

public class BoardPosition {

  protected int row, col;
  
  public BoardPosition(int row, int col) {
    this.row = row;
    this.col = col;
  }


  @Override
  public boolean equals(Object o) {
    if (o instanceof BoardPosition position) {
      return position.row == row && position.col == col;
    }
    else return false;
  }
}
