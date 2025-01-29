package com.example.a3_1.model;

import java.util.ArrayList;

import com.example.a3_1.model.Model.PieceType;

public class BoardStateNode {
  
  public PieceType[][] board;
  protected PieceType pieceMoved;
  protected int numberPiecesMoved;
  protected BoardPosition movePosition;
  public ArrayList<BoardPosition> winningSequence;
  protected double minimaxValue;
  protected ArrayList<BoardStateNode> children;

  // Constructor for initial empty board state
  public BoardStateNode(PieceType[][] board) {
    this.board = board;
    pieceMoved = PieceType.Computer;
    movePosition = new BoardPosition(0, 0);
    minimaxValue = -Double.MAX_VALUE;
  }

  // Constructor for board state after a move
  public BoardStateNode(BoardStateNode parentState, BoardPosition movePosition) {

    // keep track of which piece moved and where
    pieceMoved = (parentState.pieceMoved == PieceType.Computer) ? PieceType.Player : PieceType.Computer;
    this.movePosition = movePosition;

    // create state board from parent board
    getStateBoard(parentState.board);
    numberPiecesMoved = parentState.numberPiecesMoved + 1;

    // pre-set default minimax value for convenience in getMinimaxStateTree function
    minimaxValue = (pieceMoved == PieceType.Computer) ? -Double.MAX_VALUE : Double.MAX_VALUE;
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


  public boolean isTieState() {
    return numberPiecesMoved == board.length * board[0].length;
  }


  public boolean isWinState() {
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


  private int getSequenceScore(int row, int col, PieceType piece) {

    int sequenceScore = 0;

    for (int[] v: new int[][] {{0,1}, {0,-1}, {1,0}, {-1,0}, {1,1}, {-1,-1}, {-1,1}, {1,-1}}) {
      
      int i = 1;
      boolean threatPossible = true;
      boolean emptySpaceUsed = false;

      while (i <= 3 && threatPossible) {

        int rowDi = row + v[0] * i;
        int colDi = col + v[1] * i;

        if (rowDi >= 0 && rowDi < board.length && colDi >= 0 && colDi < board[0].length) {
          if (board[rowDi][colDi] == PieceType.None) { // empty space in sequence
            if (!emptySpaceUsed) {
              emptySpaceUsed = true;
              i++;
            }
            else threatPossible = false;
          }
          else if (board[rowDi][colDi] != piece) { // opponent piece blocking
            threatPossible = false;
          }
          else i++;
        }
        else threatPossible = false;
      }

      int threatBonus = (threatPossible) ? 2 : 1;
      // sequenceScore += Math.pow((i - 1) * 2, 4) * threatBonus;

      int x = 0;
      switch (i) {
        case 2 -> x = 10;
        case 3 -> x = 50;
        case 4 -> x = 1000;
      }
      sequenceScore += x * threatBonus;
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


  public void evaluateBoard() {

    minimaxValue = 0; // reset value from default value only applicable to internal nodes
    
    for (int row = 0; row < board.length; row++) {
      for (int col = 0; col < board[0].length; col++) {

        PieceType piece = board[row][col];
        if (piece != PieceType.None) { // only evaluate player and computer tiles
          double modifier = (piece == PieceType.Computer) ? 1.0 : -2.0;

          minimaxValue += getSequenceScore(row, col, piece) * modifier;
          // minimaxValue += (3 - Math.abs(3 - col)) * modifier;
          // minimaxValue = minimaxValue / numberPiecesMoved;
        }
      }
    }
  }
}
