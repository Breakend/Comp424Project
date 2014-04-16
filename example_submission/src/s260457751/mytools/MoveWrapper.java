package s260457751.mytools;

import java.util.ArrayList;

import halma.CCBoard;
import halma.CCMove;

public class MoveWrapper {
	
	public boolean isHopSequence;
	public CCBoard board;
	//might need to make this less memory intensive
	public ArrayList<CCMove> moves;
	public CCMove lastMove;
	
	public MoveWrapper(ArrayList<CCMove> moves, CCBoard current, boolean hop, CCMove last){
		this.moves = moves;
		this.board = current;
		this.lastMove = last;
		this.isHopSequence = hop;
	}
	
	public MoveWrapper(CCBoard current){
		board = current;
		moves = new ArrayList<CCMove>();
	}
	
	public MoveWrapper(CCMove move, CCBoard current){
		this.lastMove = move;
		board = current;
		moves = new ArrayList<CCMove>();
		moves.add(move);
	}

	@Override
	public Object clone() {
		return new MoveWrapper(new ArrayList<CCMove>(moves), (CCBoard) board.clone(), isHopSequence, lastMove);
	}

}
