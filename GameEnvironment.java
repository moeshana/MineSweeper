package MineSweeper;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
Create a mine sweeper environment. Include all mine and clue information.
This information is not available for agent. 
When agent want to go specific cell, we get that information of specific cell from this class.
@author Junfeng Zhao
*/
public class GameEnvironment {
	final int MINE_PLACEHOLDER = Integer.MIN_VALUE;
	private int failedCounter;
	private int dim;
	private int mineQuantity;
	private int[][] environment;
	private Queue<MinePoint> dropedMine;
	private int[] xDirection = {1, 0, -1, 0, 1, 1, -1, -1};
	private int[] yDirection = {0, 1, 0, -1, 1, -1, 1, -1};
	
	public static void main(String[] args) {
		GameEnvironment a = new GameEnvironment(5, 3, 213);
		a.printEnvironment();
		System.out.println("=====================");
		for (int row = 0; row < a.dim; row++) {
			for (int col = 0; col < a.dim; col++) {
				a.mistake(row, col);
			}
		}
		a.printEnvironment();
	}
	
	public GameEnvironment(int dim, int mine, int mode) {
		this.dim = dim;
		this.mineQuantity = mine;
		this.environment = new int[dim][dim];
		this.failedCounter = 0;
		this.dropedMine = new LinkedList<MinePoint>();
//		initEnvironment();
		switch(mode) {
		case -1 :
			falseNegativeEnvironment();
			break;
		case 0 :
			initUncertainEnvironment();
			break;
		case 1 :
			falsePositiveEnvironment();
			break;
		default :
			initEnvironment();	
	}		

	}
	
	/**
	Get a information of a specific cell. It may be mine or clue.
	@param x x value of cell.
	@param y y value of cell.
	@return 0 : safe, -1 : mine, other positive number : clue.
	*/
	public int iWannaInfo(int x, int y) {
		return this.environment[x][y];
	}
	
	/**
	Print environment to test
	In this environment:
	0 : safe
	1 to +00 : clue
	-1: mine
	*/
	public void printEnvironment() {
		for (int i = 0; i < this.dim; i++) {
			for (int y = 0; y < this.dim; y++) {
				System.out.print("  " + environment[i][y] + "  ");
			}
			System.out.println();
		}
	}
	
	/**
	We allow mistakes happen by getting off that mine and continue game.
	In this method, it will re-compute clue around giving mine and remove the mine from environment.  => cancelled
	And environment have a failed counter to record how many times this method was called.
	@param x x value of mine discovered by mistake
	@param y y value of mine discovered by mistake
	*/
	public void mistake(int x, int y) {
		if (environment[x][y] == MINE_PLACEHOLDER) {
			this.failedCounter += 1;
			System.out.println("Failed " + failedCounter + " times (point " + x + " : " + y + ")");
		}
	}
	/**
	Initial environment for the game. 
	setup mine, and compute clue for each cell.
	*/
	public void initEnvironment() {
		int count = 0;
		while (count < this.mineQuantity) {
			int x = (int)(Math.random() * dim);
			int y = (int)(Math.random() * dim);
			if (checkValidMine(x, y)) {
				environment[x][y] = MINE_PLACEHOLDER;
				updateClue(x,y);
				count++;
			}
		}
	}
	
	private void falseNegativeClue(int x, int y) {
		Random rand = new Random();
		for (int i = 0; i < 8; i++) {
			if (checkValidPosition(x + xDirection[i], y + yDirection[i])) {
				double doubleRandom = rand.nextDouble();
				if (doubleRandom >= 0.2 & environment[x + xDirection[i]][y + yDirection[i]] != MINE_PLACEHOLDER) {
					environment[x + xDirection[i]][y + yDirection[i]] += 1;
				}
			}
		}
	}

	private void falsePositiveClue(int x, int y) {
		Random rand = new Random();
		int count = 1;
		double doubleRandom = rand.nextDouble();

		for (int i = 0; i < 8; i++) {
			if (checkValidPosition(x + xDirection[i], y + yDirection[i])) {
				if (doubleRandom <= Math.pow(0.2, count) & environment[x + xDirection[i]][y + yDirection[i]] != MINE_PLACEHOLDER) {
					environment[x + xDirection[i]][y + yDirection[i]] += 1;
					count++;
				}
			}

		}
	}
	
	public void falseNegativeEnvironment() {
		int count = 0;
		while (count < this.mineQuantity) {
			int x = (int)(Math.random() * dim);
			int y = (int)(Math.random() * dim);
			if (checkValidMine(x, y)) {
				environment[x][y] = MINE_PLACEHOLDER;
				falseNegativeClue(x,y);
				count++;
			}
		}
	}
	
	
	public void falsePositiveEnvironment() {
		int count = 0;
		while (count < this.mineQuantity) {
			int x = (int)(Math.random() * dim);
			int y = (int)(Math.random() * dim);
			if (checkValidMine(x, y)) {
				environment[x][y] = MINE_PLACEHOLDER;
				updateClue(x,y);
				count++;
			}
		}

		for(int i=0; i< this.dim; i=i+1){
			for(int j=0; j< this.dim; j=j+1){
				if(environment[i][j] != MINE_PLACEHOLDER){
					falsePositiveClue(i,j);
				}
			}
		}
	}
	
	public void initUncertainEnvironment() {
		int count = 0;
		while (count < this.mineQuantity) {
			int x = (int)(Math.random() * dim);
			int y = (int)(Math.random() * dim);
			if (checkValidMine(x, y)) {
				environment[x][y] = MINE_PLACEHOLDER;
				falseNegativeClue(x,y);
				count++;
			}
		}
		for(int i=0; i< this.dim; i=i+3){
			for(int j=0; j< this.dim; j=j+3){
				if(environment[i][j] != MINE_PLACEHOLDER){
					falsePositiveClue(i,j);
				}
			}
		}
	}

//	/**
//	Check around how many mines.
//	@param x x value of position want to check
//	@param y y value of position want to check
//	@return int clue number
//	*/
//	private int checkClue(int x, int y) {
//		int clue = 0;
//		for (int i = 0; i < 8; i++) {
//			if (checkValidPosition(x + xDirection[i], y + yDirection[i])) {
//				if (environment[x + xDirection[i]][y + yDirection[i]] == MINE_PLACEHOLDER) {
//					clue += 1;
//				}
//			}
//		}
//		return clue;
//	}
	
	/**
	Update the clue after we put a new mine.
	@param x x value of mine. 
	@param y y value of mine.
	*/
	private void updateClue(int x, int y) {
		for (int i = 0; i < 8; i++) {
			if (checkValidPosition(x + xDirection[i], y + yDirection[i])) {
				if (environment[x + xDirection[i]][y + yDirection[i]] != MINE_PLACEHOLDER) {
					environment[x + xDirection[i]][y + yDirection[i]] += 1;
				}
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
	Check the giving point is a good place to put a mine.
	@param x x value of point.
	@param y y value of point.
	@return true if there is no mine, otherwise there is a mine already.
	*/
	private Boolean checkValidMine(int x, int y) {
		return environment[x][y] == MINE_PLACEHOLDER ? false : true;
	}
	
	/**
	Getter of failed counter
	@return int how many time agent failed.
	*/
	public int getFailedCounter() {
		return this.failedCounter;
	}
	
	/**
	reset the map, put all removed mine back
	*/
	public void recover() {
		this.failedCounter = 0;
		while (!dropedMine.isEmpty()) {
			MinePoint np = dropedMine.poll();
			environment[np.getX()][np.getY()] = MINE_PLACEHOLDER;
			updateClue(np.getX(), np.getY());
		}
	}
}