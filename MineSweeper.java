package MineSweeper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class MineSweeper {
	private final int DIM = 20;
	private final int MINE_QUANTITY = 300;
	private final int SPOT_SIZE = 30;
	private GameEnvironment gameEnv;
	private Spot[][] map;
	private JFrame mainWindow;
	private JPanel mainPanel;
	private JPanel displayPanel;
	
	public static void main(String... args) {
		EventQueue.invokeLater(MineSweeper::new);
	}
	public MineSweeper() {
		initWindow();
		setupMap();
		whosYourDaddy();

	}
	
	/**
	Test map only..
	*/
	private void whosYourDaddy() {
		
		for (int row = 0; row < DIM; row++) {
			for (int col = 0; col < DIM; col++) {
				int info = gameEnv.iWannaInfo(row, col);
				if (info == -1) {
					map[row][col].setMayMine(true);
				} else {
					map[row][col].setClue(info);
				}
			}
		}
		
		int myRow = 19;
			for (int col = 0; col < DIM; col++) {
				if (map[myRow][col].isMayMine()) {
					gameEnv.mistake(myRow, col);
				}
		}
		
		for (int row = 0; row < DIM; row++) {
			for (int col = 0; col < DIM; col++) {
				int info = gameEnv.iWannaInfo(row, col);
				if (info == -1) {
					System.out.println(row + " : " + col);
					map[row][col].setMayMine(true);
				} else {
					map[row][col].setClue(info);					
					map[row][col].setMayMine(false);
				}
			}
		}
		this.mainWindow.repaint();

	}
	/**
	Setup map for game
	*/
	private void setupMap() {
		gameEnv = new GameEnvironment(DIM, MINE_QUANTITY);
		map = new Spot[DIM][DIM];
		for (int row = 0; row < DIM; row++) {
			for (int col = 0; col < DIM; col++) {
				Spot spot = new Spot(SPOT_SIZE);
				mainPanel.add(spot);
				map[row][col] = spot;
			}
		}
	}
	/**
	Initial main window for user
	*/
	private void initWindow() {
		mainWindow = new JFrame("MineSweeper");
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(DIM, DIM));
		mainPanel.setPreferredSize(new Dimension(SPOT_SIZE * DIM, SPOT_SIZE * DIM));
		displayPanel = new JPanel();
		displayPanel.setPreferredSize(new Dimension(SPOT_SIZE * DIM, 45));
		mainWindow.add(mainPanel, BorderLayout.CENTER);
		mainWindow.add(displayPanel, BorderLayout.NORTH);
		mainWindow.pack();
		mainWindow.setSize(new Dimension(SPOT_SIZE * DIM , SPOT_SIZE * DIM + 45 ));
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setResizable(false);
		mainWindow.setVisible(true);
	}
	
}