package com.example.a3_1.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Model {

  public enum PieceType { Player, Computer, None, Preview };

  private List<PublishSubscribe> subscribers;
  private double displaySize;

  private PieceType[][] gameBoard;
  private boolean isPlayerTurn;
  private int previewRow, previewCol;

  public Model(double displaySize) {

    this.displaySize = displaySize;
    subscribers = new ArrayList<>();

    initializeGameBoard();
    isPlayerTurn = true;
  }

  public void initializeGameBoard() {
    // initialize empty 6x7 board
    gameBoard = new PieceType[6][7]; 
    for (int i = 0; i < gameBoard.length; i++) Arrays.fill(gameBoard[i], PieceType.None);
    updateSubscribers();
  }

  public void previewTurn(int col) {
    if (isPlayerTurn) {
     // clear the previous piece preview indicator
      if (gameBoard[previewRow][previewCol] == PieceType.Preview) {
        gameBoard[previewRow][previewCol] = PieceType.None; 
      }

      int row = nextValidRow(col);
      if (nextValidRow(col) != -1) { // check if the column is not full
        // update the piece preview position and set it on the board
        previewRow = row;
        previewCol = col;
        gameBoard[previewRow][previewCol] = PieceType.Preview;
      }
      updateSubscribers();
    }
  }

  public void clearPreview() {
    // clears the piece preview indicator when the mouse is not on the board, because this just looks nicer
    gameBoard[previewRow][previewCol] = PieceType.None;
    updateSubscribers();
  }

  public void playTurn() {
    if (isPlayerTurn) { // player's turn to place
      // if the piece preview is on the screen, we know which column the player has selected and that the move is valid / the column isn't full
      if (gameBoard[previewRow][previewCol] == PieceType.Preview) { 
        gameBoard[previewRow][previewCol] = PieceType.Player; // place the piece where the preview is
        isPlayerTurn = false; // end of player turn
        
        // Check if the move that was played results in a win
        if (isWinningMove(previewRow, previewCol, PieceType.Player)) {
          System.out.println("wonnered");
        }

        updateSubscribers();
        
        // try { Thread.sleep(1000); } catch (InterruptedException e) {}

        // Computer's turn to place
        isPlayerTurn = true;

        updateSubscribers();
      }
    }
  }

  private void miniMax() {
    // Move priorities:
    // 1. Win, create sequence of 4
    // 2. Prevent opponent win, block their potential sequence of 4
    // 3. Place with the most adjacent same pieces
    // 4. Place with the most nearby same pieces
  }

  public int nextValidRow(int col) {
    // Return the next row a piece can be placed in for a column,
    for (int row = gameBoard.length - 1; row >= 0; row--) { // Search for tile to place piece from bottom up
      if (gameBoard[row][col] == PieceType.None) { // Place piece on tile if there isn't one already
        return row;
      } 
    }
    return -1; // if the column is full, return -1
  }


  // private void checkSequence() {
  //
  //   for (int i = 0; i < gameBoard.length; i++) {
  //     for(int j = 0; j < gameBoard[0].length; j++) {
  //       if (gameBoard[i][j] == PieceType.Player) {
  //         if (checkSequence(i, j, null, PieceType.Player, 1)) {
  //           System.out.println("win");
  //         
  //         }
  //       }
  //     }
  //   }
  // }


  // private boolean checkSequence(int row, int col, int[] currentDirection, PieceType piece, int sequenceCount) {
  //
  //   // Sequence of 4 found, win
  //   if (sequenceCount == 4) {
  //     return true;
  //   }
  //
  //   // if there is no current direction, this is the first piece of a potential sequence, traverse all four possible directions to check for sequences
  //   // otherwise, this piece is part of a direction that is already being explored, continue traversing in that direction only
  //   int[][] traversalDirections = (currentDirection == null) ? new int[][] {{0,1}, {1,0}, {1,1}, {1,-1}} : new int[][] { currentDirection };
  //   
  //   for (int[] direction: traversalDirections) { 
  //     int adjRow = row + direction[0];
  //     int adjCol = col + direction[1];
  //
  //     // check if adjacent is in boundaries of grid
  //     if (adjRow < gameBoard.length && adjRow >= 0 && adjCol < gameBoard[0].length && adjCol >= 0) {
  //       
  //       // if the adjacent piece is the same as the current, traverse to it
  //       if (gameBoard[adjRow][adjCol] == piece ) {
  //         return checkSequence(adjRow, adjCol, direction, piece, sequenceCount + 1);
  //       }
  //     }
  //   }
  //   return false;
  // }


  private boolean isWinningMove(int row, int col, PieceType piece) {

      // check horizontal sequence
      int horizontalLength = 1 + checkSequence(row, col, new int[] {0, 1}, piece) + checkSequence(row, col, new int[] {0, -1}, piece);
      if (horizontalLength >= 4) {
        return true;
      }
      // check vertical sequence
      int verticalLength = 1 + checkSequence(row, col, new int[] {1, 0}, piece) + checkSequence(row, col, new int[] {-1, 0}, piece);
      if (verticalLength >= 4) {
        return true;
      }
      // check left diagonal sequence
      int leftDiagonalLength = 1 + checkSequence(row, col, new int[] {1, 1}, piece) + checkSequence(row, col, new int[] {-1, -1}, piece);
      if (leftDiagonalLength >= 4) {
        return true;
      }
      // check right horizontal sequence
      int rightDiagonalLength = 1 + checkSequence(row, col, new int[] {-1, 1}, piece) + checkSequence(row, col, new int[] {1, -1}, piece);
      if (rightDiagonalLength >= 4) {
        return true;
      }

      // otherwise no sequences found, move does not result in win
      return false;
  }

  private int checkSequence(int row, int col, int[] direction, PieceType piece) {
    int adjRow = row + direction[0];
    int adjCol = col + direction[1];

    // adjacent row and col are out of grid bounds
    if (adjRow < 0 || adjRow >= gameBoard.length || adjCol < 0 || adjCol >= gameBoard[0].length) return 0;
    
    // piece at adjacent row and col does not match
    if (gameBoard[adjRow][adjCol] != piece) return 0;

    // otherwhise traverse to adjacent piece and increment count
    return 1 + checkSequence(adjRow, adjCol, direction, piece);
  }
  


  public void addSubscribers(PublishSubscribe subscribers) {
    this.subscribers = Arrays.asList(subscribers);
    updateSubscribers();
  }

  public void updateSubscribers() {
    subscribers.forEach(subscriber -> {
      subscriber.update(displaySize, gameBoard);
    });
  }
  
}
