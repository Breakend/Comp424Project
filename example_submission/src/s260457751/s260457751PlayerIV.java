package s260457751;

import halma.CCBoard;
import halma.CCMove;

import java.util.ArrayList;
import java.util.Random;

import s260457751.mytools.DepthTools;
import s260457751.mytools.MoveScore;
import s260457751.mytools.MyTools;
import boardgame.Board;
import boardgame.Move;
import boardgame.Player;

/**
 *A random Halma player.
 */
public class s260457751PlayerIV extends Player {
    Random rand = new Random();
    
    /** Provide a default public constructor */
    public s260457751PlayerIV() { super("ABPruning"); }
    public s260457751PlayerIV(String s) { super(s); }
    
    public Board createBoard() { return new CCBoard(); }

    private CCMove lastMove;
    /** Implement a very stupid way of picking moves */
    public Move chooseMove(Board theboard) 
    {
        // Cast the arguments to the objects we want to work with
        CCBoard board = (CCBoard) theboard;

//        System.out.println);
//        long start = System.currentTimeMillis();
        System.out.println("Turn count: " + board.getTurnsPlayed());
        // Use my tool for nothing
//        if(lastMove != null) System.out.println("Last Move:" + lastMove.toPrettyString());
        MoveScore ms = DepthTools.iterativeAlphaBeta(new MoveScore(null, board, Integer.MIN_VALUE), 8, Integer.MIN_VALUE, Integer.MAX_VALUE, true, this.playerID, lastMove);
        // Return a randomly selected move.
//        return ms.move;

        if(ms.isHopDescendent){
        	lastMove = ms.origHop;
            System.out.println("Move! " + ms.origHop.toPrettyString());
            System.out.println("Score! " + ms.score);
            System.out.println("--------------------------------------------------------------");

        	return ms.origHop;
        }
        
        if(ms.move.getFrom() != null && ms.move.getTo() != null)
        	lastMove = ms.move;
        
//        System.out.println("Time to move: " + (System.currentTimeMillis() - start));
        System.out.println("Move! " + ms.move.toPrettyString());
        System.out.println("Score! " + ms.score);
        System.out.println("--------------------------------------------------------------");

        return ms.move;
    }
    
}
