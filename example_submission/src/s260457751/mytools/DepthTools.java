package s260457751.mytools;

import java.awt.Point;
import java.util.ArrayList;
import halma.CCBoard;
import halma.CCMove;

public class DepthTools {

	public final static int HOP_DEPTH = 8;

	private static int distance(Point start, Point goal){
//		return Math.abs(goal.x-start.x) + Math.abs(goal.y - start.y);
		    	return (int) start.distance(goal);
	}

//	private static boolean goingBackwards(CCMove move, int playerID){       	
//		if(move.getFrom() == null || move.getTo() == null) return false;
//		Point p= new Point( 
//				(playerID == 3 || playerID == 1)?0:CCBoard.SIZE-1, 
//						(playerID == 3 || playerID == 2)?0:CCBoard.SIZE-1);
//		if(distance(move.getFrom(), p) < distance(move.getTo(), p)){
////			System.out.println("Skipping move: " + move.toPrettyString());
//			return true;
//		}
//
//		return false;
//	}

	private static boolean isMoveBack(CCMove one, CCMove two){
		return (one != null && two != null
				&& one.getFrom() != null 
				&& two.getTo() != null 
				&& one.getFrom().x == two.getTo().x 
				&& one.getFrom().y == two.getTo().y);
	}
	
	private static boolean isReverseMove(CCMove one, CCMove two){
//		if(one != null) System.out.println("One: " + one.toPrettyString());
////		if(two != null) System.out.println("Two: " + two.toPrettyString());
//		if( (one != null && two != null
//				&& one.getFrom() != null 
//				&& two.getTo() != null 
//				&& one.getTo() != null
//				&& two.getFrom() != null
//				&& one.getFrom().x == two.getTo().x 
//				&& one.getFrom().y == two.getTo().y)){
////			System.out.println("Reverse moves! Skipping!");
//
//			return true;
//
//		}
//		return false;
		return (one != null && two != null
				&& one.getFrom() != null 
				&& two.getTo() != null 
				&& one.getTo() != null
				&& two.getFrom() != null
				&& one.getFrom().x == two.getTo().x 
				&& one.getFrom().y == two.getTo().y
				&& one.getTo().x == two.getFrom().x
				&& one.getTo().y == two.getFrom().y);
	}

	private static ArrayList<MoveScore> getNextLayerFromHop(CCBoard board, CCMove orighop, CCMove latesthop, int count){
		ArrayList<MoveScore> list = new ArrayList<MoveScore>();
		for(CCMove move : board.getLegalMoves()){
			if(isReverseMove(move, latesthop) || isMoveBack(move, orighop)) continue;
			CCBoard temp = (CCBoard) board.clone();
			temp.move(move);

			if(move.isHop() && count < HOP_DEPTH){
				list.addAll(getNextLayerFromHop(temp, orighop, move, ++count));
			}else{
				list.add(new MoveScore(move, temp, Integer.MIN_VALUE, true, orighop));
			}
		}

		return list;
	}

	private static ArrayList<MoveScore> getNextLayer(CCBoard board, CCMove lastMove, int playerID){
		ArrayList<MoveScore> list = new ArrayList<MoveScore>();
		if(board.getTurn() != playerID){
//			System.out.println("GETTING FIRST MOVE POSSIBLE");
			ArrayList<CCMove> lm = board.getLegalMoves();
			CCMove temp = lm.get(0);
			int count = 1;
			while(count < lm.size() && temp.isHop()){
				temp = lm.get(count++);
			}
			CCBoard tempb = (CCBoard) board.clone();
			tempb.move(temp);
			list.add(new MoveScore(temp, tempb, Integer.MIN_VALUE));
			return list;
		}
		
		for(CCMove move : board.getLegalMoves()){
			if(isReverseMove(move, lastMove)) continue; //goingBackwards(move, board.getTurn()) || 
			if(move.isHop() && (board.getTurn() != playerID)) continue;
			CCBoard temp = (CCBoard) board.clone();
			temp.move(move);

			if(move.isHop()){
				list.addAll(getNextLayerFromHop(temp, move, move, 0));
			}
			else{
				list.add(new MoveScore(move, temp, Integer.MIN_VALUE));
			}
		}

		return list;	
	}

	public static MoveScore iterativeAlphaBeta(MoveScore boardScore, int depth, int alpha, int beta, boolean max, int playerID, CCMove lastMove){
		System.out.println("Depth: " + depth);
		boolean winner = boardScore.board.getWinner() == CCBoard.getTeamIndex(playerID);
		if(depth == 0 || winner ){
			// || timer.expired()
			boardScore.score = Heuristic.rankMoveSimple(boardScore.board, playerID);
			if(winner) boardScore.score = Integer.MAX_VALUE;
			return boardScore;
		}

		//iterate to depth
		if(max){
			ArrayList<MoveScore> nextLayer = getNextLayer(boardScore.board, lastMove, playerID);
			MoveScore best = nextLayer.get(0);
			MoveScore result;
			int curm = Integer.MIN_VALUE;
			for(MoveScore m : nextLayer){
				if(lastMove != null && lastMove.isHop() && isMoveBack(m.move, lastMove) || isReverseMove(m.move, lastMove)) continue; //for the long sequences
				System.out.println("Considering: " + m.move.toPrettyString());
				result = iterativeAlphaBeta(m, depth-1, alpha, beta, 
						CCBoard.getTeamIndex(m.board.getTurn()) == CCBoard.getTeamIndex(playerID) 
						|| m.board.getTurn() == playerID, playerID, lastMove);
				System.out.println("Move Result: "+ result.score);

				if(result.score > curm){
					curm = result.score;
					best = m;
				}
				alpha = Math.max(alpha, result.score);

				if(beta <= alpha)
					break;

			}
			best.score = alpha;
			return best;
		}
		else{
			//min
			ArrayList<MoveScore> nextLayer = getNextLayer(boardScore.board, lastMove, playerID);
			MoveScore best = nextLayer.get(0);
			MoveScore result;
			int curm = Integer.MAX_VALUE;
			for(MoveScore m : nextLayer){
//				System.out.println("Considering min: " + m.move.toPrettyString());

				result = iterativeAlphaBeta(m, depth-1, alpha, beta, 
						CCBoard.getTeamIndex(m.board.getTurn()) == CCBoard.getTeamIndex(playerID) 
						|| m.board.getTurn() == playerID, playerID, lastMove);
//				System.out.println("min Result: "+ result.score);

				if(result.score < curm){
					curm = result.score;
					best = m;
				}
				beta = Math.min(beta, result.score);

				if(beta <= alpha)
					break;
			}
			best.score = beta;
			return best;
		}

	}

}
