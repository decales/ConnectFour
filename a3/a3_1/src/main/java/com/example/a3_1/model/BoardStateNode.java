package com.example.a3_1.model;

import java.util.ArrayList;

import com.example.a3_1.model.Model.PieceType;

public class BoardStateNode {
  
  protected PieceType[][] board;
  protected PieceType pieceMoved;
  protected BoardPosition movePosition;
  protected int minimaxValue;
  protected ArrayList<BoardStateNode> children;

  // Constructor to represent the board state after the player's last actual move
  public BoardStateNode(PieceType[][] parentBoard, BoardPosition movePosition) {
    
    // Keep track of which piece moved and where
    pieceMoved = PieceType.Player;
    this.movePosition = movePosition;

    // Create state board from parent board
    getStateBoard(parentBoard);

    // Pre-set default minimax value for convenience in getMinimaxStateTree function
    minimaxValue = Integer.MIN_VALUE;
  }


  // Constructor to represent potential board states in the state tree
  public BoardStateNode(BoardStateNode parentState, BoardPosition movePosition) {

    // Keep track of which piece moved and where
    pieceMoved = (parentState.pieceMoved == PieceType.Computer) ? PieceType.Player : PieceType.Computer;
    this.movePosition = movePosition;

    // Create state board from parent board
    getStateBoard(parentState.board);

    // Pre-set default minimax value for convenience in getMinimaxStateTree function
    minimaxValue = (pieceMoved == PieceType.Computer) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
  }


  public void addChild(BoardStateNode child) {
    if (children == null) children = new ArrayList<>();
    children.add(child);
  }


  private void getStateBoard(PieceType[][] parentBoard) {
    // create shallow clone of a gameboard after a move has occured
    board = new PieceType[parentBoard.length][parentBoard[0].length];

    // Copy the values from the parent board to the child board
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[0].length; j++) {
        board[i][j] = parentBoard[i][j];
      }
    }
    // Add the move to the child board
    board[movePosition.row][movePosition.col] = pieceMoved;
  }
}
