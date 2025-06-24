import java.util.*;

public class AIPlayer {
   public enum Difficulty {
      EASY, HARD
   }

   protected int ROWS = Board.ROWS;
   protected int COLS = Board.COLS;
   protected Cell[][] cells;
   protected Seed mySeed;
   protected Seed oppSeed;
   private Difficulty difficulty;

   public AIPlayer(Board board, Difficulty difficulty) {
      this.cells = board.cells;
      this.difficulty = difficulty;
   }

   public void setSeed(Seed seed) {
      this.mySeed = seed;
      this.oppSeed = (mySeed == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
   }

   public int[] move() {
      if (difficulty == Difficulty.EASY) {
         return moveEasy();
      } else {
         return moveHard();
      }
   }

   private int[] moveEasy() {
      List<int[]> emptyCells = new ArrayList<>();
      for (int row = 0; row < ROWS; ++row) {
         for (int col = 0; col < COLS; ++col) {
            if (cells[row][col].content == Seed.NO_SEED) {
               emptyCells.add(new int[]{row, col});
            }
         }
      }

      if (!emptyCells.isEmpty()) {
         return emptyCells.get(new Random().nextInt(emptyCells.size()));
      } else {
         return new int[]{-1, -1};  // no move available
      }
   }

   private int[] moveHard() {
      int[] result = minimax(2, mySeed);
      return new int[]{result[1], result[2]}; // row, col
   }

   private int[] minimax(int depth, Seed player) {
      List<int[]> nextMoves = generateMoves();

      int bestScore = (player == mySeed) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
      int currentScore;
      int bestRow = -1;
      int bestCol = -1;

      if (nextMoves.isEmpty() || depth == 0) {
         bestScore = evaluate();
      } else {
         for (int[] move : nextMoves) {
            cells[move[0]][move[1]].content = player;
            if (player == mySeed) {
               currentScore = minimax(depth - 1, oppSeed)[0];
               if (currentScore > bestScore) {
                  bestScore = currentScore;
                  bestRow = move[0];
                  bestCol = move[1];
               }
            } else {
               currentScore = minimax(depth - 1, mySeed)[0];
               if (currentScore < bestScore) {
                  bestScore = currentScore;
                  bestRow = move[0];
                  bestCol = move[1];
               }
            }
            cells[move[0]][move[1]].content = Seed.NO_SEED;
         }
      }
      return new int[]{bestScore, bestRow, bestCol};
   }

   private List<int[]> generateMoves() {
      List<int[]> nextMoves = new ArrayList<>();
      if (hasWon(mySeed) || hasWon(oppSeed)) {
         return nextMoves;
      }

      for (int row = 0; row < ROWS; ++row) {
         for (int col = 0; col < COLS; ++col) {
            if (cells[row][col].content == Seed.NO_SEED) {
               nextMoves.add(new int[]{row, col});
            }
         }
      }
      return nextMoves;
   }

   private int evaluate() {
      int score = 0;
      score += evaluateLine(0, 0, 0, 1, 0, 2);  // row 0
      score += evaluateLine(1, 0, 1, 1, 1, 2);  // row 1
      score += evaluateLine(2, 0, 2, 1, 2, 2);  // row 2
      score += evaluateLine(0, 0, 1, 0, 2, 0);  // col 0
      score += evaluateLine(0, 1, 1, 1, 2, 1);  // col 1
      score += evaluateLine(0, 2, 1, 2, 2, 2);  // col 2
      score += evaluateLine(0, 0, 1, 1, 2, 2);  // diag
      score += evaluateLine(0, 2, 1, 1, 2, 0);  // alt diag
      return score;
   }

   private int evaluateLine(int row1, int col1, int row2, int col2, int row3, int col3) {
      int score = 0;

      if (cells[row1][col1].content == mySeed) {
         score = 1;
      } else if (cells[row1][col1].content == oppSeed) {
         score = -1;
      }

      if (cells[row2][col2].content == mySeed) {
         if (score == 1) {
            score = 10;
         } else if (score == -1) {
            return 0;
         } else {
            score = 1;
         }
      } else if (cells[row2][col2].content == oppSeed) {
         if (score == -1) {
            score = -10;
         } else if (score == 1) {
            return 0;
         } else {
            score = -1;
         }
      }

      if (cells[row3][col3].content == mySeed) {
         if (score > 0) {
            score *= 10;
         } else if (score < 0) {
            return 0;
         } else {
            score = 1;
         }
      } else if (cells[row3][col3].content == oppSeed) {
         if (score < 0) {
            score *= 10;
         } else if (score > 1) {
            return 0;
         } else {
            score = -1;
         }
      }
      return score;
   }

   private int[] winningPatterns = {
           0b111000000, 0b000111000, 0b000000111, // rows
           0b100100100, 0b010010010, 0b001001001, // cols
           0b100010001, 0b001010100               // diagonals
   };

   private boolean hasWon(Seed thePlayer) {
      int pattern = 0b000000000;
      for (int row = 0; row < ROWS; ++row) {
         for (int col = 0; col < COLS; ++col) {
            if (cells[row][col].content == thePlayer) {
               pattern |= (1 << (row * COLS + col));
            }
         }
      }
      for (int wp : winningPatterns) {
         if ((pattern & wp) == wp) return true;
      }
      return false;
   }
}