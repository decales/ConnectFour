package com.example.a3_1.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Model {

  public enum PieceType { Computer, Player, None, Preview };
  public enum GameState { InProgress, ComputerWin, PlayerWin, Tie };

  private List<PublishSubscribe> subscribers;
  private double displaySize;
  private BoardState boardState;
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
    boardState = new BoardState(new PieceType[6][7]);
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

      // check if last move resulted in win
      if (boardState.isWinState()) {         
        gameState = (boardState.pieceMoved == PieceType.Computer) ? GameState.ComputerWin : GameState.PlayerWin;
        if (boardState.pieceMoved == PieceType.Computer) computerWinCount++; else playerWinCount ++;
      }
      // check if last move resulted in tie
      else if (boardState.isTieState()) gameState = GameState.Tie;

      // otherwise play turn
      else { // next piece to play is the opposing piece of the last piece played in the current state
        switch(boardState.pieceMoved) {
          
          // player's turn
          case Computer -> {
            if (boardState.board[previewPosition.row][previewPosition.col] == PieceType.Preview) { // only play turn given the player's input
              boardState = new BoardState(boardState, previewPosition); // get updated state with player move
            }
            else return;
          }
          // computer's turn
          case Player -> {
            // determine its best move with negamax algorithm
            boardState = getComputerMove(boardState, -Double.MAX_VALUE, Double.MAX_VALUE, minimaxDepth); // get updated state with computer move
          }
        }
      }
      playTurn(); // play opponent's turn or make terminal recursive call
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


  private BoardState getComputerMove(BoardState currentState, double alpha, double beta, int depth) {

    BoardState bestState = null;

    // when maximum depth reached, evalulate the board so it can be propagated up the tree
    if (depth == 0) currentState.evaluateBoard();
    else {
      // check all columns of the board to determine which moves/child states can be made
      for (int col = 0; col < currentState.board[0].length; col++) {
        int row = nextValidRow(currentState.board, col); if (row != -1) {

          // create child state representing the board after the move  
          BoardState childState = new BoardState(currentState, new BoardPosition(row, col));

          // if the move immediately results in a terminal state, it is either the best move or the only move
          if (childState.isWinState() || childState.isTieState()) {
            currentState.score = Double.MAX_VALUE;
            bestState = childState;
          }
          else { // otherwise, recursively build tree to score and determine best state
            getComputerMove(childState, -beta, -Math.max(alpha, currentState.score), depth - 1);

            // take the maximum negated child score - the opponent's worst state is the current best state
            if (-childState.score > currentState.score) {
              currentState.score = -childState.score;
              bestState = childState;
            }
            // stop creating and exploring children state when we know the opp 
            if (currentState.score >= beta) break;         
          }
        }
      }
    }
    // best state is returned from the root of the tree and is dependant on the values propagated from terminal states
    return bestState;
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
