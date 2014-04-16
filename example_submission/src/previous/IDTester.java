package previous;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import halma.CCBoard;
import halma.CCMove;

import org.junit.Test;

import s260457751.mytools.MoveWrapper;

public class IDTester {

//	@Test
//	public void simpleMoveFinder() {
//		CCBoard start = new CCBoard();
//		MoveWrapper best = IterativeDepthTools.getBestMoveToDepth(start, 0, 1);
//		System.out.println("Best Move Seq: ");
//		for(CCMove m : best.moves){
//			System.out.println(m.toPrettyString());
//		}
//				
//	}
	
	@Test
	public void multilayerMoveFinder() {
		CCBoard start = new CCBoard();
//		MoveWrapper best = IterativeDepthTools.getBestMoveToDepth(start, 0, 1);
//		System.out.println("Best Move Seq: ");
//		for(CCMove m : best.moves){
//			System.out.println(m.toPrettyString());
//		}
		
		Random rand= new Random();

		
		for(int i=0;i<500;i++){
			ArrayList<CCMove> lm = start.getLegalMoves();
			start.move(lm.get(Math.abs(rand.nextInt())%lm.size()));
		}
		
		MoveWrapper best = IterativeDepthTools.getBestMoveToDepth(start, 0, 1);
		System.out.println("Best Move Seq: ");
		for(CCMove m : best.moves){
			System.out.println(m.toPrettyString());
		}
				
	}
	
    private boolean boardsEqual(CCBoard one, CCBoard two){
    	for(int i=0;i<4;i++){
    		//for each player compare the pieces
    		if(!one.getPieces(i).equals(two.getPieces(i)))
    			return false;
    	}
    	return true;
    }
    
    @Test
    public void monteCarloTester(){
    	s260457751PlayerMC p = new s260457751PlayerMC();
    	s260457751PlayerMC m = new s260457751PlayerMC();
		CCBoard start = new CCBoard();
		while(start.getTurn() == 0)
			start.move(p.chooseMove(start));
		while(start.getTurn() == 1)
			start.move(m.chooseMove(start));
    }
    
	@Test
	public void boardTester(){
		CCBoard start = new CCBoard();
		CCBoard clone = (CCBoard) start.clone();
		
		start.move(start.getLegalMoves().get(0));
		clone.move(clone.getLegalMoves().get(0));
		
		assertEquals(boardsEqual(start, clone), true);
	}
//	
//	@Test
//	public void monteCarloFinder(){
//		CCBoard start = new CCBoard();
//		MoveWrapper best = IterativeDepthTools.getBestMoveToDepthWithMC(start, 0, 1);
//		System.out.println("Best Move Seq: ");
//		for(CCMove m : best.moves){
//			System.out.println(m.toPrettyString());
//		}
//	}
//	
//	
//	@Test
//	public void mediumMoveFinder() {
//		CCBoard start = new CCBoard();
//		MoveWrapper best = IterativeDepthTools.getBestMoveToDepth(start, 0, 2);
//		System.out.println("Best Move Seq: ");
//		for(CCMove m : best.moves){
//			System.out.println(m.toPrettyString());
//		}
//		
//		if(best.isHopSequence){
//			System.out.println("HOP!");
//		}
//			
//		start.move(best.moves.get(0));
////		
////		Random rand= new Random();
////		while(start.getTurn() != 0){
//////			start.move(rand.nextInt());
////		}
//		
//	}
//	

	
}
