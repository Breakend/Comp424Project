package previous;

import halma.CCBoard;
import halma.CCMove;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Attempt at using real monte carlo methods
 * @author Peter Henderson
 *
 */
public class MCTreeNode {
	public CCBoard current;
	public CCMove move;
	public ArrayList<CCMove> moves;
	public boolean isHop;
	Random rand = new Random();

	public MCTreeNode(CCBoard board){
		this.current = board;
	}

	public MCTreeNode(CCBoard board, CCMove move){
		this.current = board;
		this.move = move;
	}
	
	public MCTreeNode(CCBoard board, CCMove move, boolean isHop){
		this.current = board;
		this.move = move;
		this.moves = new ArrayList<CCMove>();
		this.moves.add(move);
		this.isHop = true;
	}

	static Random r = new Random();
	//    static int nActions = 5;
	static double epsilon = 1e-6;

	public ArrayList<MCTreeNode> children;
	long nVisits, totValue;

	public void selectAction() {
		List<MCTreeNode> visited = new LinkedList<MCTreeNode>();
		MCTreeNode cur = this;
		visited.add(this);
		while (!cur.isLeaf()) {
			cur = cur.select();
			// System.out.println("Adding: " + cur);
			visited.add(cur);
		}
		cur.expand();
		MCTreeNode newNode = cur.select();
		visited.add(newNode);
		int value = rollOut(newNode);
		for (MCTreeNode node : visited) {
			// would need extra logic for n-player game
			// System.out.println(node);
			node.updateStats(value);
		}
	}

	private ArrayList<MCTreeNode> expandHop(MCTreeNode hopped){
		ArrayList<MCTreeNode> nodes = new ArrayList<MCTreeNode>();
		ArrayList<CCMove> lm = hopped.current.getLegalMoves();
		for(CCMove m : lm){
			CCBoard clone = (CCBoard) hopped.current.clone();
			clone.move(m);
			MCTreeNode newnode = new MCTreeNode(clone, m);
			newnode.moves = new ArrayList<CCMove>(hopped.moves);
			newnode.moves.add(m);
			newnode.isHop = true;
			
			if(m.isHop())
				nodes.addAll(expandHop(newnode));
			else
				nodes.add(newnode);
				
		}
		
		return nodes;

	}

	public void expand() {
		children = new ArrayList<MCTreeNode>();
		for(CCMove move : current.getLegalMoves()){
			CCBoard clone = (CCBoard) current.clone();
			clone.move(move);
			if(move.isHop()){
				children.addAll(expandHop(new MCTreeNode(clone, move, true)));
			}
			else
				children.add(new MCTreeNode(clone, move));
		}
	}

	public MCTreeNode select() {
		MCTreeNode selected = null;
		double bestValue = Integer.MIN_VALUE;
		for (MCTreeNode c : children) {
			double uctValue =
					c.totValue / (c.nVisits + epsilon) +
					Math.sqrt(Math.log(nVisits+1) / (c.nVisits + epsilon)) +
					r.nextDouble() * epsilon;
			// small random number to break ties randomly in unexpanded nodes
			// System.out.println("UCT value = " + uctValue);
			if (uctValue > bestValue) {
				selected = c;
				bestValue = uctValue;
			}
		}
		// System.out.println("Returning: " + selected);
		if(selected == null) throw new NullPointerException();
		return selected;
	}

	public boolean isLeaf() {
		return children == null;
	}

	public int rollOut(MCTreeNode tn) {
		// ultimately a roll out will end in some value
		// assume for now that it ends in a win or a loss
		// and just return this at random
		CCBoard board = tn.current;
		for(int i=0;i<5;i++){
			int depth = 0;
			board = (CCBoard) board.clone();
//			CCMove last = null;
			while(depth++ < 10){
				ArrayList<CCMove> lm = current.getLegalMoves();
				CCMove temp = lm.get(Math.abs(rand.nextInt()%lm.size()));
//				if(temp.getTo() != null)
//					last = temp;
				current.move(temp);
			}
		}
		
		return Heuristic.rankMoveSimpleWithDiagonalPenalty(tn.current, tn.current.getTurn());
	}

	public void updateStats(int value) {
		nVisits++;
		if(totValue < Integer.MAX_VALUE) totValue += value;
	}

	public int arity() {
		return children == null ? 0 : children.size();
	}
}