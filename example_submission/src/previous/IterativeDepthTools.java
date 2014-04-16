package previous;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import s260457751.mytools.MoveWrapper;
import halma.CCBoard;
import halma.CCMove;

public class IterativeDepthTools {

	public final static int HOP_DEPTH = 7;
	public final static int MONTE_CARLO_ITERATIONS = 5;
	public final static int MONTE_CARLO_DEPTH = 10;

	private static boolean isReverseMove(CCMove one, CCMove two){
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
	
	public static boolean containsReverseMove(ArrayList<CCMove> moves, CCMove reverse){
		for(CCMove move: moves){
			if(isMoveBackForHop(move, reverse)) return true;
		}
		return false;
	}

	public static ArrayList<MoveWrapper> getHopSequence(MoveWrapper hop, int depth){
		ArrayList<MoveWrapper> list = new ArrayList<MoveWrapper>();
		ArrayList<CCMove> currentLegalMoves = hop.board.getLegalMoves();
		//		while(currentLegalMoves.size() != 1 || depth != HOP_DEPTH){
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
		//		}
	}
	
	public static int getPseudoMonteCarloScore(CCBoard board, int playerID){
		int score = 0;
		for(int i=0;i<MONTE_CARLO_ITERATIONS;i++){
			if(score == Integer.MAX_VALUE) break;
			int depth = 0;
			CCBoard current = (CCBoard) board.clone();
//			CCMove last = null;
			while(depth++ < MONTE_CARLO_DEPTH){
				ArrayList<CCMove> lm = current.getLegalMoves();
				CCMove temp = lm.get(Math.abs(rand.nextInt()%lm.size()));
//				if(temp.getTo() != null)
//					last = temp;
				current.move(temp);
			}
			score += Heuristic.rankMoveSimpleWithDiagonalPenalty(current, playerID);
		}
		return score/MONTE_CARLO_ITERATIONS;
	}
	
//	public static ArrayList<MoveWrapper> getHopSequence2(MoveWrapper hop, int depth){
//		ArrayList<MoveWrapper> list = new ArrayList<MoveWrapper>();
//		ArrayList<CCMove> currentLegalMoves = hop.board.getLegalMoves();
//		//		while(currentLegalMoves.size() != 1 || depth != HOP_DEPTH){
//		for(CCMove m : currentLegalMoves){
//			if(containsReverseMove(hop.moves, m)) continue;
//			MoveWrapper hopper = (MoveWrapper) hop.clone();
//			hopper.moves.add(m);
//			hopper.board.move(m);
//			hopper.lastMove = m;
//			if(m.isHop()){
//				int count = depth;
//				ArrayList<MoveWrapper> consider = new ArrayList<MoveWrapper>();
//				while(count < HOP_DEPTH){
//					ArrayList<CCMove> nextLegalMoves = hopper.board.getLegalMoves();
//					for(CCMove nextm : nextLegalMoves){
//						if(containsReverseMove(hop.moves, nextm)) continue;
//						MoveWrapper hopper2 = (MoveWrapper) hop.clone();
//						hopper2.moves.add(m);
//						hopper2.board.move(m);
//						hopper2.lastMove = m;
//						list.add(hopper2);
//					}
//				}
//				
//			}
//			else{
//				list.add(hopper);		
//			}
//		}
//
//		//make sure the last move is a null move before adding it to the list (i.e. should never
//		//get neverending jump sequence, I hope)
//		for(MoveWrapper m : list){
//			if(m.lastMove == null || (m.lastMove.getFrom() != null && m.lastMove.getTo() != null)){
//				m.moves.add(new CCMove(m.lastMove.getPlayer_id(), null, null));
//			}
//		}
//
//		return list;
//		//		}
//	}
	
	
	static Random rand = new Random();

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
				if(move.isHop()){
//					System.out.println("Getting next possible hop sequence from:");
//					System.out.println(move.toPrettyString());
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

	public static MoveWrapper getBestMoveToDepthNoD(CCBoard board, int playerID, int depth){
		ArrayList<MoveWrapper> currentMoves = new ArrayList<MoveWrapper>();
		currentMoves.add(new MoveWrapper(board));
		int score = Integer.MIN_VALUE;
		MoveWrapper best = null;
		/**
		 * Iterate to depth
		 */
		for(int i=0;i<depth;i++){
//			System.out.println("Depth: " + i);
			ArrayList<MoveWrapper> moveLayer = getNextMoveLayer(currentMoves, playerID, i);
			for(MoveWrapper move : moveLayer){
//				System.out.println("Considering Move: " + move.moves.get(0).toPrettyString());
				int temp = Heuristic.rankMoveSimple(move.board, playerID);
//				System.out.println("Score: " + temp);
				if(temp > score){
					best = move;
					score = temp;
				}
//				else if(temp == score && !best.isHopSequence && move.isHopSequence){
//					best = move;
//				}
//				
				currentMoves = moveLayer;
			}
		}
		
		return best;
	}

	public static MoveWrapper getBestMoveToDepth(CCBoard board, int playerID, int depth){
		ArrayList<MoveWrapper> currentMoves = new ArrayList<MoveWrapper>();
		currentMoves.add(new MoveWrapper(board));
		int score = Integer.MIN_VALUE;
		MoveWrapper best = null;
		/**
		 * Iterate to depth
		 */
		for(int i=0;i<depth;i++){
//			System.out.println("Depth: " + i);
			ArrayList<MoveWrapper> moveLayer = getNextMoveLayer(currentMoves, playerID, i);
			for(MoveWrapper move : moveLayer){
				System.out.println("Move:");
				for(CCMove mo : move.moves){
					System.out.println(mo.toPrettyString());
				}
//				System.out.println("Considering Move: " + move.moves.get(0).toPrettyString());
				int temp = Heuristic.rankMoveSimpleWithDiagonalPenalty(move.board, playerID);
//				System.out.println("Score: " + temp);
				if(temp > score){
					best = move;
					score = temp;
				}
//				else if(temp == score && !best.isHopSequence && move.isHopSequence){
//					best = move;
//				}
//				
				currentMoves = moveLayer;
			}
		}
		
		return best;
	}
	
	public static MoveWrapper getBestMoveToDepthWithMC(CCBoard board, int playerID, int depth){
		ArrayList<MoveWrapper> currentMoves = new ArrayList<MoveWrapper>();
		currentMoves.add(new MoveWrapper(board));
		int score = Integer.MIN_VALUE;
		MoveWrapper best = null;
		/**
		 * Iterate to depth
		 */
		for(int i=0;i<depth;i++){
//			System.out.println("Depth: " + i);
			ArrayList<MoveWrapper> moveLayer = getNextMoveLayer(currentMoves, playerID, i);
			
			for(MoveWrapper move : moveLayer){
//				System.out.println("Considering Move: " + move.moves.get(0).toPrettyString());
//				Point last;
//				if(move.moves.get(move.moves.size()-1).getTo() == null && move.moves.size() >1)
//					last = move.moves.get(move.moves.size()-2).getTo();
//				else
//					last = move.moves.get(move.moves.size()-1).getTo();
				int temp = Heuristic.rankMoveSimpleWithDiagonalPenalty(move.board, playerID);
				temp += getPseudoMonteCarloScore(move.board, playerID)/10; //weight the monte carlo simulations less
//				System.out.println("Score: " + temp);
				if(temp > score){
					best = move;
					score = temp;
				}
//				else if(temp == score && !best.isHopSequence && move.isHopSequence){
//					best = move;
//				}
//				
				currentMoves = moveLayer;
			}
		}
		
		return best;
	}

}
