package com.example.a3_1.model;

public class BoardPosition {

  protected int row, col;
  
  public BoardPosition(int row, int col) {
    this.row = row;
    this.col = col;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof BoardPosition otherPosition) {
      return otherPosition.row == row && otherPosition.col == col;
    }
    else return false;
  }
}
