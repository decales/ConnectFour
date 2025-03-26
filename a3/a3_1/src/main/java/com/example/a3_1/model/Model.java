package com.example.a3_1.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import javafx.util.Pair;
import com.example.a3_1.model.AppState.GameState;

public class Model {

  public enum PieceType { Computer, Player, None };

  private List<PublishSubscribe> subscribers;
  private AppState appState;
  private BoardState boardState;
  private Stack<BoardState> previousBoardStates;
  private HashMap<BoardState, Pair<Double, BoardPosition>> stateMemo;

  public Model(double displaySize) {

    // application/game data
    subscribers = new ArrayList<>();
    appState = new AppState();
    appState.displaySize = displaySize;
    appState.maxDepth = 4;
    
    // board data
    previousBoardStates = new Stack<>(); // stack to keep track of previous player board states for undo functionality
    stateMemo = new HashMap<>(); // memo to reduce complexity of negamax search 
    initializeGame(6, 7);
  }


  public void initializeGame(int rowCount, int colCount) {
    // set game state and initialize board state with empty board based on the selected dimensions
    boardState = new BoardState(new PieceType[rowCount][colCount]);
    for (int i = 0; i < boardState.board.length; i++) Arrays.fill(boardState.board[i], PieceType.None);
    
    previousBoardStates.clear(); 
    appState.canUndo = false;
    appState.state = GameState.InProgress;
    updateSubscribers();
  }


  public void initializeGame() {
    // used in reset button
    initializeGame(boardState.board.length, boardState.board[0].length);
  }


  public void setDepth(int maxDepth) {
    // used to set negamax traversal depth cutoff in dropdown box
    appState.maxDepth = maxDepth;
    stateMemo.clear();
  }
  

  public void undoTurn() {
    // reset to the board state before the player's last move
    boardState = previousBoardStates.pop();
    appState.canUndo = !previousBoardStates.isEmpty();

    // if the last move resulted in a win or tie, undo that too
    switch (appState.state) {
      case PlayerWin -> { appState.playerScore--; appState.state = GameState.InProgress; }
      case ComputerWin -> { appState.computerScore--; appState.state = GameState.InProgress; }
      case Draw -> { appState.state = GameState.InProgress; }
    }
    updateSubscribers();
  }

  
  public void previewMove(int col) {
    if (boardState.moveType == PieceType.Computer && appState.state == GameState.InProgress) { // player's turn
      // player move preview only displays when previewPosition is not null
      boardState.movePosition = getValidMove(boardState.board, col);
      appState.canMove = boardState.movePosition != null; // lock to prevent move until mouse is moved again
      updateSubscribers();
    }
  }


  private BoardPosition getValidMove(PieceType[][] board, int col) {
    // look for the next empty position in the given column starting from the bottom row
    if (col != -1) {
      for (int row = board.length - 1; row >= 0; row --) {
        if (board[row][col] == PieceType.None) return new BoardPosition(row, col); // return the first available empty position
      }
    }
    return null; // otherwise, column is full (or invalid for piece preview)
  }


  public void playMove() {
    if (appState.state == GameState.InProgress) {
      // next piece to play is the opponent of the last piece played in the current state
      switch(boardState.moveType) {
        case Computer -> { // player turn
            if (appState.canMove) { 
              previousBoardStates.push(boardState); // save state before making the move for undo purposes
              boardState = new BoardState(boardState, boardState.movePosition); // get state after move
              if (boardState.isWinState()) { appState.state = GameState.PlayerWin; appState.playerScore ++; }
              else if (boardState.isDrawState()) appState.state = GameState.Draw;
              appState.canMove = false;
              playMove(); // play computer's turn if move was not win/tie
            }
        }
        case Player -> { // computer turn
          boardState = new BoardState(boardState, getComputerMove(boardState, -Double.MAX_VALUE, Double.MAX_VALUE, appState.maxDepth)); 
          if (boardState.isWinState()) { appState.state = GameState.ComputerWin; appState.computerScore ++; }
          else if (boardState.isDrawState()) appState.state = GameState.Draw;
        }
      }
    }
    appState.canUndo = !previousBoardStates.isEmpty();
    updateSubscribers();
  }


  // memoized negamax algorithm with AB pruning
  private BoardPosition getComputerMove(BoardState currentState, double alpha, double beta, int depth) {
    
    // initialize 'best move' as random move instead of null - fail safe to prevent returning null moves (even though this shouldn't happen!)
    BoardPosition bestMove = getValidMove(currentState.board, (int) (Math.random() * currentState.board[0].length));

    // if state has already been evaluated, return its score directly
    if (stateMemo.containsKey(currentState)) {
      currentState.score = stateMemo.get(currentState).getKey(); // best score
      bestMove = stateMemo.get(currentState).getValue(); // best move
    }
    else {
      // when terminal state reached, evalulate the board so it can be propagated up the tree
      if (currentState.isWinState() || currentState.isDrawState()) bestMove = currentState.movePosition; // default score -infinity - will be inverted
      else if (depth == 0) currentState.evaluateBoard();
      else {
        // check all columns of the board to determine which moves/child states can be made
        for (int col = 0; col < currentState.board[0].length; col++) {
          BoardPosition movePosition = getValidMove(currentState.board, col); if (movePosition != null) {

            // create child state representing the board after the move and recursively propagate scores to determine best state at current level
            BoardState childState = new BoardState(currentState, movePosition);
            getComputerMove(childState, -beta, -Math.max(alpha, currentState.score), depth - 1);

            // take the maximum negated child score - the opponent's worst state is the current best state
            if (-childState.score > currentState.score) {
              currentState.score = -childState.score;
              bestMove = movePosition;
            }
          }
          // stop creating and exploring children state when we know the opp 
          if (currentState.score >= beta) break;        
        }
      // add state to the memo now that best child and score are determined
      stateMemo.put(currentState, new Pair<>(currentState.score, bestMove));
      }
    }
    // best state is taken at the root of the tree and is dependant on the values propagated from terminal states
    return bestMove;
  }


  public void addSubscribers(PublishSubscribe... subscribers) {
    this.subscribers = Arrays.asList(subscribers);
    updateSubscribers();
  }


  public void updateSubscribers() {
    subscribers.forEach(subscriber -> subscriber.update(appState, boardState));
  }
}
