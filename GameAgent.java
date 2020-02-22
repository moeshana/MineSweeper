package MineSweeper;

import java.util.HashSet;

public class GameAgent {
//	private KnowledgeBase kb;
	private int[][] knownWorld;
	private HashSet<MinePoint> closedSet;
	private int dim;
	private HashSet<MinePoint> safePoints;
	private HashSet<MinePoint> minePoints;
	private int[] xDirection = {1, 0, -1, 0, 1, 1, -1, -1};
	private int[] yDirection = {0, 1, 0, -1, 1, -1, 1, -1};
	
//	public static void main(String[] args) {
//		HashSet<MinePoint> p = new HashSet<MinePoint>();
//		p.add(new MinePoint(0,0));
//		p.add(new MinePoint(1,0));
//		p.add(new MinePoint(2,0));
//		p.add(new MinePoint(3,0));
//		MinePoint p2 = p.iterator().next();
//		p.remove(p2);
//		System.out.println(p.size());
//		System.out.println(p2);
//		p2 = p.iterator().next();
//		p.remove(p2);
//		System.out.println(p.size());
//		System.out.println(p2);
//		p2 = p.iterator().next();
//		p.remove(p2);
//		System.out.println(p.size());
//		System.out.println(p2);
//		p2 = p.iterator().next();
//		p.remove(p2);
//		System.out.println(p.size());
//		System.out.println(p2);
//	}
	
	public GameAgent(int dim) {
		this.dim = dim;
//		this.kb = new KnowledgeBase(dim);
		initCloseSet();
		safePoints = new HashSet<MinePoint>();
		minePoints = new HashSet<MinePoint>();
		knownWorld = new int[dim][dim];
		initKnownWorld();
	}
	
	public Action goNext() {
		MinePoint nextPoint;	
		if (safePoints.isEmpty() && minePoints.isEmpty()) {
				nextPoint = closedSet.iterator().next();
				closedSet.remove(nextPoint);
				return new Action(nextPoint, 1);
		} else {
			if (safePoints.isEmpty()) {
					nextPoint = minePoints.iterator().next();
					minePoints.remove(nextPoint);
					return new Action(nextPoint, 0);
			} else {
					nextPoint = safePoints.iterator().next();
					safePoints.remove(nextPoint);
					return new Action(nextPoint, 1);
			}
		}
	}
	
	public void actionFeedback(Action feedback) {
		MinePoint p = feedback.getNextPoint();
		int clue = feedback.getActionCango();
		knownWorld[p.getX()][p.getY()] = clue;
		compute(p, clue);
	}
	
	
	private void compute(MinePoint p, int clue) {
		if (clue == 0) {
			for (int i = 0; i < 8; i++) {
				if (checkValidPosition(p.getX() + xDirection[i], p.getY() + yDirection[i])) {
					MinePoint np = new MinePoint(p.getX() + xDirection[i], p.getY() + yDirection[i]);
					if (knownWorld[np.getX()][np.getY()] == Integer.MAX_VALUE) {
						System.out.println(np);
						safePoints.add(np);
						closedSet.remove(np);
					}
				}
			}
		}
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
		return this.closedSet.isEmpty();
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
}
