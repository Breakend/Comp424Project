package previous;

import halma.CCBoard;
import halma.CCMove;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;

/**
 * A true monte carlo player
 */
public class s260457751PlayerMC extends Player {
	Random rand = new Random();

	/** Provide a default public constructor */
	public s260457751PlayerMC() { super("ABPruning"); }
	public s260457751PlayerMC(String s) { super(s); }

	public Board createBoard() { return new CCBoard(); }


	private MCTreeNode latestNode;
	private ListIterator iter;
	private boolean stepping;

	private boolean boardsEqual(CCBoard one, CCBoard two, int playerID){
		//for each player compare the pieces
		if(!one.getPieces(playerID).equals(two.getPieces(playerID)))
			return false;

		return true;
	}

	/** Implement a very stupid way of picking moves */
	public Move chooseMove(Board theboard) 
	{
	
		if(stepping && iter.hasNext()){
			return (Move) iter.next();
		}
		else{
			stepping = false;
		}
		// Cast the arguments to the objects we want to work with
		CCBoard board = (CCBoard) theboard;
	

		//        System.out.println);
		//        long start = System.currentTimeMillis();
		System.out.println("Turn count: " + board.getTurnsPlayed());
		// Use my tool for nothing
		//        if(lastMove != null) System.out.println("Last Move:" + lastMove.toPrettyString());
		// Return a randomly selected move.
		//        return ms.move;

		if(latestNode == null){
			latestNode = new MCTreeNode(board);
		}else{
			while(latestNode.current.getTurn() != this.playerID){
				if(latestNode.children == null){
					latestNode = new MCTreeNode(board);
					break;
				}
					
				boolean found = false;
				for(MCTreeNode n : latestNode.children){
					if(boardsEqual(board, n.current, n.current.getTurn())){
						latestNode = n;
						found=true;
					}
				}
				if(found == false){
					latestNode = new MCTreeNode(board);
					break;
				}
			}
		}
		
		for(int i=0;i<500;i++){
			latestNode.selectAction();
		}

		latestNode = latestNode.select();
		
		if(latestNode.isHop){
			stepping = true;
			iter = latestNode.moves.listIterator();
			iter.next(); //step into
			return latestNode.moves.get(0);
		}

		//        System.out.println("Time to move: " + (System.currentTimeMillis() - start));
		//        System.out.println("Move! " + ms.move.toPrettyString());
		//        System.out.println("Score! " + ms.score);
		//        System.out.println("--------------------------------------------------------------");

		return latestNode.move;
	}

}
