package previous;

import halma.CCBoard;
import halma.CCMove;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import s260457751.mytools.MoveWrapper;
import boardgame.Board;
import boardgame.Move;
import boardgame.Player;

/**
 * Player with no diagonal penalty
 */
public class s260457751PlayerV extends Player {
    Random rand = new Random();
    
    /** Provide a default public constructor */
    public s260457751PlayerV() { super("LDFS"); }
    public s260457751PlayerV(String s) { super(s); }
    
    public Board createBoard() { return new CCBoard(); }
    
    boolean haveHopSequence = false;
    
    ArrayList<CCMove> hopSequence;
    int current = 0;
    
    public Move chooseMove(Board theboard) 
    {
    	
        // Cast the arguments to the objects we want to work with
        CCBoard board = (CCBoard) theboard;
        
        System.out.println("Turn Count: " + board.getTurnsPlayed());
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
        	m = IterativeDepthTools.getBestMoveToDepthNoD(board, playerID, 1);
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
