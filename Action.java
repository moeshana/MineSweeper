package MineSweeper;

public class Action {
	private MinePoint nextPoint;
	private int action;

	public Action(MinePoint n, int p) {
		this.nextPoint = n;
		this.action = p;
		//actionCango : 1 go and check new cell; 0 mark a mine 
		//feedback : action means clue number

	}
	public MinePoint getNextPoint() {
		return nextPoint;
	}
	public void setNextPoint(MinePoint nextPoint) {
		this.nextPoint = nextPoint;
	}
	public int getActionCango() {
		return action;
	}
	public void setActionCango(int actionCango) {
		this.action = actionCango;
	}

}
