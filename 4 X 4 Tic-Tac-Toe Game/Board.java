package aiTicTacToeGame;

import java.awt.*;
import java.util.Arrays;
/**
 * The Board class models the ROWS-by-COLS game board.
 */
public class Board {
   private final Cell[][] cells; // composes of 2D array of ROWS-by-COLS Cell instances
 
   /** Constructor to initialize the game board with empty cells*/
   public Board() {
      cells = new Cell[GameMain.ROWS][GameMain.COLS]; // allocate the 2D array
      for (int row = 0; row < GameMain.ROWS; ++row) {
         for (int col = 0; col < GameMain.COLS; ++col) {
            cells[row][col] = new Cell(row, col); // allocate element of array i.e. empty cell
         }
      }
   }

   // public getter and setter methods
   public Cell getCell(int i, int j){
     return cells[i][j];
   }
   public Cell[][] getCells(){
     return cells;
   }
 
   /** Initialize (or re-initialize) the game board */
   public void init() {
      for (int row = 0; row < GameMain.ROWS; ++row) {
         for (int col = 0; col < GameMain.COLS; ++col) {
            cells[row][col].setContent(Seed.EMPTY); // clear the cell content
         }
      }
   }
 
   /** Return true if it is a draw (hasWon is false and no more EMPTY cell) 
   * need to check hasWon first! */
   public boolean isDraw() {
      for (int row = 0; row < GameMain.ROWS; ++row) {
         for (int col = 0; col < GameMain.COLS; ++col) {
            if (cells[row][col].getContent() == Seed.EMPTY) {
               return false; // an empty seed found, not a draw, exit
            }
         }
      }
      return true; // no empty cell, it's a draw
   }
 
   /** Return true if the player with "seed" has won after placing at
       (seedRow, seedCol) */
   public boolean hasWon(Seed seed, int seedRow, int seedCol) {
    
      // debug as follows: 
      // for(int i = 0; i < cells.length; i++){
      //   for(int j = 0; j < cells[0].length; j++){
      //     if(cells[i][j].content == Seed.NOUGHT){
      //       System.out.printf("%d", 1);
      //     }else if(cells[i][j].content == Seed.CROSS){
      //       System.out.printf("%d", 0);
      //     }else{
      //       System.out.printf("%d", 2);
      //     }
      //   }
      //   System.out.println("");
      // }
      // System.out.println("");
      // System.out.println(seedRow);
      // System.out.println(seedCol);
        
      // if(seed == Seed.NOUGHT) {
      //    System.out.println("user");
      // }
      // System.out.println(cells[seedRow][3].content == seed);

      return (cells[seedRow][0].getContent() == seed   // 4-in-the-row
                 && cells[seedRow][1].getContent() == seed
                 && cells[seedRow][2].getContent() == seed
                 && cells[seedRow][3].getContent() == seed
             || cells[0][seedCol].getContent() == seed // 4-in-the-column
                 && cells[1][seedCol].getContent() == seed
                 && cells[2][seedCol].getContent() == seed
                 && cells[3][seedCol].getContent() == seed
             || seedRow == seedCol              // 4-in-the-diagonal
                 && cells[0][0].getContent() == seed
                 && cells[1][1].getContent() == seed
                 && cells[2][2].getContent() == seed
                 && cells[3][3].getContent() == seed
             || seedRow + seedCol == 3          // 4-in-the-opposite-diagonal
                 && cells[0][3].getContent() == seed
                 && cells[1][2].getContent() == seed
                 && cells[2][1].getContent() == seed
                 && cells[3][0].getContent() == seed);
   }
 
   /** Paint itself on the graphics canvas, given the Graphics context */
   public void paint(Graphics g) {
      // Draw the grid-lines
      g.setColor(Color.GRAY);
      for (int row = 1; row < GameMain.ROWS; ++row) {
        /*fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
          Fills the specified rounded corner rectangle with the current color. 
          The left and right edges of the rectangle are at x and x + width - 1, respectively. 
          The top and bottom edges of the rectangle are at y and y + height - 1.
        */
         g.fillRoundRect(0, GameMain.CELL_SIZE * row - GameMain.GRID_WIDHT_HALF,
               GameMain.CANVAS_WIDTH - 1, GameMain.GRID_WIDTH,
               GameMain.GRID_WIDTH, GameMain.GRID_WIDTH);
      }
      for (int col = 1; col < GameMain.COLS; ++col) {
         g.fillRoundRect(GameMain.CELL_SIZE * col - GameMain.GRID_WIDHT_HALF, 0,
               GameMain.GRID_WIDTH, GameMain.CANVAS_HEIGHT - 1,
               GameMain.GRID_WIDTH, GameMain.GRID_WIDTH);
      }
 
      // Draw all the cells
      for (int row = 0; row < GameMain.ROWS; ++row) {
         for (int col = 0; col < GameMain.COLS; ++col) {
            cells[row][col].paint(g);  // ask the cell to paint itself
         }
      }
   }
}
