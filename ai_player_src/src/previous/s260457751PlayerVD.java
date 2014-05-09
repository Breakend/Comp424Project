package previous;

import halma.CCBoard;
import halma.CCMove;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import sXXXXXXXXX.mytools.MoveWrapper;
import sXXXXXXXXX.mytools.MyFinalTools;
import boardgame.Board;
import boardgame.Move;
import boardgame.Player;

/**
 * Player with diagonal penalty
 */
public class sXXXXXXXXXPlayerVD extends Player {
    Random rand = new Random();
    
    /** Provide a default public constructor */
    public sXXXXXXXXXPlayerVD() { super("LDFS"); }
    public sXXXXXXXXXPlayerVD(String s) { super(s); }
    
    public Board createBoard() { return new CCBoard(); }
    
    boolean haveHopSequence = false;
    
    ArrayList<CCMove> hopSequence;
    int current = 0;
    
    /** Implement a very stupid way of picking moves */
    public Move chooseMove(Board theboard) 
    {
    	
        // Cast the arguments to the objects we want to work with
        CCBoard board = (CCBoard) theboard;
        
//        System.out.println("Turn Count: " + board.getTurnsPlayed());
        CCMove mov = null;
        if(haveHopSequence && hopSequence != null && current < hopSequence.size()){
        	mov = hopSequence.get(current++);
        }
        else{
        	haveHopSequence = false;
        	current = 0;
        }
        
        MoveWrapper m = null;

        // Use my tool for nothing
        if(mov != null && board.isLegal(mov))
        	return mov;
        else{
        	m = MyFinalTools.getBestMoveToDepth(board, playerID, 1);
        }
        	//        MoveScore ms = MyTools.alphaBeta(board, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, true, this.playerID );
        
        if(m.isHopSequence || ( m.moves.size() > 1 && m.moves.get(1).isHop())){
        	haveHopSequence = true;
        	hopSequence = m.moves;
        	current = 1;
        }
        else{
        	haveHopSequence =false;
        }
        // Return a randomly selected move.
//        return ms.move;
        return m.moves.get(0);
    }
    
}
