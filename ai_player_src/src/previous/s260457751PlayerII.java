package previous;

import halma.CCBoard;
import halma.CCMove;

import java.util.ArrayList;
import java.util.Random;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;

/**
 * Attemping AB pruning
 */
public class sXXXXXXXXXPlayerII extends Player {
    Random rand = new Random();
    
    /** Provide a default public constructor */
    public sXXXXXXXXXPlayerII() { super("XXXXXXXXX"); }
    public sXXXXXXXXXPlayerII(String s) { super(s); }
    
    public Board createBoard() { return new CCBoard(); }

    private CCMove lastMove;
    /** Implement a very stupid way of picking moves */
    public Move chooseMove(Board theboard) 
    {
        // Cast the arguments to the objects we want to work with
        CCBoard board = (CCBoard) theboard;

        // Use my tool for nothing
//        CustomTimer.getInstance().set(800); //set to 600 millis
//        CustomTimer.getInstance().start();
        
        MoveScore ms = MyTools.alphaBeta(board, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, true, this.playerID );
        // Return a randomly selected move.
        System.out.println("Move! " + ms.move.toPrettyString());
        System.out.println("Score! " + ms.score);
        System.out.println("--------------------------------------------------------------");

        this.lastMove = ms.move;
        
        return ms.move;
    }
    
}
