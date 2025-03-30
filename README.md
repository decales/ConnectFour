## ConnectFour

This application is a simple [Connect Four](https://en.wikipedia.org/wiki/Connect_Four) game where a user player competes against a negamax-based computur player. The application also allows the user to control various gameplay elements that affect the state of the
board and the behaviour and efficiency of the computer player in determining its next best move.

### Getting Started
In its current state, the game can only be played by cloning the repository and running the application directly:

```bash
git clone https://github.com/decales/ConnectFour && cd ConnectFour/ConnectFour && ./run
```
Should you wish to build an executable JAR file, you may do so by executing ```./build``` within the repository, and the
JAR will be found in the *out/* directory. Please note that Java 21+ is required to run the application.

### Gameplay

At the start of each match, the user (light-coloured, cyan-accented pieces) always moves first. To place a piece,
hover the cursor over the board, and click on the column where the piece should be placed. While selecting a
column, a piece preview indicator will appear in the first available row of a given column to show exactly where
the piece can be placed (assuming the move is valid and the column is not full, otherwise the indicator will not
appear). The computer (dark-coloured, magenta accented pieces) will place immediately after the user’s turn,
and this process repeats until either player wins or the game ends in a draw.
Below the board is a toolbar that allows the user to control various aspects of the game. The reset button clears
the board of all placed pieces. An ongoing match can be reset at any time, and must be reset to begin a new
match when it has ended. The undo button resets the board to the state before the user’s last move. In addition to
allowing the user to cheat (which is only fair if the computer can evaluate future board states), undoing a move is
useful to observe the consistency of the computer’s behaviour, especially when experimenting with the other
control components. The depth drop-down menu allows the user to specify the negamax cut-off depth, the
number of future moves from the current state the computer can consider to determine its next move. Lastly, the
size of the board can be changed using the dimension toggle buttons. It should be noted that the win-condition to
connect four pieces does not differ between board sizes.

### Structure

The game is implemented as a JavaFX application that uses an MVC-like design pattern to structure its various classes. In this case, there is a single model class containing the game logic and data that communicates with various view classes through a publish-subscribe mechanism. A simple controller class is used to handle the events generated in the view classes and update the model. The project directory structure is organized to reflect the components of MVC, where most of the classes are organized into separate *model/* and *view/* sub-directories. The rest of the classes, including the controller class, are not contained in a sub-directory:

- *model/*
    - *AppState.java* – data class containing game, UI, and general state data
    - *BoardPosition.java* – helper data class to represent a row-column position on the board
    - *BoardState.java* – class to represent the state of the board at a given moment during a match
    - *Model.java* – main class containing the game and application logic
    - *PublishSubscribe.java* – interface to send data from the model to the view components
- *view/*
    - *bottomBar/*
        - *BottomBar.java* – parent container class representing the bottom bar as a whole
        - *DepthSelector.java* – *ComboBox* component to select the cutoff depth of the negamax algorithm
        - *DimensionsToggle.java* – collection of *RadioButton* components to select the size of the board
        - *ResetButton.java* – *Button* component to reset the board
        - *UndoButton.java* – *Button* component to undo the user’s last move on the board
    - *gameBoard/*
        - *GameBoard.java* – *GridPane* container class to represent the game board
        - *BoardPiece.java* – component to represent a player’s piece on the *GameBoard*
    - *topBar/*
        - *ScoreLabel.java* – *Label* component to display the number of wins for both players
        - *StateLabel.java* – *Label* component to display the current state of a match
        - *TopBar.java* – parent container class representing the top bar as a whole
- *App.java* – class to initialize components related to the application itself (stage, scene, root, etc.)
- *Controller.java* – event handling class used by view components to perform actions in the model
- *Main.java* – entry point class required to build JavaFX .jar files

The most important logic is contained in the *model/* directory, particularly in *Model.java* and *BoardState.java*.
These classes contain all the logic to initialize the board and allow a user to play a match against the computer.
The other classes, while managing their own states, only do so in a way to visually reflect the state of the model,
and are therefore dependent on it.

### Negamax

The negamax algorithm is implemented in *Model.java* directly in the *getComputerMove()* function. With aid of
various helper functions also contained the model, this function returns a *BoardPosition* representing the best
move the computer can make given the current *BoardState*. Following standard minimax/negamax logic, this is
accomplished by creating a game tree of children *BoardState* instances representing the subsequent states of the
board based on the moves that are possible from the parent, current state. When a terminal state is reached upon
expanding the game-tree, it is evaluated with the *evaluateBoard()* function of the *BoardState* class, and its value
is propagated up the tree to choose the best *BoardPosition* move at each level. It is only at the root of the tree
where the final *BoardPosition* is returned from *getEvaluate()* and used to create a new state representing the
computer’s move.

#### Evaluation

Terminal states are evaluated in *getComputerMove()* in two contexts. Firstly, win/draw states are evaluated with
the maximum possible score, as it follows that these are the best (or in the case of draws, only) possible states to
achieve when the broad objective is to win a match. In the implementation specifically, win/draw states are not
directly assigned a value within *getComputerMove()*, but are simply left with the -Double.MAX_VALUE score
that *BoardState* objects are initialized with. Game boards are evaluated from the perspective of the player that
moved last, so when a win/draw state is encountered, its default negative value is negated after being propagated
one level up the tree to align with the fact that this the best state from the perspective of the player where the
state was created.When a state is encountered at the maximum depth of the game-tree based on the depth-cutoff, it is evaluated in
the *evaluateBoard()* function using two heuristics. The first and typically least impactful assigns higher scores to
states that have more pieces in the centre column(s) of the board. The intuition behind this is based on the idea
that controlling these columns gives more opportunity to players to connect four pieces in all directions. While
this is an important factor to consider, it is the "least impactful" in the sense that it is implicitly evaluated and
made redundant (except in cases where the depth-cutoff value is low) by the second heuristic. To score
potentially winning states, all possible ‘windows’ are examined on the board, representing the four-length
segments where pieces can be placed to win a match. To score this heuristic, the evaluation function iterates
through each individual position of the board, and counts the number of pieces in each possible window in all
directions originating from the current position. The more pieces in a window that belong to the player that
moved last in the state, the lower the score (again, using lower values because the total score is negated in the
level where the state was created), and vice-versa for their opponent. The evaluation also accounts for and
assigns higher values to windows with empty positions, as this is indicative of a potential segment to win the
match. Out of all the method I have experimented with while developing the application, this leads to the most
consistent and formidable computer opponent. Even when it knows it has you beat, the computer will often make
non-winning moves just to maximize the number of windows is has secured before delivering the final blow
(either by design or by malicious intent, whichever you want to believe).

#### Optimization

Several optimizations are made in the negamax implementation to reduce the complexity of calculating and
evaluating the states of the game-tree. Firstly, the depth-cutoff prevents the entire game-tree from being
expanded each time *getComputerMove()* is called, limiting the computer to look ahead at most 8 levels to
determine its best move. The most crucial optimization is the addition of alpha-beta pruning. This allows the
algorithm to keep track of the highest/lowest valued states as it recursively explores the tree, taking advantage of
the alternating perspectives at each level and the zero-sum nature of the game to prevent expanding and
evaluating states that that need not be considered. The final optimization is the use of memoization to eliminate
the overhead of re-evaluating previously visited child states. This is implemented in the form of a *HashMap* that
uses *BoardStates* as keys and BoardPosition-double pair values to keep track of the values of the best children
states and the moves that lead to them. While memoization is not necessary in games like connect-4, it is
particularly useful when using the 8x9 board with the maximum depth-cutoff. Despite the benefit of
memoization, however, it is inadvisable to play the game with these settings, as the game-tree must still be
explored at least a few times throughout the match to populate the memo, which can take a long time to calculate
(with my system, ~10 seconds in the worst case when AB-pruning seems to have little effect in the current state).
It should also be noted that changing the cut-off depth during an on-going game clears the memo to account for
the difference of how states can be evaluated at differing depths.

### Bugs & Issues

In its current state, there are no known bugs or issues to report. The only thing worth mentioning is that the
computer can behave inconsistently at specific depth cut-offs. For example, at values of 3 and 5, the computer
often blunders its moves and can be beaten very easily early into a match, but seems to play intelligently at lower
cut-off values (excluding 1). This is likely the result of improperly handling the cut-off with regards to the
alternating player perspectives. Both values are odd numbers, meaning the last player to move in the terminal
states is always the computer, but there seem to be no issues at a cut-off value of 7. In general, without
considering impact on performance, I find that the best cut-off depths are 4, 6, 7 and 8.

