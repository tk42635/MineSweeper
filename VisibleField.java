// Name: De Huo
// USC NetID: dehuo
// CS 455 PA3
// Fall 2019


/**
  VisibleField class
  This is the data that's being displayed at any one point in the game (i.e., visible field, because it's what the
  user can see about the minefield), Client can call getStatus(row, col) for any square.
  It actually has data about the whole current state of the game, including  
  the underlying mine field (getMineField()).  Other accessors related to game status: numMinesLeft(), isGameOver().
  It also has mutators related to actions the player could do (resetGameDisplay(), cycleGuess(), uncover()),
  and changes the game state accordingly.
  
  It, along with the MineField (accessible in mineField instance variable), forms
  the Model for the game application, whereas GameBoardPanel is the View and Controller, in the MVC design pattern.
  It contains the MineField that it's partially displaying.  That MineField can be accessed (or modified) from 
  outside this class via the getMineField accessor.  
 */
public class VisibleField {
   // ----------------------------------------------------------   
   // The following public constants (plus numbers mentioned in comments below) are the possible states of one
   // location (a "square") in the visible field (all are values that can be returned by public method 
   // getStatus(row, col)).
   
   // Covered states (all negative values):
   public static final int COVERED = -1;   // initial value of all squares
   public static final int MINE_GUESS = -2;
   public static final int QUESTION = -3;

   // Uncovered states (all non-negative values):
   
   // values in the range [0,8] corresponds to number of mines adjacent to this square
   
   public static final int MINE = 9;      // this loc is a mine that hasn't been guessed already (end of losing game)
   public static final int INCORRECT_GUESS = 10;  // is displayed a specific way at the end of losing game
   public static final int EXPLODED_MINE = 11;   // the one you uncovered by mistake (that caused you to lose)
   // ----------------------------------------------------------   
  
   // <put instance variables here>
   private MineField mineField;
   private int[][] status; // A field to store every square's state (from -3 to 11)
   private int numGuess; 
   private boolean isExploded; // Indicates whether the player has clicked on a mine

   /**
      Create a visible field that has the given underlying mineField.
      The initial state will have all the mines covered up, no mines guessed, and the game
      not over.
      @param mineField  the minefield to use for for this VisibleField
    */
   public VisibleField(MineField mineField) {
      this.mineField = mineField;
      status = new int[mineField.numRows()][mineField.numCols()];
      for(int i = 0; i < mineField.numRows(); i++)
         for(int j = 0; j < mineField.numCols(); j++)
            status[i][j] = COVERED;
      numGuess = 0;
      isExploded = false;
   }
   
   
   /**
      Reset the object to its initial state (see constructor comments), using the same underlying
      MineField. 
   */     
   public void resetGameDisplay() {
      numGuess = 0;
      isExploded = false;
      for(int i = 0; i < mineField.numRows(); i++)
         for(int j = 0; j < mineField.numCols(); j++)
            status[i][j] = COVERED;
   }
  
   
   /**
      Returns a reference to the mineField that this VisibleField "covers"
      @return the minefield
    */
   public MineField getMineField() {
      return mineField;       // DUMMY CODE so skeleton compiles
   }
   
   
   /**
      Returns the visible status of the square indicated.
      @param row  row of the square
      @param col  col of the square
      @return the status of the square at location (row, col).  See the public constants at the beginning of the class
      for the possible values that may be returned, and their meanings.
      PRE: getMineField().inRange(row, col)
    */
   public int getStatus(int row, int col) {
      return status[row][col];       // DUMMY CODE so skeleton compiles
   }

   
   /**
      Returns the the number of mines left to guess.  This has nothing to do with whether the mines guessed are correct
      or not.  Just gives the user an indication of how many more mines the user might want to guess.  This value can
      be negative, if they have guessed more than the number of mines in the minefield.     
      @return the number of mines left to guess.
    */
   public int numMinesLeft() {
      int numMinesLeft = getMineField().numMines() - numGuess;
      if (numMinesLeft < 0)
         return 0;
      else
         return numMinesLeft;       // DUMMY CODE so skeleton compiles

   }
 
   
   /**
      Cycles through covered states for a square, updating number of guesses as necessary.  Call on a COVERED square
      changes its status to MINE_GUESS; call on a MINE_GUESS square changes it to QUESTION;  call on a QUESTION square
      changes it to COVERED again; call on an uncovered square has no effect.  
      @param row  row of the square
      @param col  col of the square
      PRE: getMineField().inRange(row, col)
    */
   public void cycleGuess(int row, int col) {
      if (status[row][col] == COVERED)
      {
         status[row][col] = MINE_GUESS;
         numGuess++;
      }
      else if (status[row][col] == MINE_GUESS)
      {
         status[row][col] = QUESTION;
         numGuess--;
      }
      else if (status[row][col] == QUESTION)
         status[row][col] = COVERED;
      
   }

   
   /**
      Uncovers this square and returns false iff you uncover a mine here.
      If the square wasn't a mine or adjacent to a mine it also uncovers all the squares in 
      the neighboring area that are also not next to any mines, possibly uncovering a large region.
      Any mine-adjacent squares you reach will also be uncovered, and form 
      (possibly along with parts of the edge of the whole field) the boundary of this region.
      Does not uncover, or keep searching through, squares that have the status MINE_GUESS. 
      Note: this action may cause the game to end: either in a win (opened all the non-mine squares)
      or a loss (opened a mine).
      @param row  of the square
      @param col  of the square
      @return false   iff you uncover a mine at (row, col)
      PRE: getMineField().inRange(row, col)
    */
   public boolean uncover(int row, int col) {
      if (mineField.hasMine(row, col) == true)
      {
         isExploded = true;
         updateExplosion(row, col);
         return false;
      }
      else
      {
         recursiveUncover(row, col);
         return true;   
      }
   
   }
   
   /**
    * Helper method to do recursive call when the play click on a square with no mines adjacent to it.
    * @param row of the square
    * @param col of the square
    */
   private void recursiveUncover(int row, int col) {
      if (!getMineField().inRange(row, col) || status[row][col] >= 0 || status[row][col] == -2)
         return;
         
      status[row][col] = mineField.numAdjacentMines(row, col);
      if (status[row][col] == 0) {
         for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++)
               if (i != 0 || j != 0)
                  recursiveUncover(row + i, col + j);

      }
   }
   
   /**
    * Helper method to update status[][] after player has clicked on a mine.
    * @param row of the square
    * @param col of the square
    */
   private void updateExplosion(int row, int col) {
      status[row][col] = EXPLODED_MINE;
      for(int i = 0; i < mineField.numRows(); i++)
         for(int j = 0; j < mineField.numCols(); j++)
         {
            if (status[i][j] == COVERED && mineField.hasMine(i,j) == true)
               status[i][j] = MINE;
            if (status[i][j] == MINE_GUESS && mineField.hasMine(i,j) == false)
               status[i][j] = INCORRECT_GUESS;
         }
            
   }
 
   
   /**
      Returns whether the game is over.
      (Note: This is not a mutator.)
      @return whether game over
    */
   public boolean isGameOver() {
      if (isExploded == true)
         return true;
         
      for (int i = 0; i < getMineField().numRows(); i++)
         for (int j = 0; j < getMineField().numCols(); j++)
            if (status[i][j] == COVERED && getMineField().hasMine(i, j) == false)
               return false;

      return true;       // DUMMY CODE so skeleton compiles
   }
 
   
   /**
      Returns whether this square has been uncovered.  (i.e., is in any one of the uncovered states, 
      vs. any one of the covered states).
      @param row of the square
      @param col of the square
      @return whether the square is uncovered
      PRE: getMineField().inRange(row, col)
    */
   public boolean isUncovered(int row, int col) {
      if (status[row][col] >= 0)
         return true;
      else
         return false;       // DUMMY CODE so skeleton compiles
   }
   
 
   // <put private methods here>
   
}
