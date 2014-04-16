package s260457751;

import halma.CCBoard;
import halma.CCMove;

import java.util.ArrayList;
import java.util.Random;


import s260457751.mytools.MoveWrapper;
import s260457751.mytools.MyFinalTools;
import boardgame.Board;
import boardgame.Move;
import boardgame.Player;

/**
 * Final player using a modified depth limited search
 */
public class s260457751Player extends Player {
    Random rand = new Random();
    
    /** Provide a default public constructor */
    public s260457751Player() { super("260457751"); }
    public s260457751Player(String s) { super(s); }
    
    public Board createBoard() { return new CCBoard(); }
    
    boolean haveHopSequence = false;
    
    //If found a hop sequence, stick with it, don't recompute to save time and prevent hops back and forth
    ArrayList<CCMove> hopSequence;
    int current = 0;

    /**
     * Basic choose move method
     */
    public Move chooseMove(Board theboard) 
    {
    	
        // Cast the arguments to the objects we want to work with
        CCBoard board = (CCBoard) theboard;
        
//        System.out.println("Turn Count: " + board.getTurnsPlayed());
        CCMove mov = null;
        
        //If have a previous hopsequence then use it, otherwise reset
        if(haveHopSequence && hopSequence != null && current < hopSequence.size()){
        	mov = hopSequence.get(current++);
        }
        else{
        	haveHopSequence = false;
        	current = 0;
        }
        
        MoveWrapper m = null;

        //if the mov is still legal (as in it has been stored from before) then use it
        //otherwise recalculate it
        if(mov != null && board.isLegal(mov))
        	return mov;
        else{
        	m = MyFinalTools.getBestMoveToDepth(board, playerID, 1);
        }
        
        //If it's a hop sequence store it to save time on calculations later
        if(m.isHopSequence || ( m.moves.size() > 1 && m.moves.get(1).isHop())){
        	haveHopSequence = true;
        	hopSequence = m.moves;
        	current = 1;
        }
        else{
        	haveHopSequence =false;
        }

        return m.moves.get(0);
    }
    
}
