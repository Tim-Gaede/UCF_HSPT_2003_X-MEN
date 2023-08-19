/****************************************************************************
 *          17th Annual UCF High School Programming Tournament              *
 *                              May 2, 2003                                 *
 *                     University of Central Florida                        *
 *                                                                          *
 *                                                                          *
 * Special thanks to our sponsors: UPE, SAIC, ACM, and HARRIS               *
 *                                                                          *
 *                                                                          *
 * Problem:         Xmen                                                    *
 * Problem Author:  Jason                                                   *
 * Solution Author: Phil                                                    *
 * Data Author:     Mike W                                                  *
 ***************************************************************************/
import java.io.*;
import java.util.*;

/*The basic idea here is that we'll start out with a board with only enemies
  Since the mutants can move anywhere, we don't care what their starting locations are.
  Then we'll try to place Wolverine on every unoccupied square, see what the best is,
  then do the same with Cyclops.
  
  The only tricky part is computing the score on the edges of the board - lots of
  nasty ifs*/

//Global Variables.  They're terrible in real code, but fair game in a contest
//The board will have one of three values in each square:
//  0: empty square
//  1: enemy present ('X' in the input)
//  2: Wolverine (after his final place is determined)
//I don't bother to record Cyc's position, since it doesn't matter for score computation
//(Wolverine doesn't count as an enemy for scoring Cyclops, nor does he count as an empty space)

//One more thing - I managed to flip x and y on the board.  Throughout, the first coordinate
//referenced in the board will be the y coordinate - ranging from 0 to height
public class xmen {

  static int[][] board = new int[55][55];
  static int width, height;

  //This function returns the number of kills Wolverine will get in a given position
  public static int ScoreWolverine(int y, int x)
  {
    int score = 0;
    if (y > 0)
    {
      if (x > 0)
        score += (board[y-1][x-1] == 1) ? 1:0;
      score += (board[y-1][x] == 1)? 1:0;
      if (x < width-1)
        score += (board[y-1][x+1] == 1)? 1:0;
    }
    if (y < height-1)
    {
      if (x > 0)
        score += (board[y+1][x-1] == 1)? 1:0;
      score += (board[y+1][x] == 1)? 1:0;
      if (x < width-1)
        score += (board[y+1][x+1] == 1)? 1:0;
    }
    if (x > 0)
      score += (board[y][x-1] == 1)? 1:0;
    if (x < width-1)
      score += (board[y][x+1] == 1)? 1:0;
    return score;
  }

  //This function gives Cyclops' kills in a given position - note that he has 8 choices
  //for which way to face.  We'll test them all an return the best
  public static int ScoreCyclops(int y, int x)
  {
    //This function uses the power of C for loops.  You could do the same thing with a simpler 
    //for loop (like you'd see in Pascal or BASIC), it would just take more room.
    int bestScore = 0;
    int score, tx, ty;
    //Up
    for (tx=x, ty=y-1, score=0;ty >= 0;ty--)
      score += (board[ty][tx] == 1)? 1:0;
    if (score > bestScore)
      bestScore = score;

    //Down
    for (tx=x, ty=y+1, score=0;ty < height;ty++)
      score += (board[ty][tx] == 1)? 1:0;
    if (score > bestScore)
      bestScore = score;

    //Left
    for (tx=x-1, ty=y, score=0;tx >= 0;tx--)
      score += (board[ty][tx] == 1)? 1:0;
    if (score > bestScore)
      bestScore = score;

    //Right
    for (tx=x+1, ty=y, score=0;tx < width;tx++)
      score += (board[ty][tx] == 1)? 1:0;
    if (score > bestScore)
      bestScore = score;

    //Up-Left
    for (tx=x-1, ty=y-1, score=0;(ty >= 0) && (tx >= 0);ty--, tx--)
      score += (board[ty][tx] == 1)? 1:0;
    if (score > bestScore)
      bestScore = score;

    //Up-Right
    for (tx=x+1, ty=y-1, score=0;(ty >= 0) && (tx < width);ty--, tx++)
      score += (board[ty][tx] == 1)? 1:0;
    if (score > bestScore)
      bestScore = score;

    //Down-Left
    for (tx=x-1, ty=y+1, score=0;(ty < height) && (tx >= 0);ty++, tx--)
      score += (board[ty][tx] == 1)? 1:0;
    if (score > bestScore)
      bestScore = score;

    //Down-Right
    for (tx=x+1, ty=y+1, score=0;(ty < height) && (tx < width);ty++, tx++)
      score += (board[ty][tx] == 1)? 1:0;
    if (score > bestScore)
      bestScore = score;

    return bestScore;
  }

  //This function iterates through the empty squares and keeps track of Wolverine's
  //best spot.  Once it's tested all of the squares, it moves Wolverine to that best spot
  //so that he's in the way for Cyclops
  public static int PlaceAndScoreWolverine()
  {
    int bestScore = -1;
    int bestLocX = -1, bestLocY = -1;
    for (int i=0;i<height;i++)
      for (int j=0;j<width;j++)
        if (board[i][j] == 0)
        {
          int score = ScoreWolverine(i, j);
          if (score > bestScore)
          {
            bestScore = score;
            bestLocX = j;
            bestLocY = i;
          }
        }
    board[bestLocY][bestLocX] = 2;
    return bestScore;
  }

  //This function does the same thing for Cyclops.  In fact, it even moves him at the end, even though
  //that's not necessary.  Witness the power of cut&paste!
  public static int PlaceAndScoreCyclops()
  {
    int bestScore = -1;
    int bestLocX = -1, bestLocY = -1;
    for (int i=0;i<height;i++)
      for (int j=0;j<width;j++)
        if (board[i][j] == 0)
        {
          int score = ScoreCyclops(i, j);
          if (score > bestScore)
          {
            bestScore = score;
            bestLocX = j;
            bestLocY = i;
          }
        }
    board[bestLocY][bestLocX] = 2;
    return bestScore;
  }

  //The main function reads the input, calls the worker functions, and prints the output
  public static void main (String[] argv) throws IOException
  {
    int nCombats;
    String line;
    StringTokenizer stk;
    BufferedReader in = new BufferedReader(new FileReader("xmen.in"));
    nCombats = Integer.parseInt(in.readLine().trim());
		
    for (int n=1;n<=nCombats;n++)
    {
      stk = new StringTokenizer(in.readLine(), " ");
			
      width = Integer.parseInt(stk.nextToken());
      height = Integer.parseInt(stk.nextToken());
      for (int i=0;i<height;i++)
      {
        line = in.readLine();
        for(int j=0;j<width;j++)
          board[i][j] = (line.charAt(j) == 'X') ? 1 : 0;
      }
      int nWolverineKills = PlaceAndScoreWolverine();
      int nCyclopsKills   = PlaceAndScoreCyclops();
      System.out.println("Combat " + n + ":" );
      //The order of these ifs is important - the spec says that in case of tie, Wolverine should be
      //printed first.  Since the "Cyclops Wins" case is tested first, ties and wolverine wins are
      //treated the same way
      if (nCyclopsKills > nWolverineKills)
      {
        System.out.println("  Cyclops:   " + nCyclopsKills );
        System.out.println("  Wolverine: " + nWolverineKills );
      }
      else
      {
        System.out.println("  Wolverine: " + nWolverineKills );
        System.out.println("  Cyclops:   " + nCyclopsKills );
      }
      System.out.println();
    }
  }

}
