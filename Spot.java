package MineSweeper;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class Spot extends JPanel{
	private static final long serialVersionUID = 1L;
	private int clue;
	private boolean mayMine;
	private boolean unknown;
	
	public Spot(int size) {
		super();
		this.clue = 0;
		this.mayMine = false;
		this.unknown = true;
		setLayout(null);
		setEnabled(true);
		setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK));
	}

	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		if (this.unknown) {
			this.setBackground(Color.LIGHT_GRAY);
			Graphics2D g2d = (Graphics2D)g.create();
			AffineTransform at = new AffineTransform();
			at.translate(this.getHeight()/2 - 4 ,this.getWidth()/2 + 5);
			g2d.transform(at);
			g2d.setFont(new Font("Arial",Font.BOLD,15));
			g2d.drawString("?", 0, 0);
			g2d.dispose();	
		} else {
			if (this.mayMine) {
				this.setBackground(new Color(239,139,137));
				Graphics2D g2d = (Graphics2D)g.create();
				AffineTransform at = new AffineTransform();
				at.translate(this.getHeight()/2 - 6 ,this.getWidth()/2 + 5);
				g2d.transform(at);
				g2d.setFont(new Font("Arial",Font.BOLD,15));
				g2d.drawString("M", 0, 0);
				g2d.dispose();	
			} else {
				this.setBackground(Color.white);
				if (this.clue == 0) {
					this.setBackground(new Color(124, 243, 131));
				}
				Graphics2D g2d = (Graphics2D)g.create();
				AffineTransform at = new AffineTransform();
				at.translate(this.getHeight()/2 - 4 ,this.getWidth()/2 + 5);
				g2d.transform(at);
				g2d.setFont(new Font("Arial",Font.BOLD,15));
				g2d.drawString(String.valueOf(this.clue), 0, 0);
				g2d.dispose();	
			}
		}
	}
	
	public int getClue() {
		return clue;
	}

	public void setClue(int clue) {
		this.clue = clue;
		this.unknown = false;
	}

	public boolean isMayMine() {
		return mayMine;
	}

	public void setMayMine(boolean mayMine) {
		this.mayMine = mayMine;
		this.unknown = false;
	}
}
