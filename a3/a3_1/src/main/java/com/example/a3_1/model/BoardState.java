package com.example.a3_1.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import com.example.a3_1.model.Model.PieceType;

public class BoardState {

  public PieceType[][] board;
  protected double score = -Double.MAX_VALUE;
  protected int numberPiecesMoved;
  protected PieceType pieceMoved;
  protected BoardPosition movePosition;
  public List<BoardPosition> winningSequence;

  // Constructor for initial empty board state
  public BoardState(PieceType[][] board) {
    pieceMoved = PieceType.Computer; // computer is always "last to move" in initial state because player moves first
    movePosition = new BoardPosition(0, 0);
    this.board = board;
  }

  // Constructor for board state after a move
  public BoardState(BoardState parentState, BoardPosition movePosition) {
    // keep track of which piece moved and where
    pieceMoved = (parentState.pieceMoved == PieceType.Computer) ? PieceType.Player : PieceType.Computer; // alternate players on state creation
    this.movePosition = movePosition;
    numberPiecesMoved = parentState.numberPiecesMoved + 1;
    getStateBoard(parentState.board); // create state board from parent board
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
    // check if a move results in a horizontal, vertical, or diagonal sequence of at least 4, checked in this order
    for (int[][] v : new int[][][] { {{0,1},{0,-1}}, {{1,0},{-1,0}}, {{1,1},{-1,-1}}, {{-1,1},{1,-1}} }) {

      // movePosition is always the last move made - only need to check for sequence from this position
      List<BoardPosition> sequence = getSequence(new LinkedList<BoardPosition>(List.of(movePosition)), v[0]);
      List<BoardPosition> _sequence = getSequence(new LinkedList<BoardPosition>(List.of(movePosition)), v[1]);

      // total length of both sequences is at least 4, move results in  a win 
      if ((sequence.size() + _sequence.size()) - 1 >= 4) {
        winningSequence = Stream.of(sequence, _sequence).flatMap(List<BoardPosition>::stream).toList();
        return true;
      }
    }                                                               
    return false; // no sequences found, move does not result in win 
  }


  private List<BoardPosition> getSequence(List<BoardPosition> sequence, int[] v) {
    // add the direction offsets to the current row and col to get adjacent row and col
    BoardPosition currentPosition = sequence.getLast();
    int adjRow = currentPosition.row + v[0];
    int adjCol = currentPosition.col + v[1];

    // sequence ends at grid boundary
    if (adjRow < 0 || adjRow >= board.length || adjCol < 0 || adjCol >= board[0].length) return sequence;
    
    // sequence ends at opponent piece
    if (board[adjRow][adjCol] != pieceMoved) return sequence;

    // otherwhise traverse to adjacent piece in direction of sequence
    sequence.add(new BoardPosition(adjRow, adjCol));
    return getSequence(sequence, v);
  }


  public void evaluateBoard() {
    // because scores are inverted each negaMax call and evaluation is from perspective of the player that moved last
    // negative scores are better for this player and positive score are better for their opponent

    // loop through board bottom-right to top-left, evaluating the piece positions using two heuristics
    score = 0;
    for (int i = board.length - 1; i >= 0; i--) {
      for (int j = board[0].length - 1; j >= 0; j--) {

        // heuristic #1 - add/subtract points based on how close a piece is to the center column
        if (board[i][j] != PieceType.None) {
          score += Math.pow((board[0].length / 2) - Math.abs((board[0].length / 2) - j), 2) * ((board[i][j]) == pieceMoved ? -1 : 1);
        }

        // heuristic #2 - score all 4-length 'windows' in board
        // array to keep track of matching [i][0] and opponent [i][1] piece counts for each directional window
        int[][] pieceCounts = new int [4][2];

        for (int k = 0; k <= 3; k++) {
          // check horizontally - [0][j]
          if (j - 3 >= 0) {
            if (board[i][j - k] == pieceMoved) pieceCounts[0][0] ++; // matching piece -> - score
            else if (board[i][j - k] != PieceType.None) pieceCounts[0][1] ++; // opponent piece -> + score
          }
          // check vertically - [1][j]
          if (i - 3 >= 0) {
            if (board[i - k][j] == pieceMoved) pieceCounts[1][0] ++;
            else if (board[i - k][j] != PieceType.None) pieceCounts[1][1] ++;
          }
          // check left-diagonal - [2][j]
          if (j - 3 >= 0 && i - 3 >= 0) {
            if (board[i - k][j - k] == pieceMoved) pieceCounts[2][0] ++;
            else if (board[i - k][j - k] != PieceType.None) pieceCounts[2][1] ++;
          }
          // check right-diagonal - [3][j]
          if (j + 3 < board[0].length && i - 3 >= 0) {
            if (board[i - k][j + k] == pieceMoved) pieceCounts[3][0] ++;
            else if (board[i - k][j + k] != PieceType.None) pieceCounts[3][1] ++;
          }
        }
        // loop through piece count array - score window by taking difference of opponent v. matching pieces
        for (int[] windowCount : pieceCounts) {
          // 1.5x bonus when window contains contains empty square - threat potential
          score += Math.pow(windowCount[1] - windowCount[0], 3) * (((windowCount[0] + windowCount[1] < 4) ? 1.5 : 1)); 
        }
      }
    }
  }


  @Override
  public int hashCode() {
    return Arrays.deepHashCode(board);
  }
  

  @Override
  public boolean equals(Object o) {
    if (o instanceof BoardState boardState) {
      return Arrays.deepEquals(board, boardState.board);
    }
    else return false;
  }
}
