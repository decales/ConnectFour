package com.example.a3_1.model;

import java.util.ArrayList;
import com.example.a3_1.model.Model.PieceType;

public class BoardStateNode {
  
  public PieceType[][] board;
  protected PieceType pieceMoved;
  protected BoardPosition movePosition;
  protected int minimaxValue;
  protected ArrayList<BoardStateNode> children;

  // Constructor for initial empty board state
  public BoardStateNode(PieceType[][] board) {
    this.board = board;
    pieceMoved = PieceType.Computer;
    minimaxValue = Integer.MAX_VALUE;
  }

  // Constructor for board state after a move
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


  public boolean isWinningState() {
    // very cool and fancy loop to check if a move results in a horizontal, vertical, or diagonal sequence of at least 4, checked in this order
    for (int[][] v: new int[][][] { {{0,1},{0,-1}}, {{1,0},{-1,0}}, {{1,1},{-1,-1}}, {{-1,1},{1,-1}} }) {
      
      // length of sequence is the sum of the lengths traversing both opposing directions of v starting from the position of the move
      int l1 = getSequence(movePosition.row, movePosition.col, v[0], pieceMoved);
      int l2 = getSequence(movePosition.row, movePosition.col, v[1], pieceMoved);
      if (10 + l1 + l2 >= 40) return true;
    }                                                               
    return false; // no sequences found, move does not result in win 
  }


  public int scoreWinningSequences(int row, int col, PieceType pieceType) {

    int sequenceScore = 0;
    // for (int[] v: new int[][] { {0,1}, {0,-1}, {1,0}, {-1,0}, {1,1}, {-1,-1}, {-1,1}, {1,-1} }) {
    for (int[] v: new int[][] { {0,1}, {1,0}, {1,1}, {1,-1} }) {
      
      // retrieve the sequence score in a given direction v, +10 for each matching piece in the sequence
      int l1 = getSequence(row, col, v, pieceType);

      // if the value returned by getSequence is odd, the sequence ends at an empty tile -> potential winning sequence
      // if (l1 % 2 == 1) sequenceScore += Math.pow((l1 - 1) / 10, 2); // exponential score for length of sequence;
      sequenceScore += Math.pow(l1, 2);
    }                                                               
    return sequenceScore;
  }


  private int getSequence(int row, int col, int[] direction, PieceType pieceMoved) {
    // add the direction offsets to the current row and col to get adjacent row and col
    int adjRow = row + direction[0];
    int adjCol = col + direction[1];

    // sequence ends at grid boundary
    if (adjRow < 0 || adjRow >= board.length || adjCol < 0 || adjCol >= board[0].length) return 0;
    
    // sequence ends at empty tile, add odd number to keep track of this property in return value
    if (board[adjRow][adjCol] == PieceType.None) return 1;

    // sequence ends at opponent piece
    if (board[adjRow][adjCol] != pieceMoved) return 0;

    // otherwhise traverse to adjacent piece and increment count +10 for additional matching piece
    return 10 + getSequence(adjRow, adjCol, direction, pieceMoved);
  }


  public void evaluateStateBoard() {

    // priority #1 - piece wins the game on last move
    if (isWinningState()) {
      minimaxValue = (pieceMoved == PieceType.Player) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
      return;
    }

    minimaxValue = 0; // reset value from default value only applicable to internal nodes

    for (int row = 0; row < board.length; row++) {
      for (int col = 0; col < board[0].length; col++) {
        
        PieceType piece = board[row][col];
        if (piece != PieceType.None) { // only evaluate player and computer tiles

          // modifier to add or subtract points from total score based on whether the current piece belongs to player or computer
          int modifier = (piece == PieceType.Player) ? 1 : -1; 

          // add/subtract points based on how close a piece is to the center column from 0 (outer column) to 30 (center column)
          minimaxValue += 3 - Math.abs(3 - col) *  modifier;

          // add/subtract based on the sequences of matching pieces that start from a piece
          minimaxValue += scoreWinningSequences(row, col, piece) * modifier;
        }
      }
    }
  }
}
