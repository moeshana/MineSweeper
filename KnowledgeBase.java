package MineSweeper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class KnowledgeBase {
	final int MINE_PLACEHOLDER = Integer.MIN_VALUE;
	private int[][] knownWorld;
	private int dim;
	private HashSet<MinePoint> closedSet;
	private HashSet<MinePoint> safePoints;
	private HashSet<MinePoint> minePoints;
	private Queue<Action> unsurePoints;
	private HashSet<MinePoint> boundary;
	
	private int[] xDirection = {1, 0, -1, 0, 1, 1, -1, -1};
	private int[] yDirection = {0, 1, 0, -1, 1, -1, 1, -1};
	
	public KnowledgeBase(int dim, HashSet<MinePoint> safePoints, 
						HashSet<MinePoint> minePoints,
						HashSet<MinePoint> closedSet, 
						Queue<Action> unsurePoints, 
						int[][] knownWorld) {
		this.dim = dim;
		this.safePoints = safePoints;
		this.minePoints = minePoints;
		this.closedSet = closedSet;
		this.unsurePoints = unsurePoints;
		this.knownWorld = knownWorld;
		this.boundary = new HashSet<MinePoint>();
//		for (int i = 0; i < dim; i++) {
//			for (int r = 0; r < dim; r++) {
//				System.out.print(knownWorld[i][r] + " ");
//			}
//			System.out.println();
//		}
		
	}
	private boolean basicCompute(MinePoint p, int clue) {
		boolean findNewInfo = false;
		if (clue == 0) {
			for (int i = 0; i < 8; i++) {
				if (checkValidPosition(p.getX() + xDirection[i], p.getY() + yDirection[i])) {
					MinePoint np = new MinePoint(p.getX() + xDirection[i], p.getY() + yDirection[i]);
					if (knownWorld[np.getX()][np.getY()] == Integer.MAX_VALUE) {
						safePoints.add(np);
						boundary.remove(np);
						closedSet.remove(np);
						knownWorld[np.getX()][np.getY()] = 0;
						findNewInfo = true;
					}
				}
			}
		} else {
			int mineCount = 0;
			Queue<MinePoint> temp = new LinkedList<MinePoint>(); 
			for (int i = 0; i < 8; i++) {
				if (checkValidPosition(p.getX() + xDirection[i], p.getY() + yDirection[i])) {
					MinePoint np = new MinePoint(p.getX() + xDirection[i], p.getY() + yDirection[i]);
					if (knownWorld[np.getX()][np.getY()] == Integer.MAX_VALUE) {
						mineCount++;
						temp.add(np);
						if ((!boundary.contains(np)) && (!minePoints.contains(np)) && (!safePoints.contains(np)) ) {
							System.out.println("boundary add " + np + " ; ");
							boundary.add(np);
							closedSet.remove(np);
						}
					} else {
						if (knownWorld[np.getX()][np.getY()] == MINE_PLACEHOLDER) {
							mineCount++;
						} 
					}
				}
			}
			if (mineCount != clue) {
				unsurePoints.add(new Action(p, clue));    //<================================================================review here
			} else {
				while (!temp.isEmpty()) {
					MinePoint nextPoint = temp.poll();
					knownWorld[nextPoint.getX()][nextPoint.getY()] = MINE_PLACEHOLDER;
					minePoints.add(nextPoint);
					boundary.remove(nextPoint);
					System.out.println("ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" + nextPoint);
				}
				findNewInfo = true;
			}
		}
		System.out.println("================>safe Point s " + safePoints);
		System.out.println("================>mine Point s " + minePoints);
		return findNewInfo;
	}
	
	public boolean improvedCompute() {
		System.out.println("improved COMPUTER ====> " + minePoints.size() + " : " + safePoints.size());
		boolean findNew = false;
//		if (doubleCheck()) {
//			return true;
//		} 
		
//		int roundCounter = 0;
//		int[][] probCounter = new int[dim][dim];


				
		if (boundary.size() == 0) {
			return false;
		}
		
		
		Iterator<MinePoint> apit = this.boundary.iterator();
//		System.out.println("start for loop ========<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		
		
		while (apit.hasNext()) {
			Queue<MinePoint> firstRoundSafe = new LinkedList<MinePoint>();
			Queue<MinePoint> firstRoundMine = new LinkedList<MinePoint>();
//			Queue<MinePoint> secondRoundSafe = new LinkedList<MinePoint>();
//			Queue<MinePoint> secondRoundMine = new LinkedList<MinePoint>();
			MinePoint ap = apit.next();
			System.out.println("ap is " + ap + " : "  + boundary);
			
			int firstRound = assumeMine(ap, apit, firstRoundSafe, firstRoundMine);
			if (firstRound > -1) {
				if (!assumeSafe(ap, apit)) {
					while (!firstRoundSafe.isEmpty()) {
						MinePoint np = firstRoundSafe.poll();
						safePoints.add(np);
						
						knownWorld[np.getX()][np.getY()] = 0;
						closedSet.remove(np);
					}
					while (!firstRoundMine.isEmpty()) {
						MinePoint np = firstRoundMine.poll();
						minePoints.add(np);
						
						knownWorld[np.getX()][np.getY()] = MINE_PLACEHOLDER;
						closedSet.remove(np);
					}
				}
			}
			if (!(safePoints.isEmpty() && minePoints.isEmpty())) {
				int size = safePoints.size();
				Iterator<MinePoint> it = safePoints.iterator();
				for (int i = 0; i < size; i++) {
					boundary.remove(it.next());
				}
				size = minePoints.size();
				it = minePoints.iterator();
				for (int i = 0; i < size; i++) {
					boundary.remove(it.next());
				}
				return true;
			}
		}
//		System.out.println("end for loop ========<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		return findNew;
		
	} 
	
	private boolean assumeSafe(MinePoint testPoint, Iterator<MinePoint> apit) {
		int[][]testWorld = copyEnv();	
		if (hasContradiction(testWorld, testPoint, false)) {
			System.out.println(testPoint + "get here mine in assume safe");
			minePoints.add(testPoint);
			apit.remove();
			knownWorld[testPoint.getX()][testPoint.getY()] = MINE_PLACEHOLDER;
			closedSet.remove(testPoint);
			return false;
		}
		return true;		
	}
	
	private int assumeMine(MinePoint testPoint, Iterator<MinePoint> apit, 
							   Queue<MinePoint> firstRoundSafe, Queue<MinePoint> firstRoundMine) {
		System.out.println("=========== hard test ================");
		int[][]testWorld = copyEnv();	
		if (hasContradiction(testWorld, testPoint, true)) {
			System.out.println(testPoint + "get here safe");
			apit.remove();
			safePoints.add(testPoint);
			knownWorld[testPoint.getX()][testPoint.getY()] = 0;
			closedSet.remove(testPoint);
			return -1;
		} else {
			HashSet<MinePoint> donePoint = new HashSet<MinePoint>();
			Queue<MinePoint> nextStep = new LinkedList<MinePoint>();
			nextStep.add(testPoint);
			while (!nextStep.isEmpty()) {
				MinePoint currentPoint = nextStep.poll();
				
//				System.out.println("now test next step : " + currentPoint);
				
				if (donePoint.contains(currentPoint)) {
					continue;
				}
				for (int i = 0; i < 8; i++) {
					if (checkValidPosition(currentPoint.getX() + xDirection[i], currentPoint.getY() + yDirection[i])
							&& testWorld[currentPoint.getX() + xDirection[i]][currentPoint.getY() + yDirection[i]] != Integer.MAX_VALUE) {
						MinePoint cp = new MinePoint(currentPoint.getX() + xDirection[i], currentPoint.getY() + yDirection[i]);
//						System.out.println("now in currentpoint adjance : " + currentPoint +   " : " + cp +  " => " + testWorld[cp.getX()][cp.getY()]);
						if (true) {   // remove here
							int clue = testWorld[cp.getX()][cp.getY()]; 
							int mineCounter = 0;
							int adjance = 0;
							int discoverd = 0;
							int countForClue = 0;
							Queue<MinePoint> temp = new LinkedList<MinePoint>(); 
							for (int j = 0; j < 8; j++) {
								if (checkValidPosition(cp.getX() + xDirection[j], cp.getY() + yDirection[j])) {
									adjance += 1;
									MinePoint cp2 = new MinePoint(cp.getX() + xDirection[j], cp.getY() + yDirection[j]);
									if (testWorld[cp2.getX()][cp2.getY()] == MINE_PLACEHOLDER) {
										mineCounter += 1;
										countForClue += 1;
									} else {
										if (testWorld[cp2.getX()][cp2.getY()] == Integer.MAX_VALUE 
												|| testWorld[cp2.getX()][cp2.getY()] < 0) {  //added this constraint
											countForClue += 1;
											temp.add(cp2);
										} else {
											discoverd += 1;
										}
									}
								}
							} 
							if (Math.abs(clue) == countForClue) {
								while (!temp.isEmpty()) {
									MinePoint nextPoint = temp.poll();
									System.out.println("this may mine : " + nextPoint);
									testWorld[nextPoint.getX()][nextPoint.getY()] = MINE_PLACEHOLDER;
									firstRoundMine.add(nextPoint);
									nextStep.add(nextPoint);
								}
							}
							if (clue == mineCounter) {
								while (!temp.isEmpty()) {
									MinePoint nextPoint = temp.poll();
									System.out.println("this may safe : " + nextPoint);
									testWorld[nextPoint.getX()][nextPoint.getY()] = Integer.MAX_VALUE - 1;
									firstRoundSafe.add(nextPoint);
									nextStep.add(nextPoint);
								}
							}
							if (adjance == discoverd) {
								donePoint.add(cp);
							}
						}
					}
				}
				//inside while
				
				
			}
			
			
		}
		if (!isVaildMapAllCheck(testWorld)) {
			System.out.println(testPoint + "get here safe in the end......");
			apit.remove();
			safePoints.add(testPoint);
			knownWorld[testPoint.getX()][testPoint.getY()] = 0;
			closedSet.remove(testPoint);
			return -1;
		} else {
			
		}
		return 1;
	}

	private boolean hasContradiction(int[][] testWorld, MinePoint testPoint, boolean isTestMine) {
		boolean res = false;
		if (isTestMine) {
			testWorld[testPoint.getX()][testPoint.getY()] = MINE_PLACEHOLDER;
			testWorld = computeClue(testWorld);
//			if (!isVaildMap(testWorld, testPoint)) {
			if (!isVaildMapAllCheck(testWorld, testPoint)) {
				res = true;
			}
		} else {
			testWorld[testPoint.getX()][testPoint.getY()] = 0;
			testWorld = computeClue(testWorld);
//			if (!isVaildMap(testWorld, testPoint)) { //remove it 
//			if (!isValidMapForPutSafe(testWorld, testPoint)) {
			if (!isVaildMapAllCheck(testWorld, testPoint)) { // this works well
				res = true;
			}
		}
		return res;
	}

	
	
//	/** keep going with known test world
//	*/
//	private int[][] induceMine(int[][] testWorld) {
//		// TODO Auto-generated method stub
//		return testWorld;
//	}
	
	private boolean isVaildMapAllCheck(int[][] map, MinePoint test) {
		int count = 0;
		boolean res = true;
		System.out.println("test world looks like ===================================");
		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				System.out.print(map[row][col] + " ");
			}
			System.out.println();
		}
		System.out.println("test world looks like done ===================================");
		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				count ++;
				if (row == test.getX() && col == test.getY()) {
					continue;
				}
				res &=isVaildMap(map, new MinePoint(row, col));
				if (!res) {
					return res;
				}
			}	
		}
		System.out.println("in check all map : check " + count + " times...");
		return res;
	}
	
	private boolean isVaildMapAllCheck(int[][] map) {
		return (isVaildMapAllCheck(map, new MinePoint(-1,-1)));
	}
	
	/**
	when we put a mine or safe into a boundary cell, check if there are any violates.
	*/
	private boolean isVaildMap(int[][] testWorld, MinePoint testPoint) {
		int clue = testWorld[testPoint.getX()][testPoint.getY()];
		if (clue < 0 || clue >= Integer.MAX_VALUE - 1) {
			return true;
		}
		int mineCount = 0;
		int unknown = 0;
		for (int i = 0; i < 8; i++) {
			if (checkValidPosition(testPoint.getX() + xDirection[i], testPoint.getY() + yDirection[i])) {
				if (testWorld[testPoint.getX() + xDirection[i]][testPoint.getY() + yDirection[i]] == this.MINE_PLACEHOLDER) {
					mineCount++;
				} else {
					if (testWorld[testPoint.getX() + xDirection[i]][testPoint.getY() + yDirection[i]] == Integer.MAX_VALUE 
							||  testWorld[testPoint.getX() + xDirection[i]][testPoint.getY() + yDirection[i]] < 0) {
						unknown++;
					}
				}
			}
		}
		if (mineCount > Math.abs(clue)) {
			System.out.println("not vaild : " + testPoint + " : " + mineCount + " : " + clue);
			return false;
		} else {	
			if ((unknown + mineCount) < Math.abs(clue)) {
				System.out.println("not vaild in guess : " + testPoint + " : " + mineCount + " : " + unknown + " : " + clue);
				return false;
			}
		}
		System.out.println("map looks good");
		return true;
	}
	
	private int[][] computeClue(int[][] testWorld) {
		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				if (testWorld[row][col] == MINE_PLACEHOLDER) {
					for (int i = 0; i < 8; i++) {
						if (checkValidPosition(row + xDirection[i], col + yDirection[i])) {
							if (testWorld[row + xDirection[i]][col + yDirection[i]] != MINE_PLACEHOLDER) {
								if (testWorld[row + xDirection[i]][col + yDirection[i]] == Integer.MAX_VALUE) {
									testWorld[row + xDirection[i]][col + yDirection[i]] = -1;
								} else {
									if (testWorld[row + xDirection[i]][col + yDirection[i]] < 0) {
										testWorld[row + xDirection[i]][col + yDirection[i]] -= 1;
									}
								}
							}
						}
					}
				}
 			}
		}
		return testWorld;
	}	
	
	/**
	copy a known world map for testing.
	only record mine and safe position, clue info
	@return int[][] copied map
	*/
	private int[][] copyEnv() {
		int[][] testWorld = new int[dim][dim];
		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				testWorld[row][col] = knownWorld[row][col];
			}
		}
		return testWorld;
	}

	/**
	update info from previous action
	*/
	public void updateFromFeedback(Action newClue) {
		knownWorld[newClue.getNextPoint().getX()][newClue.getNextPoint().getY()] = newClue.getActionCango();
		basicCompute(newClue.getNextPoint(), newClue.getActionCango());		
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
		return this.closedSet.isEmpty() && this.safePoints.isEmpty() && this.minePoints.isEmpty() && this.boundary.isEmpty();
	}
	public MinePoint randomlyPickup() {
		System.out.println(closedSet.size() + " : " + boundary.size() + " : " + safePoints.size() + " : " + minePoints.size());
		MinePoint nextPoint = null;
		if (!closedSet.isEmpty()) {
			System.out.println("from closed set");
			nextPoint = closedSet.iterator().next();
			closedSet.remove(nextPoint);
		} else {
			if (!boundary.isEmpty()) {
				System.out.println("from boundary set");
				nextPoint = boundary.iterator().next();
				boundary.remove(nextPoint);
			}		
		}
		return nextPoint;
	}
	
}
