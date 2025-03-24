package com.example.a3_1.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Model {

  public enum PieceType { Computer, Player, None };
  public enum GameState { InProgress, ComputerWin, PlayerWin, Tie };

  private List<PublishSubscribe> subscribers;
  private double displaySize;
  private BoardState boardState;
  private int minimaxDepth;
  private BoardPosition previewPosition;
  private GameState gameState;
  private int playerWinCount, computerWinCount;
  private HashMap<BoardState, Double> boardMemo;

  public Model(double displaySize) {

    // initialize application data
    this.displaySize = displaySize;
    subscribers = new ArrayList<>();
    boardMemo = new HashMap<>();
    minimaxDepth = 4;
    initializeGame(); 
  }


  public void initializeGame() {
    // initialize game state and vars
    gameState = GameState.InProgress;

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
      // player move preview only displays when previewPosition is not null
      previewPosition = nextValidPosition(boardState.board, col);
      updateSubscribers();
    }
  }


  private BoardPosition nextValidPosition(PieceType[][] board, int col) {
    if (col != -1) {
      // look for the next empty position in the given column starting from the bottom row
      for (int row = board.length - 1; row >= 0; row --) {
        if (board[row][col] == PieceType.None) return new BoardPosition(row, col); // return the first available empty position
      }
    }
    return null; // otherwise, column is full (or invalid for piece preview)
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
            if (previewPosition != null) { // only play turn after the player's input, using previewPosition as 'lock' of sort
              boardState = new BoardState(boardState, previewPosition); // get updated state with player move
              previewPosition = null;
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
 

  private BoardState getComputerMove(BoardState currentState, double alpha, double beta, int depth) {

    BoardState bestState = null;

    // when maximum depth reached, evalulate the board so it can be propagated up the tree
    if (depth == 0) currentState.evaluateBoard();
    else {
      // check all columns of the board to determine which moves/child states can be made
      for (int col = 0; col < currentState.board[0].length; col++) {
        BoardPosition movePosition = nextValidPosition(currentState.board, col); if (movePosition != null) {

          // create child state representing the board after the move  
          BoardState childState = new BoardState(currentState, movePosition);

          // if the move immediately results in a terminal state, it is either the best move or the only move
          if (childState.isWinState() || childState.isTieState()) {
            // currentState.score = Double.MAX_VALUE;
            currentState.score = 1337;
            bestState = childState;
          }
          else { // otherwise, build tree and propagate scores to determine best state at current level
            getComputerMove(childState, -beta, -Math.max(alpha, currentState.score), depth - 1); // recursively determine child's score

            // take the maximum negated child score - the opponent's worst state is the current best state
            if (-childState.score > currentState.score) {
              currentState.score = -childState.score;
              bestState = childState;
            }
          }
          // stop creating and exploring children state when we know the opp 
          if (currentState.score >= beta) break;        
        }
      }
    }
    // best state is used at the root of the tree and is dependant on the values propagated from terminal states
    return bestState;
  }
  

  public void addSubscribers(PublishSubscribe... subscribers) {
    this.subscribers = Arrays.asList(subscribers);
    updateSubscribers();
  }


  public void updateSubscribers() {
    subscribers.forEach(subscriber -> {
      subscriber.update(displaySize, gameState, boardState, previewPosition, playerWinCount, computerWinCount);
    });
  }
}
            // // check if child score is already in the memo
            // if (boardMemo.containsKey(childState)) {
            //   System.out.println("HIT");
            //   childState.score = boardMemo.get(childState);
            // }
            // else { // otherwise recursively determine child's score then add it to the memo
            //   getComputerMove(childState, -beta, -Math.max(alpha, currentState.score), depth - 1);
            //   boardMemo.put(childState, childState.score);
            // }
