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
	private int randomCount;

	
	public ImprovedGameAgent(int dim) {
		this.dim = dim;
		this.randomCount = 0;
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
					this.randomCount += 1;
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
	Tell controller, game over.
	Means all the cell are explored already.
	@return true game over, otherwise false;
	*/
	public boolean gameOver() {
		return kb.gameOver();
	}
	
	/**
	how many times we randomly pick up a point instead of compute.
	@param int times we randomly pick up
	*/
	public int getRandomCounter() {
		return this.randomCount;
	}
	
}
