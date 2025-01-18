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
    isPlayerTurn = true; // false -> computer turn


  }

  public void initializeGameBoard() {
    // initialize empty 6x7 board
    gameBoard = new PieceType[6][7]; 
    for (int i = 0; i < gameBoard.length; i++) Arrays.fill(gameBoard[i], PieceType.None);
    updateSubscribers();
  }

  public void previewTurn(int col) {

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

        // Computer's turn to place
        isPlayerTurn = true;

        updateSubscribers();
      }
    }
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
