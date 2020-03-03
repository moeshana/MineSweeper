package MineSweeper;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class ImprovedGameAgent {

	private KnowledgeBase kb;
	private int[][] knownWorld;
	private HashSet<MinePoint> closedSet;
	private int dim;
	private HashSet<MinePoint> safePoints;
	private HashSet<MinePoint> minePoints;
	private Queue<Action> unsurePoints;

	
	public ImprovedGameAgent(int dim) {
		this.dim = dim;
		initCloseSet();
		safePoints = new HashSet<MinePoint>();
		minePoints = new HashSet<MinePoint>();
		unsurePoints = new LinkedList<Action>();
		knownWorld = new int[dim][dim];
		initKnownWorld();
		this.kb = new KnowledgeBase(dim, this.safePoints, this.minePoints, this.closedSet, this.unsurePoints, this.knownWorld);
	}
	
	public Action goNext() {
		MinePoint nextPoint;	
		if (safePoints.isEmpty() && minePoints.isEmpty()) {
				if (kb.improvedCompute()) {
					return goNext(); // update from knowledge base
				} else {
					nextPoint = kb.randomlyPickup();
					System.out.println("Randomly pick up : " + nextPoint); 
				}
				return new Action(nextPoint, 1);
		} else {
			if (safePoints.isEmpty()) {
					nextPoint = minePoints.iterator().next();
					System.out.print("mine :");
					minePoints.remove(nextPoint);
					return new Action(nextPoint, 0);
			} else {
					nextPoint = safePoints.iterator().next();
					safePoints.remove(nextPoint);
					System.out.print("safe :");
					return new Action(nextPoint, 1);
			}
		}
	}
	
	public void actionFeedback(Action feedback) {
		kb.updateFromFeedback(feedback);
	}
	
	/**
	Initial knwon world
	*/
	private void initKnownWorld() {
		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				this.knownWorld[row][col] = Integer.MAX_VALUE;
			}
		}
	}
	
	/**
	Initial closeSet by putting all the point into it.
	After finish one action, remove it from closedSet until it becomes empty.
	*/
	private void initCloseSet() {
		this.closedSet = new HashSet<MinePoint>();
		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				this.closedSet.add(new MinePoint(row, col));
			}
		}
	}

	
	/**
	Check the giving point is valid or not.
	@param x x value of point.
	@param y y value of point.
	@return true if the point is valid, otherwise false.
	*/
	private Boolean checkValidPosition(int x, int y) {
		return (x < 0 || x > this.dim - 1 || y < 0 || y > this.dim - 1) ? false : true; 
	}
	/**
	Tell controller, game over.
	Means all the cell are explored already.
	@return true game over, otherwise false;
	*/
	public boolean gameOver() {
		return kb.gameOver();
	}
	
}
