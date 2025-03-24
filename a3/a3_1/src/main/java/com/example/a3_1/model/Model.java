package com.example.a3_1.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;

public class Model {

  public enum PieceType { Computer, Player, None };
  public enum GameState { InProgress, ComputerWin, PlayerWin, Tie };

  private List<PublishSubscribe> subscribers;
  private double displaySize;
  private BoardState boardState;
  private int maxDepth;
  private BoardPosition previewPosition;
  private GameState gameState;
  private int playerWinCount, computerWinCount;
  private HashMap<BoardState, Pair<Double, BoardState>> boardMemo;

  public Model(double displaySize) {

    // initialize application data
    this.displaySize = displaySize;
    subscribers = new ArrayList<>();
    boardMemo = new HashMap<>();
    maxDepth = 4;
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


  public void setDepth(int maxDepth) {
    // used to set negamax traversal depth cutoff in dropdown box
    this.maxDepth = maxDepth;
    boardMemo.clear();
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
            if (previewPosition != null) { // only play turn after mouse click, using previewPosition as 'lock' of sort
              boardState = new BoardState(boardState, previewPosition); // get updated state with player move
              previewPosition = null;
            }
            else return;
          }
          // computer's turn
          case Player -> {
            boardState = getComputerMove(boardState, -Double.MAX_VALUE, Double.MAX_VALUE, maxDepth); // get updated state with computer move
          }
        }
      }
      playTurn(); // play opponent's turn or make terminal recursive call after game has ended
    }
  }
 

  // memoized negamax algorithm with AB pruning
  private BoardState getComputerMove(BoardState currentState, double alpha, double beta, int depth) {
    
    BoardState bestState = null;

    // if state has already been evaluated, return its score directly
    if (boardMemo.containsKey(currentState)) {
      currentState.score = boardMemo.get(currentState).getKey(); // best score
      bestState = boardMemo.get(currentState).getValue(); // best child state
    }
    else {
      // when terminal state reached, evalulate the board so it can be propagated up the tree
      if (currentState.isWinState() || currentState.isTieState()) bestState = currentState; // score is already -Double.MAX_VALUE - will be inverted
      else if (depth == 0) currentState.evaluateBoard();
      else {
        // check all columns of the board to determine which moves/child states can be made
        for (int col = 0; col < currentState.board[0].length; col++) {
          BoardPosition movePosition = nextValidPosition(currentState.board, col); if (movePosition != null) {

            // create child state representing the board after the move and recursively propagate scores to determine best state at current level
            BoardState childState = new BoardState(currentState, movePosition);
            getComputerMove(childState, -beta, -Math.max(alpha, currentState.score), depth - 1);

            // take the maximum negated child score - the opponent's worst state is the current best state
            if (-childState.score > currentState.score) {
              currentState.score = -childState.score;
              bestState = childState;
            }
          }
          // stop creating and exploring children state when we know the opp 
          if (currentState.score >= beta) break;        
        }
        // add state to the memo now that best child and score are determined
        boardMemo.put(currentState, new Pair<>(currentState.score, bestState));
      }
    }

    // best state is used at the root of the tree and is dependant on the values propagated from terminal states
    if (depth == maxDepth) System.out.println(currentState.score);
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
