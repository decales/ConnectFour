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
  private BoardPosition previewPosition;

  public Model(double displaySize) {

    this.displaySize = displaySize;
    subscribers = new ArrayList<>();

    initializeGameBoard();
    isPlayerTurn = true;
    previewPosition = new BoardPosition(0, 0);
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
      if (gameBoard[previewPosition.row][previewPosition.col] == PieceType.Preview) {
        gameBoard[previewPosition.row][previewPosition.col] = PieceType.None; 
      }

      int row = nextValidRow(gameBoard, col);
      if (row != -1) { // check if the column is not full
        // update the piece preview position and set it on the board
        previewPosition = new BoardPosition(row, col);
        gameBoard[previewPosition.row][previewPosition.col] = PieceType.Preview;
      }
      updateSubscribers();
    }
  }


  public void clearPreview() {
    // clears the piece preview indicator when the mouse is not on the board, because this just looks nicer
    gameBoard[previewPosition.row][previewPosition.col] = PieceType.None;
    updateSubscribers();
  }


  public void playTurn() {
    if (isPlayerTurn) { // player's turn to place

      // if the piece preview is on the screen, we know which column the player has selected and that the move is valid / the column isn't full
      if (gameBoard[previewPosition.row][previewPosition.col] == PieceType.Preview) {

        BoardStateNode playerMoveState = new BoardStateNode(gameBoard, previewPosition); // represent current state of the board after the move
        gameBoard[previewPosition.row][previewPosition.col] = PieceType.Player; // update gameBoard (different than that in state) with move
        isPlayerTurn = false; // end of player's turn
        
        // Check if the move that was played results in a win
        if (isWinningState(playerMoveState)) {
          System.out.println("you wonnered");
          return;
        }

        // Computer's turn, retrieve the state representing the best move it can make given the player's move
        BoardStateNode computerMoveState = getComputerMove(playerMoveState); 
        gameBoard[computerMoveState.movePosition.row][computerMoveState.movePosition.col] = PieceType.Computer;
        updateSubscribers();

        // Check if the move that was played results in a win
        if (isWinningState(computerMoveState)) {
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
  

  private BoardStateNode getComputerMove(BoardStateNode currentState) {
    // recursively build state tree and determine minimax values from a current board state
    getMinimaxStateTree(currentState, 4);

    // Retrieve state representing best move the computer can make given the current state
    // int minimumValue = Integer.MAX_VALUE;
    // BoardStateNode bestState = null;
    //
    // for (BoardStateNode child : currentState.children) {
    //   System.out.println(child.minimaxValue);
    //   if (child.minimaxValue < minimumValue) {
    //     minimumValue = child.minimaxValue;
    //     bestState = child;
    //   }
    // }
    int minimumValue = Integer.MIN_VALUE;
    BoardStateNode bestState = null;

    for (BoardStateNode child : currentState.children) {
      System.out.println(child.minimaxValue);
      if (child.minimaxValue > minimumValue) {
        minimumValue = child.minimaxValue;
        bestState = child;
      }
    }
    return bestState;
  }


  private void getMinimaxStateTree(BoardStateNode currentState, int depth) {
    
    // current state is a leaf-node, score it based on how good its move is
    if (isWinningState(currentState) || depth == 0) {

      currentState.minimaxValue = 0; // clear default value used for internal nodes

      // Priority #1 - move results in a win: +1000
      if (isWinningState(currentState)) currentState.minimaxValue += 1000;
      // Priority #2 - move prevents a loss: +100
      if (isWinningState(new BoardStateNode(currentState, currentState.movePosition))) currentState.minimaxValue += 100;
      // Priority #3 - caluclate points based on the distances between the move and each matching piece on the board
      currentState.minimaxValue += 20;

      return;
    }

    // Otherwise, current state is internal node, check all columns of its board to determine which moves can be made
    for (int col = 0; col < currentState.board[0].length; col++) {

      int row = nextValidRow(currentState.board, col);
      if (row != -1) {

        // Create child node representing the board state after the move, and link it to the current state node
        BoardStateNode childState = new BoardStateNode(currentState, new BoardPosition(row, col));
        currentState.addChild(childState);

        getMinimaxStateTree(childState, depth - 1); // continue to recursively build tree

        // current node's value can now be determined after recursion, take min or max of Vchild based on which piece is playing
        currentState.minimaxValue = (currentState.pieceMoved == PieceType.Computer)
        ? Math.min(currentState.minimaxValue, childState.minimaxValue) // computer - take min value
        : Math.max(currentState.minimaxValue, childState.minimaxValue); // player - take max value
      }
    }
  }


  // // breadth-first version
  // private BoardStateNode getGameStateTree() {
  //
  //   Queue<BoardStateNode> nodeQueue = new LinkedList<BoardStateNode>();
  //   BoardStateNode rootNode = new BoardStateNode(gameBoard, PieceType.Player);
  //   nodeQueue.add(rootNode);
  //
  //   while(!nodeQueue.isEmpty()) {
  //
  //     BoardStateNode currentNode = nodeQueue.poll();
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
  //         BoardStateNode childNode = new BoardStateNode(childBoard, pieceToPlay);
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


  private boolean isWinningState(BoardStateNode state) {
    // Very cool and fancy loop to check if a move results in a horizontal, vertical, or diagonal sequence of at least 4, checked in this order
    for (int[][] v: new int[][][] { {{0,1},{0,-1}}, {{1,0},{-1,0}}, {{1,1},{-1,-1}}, {{-1,1},{1,-1}} }) {

      // length of sequence is the sum of the lengths traversing both opposing directions of v starting from the position of the move
      int l1 = getSequence(state.board, state.movePosition.row, state.movePosition.col, v[0], state.pieceMoved);
      int l2 = getSequence(state.board, state.movePosition.row, state.movePosition.col, v[1], state.pieceMoved);

      // sequence found, move results in win
      if (l1 + l2 + 1 >= 4) return true; // +1 to account for move
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
