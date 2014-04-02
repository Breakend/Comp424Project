package s260457751.mytools;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import halma.CCBoard;
import halma.CCMove;

public class MyTools {
	public static CustomTimer timer = CustomTimer.getInstance();
	public static Random rand = new Random();
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
	public boolean checkIfInBase(int player_id, Point piece){
		return (CCBoard.bases[player_id^3].contains(piece));
	}
    
    /**
     * Really really simple scoring function, min score is better
     * @param move 
     * @return
     */
    private static int rankMoveSimple(CCBoard board, int playerID){
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
    			score -= distance(piece, p);
    			if(!sorroundingHasFriendlyPiece(board,piece, playerID))
    				score -= 2;
    	}
    	
    	//could optimize this to go in the loop above for less time complexity
    	score += countInGoalBase(board, playerID)*4;
    	score -= countInHomeBase(board, playerID)*4;
    	
    	//TODO: disinsentivize stragglers
    	
    	return score;
    }
    
    private static int rankMoveComplex(CCBoard board, int playerID){
    	int score = rankMoveSimple(board, playerID);
    	for(int i=0;i<4;i++){
    		if(i == playerID) continue; //really basic unperformant
    		score -= rankMoveSimple(board, i);
    	}
    	return score;
    }
    
    private static int distance(Point start, Point goal){
//    	return Math.abs(goal.x-start.x) + Math.abs(goal.y - start.y);
    	return (int) start.distance(goal);
    }
    
    private static boolean sorroundingHasFriendlyPiece(CCBoard b, Point p, int playerID){
    	Point temp;
    	for(int i=Math.max(p.x-2, 0);i<Math.min(p.x+2,  CCBoard.SIZE-1);i++){
        	for(int j=Math.max(p.y-2, 0);j<Math.min(p.y+2,  CCBoard.SIZE-1);j++){
        		if(i == p.x && j == p.y) continue;
        		temp = new Point(i, j);
        		Integer id = b.getPieceAt(temp);
            	if(id != null) return true; 
            	
        	}
    	}
    	return false;
    }
    
    private static ArrayList<CCMove> getMovesWithoutBackwards(ArrayList<CCMove> moves, int playerID){
    	ArrayList<CCMove> fixed = new ArrayList<CCMove>(moves.size());
       	Point p= new Point( 
    				(playerID == 3 || playerID == 1)?0:CCBoard.SIZE-1, 
    						(playerID == 3 || playerID == 2)?0:CCBoard.SIZE-1);
    	for(CCMove move: moves){
    		if(move.getFrom() == null || move.getTo() == null) continue;
    		if(distance(move.getFrom(), p) <= distance(move.getTo(), p)){
    			System.out.println("Removed: " + move.toPrettyString());
    			continue;
    		}
    		
    		fixed.add(move);
    	}
    	
    	return fixed;
    }
    
    public static boolean goingBackwards(CCMove move, int playerID){       	
    	if(move.getFrom() == null || move.getTo() == null) return false;
       	Point p= new Point( 
    				(playerID == 3 || playerID == 1)?0:CCBoard.SIZE-1, 
    						(playerID == 3 || playerID == 2)?0:CCBoard.SIZE-1);
    	if(distance(move.getFrom(), p) < distance(move.getTo(), p)){
    		System.out.println("Skipping move: " + move.toPrettyString());
    		return true;
    	}
    	
    	return false;
    }
    
	public static MoveScore alphaBeta(CCBoard board, int depth, int alpha, int beta, boolean max, int playerID){
//		System.out.println("Depth: "+ depth);
//		System.out.println("Alpha: " + alpha);
//		System.out.println("Beta: " + beta);
		if(depth == 0 || board.getWinner() == playerID) // || timer.expired()
			return new MoveScore(new CCMove(playerID, null, null), rankMoveComplex(board, playerID));
		
		if(max){
			MoveScore result;
			int curm = Integer.MIN_VALUE;
			CCMove curMove = new CCMove(playerID, null, null);
			for(CCMove m : board.getLegalMoves()){
				if(goingBackwards(m, board.getTurn())) continue;
//				if(board.getTurn() == playerID && isHopBack(lastPlayerMove, m)) continue;
				if(depth == 3) System.out.println("Considering move: "+ m.toPrettyString());
				CCBoard temp = (CCBoard) board.clone();
				temp.move(m);
				result = alphaBeta(temp, depth-1, alpha, beta, 
						CCBoard.getTeamIndex(temp.getTurn()) == CCBoard.getTeamIndex(playerID) 
						|| temp.getTurn() == playerID, playerID);
				if(depth == 3)System.out.println("Move Result: "+ result.score);
				
				if(result.score > curm){
					curm = result.score;
					curMove = m;
				}
				alpha = Math.max(alpha, result.score);

				if(beta <= alpha)
					break;
			}
			System.out.println("Max result: " + curMove.toPrettyString() + " : " + alpha);
			
			return new MoveScore(curMove, alpha);
		}
		else{
			MoveScore result;
			int curm = Integer.MAX_VALUE;
			CCMove curMove = new CCMove(playerID, null, null);

			//trying to choose a random move for the min
//			ArrayList<CCMove> lm =  board.getLegalMoves();
//			CCMove m =lm.get(rand.nextInt(lm.size()));
//			CCBoard temp = (CCBoard) board.clone();
//			temp.move(m);
//			result = alphaBeta(temp, depth-1, alpha, beta, CCBoard.getTeamIndex(temp.getTurn()) == CCBoard.getTeamIndex(playerID) 
//					|| temp.getTurn() == playerID, playerID);
//			beta = Math.min(beta, result.score);

			for(CCMove m : board.getLegalMoves()){
				if(goingBackwards(m, board.getTurn())) continue;

//				System.out.println("Considering min move: "+ m.toPrettyString());
				//Shouldn't take into account hop back because this should only be opponents stuff
				CCBoard temp = (CCBoard) board.clone();
				temp.move(m);
				result = alphaBeta(temp, depth-1, alpha, beta, CCBoard.getTeamIndex(temp.getTurn()) == CCBoard.getTeamIndex(playerID) 
						|| temp.getTurn() == playerID, playerID);
//				System.out.println("Min Move Result: "+ result.score);
				
				if(result.score < curm){
					curm = result.score;
					curMove = m;
				}
				
				beta = Math.min(beta, result.score);

				if(beta <= alpha)
					break;
			}
			System.out.println("Min result: " + curMove.toPrettyString() + " : " + beta);

			return new MoveScore(curMove, beta);
		}
	}





}



