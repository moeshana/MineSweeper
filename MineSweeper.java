package MineSweeper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class MineSweeper {
	private final int DIM = 15;
	private final int MINE_QUANTITY = 20;
	private final int SPOT_SIZE = 30;
	private GameEnvironment gameEnv;
	private MineSpot[][] map;
	private JFrame mainWindow;
	private JPanel mainPanel;
	private JPanel displayPanel;
	private javax.swing.Timer timer;
	private GameAgent gameAgent;

	
	public static void main(String... args) {
		EventQueue.invokeLater(MineSweeper::new);
	}
	public MineSweeper() {
		initWindow();
		setupMap();
		startGame();
//		whosYourDaddy();
	}
	private void startGame() {
		gameAgent = new GameAgent(this.DIM);
		this.timer = new javax.swing.Timer(
				200, 
				ae -> {
					if (!gameAgent.gameOver()) {
						Action next = gameAgent.goNext();
						int clue = gameEnv.iWannaInfo(next.getNextPoint().getX(), next.getNextPoint().getY());
						
						if (next.getActionCango() == 0) {  // no feedback
							map[next.getNextPoint().getX()][next.getNextPoint().getY()].setMayMine(true);
						} else {
							if (clue == -1) {
								// go and check but get a mine => AI wrong
								gameEnv.mistake(next.getNextPoint().getX(), next.getNextPoint().getY());
								clue = gameEnv.iWannaInfo(next.getNextPoint().getX(), next.getNextPoint().getY());
								gameAgent.actionFeedback(new Action(next.getNextPoint(), clue));								
								map[next.getNextPoint().getX()][next.getNextPoint().getY()].setWrong();
							} else {
								// correctly, and mark a mine
								// no feedback
								gameAgent.actionFeedback(new Action(next.getNextPoint(), clue));
								map[next.getNextPoint().getX()][next.getNextPoint().getY()].setClue(clue);
							}
						}
						//update map.
						mainWindow.repaint();
					} else {
						this.timer.stop();
						System.out.println(calcResult());
						System.out.println("Done");
						mainWindow.repaint();
					}
				});
			timer.start();

	}
	
	/**
	Test map only..
	*/
	@SuppressWarnings("unused")
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
					map[row][col].setMayMine(true);
				} else {
					map[row][col].setClue(info);					
					map[row][col].setMayMine(false);
				}
			}
		}
		this.mainWindow.repaint();
	}
	private String calcResult(){
		int total = 0;
		int correct = 0;
		int failedCount = gameEnv.getFailedCounter();
		for (int row = 0; row < DIM; row++) {
			for (int col = 0; col < DIM; col++) {
				if (map[row][col].isMayMine()) {
					total += 1;
					if (gameEnv.iWannaInfo(row, col) == -1) {
						correct += 1;
					} else {
						map[row][col].setWrong2();
					}
				}
			}
		}
		return failedCount + " / " + MINE_QUANTITY + " : " + correct + " / " + total;
	}
	/**
	Setup map for game
	*/
	private void setupMap() {
		gameEnv = new GameEnvironment(DIM, MINE_QUANTITY);
		map = new MineSpot[DIM][DIM];
		for (int row = 0; row < DIM; row++) {
			for (int col = 0; col < DIM; col++) {
				MineSpot spot = new MineSpot(SPOT_SIZE);
				mainPanel.add(spot);
				map[row][col] = spot;
			}
		}
	}
	private void restart() {
		System.out.println(">>>>>>>>>>>restart the game<<<<<<<<<<<");
		if (timer != null) {
			timer.stop();
		}
		for (int row = 0; row < DIM; row++) {
			for (int col = 0; col < DIM; col++) {
				map[row][col].reset();
			}
		}
		gameEnv.recover();
		mainWindow.repaint();
		startGame();
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
		JButton replay = new JButton("re-play");
		replay.addActionListener(ae -> {
			restart();
		});
		displayPanel.add(replay);
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