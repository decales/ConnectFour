package com.example.a3_1.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Model {

  public enum PieceType { Computer, Player, None, Preview };
  public enum GameState { InProgress, ComputerWin, PlayerWin, Tie };

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
    // used to set minimax depth in dropdown box
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
    if (boardState.board[previewPosition.row][previewPosition.col] == PieceType.Preview && gameState == GameState.InProgress) {
      boardState.board[previewPosition.row][previewPosition.col] = PieceType.None;
      updateSubscribers();
    }
  }


  public void playTurn() {
    updateSubscribers();
    if (gameState == GameState.InProgress) {

      if (boardState.isTieState()) {
        gameState = GameState.Tie;
        return;
      }

      switch(boardState.pieceMoved) { // next piece to play is the opposing piece of the last piece played in the current state                      
        
        case Computer -> { // player's turn
          if (boardState.board[previewPosition.row][previewPosition.col] == PieceType.Preview) { // only play turn given the player's input
            boardState = new BoardStateNode(boardState, previewPosition); // get updated state with player move

            // check if move was a win, update state and win count if so
            if (boardState.isWinState()) {
              gameState = GameState.PlayerWin;
              playerWinCount ++;
            }
            playTurn(); // play opponent's turn
          }
        }

        case Player -> { // computer's turn
          boardState = getComputerMove(); // get updated state with computer move
          
          // check if move was a win, update state and win count if so
          if (boardState.isWinState()) {
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
    // recursively build state tree and determine minimax values from the current board state (after player's move)
    getMinimaxStateTree(boardState, -Double.MAX_VALUE, Double.MAX_VALUE, minimaxDepth);

    // retrieve state representing best move the computer can make given the current state
    BoardStateNode bestState = new BoardStateNode(null);
    for (BoardStateNode childState : boardState.children) {
      if (childState.isWinState()) {
        bestState = childState;
        break;
      }
      else if (childState.minimaxValue >= bestState.minimaxValue) {
        bestState = childState;
      }
    }
    System.out.println(String.format("\nMove: %s\nPos: row %d col %d\nNum pieces: %d / %d\nisWin: %b\nisTie: %b\n",
          bestState.pieceMoved, bestState.movePosition.row, bestState.movePosition.col, bestState.numberPiecesMoved,
          bestState.board.length * bestState.board[0].length, bestState.isWinState(), bestState.isTieState()));

    return bestState;
    // return boardState.children.stream().max((c1, c2) -> Double.compare(c1.minimaxValue, c2.minimaxValue)).orElse(null);
  }


  private void getMinimaxStateTree(BoardStateNode currentState, double alpha, double beta, int depth) {

    // if current state is a leaf-node (win, tie, or max depth state) score it based on how good its move is
    if (currentState.isWinState() || currentState.isTieState() || depth == 0) {
      currentState.evaluateBoard();
      return;
    }

    // otherwise, check all columns of its board to determine which moves can be made and detemine its value recursively
    for (int col = 0; col < currentState.board[0].length; col++) {

      int row = nextValidRow(currentState.board, col);
      if (row != -1) {

        // create child node representing the board state after the move, and link it to the current state node
        BoardStateNode childState = new BoardStateNode(currentState, new BoardPosition(row, col));
        currentState.addChild(childState);

        // recursively build tree and determine minimax values from the bottom up
        getMinimaxStateTree(childState, alpha, beta, depth - 1);

        // current node's value can now be determined after recursion, take min or max of child based on which piece is playing
        switch(currentState.pieceMoved) {

          case Computer -> { // take max value, check alpha
            currentState.minimaxValue = Math.max(currentState.minimaxValue, childState.minimaxValue);
            alpha = Math.max(alpha, currentState.minimaxValue);
          }
          case Player -> { // take min value, check beta
            currentState.minimaxValue = Math.min(currentState.minimaxValue, childState.minimaxValue);
            beta = Math.min(beta, currentState.minimaxValue);
          }
        }
        if (alpha >= beta) break; // no need to continue building tree, min/max of branch already determined
      }
    }
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
