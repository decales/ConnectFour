package com.example.a3_1.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Model {

  public enum PieceType { Player, Computer, None, Preview };

  private List<PublishSubscribe> subscribers;
  private double displaySize;
  private BoardStateNode boardState;
  private int minimaxDepth;
  private boolean isPlayerTurn;
  private BoardPosition previewPosition;

  public Model(double displaySize) {

    this.displaySize = displaySize;
    subscribers = new ArrayList<>();

    initializeGameBoard();
    minimaxDepth = 2;
    isPlayerTurn = true;
    previewPosition = new BoardPosition(0, 0);
  }


  public void initializeGameBoard() {
    // initialize state with empty 6x7 board
    boardState = new BoardStateNode(new PieceType[6][7]);
    for (int i = 0; i < boardState.board.length; i++) Arrays.fill(boardState.board[i], PieceType.None);
    updateSubscribers();
  }


  public void previewTurn(int col) {
    if (isPlayerTurn) {
     // clear the previous piece preview indicator
      if (boardState.board[previewPosition.row][previewPosition.col] == PieceType.Preview) {
        boardState.board[previewPosition.row][previewPosition.col] = PieceType.None; 
      }

      int row = nextValidRow(boardState.board, col);
      if (row != -1) { // check if the column is not full
        // update the piece preview position and set it on the board
        previewPosition = new BoardPosition(row, col);
        boardState.board[previewPosition.row][previewPosition.col] = PieceType.Preview;
      }
      updateSubscribers();
    }
  }


  public void clearPreview() {
    // clears the piece preview indicator when the mouse is not on the board, because this just looks nicer
    boardState.board[previewPosition.row][previewPosition.col] = PieceType.None;
    updateSubscribers();
  }


  public void playTurn() {
    if (isPlayerTurn) { // used as a 'lock' to prevent player placing pieces when it is the computer's turn

      // if the piece preview is on the screen, the player has a selected a valid column to place a piece
      if (boardState.board[previewPosition.row][previewPosition.col] == PieceType.Preview) {

        // place the piece using the position of the preview
        boardState = new BoardStateNode(boardState, previewPosition); // represent current state of the board after the move
        isPlayerTurn = false; // end of player's turn
        
        // Check if the move that was played results in a win
        if (boardState.isWinningState()) {
          System.out.println("you wonnered");
          return;
        }

        // Computer's turn, retrieve the state representing the best move it can make given the player's move
        boardState = getComputerMove(); 
        updateSubscribers();

        // Check if the move that was played results in a win
        if (boardState.isWinningState()) {
          System.out.println("computer wonnered");
          return;
        }

        // try { Thread.sleep(1000); } catch (InterruptedException e) {}
        isPlayerTurn = true;
      }
    }
  }


  public int nextValidRow(PieceType[][] board, int col) {
    // Return the next row a piece can be placed in for a column,
    for (int row = board.length - 1; row >= 0; row--) { // Search for tile to place piece from bottom up
      if (board[row][col] == PieceType.None) { // Place piece on tile if there isn't one already
        return row;
      } 
    }
    return -1; // if the column is full, return -1
  }
  

  private BoardStateNode getComputerMove() {
    // recursively build state tree and determine minimax values from the current board state
    getMinimaxStateTree(boardState, Integer.MIN_VALUE, Integer.MAX_VALUE, minimaxDepth);

    // Retrieve state representing best move the computer can make given the current state
    BoardStateNode bestState = null;
    for (BoardStateNode childState : boardState.children) {
      if (childState.minimaxValue <= boardState.minimaxValue) {
        boardState.minimaxValue = childState.minimaxValue;
        bestState = childState;
      }
    }
    System.out.println("Computer choose " + bestState.minimaxValue);
    boardState.children.clear();
    return bestState;
  }


  private void getMinimaxStateTree(BoardStateNode currentState, int alpha, int beta, int depth) {
    
    // current state is a leaf-node (winning state or max depth state) score it based on how good its move is
    if (currentState.isWinningState() || depth == 0) {
      currentState.evaluateStateBoard();
      System.out.println(String.format("%s node - depth %d - set %d", currentState.pieceMoved, depth, currentState.minimaxValue));
      return;
    }

    // Otherwise, current state is internal node, check all columns of its board to determine which moves can be made
    for (int col = 0; col < currentState.board[0].length; col++) {

      int row = nextValidRow(currentState.board, col);
      if (row != -1) {

        // Create child node representing the board state after the move, and link it to the current state node
        BoardStateNode childState = new BoardStateNode(currentState, new BoardPosition(row, col));
        currentState.addChild(childState);

        getMinimaxStateTree(childState, alpha, beta, depth - 1); // continue to recursively build tree

        // current node's value can now be determined after recursion, take min or max of child based on which piece is playing
        switch(currentState.pieceMoved) {

          case Computer -> { // take min value, check beta
            currentState.minimaxValue = Math.min(currentState.minimaxValue, childState.minimaxValue);
            beta = Math.min(currentState.minimaxValue, beta);
          }
          case Player -> { // take max value, check alpha
            currentState.minimaxValue = Math.max(currentState.minimaxValue, childState.minimaxValue);
            alpha = Math.max(currentState.minimaxValue, alpha);
          }
        }
        if (alpha >= beta) return; // no need to continue building tree, min/max of branch already determined
      }
    }
    System.out.println(String.format("%s node - depth %d - choose %d\n", currentState.pieceMoved, depth, currentState.minimaxValue));
  }


  public void addSubscribers(PublishSubscribe subscribers) {
    this.subscribers = Arrays.asList(subscribers);
    updateSubscribers();
  }


  public void updateSubscribers() {
    subscribers.forEach(subscriber -> {
      subscriber.update(displaySize, boardState);
    });
  }
}
