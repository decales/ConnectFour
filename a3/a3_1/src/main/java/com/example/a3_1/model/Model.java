package com.example.a3_1.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Model {

  public enum PieceType { Computer, Player, None, Preview };
  public enum GameState { InProgress, ComputerWin, PlayerWin };

  private List<PublishSubscribe> subscribers;
  private double displaySize;
  private BoardStateNode boardState;
  private int minimaxDepth;
  private BoardPosition previewPosition;
  private GameState gameState;
  private int playerWinCount, computerWinCount;

  public Model(double displaySize) {

    // initialize application data
    this.displaySize = displaySize;
    subscribers = new ArrayList<>();

    minimaxDepth = 4;
    initializeGame(); 
  }


  public void initializeGame() {
    // initialize game state and vars
    gameState = GameState.InProgress;
    previewPosition = new BoardPosition(0, 0);

    // initialize board state with empty 6x7 board
    boardState = new BoardStateNode(new PieceType[6][7]);
    for (int i = 0; i < boardState.board.length; i++) Arrays.fill(boardState.board[i], PieceType.None);
    updateSubscribers();
  }


  public void setMinimaxDepth(int minimaxDepth) {
    this.minimaxDepth = minimaxDepth;
  }


  public void previewTurn(int col) {
    if (boardState.pieceMoved == PieceType.Computer && gameState == GameState.InProgress) { // player's turn
      
      clearPreview(); // clear the previous piece preview indicator

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
    if (boardState.board[previewPosition.row][previewPosition.col] == PieceType.Preview) {
      boardState.board[previewPosition.row][previewPosition.col] = PieceType.None;
      updateSubscribers();
    }
  }


  public void playTurn() {
    updateSubscribers();
    if (gameState == GameState.InProgress) {

      switch(boardState.pieceMoved) { // next piece to play is the opposing piece of the last piece played in the current state                      
        
        case Computer -> { // player's turn
          if (boardState.board[previewPosition.row][previewPosition.col] == PieceType.Preview) { // only play turn given the player's input
            boardState = new BoardStateNode(boardState, previewPosition); // get updated state with player move

            // check if move was a win, update state and win count if so
            if (boardState.isWinningState()) {
              gameState = GameState.PlayerWin;
              playerWinCount ++;
            }
            playTurn(); // play opponent's turn
          }
        }

        case Player -> { // computer's turn
          boardState = getComputerMove(); // get updated state with computer move
          
          // check if move was a win, update state and win count if so
          if (boardState.isWinningState()) {
            gameState = GameState.ComputerWin;
            computerWinCount ++;
          }
          playTurn(); // play opponent's turn
        }
      }
    }
  }


  private int nextValidRow(PieceType[][] board, int col) {
    // return the next row a piece can be placed in for a column,
    for (int row = board.length - 1; row >= 0; row--) { // Search for tile to place piece from bottom up
      if (board[row][col] == PieceType.None) { // Place piece on tile if there isn't one already
        return row;
      } 
    }
    return -1; // column is full, -1 for no valid move
  }
  

  private BoardStateNode getComputerMove() {
    // recursively build state tree and determine minimax values from the current board state
    getMinimaxStateTree(boardState, Integer.MIN_VALUE, Integer.MAX_VALUE, minimaxDepth);

    // retrieve state representing best move the computer can make given the current state
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

    // otherwise, current state is internal node, check all columns of its board to determine which moves can be made
    for (int col = 0; col < currentState.board[0].length; col++) {

      int row = nextValidRow(currentState.board, col);
      if (row != -1) {

        // create child node representing the board state after the move, and link it to the current state node
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


  public void addSubscribers(PublishSubscribe... subscribers) {
    this.subscribers = Arrays.asList(subscribers);
    updateSubscribers();
  }


  public void updateSubscribers() {
    subscribers.forEach(subscriber -> {
      subscriber.update(displaySize, boardState, gameState, playerWinCount, computerWinCount);
    });
  }
}
