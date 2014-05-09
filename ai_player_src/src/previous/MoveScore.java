package previous;

import halma.CCBoard;
import halma.CCMove;

/**
 * A wrapper for keeping track of a move and its score
 * 
 * @author Peter Henderson
 *
 */
public class MoveScore {
	public CCMove move;
	public CCBoard board;
	public int score;
	public boolean isHopDescendent;
	public CCMove origHop;
	
	public MoveScore(CCMove move, int score){
		this.score = score;
		this.move = move;
	}
	
	public MoveScore(CCMove move, CCBoard board, int score){
		this.score = score;
		this.move = move;
		this.board = board;
	}
	
	public MoveScore(CCMove move, CCBoard board, int score, boolean hop, CCMove orig){
		this.score = score;
		this.move = move;
		this.board = board;
		this.isHopDescendent = hop;
		this.origHop = orig;
	}
}
