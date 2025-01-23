package com.example.a3_1.model;

import java.util.ArrayList;


import com.example.a3_1.model.Model.PieceType;

public class BoardStateNode {
  
  public PieceType[][] board;
  protected PieceType pieceMoved;
  protected BoardPosition movePosition;
  public ArrayList<BoardPosition> winningSequence;
  protected int minimaxValue;
  protected ArrayList<BoardStateNode> children;

  // Constructor for initial empty board state
  public BoardStateNode(PieceType[][] board) {
    this.board = board;
    pieceMoved = PieceType.Computer;
    movePosition = new BoardPosition(0, 0);
    minimaxValue = Integer.MAX_VALUE;
  }

  // Constructor for board state after a move
  public BoardStateNode(BoardStateNode parentState, BoardPosition movePosition) {

    // keep track of which piece moved and where
    pieceMoved = (parentState.pieceMoved == PieceType.Computer) ? PieceType.Player : PieceType.Computer;
    this.movePosition = movePosition;

    // create state board from parent board
    getStateBoard(parentState.board);

    // pre-set default minimax value for convenience in getMinimaxStateTree function
    minimaxValue = (pieceMoved == PieceType.Computer) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
  }


  public void addChild(BoardStateNode child) {
    if (children == null) children = new ArrayList<>();
    children.add(child);
  }


  private void getStateBoard(PieceType[][] parentBoard) {
    // create shallow clone of a gameboard after a move has occurred
    board = new PieceType[parentBoard.length][parentBoard[0].length];

    // copy the values from the parent board to the child board
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[0].length; j++) {
        board[i][j] = parentBoard[i][j];
      }
    }
    // add the move to the child board
    board[movePosition.row][movePosition.col] = pieceMoved;
  }


  public boolean isWinningState() {
    // very cool and fancy loop to check if a move results in a horizontal, vertical, or diagonal sequence of at least 4, checked in this order
    for (int[][] v: new int[][][] { {{0,1},{0,-1}}, {{1,0},{-1,0}}, {{1,1},{-1,-1}}, {{-1,1},{1,-1}} }) {
      
      // length of sequence is the sum of the lengths traversing both opposing directions of v starting from the position of the move
      ArrayList<BoardPosition> sequence = getSequence(movePosition.row, movePosition.col, v[0], pieceMoved);
      sequence.removeFirst();
      sequence.addAll(getSequence(movePosition.row, movePosition.col, v[1], pieceMoved));

      if (sequence.size() >= 4) {
        winningSequence = sequence;
        return true;
      }
    }                                                               
    return false; // no sequences found, move does not result in win 
  }


  public int scoreWinningSequences(int row, int col, PieceType pieceType) {

    int sequenceScore = 0;
    // for (int[] v: new int[][] { {0,1}, {0,-1}, {1,0}, {-1,0}, {1,1}, {-1,-1}, {-1,1}, {1,-1} }) {
    for (int[] v: new int[][] { {0,1}, {1,0}, {1,1}, {1,-1} }) {
      // retrieve the a sequence of matching pieces in a given direction v, increment the score by the exponential seuqnce length
      ArrayList<BoardPosition> sequence = getSequence(row, col, v, pieceType);
      sequenceScore += Math.pow(sequence.size(), 2);
    }                                                               
    return sequenceScore;
  }


  private ArrayList<BoardPosition> getSequence(int startRow, int startCol, int[] direction, PieceType pieceMoved) {
    ArrayList<BoardPosition> sequence = new ArrayList<>();
    sequence.add(new BoardPosition(startRow, startCol));
    getSequence(sequence, direction, pieceMoved);
    return sequence;
  }


  private void getSequence(ArrayList<BoardPosition> sequence, int[] direction, PieceType pieceMoved) {
    // add the direction offsets to the current row and col to get adjacent row and col
    BoardPosition currentPosition = sequence.getLast();
    int adjRow = currentPosition.row + direction[0];
    int adjCol = currentPosition.col + direction[1];

    // sequence ends at grid boundary
    if (adjRow < 0 || adjRow >= board.length || adjCol < 0 || adjCol >= board[0].length) return;
    
    // sequence ends at opponent piece
    if (board[adjRow][adjCol] != pieceMoved) return;

    // otherwhise traverse to adjacent piece in direction of sequence
    sequence.add(new BoardPosition(adjRow, adjCol));
    getSequence(sequence, direction, pieceMoved);
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
