package s260457751.mytools;

import halma.CCBoard;

import java.awt.Point;
import java.util.ArrayList;

public class Heuristic {

	/**
	 * Check if a player has a piece in a certain base
	 * @param player_id the id of the player to check
	 * @param base_id the id of the base to check
	 * @return true if any piece belonging to player_id is in base_id
	 */
	public static int countInHomeBase(CCBoard board, int player_id){
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
	public static int countInGoalBase(CCBoard board, int player_id){
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
	public static boolean checkIfInBase(int player_id, Point piece){
		return (CCBoard.bases[player_id^3].contains(piece));
	}
    
	public static int distanceFromDiagonal(Point piece){
		return (Math.abs(piece.x - 8) + Math.abs(piece.y-8))/2;
	}
	
    /**
     * Really really simple scoring function, min score is better
     * @param move 
     * @return
     */
    public static int rankMoveSimple(CCBoard board, int playerID){
    	int score = 0;
    	ArrayList<Point> pieces = board.getPieces(playerID);
    	
    	
    	//TODO: make this distance function better;
    	//This is the furthest diagonal point
//    	Point p= new Point( (playerID == 3 || playerID == 1)?0:CCBoard.SIZE-1, 
//				(playerID == 3 || playerID == 2)?0:CCBoard.SIZE-1);
    	
    	Point p= new Point( 
				(playerID == 3 || playerID == 1)?0:CCBoard.SIZE-1, 
						(playerID == 3 || playerID == 2)?0:CCBoard.SIZE-1);
//    	System.out.println("PID,endpoint: (" + playerID + "," + p.toString() + ")");
    	for(Point piece : pieces){
//    		if(!checkIfInBase(playerID, piece))
    			score += Math.abs(30-distance(piece, p));
//    		else
//    			score += 32;
//    			score -= distanceFromDiagonal(piece); //small penalty for straying from the diagonal
    			if(!sorroundingHasFriendlyPiece(board,piece, playerID))
    				score -= 2;
    	}
    	
    	//could optimize this to go in the loop above for less time complexity
    	int inbase = countInGoalBase(board, playerID);
    	score += inbase*5;
    	score -= countInHomeBase(board, playerID)*5;
    	
    	//TODO: disinsentivize stragglers
    	
    	return score;
    }
    
    public static int rankMoveComplex(CCBoard board, int playerID){
    	int score = rankMoveSimple(board, playerID);
    	for(int i=0;i<4;i++){
    		if(i == playerID) continue; //really basic unperformant
    		if(CCBoard.getTeamIndex(i) == CCBoard.getTeamIndex(playerID))
    				score += rankMoveSimple(board, i);
    		else score -= rankMoveSimple(board, i);
    	}
    	return score;
    }
    
    public static int distance(Point start, Point goal){
    	return Math.abs(goal.x-start.x) + Math.abs(goal.y - start.y);
//    	return (int) start.distance(goal);
    }
    
    private static boolean sorroundingHasFriendlyPiece(CCBoard b, Point p, int playerID){
    	Point temp;
//    	int count = 0;
    	for(int i=Math.max(p.x-2, 0);i<Math.min(p.x+2,  CCBoard.SIZE-1);i++){
        	for(int j=Math.max(p.y-2, 0);j<Math.min(p.y+2,  CCBoard.SIZE-1);j++){
        		if(i == p.x && j == p.y) continue;
        		temp = new Point(i, j);
        		Integer id = b.getPieceAt(temp);
            	if(id != null && (id == playerID || CCBoard.getTeamIndex(id) == CCBoard.getTeamIndex(playerID))){
//            		if(count >= 2)return true; 
//            		count++;
            		return true;
            	}
            	
        	}
    	}
    	return false;
    }
	
}
