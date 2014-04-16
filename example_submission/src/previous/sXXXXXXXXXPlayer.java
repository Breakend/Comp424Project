package previous;

import halma.CCBoard;
import halma.CCMove;

import java.util.ArrayList;
import java.util.Random;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;

/**
 * Template
 */
public class sXXXXXXXXXPlayer extends Player {
    Random rand = new Random();
    
    /** Provide a default public constructor */
    public sXXXXXXXXXPlayer() { super("XXXXXXXXX"); }
    public sXXXXXXXXXPlayer(String s) { super(s); }
    
    public Board createBoard() { return new CCBoard(); }

    /** Implement a very stupid way of picking moves */
    public Move chooseMove(Board theboard) 
    {
        // Cast the arguments to the objects we want to work with
        CCBoard board = (CCBoard) theboard;

        // Use my tool for nothing
//        MoveScore ms = MyTools.alphaBeta(board, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, true, this.playerID );
        // Return a randomly selected move.
//        return ms.move;
        return null;
    }
    
}
