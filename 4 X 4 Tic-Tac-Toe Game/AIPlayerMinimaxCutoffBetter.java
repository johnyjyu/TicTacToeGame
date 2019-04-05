package aiTicTacToeGame;

import java.util.*;
/** Computer player i.e. AIPlayer using Minimax with cutoff and eval function algorithm
 * Eval(s) = 100x_3 + 10x_2 + x_1 -(100o_3 + 10o_2 + o_1)
 */
public class AIPlayerMinimaxCutoffBetter extends AbstractAIPlayer {
	private boolean cutoffOccurred = false;
	private int maxDepth = 0;
	private int totalNumNodes = 0;
	private int numPrunMax = 0;
	private int numPrunMin = 0;
   private int HIGHEST_DIFFICULT_LEVEL = 9; // chosen to make sure max player choose move in 10s
   private int depthLimit; // depth searched by computer
 
   /** Constructor with the given game board */
   public AIPlayerMinimaxCutoffBetter(Board board, int difficultLevel) {
      super(board);
      int depthLimit = HIGHEST_DIFFICULT_LEVEL-(8-4*difficultLevel);
      this.depthLimit = depthLimit;
      setSeed(Seed.CROSS);
   }
 
   /** Get next best move for computer. Return int[2] of {row, col} */
   @Override
   int[] move() {
   	long startTime = System.nanoTime();
      // int depthLimit = ;

      int[] result = minimax(depthLimit, depthLimit, mySeed, -1000, 1000); // depth, max turn, alpha, beta
      long endTime = System.nanoTime();
      long duration = (endTime - startTime)/100000000;  //divide by 1000000 00 to get 100 milliseconds.

      // output info on screen
      // 1, whether cutoff occurred
      if(cutoffOccurred){
      	System.out.println("Cut off occurred!");
      }else {
      	System.out.println("Cut off didn't occur!");
      }
      // 2, maximun depth reached
      System.out.printf("Maximun depth reached is: %d\n", maxDepth);
      // 3, total # of nodes generated (including root node)
      System.out.printf("Total number of nodes generated is: %d\n", totalNumNodes);

      // 4, # of times pruning occurred within the MAX_VALUE function
      System.out.printf("Number of times pruning occurred within the MAX_VALUE function is: %d\n", numPrunMax);

      // 5, # of times pruning occurred within the MIN_VALUE function
      System.out.printf("Number of times pruning occurred within the MIN_VALUE function is: %d\n", numPrunMin);


      return new int[] {result[1], result[2]};   // row, col
   }
 
   /** Recursive minimax at level of depth for either maximizing or minimizing player with alpha-beta cut-off.
       Return int[3] of {score, row, col}  */
   private int[] minimax(int depthLimit, int depth, Seed player, int alpha, int beta) {
      // Generate possible next  moves in a List of int[2] of {row, col}.
      List<int[]> nextMoves = generateMoves();
 
      int score;
      int bestRow = -1;
      int bestCol = -1;

 
      maxDepth = maxDepth > (depthLimit - depth) ? maxDepth : depthLimit - depth;
      totalNumNodes += 1;

      if (nextMoves.isEmpty() || depth == 0) {
         // Gameover or depth reached, evaluate score
      	if(!nextMoves.isEmpty()){
      		cutoffOccurred = true;
      	}
      	 score = evaluate();
      	 // System.out.println(score);
         return new int[] {score, bestRow, bestCol};
      } else {
         for (int[] move : nextMoves) {
            // Try this move for the current "player"
            cells[move[0]][move[1]].setContent(player);
            if (player == mySeed) {  // mySeed (computer) is maximizing player
               score = minimax(depthLimit, depth - 1, oppSeed, alpha, beta)[0];
               if (score > alpha) {
                  alpha = score;
                  bestRow = move[0];
                  bestCol = move[1];
               }
            } else {  // oppSeed is minimizing player
               score = minimax(depthLimit, depth - 1, mySeed, alpha, beta)[0];
               if (score < beta) {
                  beta = score;
                  bestRow = move[0];
                  bestCol = move[1];
               }
            }
            // Undo move
            cells[move[0]][move[1]].setContent(Seed.EMPTY);
            // cut off
            if(alpha >= beta) {
            	if(player == mySeed) {
            		numPrunMax += 1;
            	}else {
            		numPrunMin += 1;
            	}
            	break;
            }
         }
      }

      return new int[] {(player == mySeed) ? alpha : beta, bestRow, bestCol};
   }
 
   /** Find all valid next moves.
       Return List of moves in int[2] of {row, col} or empty list if gameover */
   private List<int[]> generateMoves() {
      List<int[]> nextMoves = new ArrayList<int[]>(); // allocate List
      // If gameover, i.e., no next move
      if (hasWon(mySeed) || hasWon(oppSeed)) {
         return nextMoves;   // return empty list
      }
      // Search for empty cells and add to the List
      for (int row = 0; row < ROWS; ++row) {
         for (int col = 0; col < COLS; ++col) {
            if (cells[row][col].getContent() == Seed.EMPTY) {
               nextMoves.add(new int[] {row, col});
            }
         }
      }
      return nextMoves;
   }
 
   /** The heuristic evaluation function for the current board2
   		where x_n is number of rows, colums, diagoals with exactly n Xs and no Os
   			  O_n ...                                                O....     Xs


       @Return +1000, +100, +10, +1 for EACH 4-, 3-, 2-, 1-in-a-line for computer and non-in-the-same-line for oppoent.
               -1000, -100, -10, -1 for EACH 4-, 3-, 2-, 1-in-a-line for opponent and non-in-the-same-line for computer.
               0 otherwise   */
   private int evaluate() {
      int score = 0;
      // Evaluate score for each of the 10 lines (4 rows, 4 columns, 2 diagonals)
      score += evaluateLine(0, 0, 0, 1, 0, 2, 0, 3);  // row 0
      score += evaluateLine(1, 0, 1, 1, 1, 2, 1, 3);  // row 1
      score += evaluateLine(2, 0, 2, 1, 2, 2, 2, 3);  // row 2
      score += evaluateLine(3, 0, 3, 1, 3, 2, 3, 3);  // row 3
      score += evaluateLine(0, 0, 1, 0, 2, 0, 3, 0);  // col 0
      score += evaluateLine(0, 1, 1, 1, 2, 1, 3, 1);  // col 1
      score += evaluateLine(0, 2, 1, 2, 2, 2, 3, 2);  // col 2
      score += evaluateLine(0, 3, 1, 3, 2, 3, 3, 3);  // col 2
      score += evaluateLine(0, 0, 1, 1, 2, 2, 3, 3);  // diagonal
      score += evaluateLine(0, 3, 1, 2, 2, 1, 3, 0);  // alternate diagonal
      return score;
   }
 
   /** The heuristic evaluation function for the given line of 4 cells
       @Return +1000, +6, +3, +1 for 4-, 3-, 2-, 1-in-a-line for computer.
               -1000, -6, -3, -1 for 4-, 3-, 2-, 1-in-a-line for opponent.
               0 otherwise */
   private int evaluateLine(int row1, int col1, int row2, int col2, int row3, int col3, int row4, int col4) {
      int score = 0;
 
      // First cell
      if (cells[row1][col1].getContent() == mySeed) {
         score = 1;
      } else if (cells[row1][col1].getContent() == oppSeed) {
         score = -1;
      }

      // Second cell
      if (cells[row2][col2].getContent() == mySeed) {
         if (score == 1) {   // cell1 is mySeed
            score = 10;
         } else if (score == -1) {  // cell1 is oppSeed
            return 0;
         } else {  // cell1 is empty
            score = 1;
         }
      } else if (cells[row2][col2].getContent() == oppSeed) {
         if (score == -1) { // cell1 is oppSeed
            score = -10;
         } else if (score == 1) { // cell1 is mySeed
            return 0;
         } else {  // cell1 is empty
            score = -1;
         }
      }
      // System.out.printf("cell1 %d ", score);
      
 
      // Third cell
      if (cells[row3][col3].getContent() == mySeed) {
         if (score > 0) {  // cell1 and/or cell2 is mySeed
            if(score == 1) {
            	score = 10;
            }else if(score == 10) {
            	score = 100;
            }
         } else if (score < 0) {  // cell1 and/or cell2 is oppSeed
            return 0;
         } else {  // cell1 and cell2 are empty
            score = 1;
         }
      } else if (cells[row3][col3].getContent() == oppSeed) {
         if (score < 0) {  // cell1 and/or cell2 is oppSeed
            if(score == -1) {
            	score = -10;
            }else if(score == -10){
            	score = -100;
            }
         } else if (score > 1) {  // cell1 and/or cell2 is mySeed
            return 0;
         } else {  // cell1 and cell2 are empty
            score = -1;
         }
      }
      // System.out.printf("cell1 %d ", score);
      

      // Fourth cell
      if (cells[row4][col4].getContent() == mySeed) {
         if (score > 0) {  // cell1 and/or cell2 and/or cell3 is mySeed
            if(score == 1) {
            	score = 10;
            }else if(score == 10) {
            	score = 100;
            }else if(score == 100) {
            	score = 1000;  // terminal node X wins utility: +1000
            }
         } else if (score < 0) {  // cell1 and/or cell2 and/or cell3 is oppSeed
            return 0;
         } else {  // cell1 and cell2 are empty
            score = 1;
         }
      } else if (cells[row3][col3].getContent() == oppSeed) {
         if (score < 0) {  // cell1 and/or cell2 and/or cell3 is oppSeed
            if(score == -1) {
            	score = -10;
            }else if(score == -10){
            	score = -100;
            }else if(score == -100){
            	score = -1000;  // terminal node O wins utility: -1000
            }
         } else if (score > 1) {  // cell1 and/or cell2 and/or cell3 is mySeed
            return 0;
         } else {  // cell1 and cell2 are empty
            score = -1;
         }
      }
      // System.out.printf("cell1 %d ", score);
      

      return score;
   }
 
   private int[] winningPatterns = {
         0b1111000000000000, 0b0000111100000000, 0b0000000011110000, 0b0000000000001111, // rows
         0b1000010000101000, 0b0100010001000100, 0b0010001000100010, 0b0001000100010001,// cols
         0b1000010000101000, 0b0001001001001000               // diagonals
   };
 
   /** Returns true if thePlayer wins */
   private boolean hasWon(Seed thePlayer) {
      int pattern = 0b000000000;  // 9-bit pattern for the 9 cells
      for (int row = 0; row < ROWS; ++row) {
         for (int col = 0; col < COLS; ++col) {
            if (cells[row][col].getContent() == thePlayer) {
               pattern |= (1 << (row * COLS + col));
            }
         }
      }
      for (int winningPattern : winningPatterns) {
         if ((pattern & winningPattern) == winningPattern) return true;
      }
      return false;
   }
}