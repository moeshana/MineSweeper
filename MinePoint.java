package MineSweeper;

public class MinePoint {
	private int X;
	private int Y;
	public MinePoint(int x, int y) {
		this.X = x;
		this.Y = y;
	}
	public int getX() {
		return this.X;
	}
	public void setX(int x) {
		this.X = x;
	}
	public int getY() {
		return this.Y;
	}
	public void setY(int y) {
		this.Y = y;
	}
	public int hashCode() {
		return this.toString().hashCode();
	}
	public boolean equals(Object p) {
		if (p instanceof MinePoint) {
			MinePoint np = (MinePoint) p;
			return (this.X == np.getX()) && (this.Y == np.getY());
		} else {
			return false;
		}
	}
	public String toString() {
		return this.X + " - " + this.Y;
	}
}