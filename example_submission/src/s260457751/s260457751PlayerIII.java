package s260457751;

import halma.CCBoard;
import halma.CCMove;

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
public class s260457751PlayerIII extends Player {
    Random rand = new Random();
    
    /** Provide a default public constructor */
    public s260457751PlayerIII() { super("improved"); }
    public s260457751PlayerIII(String s) { super(s); }
    
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
        return findBestMove(moves, board, 0);
    }
    
    private boolean isComplete(CCBoard board){
    	System.out.println("In goal: " + (countInGoalBase(board, this.playerID)));
    	return (countInGoalBase(board, this.playerID) == 13) ? true : false;
    }
    
    private ArrayList<CCMove> removeSameHop(ArrayList<CCMove> moves, CCMove just){
    	if(just == null || just.getFrom() == null) return moves;
    	ArrayList<CCMove> fixed = new ArrayList<CCMove>(moves.size());
    	for(CCMove move : moves){
    		if(move != null && move.getTo() != null && move.getTo().x == just.getFrom().x 
    				&& move.getTo().y == just.getFrom().y){
    			System.out.println("Removed: " + move.toPrettyString());
    			continue;
    		}
    		
    		fixed.add(move);
    	}
    		
    	return fixed;
    }
    
    private CCMove findBestMove(ArrayList<CCMove> moves, CCBoard board, int count){
    	System.out.println("Find Player: "+ this.playerID);
    	if(isComplete(board)) return new CCMove(this.playerID, null, null);
    
    	CCBoard temp;
    	CCMove best = moves.get(0);
    	int bscore = Integer.MAX_VALUE;
    	for(CCMove move : moves){
    		if(move == null || goingBackwards(move, this.playerID)) continue;
//    		if(move.getTo() == null || move.getFrom() == null) continue;
    		temp = (CCBoard) board.clone();
    		temp.move(move);
    		int tscore = rankMove(temp);

//    		if(count < 3){    			
////    			if(move.isHop() && count < 3){    			
//    			CCMove move2 = findBestMove(removeSameHop(temp.getLegalMoves(), move), temp, ++count);
//    			CCBoard temp2 = (CCBoard) temp.clone();
//    			temp2.move(move2);
//    			tscore += rankMove(temp2);
//    			tscore /=2;
////    			tscore--; //for being a hop
//    		}
    		
			if(move.isHop() && count < 3){    			
				CCMove move2 = findBestMove(removeSameHop(temp.getLegalMoves(), move), temp, ++count);
				CCBoard temp2 = (CCBoard) temp.clone();
				temp2.move(move2);
				tscore += rankMove(temp2);
				tscore /=2;
	//			tscore--; //for being a hop
			}
//    		System.out.println("Move from: (" + move.getFrom()+")");
//    		System.out.println("Move to: (" + move.getTo() +")");
//    		System.out.println("Move score: " + tscore);
    		if(tscore < bscore){
    			best = move;
    			bscore = tscore;
    		}
    	}
    	
		System.out.println("Move from: (" + best.getFrom()+")");
		System.out.println("Move to: (" + best.getTo() +")");
		System.out.println("Move score: " + bscore);
    	return best;
    }
    
    private boolean sorroundingHasFriendlyPiece(CCBoard b, Point p, int playerID){
    	Point temp;
    	for(int i=Math.max(p.x-2, 0);i<Math.min(p.x+2,  CCBoard.SIZE-1);i++){
        	for(int j=Math.max(p.y-2, 0);j<Math.min(p.y+2,  CCBoard.SIZE-1);j++){
        		if(i == p.x && j == p.y) continue;
        		temp = new Point(i, j);
        		Integer id = b.getPieceAt(temp);
            	if(id != null && id == playerID) return true; 
            	
        	}
    	}
    	return false;
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
	
    public boolean goingBackwards(CCMove move, int playerID){       	
    	if(move.getFrom() == null || move.getTo() == null) return false;
       	Point p= new Point( 
    				(playerID == 3 || playerID == 1)?0:CCBoard.SIZE-1, 
    						(playerID == 3 || playerID == 2)?0:CCBoard.SIZE-1);
    	if(distance(move.getFrom(), p) <= distance(move.getTo(), p)) return true;
    	
    	return false;
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
//    	Point p= new Point( (this.playerID == 3 || this.playerID == 1)?0:CCBoard.SIZE-1, 
//				(this.playerID == 3 || this.playerID == 2)?0:CCBoard.SIZE-1);
    	
    	Point p= new Point( 
				(this.playerID == 3 || this.playerID == 1)?0:CCBoard.SIZE-1, 
						(this.playerID == 3 || this.playerID == 2)?0:CCBoard.SIZE-1);
//    	Point prev = pieces.get(0);
//    	System.out.println("PID,endpoint: (" + this.playerID + "," + p.toString() + ")");
    	for(Point piece : pieces){
    		if(!checkIfInBase(this.playerID, piece))
    			score += distance(piece, p);
//    			insentivize sticking with other pieces for leapfrogging
        		if(!sorroundingHasFriendlyPiece(board,piece,this.playerID))
    				score -= 1;
    	}
    	
    	//could optimize this to go in the loop above for less time complexity
    	score -= countInGoalBase(board, this.playerID);
    	score += countInHomeBase(board, this.playerID);
    	
    	
    	
    	return score;
    }
    
    private int distance(Point start, Point goal){
//    	return Math.abs(goal.x-start.x) + Math.abs(goal.y - start.y);
    	return (int) start.distance(goal);
    }
    
    
} // End class
