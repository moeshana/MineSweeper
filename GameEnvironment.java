package MineSweeper;

/**
Create a mine sweeper environment. Include all mine and clue information.
This information is not available for agent. 
When agent want to go specific cell, we get that information of specific cell from this class.
@author Junfeng Zhao
*/
public class GameEnvironment {
	private int dim;
	private int mineQuantity;
	private int[][] environment;
	private int[] xDirection = {1, 0, -1, 0, 1, 1, -1, -1};
	private int[] yDirection = {0, 1, 0, -1, 1, -1, 1, -1};
	
	public static void main(String[] args) {
		GameEnvironment a = new GameEnvironment(10,12);
		a.initEnvironment();
		a.printEnvironment();
	}
	
	public GameEnvironment(int dim, int mine) {
		this.dim = dim;
		this.mineQuantity = mine;
		this.environment = new int[dim][dim];
		initEnvironment();
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
	Initial environment for the game. 
	setup mine, and compute clue for each cell.
	*/
	public void initEnvironment() {
		int count = 1;
		while (count <= mineQuantity) {
			int x = (int)(Math.random() * dim);
			int y = (int)(Math.random() * dim);
			if (checkValidMine(x,y)) {
				environment[x][y] = -1;
				updateClue(x,y);
			}
			count++;
		}
	}

	/**
	Update the clue after we put a new mine.
	@param x x value of mine. 
	@param y y value of mine.
	*/
	private void updateClue(int x, int y) {
		for (int i = 0; i < 8; i++) {
			if (checkValidPosition(x + xDirection[i], y + yDirection[i])) {
				if (environment[x + xDirection[i]][y + yDirection[i]] != -1) {
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
		return environment[x][y] == -1 ? false : true;
	}
}
