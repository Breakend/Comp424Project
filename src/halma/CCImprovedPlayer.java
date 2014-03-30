package halma;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;

/**
 * Slightly improved Halma player
 */
public class CCImprovedPlayer extends Player {
    private boolean verbose = false;
    Random rand = new Random();
    
    /** Provide a default public constructor */
    public CCImprovedPlayer() { super("random"); }
    public CCImprovedPlayer(String s) { super(s); }
    
    public Board createBoard() { return new CCBoard(); }

    /** Implement a very stupid way of picking moves */
    public Move chooseMove(Board theboard) 
    {
        // Cast the arguments to the objects we want to work with

        CCBoard board = (CCBoard) theboard;
        

        // Get the list of legal moves.
        
        ArrayList<CCMove> moves = board.getLegalMoves();
        
        // Otherwise, return a randomly selected move.

//        return (CCMove) moves.get(rand.nextInt(moves.size()));
        return findBestMove(moves, board);
    }
    
    private CCMove findBestMove(ArrayList<CCMove> moves, CCBoard board){
    	CCBoard temp;
    	CCMove best = moves.get(0);
    	int bscore = 0;
    	for(CCMove move : moves){
    		temp = (CCBoard) board.clone();
    		temp.move(move);
    		int tscore = rankMove(board);
    		if(tscore > bscore){
    			best = move;
    			bscore = tscore;
    		}
    	}
    	return best;
    }
    
	/**
	 * Check if a player has a piece in a certain base
	 * @param player_id the id of the player to check
	 * @param base_id the id of the base to check
	 * @return true if any piece belonging to player_id is in base_id
	 */
	public int countInHomeBase(CCBoard board, int player_id){
		int count= 0;
		Integer IDInteger= new Integer(player_id);
		for(Point p: CCBoard.bases[player_id]){
			if(IDInteger.equals(board.board.get(p))) count++;
		}
		return count;
	}
	
	/**
	 * Check if a player has a piece in a certain base
	 * @param player_id the id of the player to check
	 * @param base_id the id of the base to check
	 * @return true if any piece belonging to player_id is in base_id
	 */
	public int countInGoalBase(CCBoard board, int player_id){
		int count= 0;
		int bid = player_id^3;
		Integer IDInteger= new Integer(player_id);
		for(Point p: CCBoard.bases[bid]){
			if(IDInteger.equals(board.board.get(p))) count++;
		}
		return count;
	}
	
	/**
	 * Check if a particular piece is in the end zone
	 * @param player_id
	 * @param piece
	 * @return
	 */
	public boolean checkIfInBase(int player_id, Point piece){
		return (CCBoard.bases[player_id^3].contains(piece));
	}
    
    /**
     * Really really simple scoring function, min score is better
     * @param move
     * @return
     */
    private int rankMove(CCBoard board){
    	int score = 0;
    	ArrayList<Point> pieces = board.getPieces(this.playerID);
    	
    	//TODO: make this distance function better;
    	//This is the furthest diagonal point
    	Point p= new Point( (this.playerID%2==0)?2:CCBoard.SIZE-3, 
				((this.playerID>>1)%2==0)?2:CCBoard.SIZE-3);
    	
    	for(Point piece : pieces){
    		if(!checkIfInBase(this.playerID, piece))
    			score += piece.distance(p.x, p.y);	
    	}
    	
    	//could optimize this to go in the loop above for less time complexity
    	score -= countInGoalBase(board, this.playerID);
    	score += countInHomeBase(board, this.playerID);
    	
    	return score;
    }
    
    
} // End class
