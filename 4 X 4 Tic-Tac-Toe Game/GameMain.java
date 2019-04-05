package aiTicTacToeGame;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * AI project, 4*4 Tic-Tac-Toe Game
 * Human player (nought) v.s. computer (cross)
 * Computer player is powered by Alpha-Beta minimax algorithm with cut off huristic evaluation function
 * Graphic version with OO design.
 */
// @SuppressWarnings("serial")
public class GameMain extends JPanel {
   // Named-constants for the game board
   protected static final String TITLE = "4 * 4 Tic Tac Toe Game";
   protected static final int ROWS = 4;  // ROWS by COLS cells
   protected static final int COLS = 4;
   // Name-constants for the various dimensions used for graphics drawing
   protected static final int CELL_SIZE = 100; // cell width and height (square)
   protected static final int CANVAS_WIDTH = CELL_SIZE * COLS;  // the drawing canvas
   protected static final int CANVAS_HEIGHT = CELL_SIZE * ROWS;
   protected static final int GRID_WIDTH = 8;  // Grid-line's width
   protected static final int GRID_WIDHT_HALF = GRID_WIDTH / 2; // Grid-line's half-width
   // Symbols (cross/nought) are displayed inside a cell, with padding from border
   protected static final int CELL_PADDING = CELL_SIZE / 6;
   protected static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;
   protected static final int SYMBOL_STROKE_WIDTH = 8; // pen's stroke width
   // private variables
   private Board board;            // the game board
   private State currentState; // the current state of the game
   private Seed currentPlayer;     // the current player
   private JLabel statusBar;       // for displaying status message
   private int difficultLevel;       // difficulty chosen for the game 0 1 2 corresponding to depth 1 5 9
   // private AIPlayerMinimaxCutoff aiMaxPlayer; // MaxPlayer

   private final Object lock = new Object();

   /** Constructor to setup the UI and game components */
   public GameMain() {
      // This JPanel fires MouseEvent
      this.addMouseListener(new MouseAdapter() {
         @Override
         public synchronized void mouseClicked(MouseEvent e) {  // mouse-clicked handler
            int mouseX = e.getX();
            int mouseY = e.getY();
            // Get the row and column clicked
            int rowSelected = mouseY / CELL_SIZE;
            int colSelected = mouseX / CELL_SIZE;

            boolean flag = false;

            if (currentState == State.PLAYING && currentPlayer == Seed.NOUGHT) {
               // synchronized (lock){
                  if (rowSelected >= 0 && rowSelected < ROWS
                     && colSelected >= 0 && colSelected < COLS
                     && board.getCell(rowSelected, colSelected).getContent() == Seed.EMPTY) {
                     board.getCell(rowSelected, colSelected).setContent(currentPlayer); // move
                     // repaint();  // Call-back paintComponent().
                     flag = board.hasWon(currentPlayer, rowSelected, colSelected);
                     updateGame(currentPlayer, rowSelected, colSelected); // update currentState
                     // Switch player
                     if(!flag){
                        currentPlayer = Seed.CROSS;
                     }
                  }
               // }
               // AI Max player playing
               // synchronized (lock){
                  if(!flag && (currentState == State.PLAYING) && currentPlayer == Seed.CROSS){
                     // create an AI player with current board and difficultLevel chosen
                     AIPlayerMinimaxCutoff aiMaxPlayer = new AIPlayerMinimaxCutoff(board, difficultLevel);
                     // AIPlayerMinimaxCutoffBetter aiMaxPlayer = new AIPlayerMinimaxCutoffBetter(board, difficultLevel);
                     // compute the best move using Alpha-beta Alg
                     int[] move = aiMaxPlayer.move();
                     board.getCell(move[0], move[1]).setContent(currentPlayer); // move
                     // repaint();  // Call-back paintComponent().
                     updateGame(currentPlayer, move[0], move[1]); // update currentState
                     // Switch player
                     currentPlayer = Seed.NOUGHT;
                  }
               // }
            }
            //  else {        // game over
            //    initGame();  // restart the game
            // }
            // Refresh the drawing canvas
            // repaint();  // Call-back paintComponent().
         }
      });

      // Setup the status bar (JLabel) to display status message
      statusBar = new JLabel("         ");
      statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 14));
      statusBar.setBorder(BorderFactory.createEmptyBorder(12, 15, 14, 15));
      statusBar.setOpaque(true);
      statusBar.setBackground(Color.LIGHT_GRAY);

      setLayout(new BorderLayout());
      add(statusBar, BorderLayout.PAGE_END); // same as SOUTH
      setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT + 50)); // account for statusBar in height

      board = new Board();   // allocate the game-board

      initGame();  // Initialize the game variables
   }

   //  set current player
   public void setCurrentPlayer(Seed player){
      this.currentPlayer = player;
   }

   /** Initialize the game-board contents and the current game state */
   public void initGame() {
      // Let user choose which level of difficulty to play, three level to choose
      String[] options = new String[] {"Easy", "Intermediate", "Difficult"};
      difficultLevel = JOptionPane.showOptionDialog(null, "Please choose level of difficulty!", "Game configarition",
        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
      // Where response == 0 for Easy, 1 for Intermediate, 2 for Difficult and -1 or 3 for Escape/Cancel.

      // Let user choose to play first or not
      if (JOptionPane.showConfirmDialog(null, "Tic-Tac_Toe Game Starting! Do you wanna play first?", "Game configarition",
       JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
         setCurrentPlayer(Seed.NOUGHT);
         // currentPlayer = Seed.NOUGHT;  // Users play first
      } else {
         setCurrentPlayer(Seed.CROSS);
         // currentPlayer = Seed.CROSS;  // Computer plays first
      }

      board.init();  // clear board to be empty
      currentState = State.STARTING;  // ready to play
      repaint();  // paint the initial board

      //  Computer plays first by choosing the first move
      if(currentState == State.STARTING) {
         if(currentPlayer == Seed.CROSS) {
            // create an AI player with the current board and difficult level
            AIPlayerMinimaxCutoff aiMaxPlayer = new AIPlayerMinimaxCutoff(board, difficultLevel);
            // AIPlayerMinimaxCutoffBetter aiMaxPlayer = new AIPlayerMinimaxCutoffBetter(board, difficultLevel);
            // compute the best move by Alpha-beta Alg with cutoff huristic evaluation function
            int[] move = aiMaxPlayer.move(); 
            board.getCell(move[0], move[1]).setContent(currentPlayer); // move
            // repaint();  // Call-back paintComponent()
            updateGame(currentPlayer, move[0], move[1]); // update currentState
            // Switch player
            currentPlayer = Seed.NOUGHT;
            currentState = State.PLAYING;
         }else {
            currentState = State.PLAYING;
         }
      }
   }

   /** Update the current State after the player with "theSeed" has placed on (row, col) */
   public void updateGame(Seed theSeed, int row, int col) {
      repaint();  // Call-back paintComponent().
      if (board.hasWon(theSeed, row, col)) {  // check for win
         if(theSeed == Seed.CROSS) {
            currentState = State.CROSS_WON;
            if (JOptionPane.showConfirmDialog(null, "Opps, Computer won! Do you wanna play again?", "Game Over",
             JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
              initGame();
            }
         }else {
            currentState = State.NOUGHT_WON;
         if (JOptionPane.showConfirmDialog(null, "Congratilations, you won! Do you wanna play again?", "Game Over",
               JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            initGame();
            }
         }
      } else if (board.isDraw()) {  // check for draw
         currentState = State.DRAW;
         if (JOptionPane.showConfirmDialog(null, "It's a draw! Do you wanna play again?", "Game Over",
          JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
           initGame();
         }
      }
      //  else {
      //       currentState = State.PLAYING;
      // }
      // Otherwise, no change to current state (PLAYING).   
   }

/** Custom painting codes on this JPanel */
   @Override
   public void paintComponent(Graphics g) {  // invoke via repaint()
      super.paintComponent(g);    // fill background
      setBackground(Color.WHITE); // set its background color

      board.paint(g);  // ask the game board to paint itself

      if (currentState == State.PLAYING) {
         statusBar.setForeground(Color.BLACK);
         if (currentPlayer == Seed.CROSS) {
            statusBar.setText("X's Turn");
         } else {
            statusBar.setText("O's Turn");
         }
      } else if (currentState == State.DRAW) {
         statusBar.setForeground(Color.RED);
         statusBar.setText("It's a Draw! Click to play again.");
      } else if (currentState == State.CROSS_WON) {
         statusBar.setForeground(Color.RED);
         statusBar.setText("'X' Won! Click to play again.");
      } else if (currentState == State.NOUGHT_WON) {
         statusBar.setForeground(Color.RED);
         statusBar.setText("'O' Won! Click to play again.");
      }
   }

   /** The entry "main" method */
   public static void main(String[] args) {
      // Run GUI construction codes in Event-Dispatching thread for thread safety
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            JFrame frame = new JFrame(TITLE);
            // Set the content-pane of the JFrame to an instance of main JPanel
            frame.setContentPane(new GameMain());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null); // center the application window
            frame.setVisible(true);            // show it
         }
      });
   }
}