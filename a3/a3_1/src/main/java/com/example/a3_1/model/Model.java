package com.example.a3_1.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

      int row = nextValidRow(gameBoard, col);
      if (row != -1) { // check if the column is not full
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
        if (isWinningMove(gameBoard, previewRow, previewCol, PieceType.Player)) {
          System.out.println("wonnered");
        }

        GameStateNode stateTree = getGameStateTree(); 
        updateSubscribers();
        // try { Thread.sleep(1000); } catch (InterruptedException e) {}
        // Computer's turn to place
        isPlayerTurn = true;
        updateSubscribers();
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


  private GameStateNode getGameStateTree() {
    GameStateNode stateRoot = new GameStateNode(gameBoard, PieceType.Player);
    getGameStateTree(stateRoot, 0);
    return stateRoot;
  }
  

  private void getGameStateTree(GameStateNode currentState, int depth) {

    if (depth == 4) return; // Limit recursive depth
    
    // Check all columns for the possible moves given the current state
    for (int col = 0; col < currentState.board[0].length; col++) {

      int row = nextValidRow(currentState.board, col);
      if (row != -1) { // column is not full -> move is valid

        // Create child state representing the move
        PieceType pieceToPlay = (currentState.lastToPlay == PieceType.Player) ? PieceType.Computer : PieceType.Player;
        PieceType[][] childBoard = getChildBoard(currentState.board, row, col, pieceToPlay);
        GameStateNode childState = new GameStateNode(childBoard, pieceToPlay);

        currentState.addChild(childState); // Link the child to the current node to create tree

        if (!isWinningMove(childState.board, row, col, pieceToPlay)) {
          getGameStateTree(childState, depth + 1);
        }
      }
    }
  }
        // Score the child board state based on how good the move is:
        // +1000 for win
        // +100 for loss prevention
        // +x based on the positions of matching pieces
        // // Priority #1 - check if move would result in win
        // if (isWinningMove(childBoard, row, col, pieceToPlay)) childState.addMinimaxValue(1000);
        //
        // // Priority #2 - check if move would prevent a loss
        // if (i
  // sWinningMove(childBoard, row, col, nextPieceToPlay)) childState.addMinimaxValue(100);
        //
        // // Priority #3 - caluclate points based on the distances between the move and each matching piece on the board
        // childState.addMinimaxValue(0);


  // // breadth-first version
  // private GameStateNode getGameStateTree() {
  //
  //   Queue<GameStateNode> nodeQueue = new LinkedList<GameStateNode>();
  //   GameStateNode rootNode = new GameStateNode(gameBoard, PieceType.Player);
  //   nodeQueue.add(rootNode);
  //
  //   while(!nodeQueue.isEmpty()) {
  //
  //     GameStateNode currentNode = nodeQueue.poll();
  //
  //     printBoard(currentNode.board);
  //
  //     // Check all columns for the possible moves given the current state
  //     for (int col = 0; col < currentNode.board[0].length; col++) {
  //
  //       int row = nextValidRow(currentNode.board, col);
  //       if (row != -1) { // column is not full -> move is valid
  //
  //         // Create child state representing the move
  //         PieceType pieceToPlay = (currentNode.lastToPlay == PieceType.Player) ? PieceType.Computer : PieceType.Player;
  //         PieceType[][] childBoard = getChildBoard(currentNode.board, row, col, pieceToPlay);
  //         GameStateNode childNode = new GameStateNode(childBoard, pieceToPlay);
  //         
  //         currentNode.addChild(childNode); // Link the child to the current node to create tree
  //
  //         // printBoard(childBoard);
  //
  //         // Add child to queue to determine its children if state does not result in win
  //         if (!isWinningMove(childBoard, row, col, pieceToPlay)) {
  //           nodeQueue.add(childNode);
  //         }
  //       }
  //     }
  //   }
  //   return rootNode;
  // }

  private void printBoard(PieceType[][] board) {
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[0].length; j++) {
        switch(board[i][j]) {
          case PieceType.None -> System.out.print(". ");
          case PieceType.Player -> System.out.print("X ");
          case PieceType.Computer -> System.out.print("O ");
          default -> {}
        }
      }
      System.out.println();
    }
    System.out.println();
  }

  private PieceType[][] getChildBoard(PieceType[][] board, int moveRow, int moveCol, PieceType pieceToPlay) {
    // helper function to create shallow clone of a gameboard after a move has occured
    PieceType[][] clone = new PieceType[board.length][board[0].length];

    // Copy the values from the parent board to the child board
    for (int i = 0; i < clone.length; i++) {
      for (int j = 0; j < clone[0].length; j++) {
        clone[i][j] = board[i][j];
      }
    }
    // Add the move to the child board
    clone[moveRow][moveCol] = pieceToPlay;
    return clone;
  }


  private boolean isWinningMove(PieceType[][] board, int row, int col, PieceType piece) {
    // Very cool and fancy loop to check if a move results in a horizontal, vertical, or diagonal sequence of at least 4, checked in this order
    for (int[][] direction: new int[][][] { {{0,1},{0,-1}}, {{1,0},{-1,0}}, {{1,1},{-1,-1}}, {{-1,1},{1,-1}} }) {
      if (1 + getSequence(board, row, col, direction[0], piece) + getSequence(board, row, col, direction[1], piece) >= 4) {
        return true; // sequence found, move results in win
      }
    }
    return false; // no sequences found, move does not result in win 
  }


  private int getSequence(PieceType[][] board, int row, int col, int[] direction, PieceType piece) {
    // add the direction offsets to the current row and col to get adjacent row and col
    int adjRow = row + direction[0];
    int adjCol = col + direction[1];

    // adjacent row and col are out of grid bounds
    if (adjRow < 0 || adjRow >= board.length || adjCol < 0 || adjCol >= board[0].length) return 0;
    
    // piece at adjacent row and col does not match
    if (board[adjRow][adjCol] != piece) return 0;

    // otherwhise traverse to adjacent piece and increment count
    return 1 + getSequence(board, adjRow, adjCol, direction, piece);
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
