package com.example.a3_1.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Model {

  private List<PublishSubscribe> subscribers;
  private double displaySize;

  private int[][] gameBoard;

  public Model(double displaySize) {

    this.displaySize = displaySize;
    subscribers = new ArrayList<>();

    initializeGameBoard();


  }

  public void initializeGameBoard() {
    gameBoard = new int[6][7]; // init empty 6x7 board
    updateSubscribers();
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
