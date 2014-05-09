package sXXXXXXXXX.mytools;

import java.util.ArrayList;
import java.util.Random;

import previous.Heuristic;
import halma.CCBoard;
import halma.CCMove;

public class MyFinalTools {

	public final static int HOP_DEPTH = 7;
	public final static int MONTE_CARLO_ITERATIONS = 5;
	public final static int MONTE_CARLO_DEPTH = 10;
	private static Random rand = new Random();

	/**
	 * Is this a move back to a previous hop position
	 * @param one
	 * @param two
	 * @return
	 */
	private static boolean isMoveBackForHop(CCMove one, CCMove two){
		return (one != null && two != null
				&& one.getFrom() != null 
				&& two.getTo() != null 
				&& one.getTo() != null
				&& two.getFrom() != null
				&& ((one.getFrom().x == two.getTo().x 
				&& one.getFrom().y == two.getTo().y)
				||( one.getTo().x == two.getTo().x
				&& one.getTo().y == two.getTo().y)));
	}
	
	/**
	 * does anything in the move contain a hop back to somewhere else in that move
	 * @param moves
	 * @param reverse
	 * @return
	 */
	public static boolean containsReverseMove(ArrayList<CCMove> moves, CCMove reverse){
		for(CCMove move: moves){
			if(isMoveBackForHop(move, reverse)) return true;
		}
		return false;
	}

	/**
	 * Get a sequence of hops until the maximum depth
	 * @param hop
	 * @param depth
	 * @return
	 */
	public static ArrayList<MoveWrapper> getHopSequence(MoveWrapper hop, int depth){
		ArrayList<MoveWrapper> list = new ArrayList<MoveWrapper>();
		ArrayList<CCMove> currentLegalMoves = hop.board.getLegalMoves();
		for(CCMove m : currentLegalMoves){
			if(containsReverseMove(hop.moves, m)) continue;
			MoveWrapper hopper = (MoveWrapper) hop.clone();
			hopper.moves.add(m);
			hopper.board.move(m);
			hopper.lastMove = m;
			if(m.isHop() && depth <= HOP_DEPTH)
				list.addAll(getHopSequence(hopper, depth+1));
			else
				list.add(hopper);		
		}

		//make sure the last move is a null move before adding it to the list (i.e. should never
		//get neverending jump sequence, I hope)
		for(MoveWrapper m : list){
			if(m.lastMove == null || (m.lastMove.getFrom() != null && m.lastMove.getTo() != null)){
				m.moves.add(new CCMove(m.lastMove.getPlayer_id(), null, null));
			}
		}

		return list;
	}
	

	/**
	 * Get the next move layer for a given player from an ArrayList of current 
	 * boards in the current move layer, for the opponents moves, will choose
	 * a random move (that is not a hop). This assumption holds until the 
	 * middle of the board, but the timing for doing full minimax is too much
	 * time and breaks the 1s limit
	 * @param boards
	 * @param playerID
	 * @return
	 */
	public static ArrayList<MoveWrapper> getNextMoveLayer(ArrayList<MoveWrapper> boards, int playerID, int depth){
		ArrayList<MoveWrapper> list = new ArrayList<MoveWrapper>();
		ArrayList<CCMove> moves;
		for(MoveWrapper board : boards){
			if(board.isHopSequence) continue;
			//while its not the player's turn just make the other players stay
			while(board.board.getTurn() != playerID){
				ArrayList<CCMove> oplm = board.board.getLegalMoves();
				board.board.move(oplm.get(Math.abs(rand.nextInt()%oplm.size())));
			}
			moves = board.board.getLegalMoves();
			for(CCMove move : moves){
				MoveWrapper clone = (MoveWrapper) board.clone();
				clone.board.move(move);
				clone.lastMove = move;
				clone.moves.add(move);
				//if its a hop get the next layers until the max depth
				if(move.isHop()){
					if(depth == 0) clone.isHopSequence = true;
					list.addAll(getHopSequence(clone, 0));
				}
				else{
					list.add(clone);
				}
			}
		}

		return list;
	}


	/**
	 * Greedy Depth limited search move finder
	 * @param board
	 * @param playerID
	 * @param depth
	 * @return
	 */
	public static MoveWrapper getBestMoveToDepth(CCBoard board, int playerID, int depth){
		ArrayList<MoveWrapper> currentMoves = new ArrayList<MoveWrapper>();
		currentMoves.add(new MoveWrapper(board));
		int score = Integer.MIN_VALUE;
		MoveWrapper best = null;

		//Iterate to depth n
		for(int i=0;i<depth;i++){
			ArrayList<MoveWrapper> moveLayer = getNextMoveLayer(currentMoves, playerID, i);
			for(MoveWrapper move : moveLayer){
				//check all the moves in the next layer and get the best one
				int temp = Heuristic.rankMoveSimpleWithDiagonalPenalty(move.board, playerID);
				
				if(temp > score){
					best = move;
					score = temp;
				}

				currentMoves = moveLayer;
			}
		}
		
		return best;
	}

}
