package MineSweeper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class MineSweeper {
	private final int DIM = 10;
	private final int MINE_QUANTITY = 15;
	private int random;
	final int MINE_PLACEHOLDER = Integer.MIN_VALUE;
	private final int SPOT_SIZE = 30;
	private GameEnvironment gameEnv;
	private MineSpot[][] map;
	private JFrame mainWindow;
	private JPanel mainPanel;
	private JPanel displayPanel;
	private javax.swing.Timer timer;

	
	public static void main(String... args) {
		EventQueue.invokeLater(MineSweeper::new);
	}
	public MineSweeper() {
		random = 0;
		initWindow();
		setupMap();
//		whosYourDaddy();
	}
	private void startGameBasic() {
		restart();
		GameAgent gameAgent = new GameAgent(this.DIM);
		this.timer = new javax.swing.Timer(
				200, 
				ae -> {
					if (!gameAgent.gameOver()) {
						Action next = gameAgent.goNext();
						int clue = gameEnv.iWannaInfo(next.getNextPoint().getX(), next.getNextPoint().getY());
						
						if (next.getActionCango() == 0) {  // no feedback
							map[next.getNextPoint().getX()][next.getNextPoint().getY()].setMayMine(true);
						} else {
							if (clue == MINE_PLACEHOLDER) {
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
						this.random = gameAgent.getRandomCounter();
						System.out.println(calcResult());
						System.out.println("Done");
						mainWindow.repaint();
					}
				});
		timer.start();
	}
	private void startGameImpoved() {
		restart();
		ImprovedGameAgent gameAgent = new ImprovedGameAgent(this.DIM);
		this.timer = new javax.swing.Timer(
				200, 
				ae -> {
					if (!gameAgent.gameOver()) {
						Action next = gameAgent.goNext();
						System.out.println(next.getNextPoint());
						int clue = gameEnv.iWannaInfo(next.getNextPoint().getX(), next.getNextPoint().getY());
						if (next.getActionCango() == 0) {  //
							map[next.getNextPoint().getX()][next.getNextPoint().getY()].setMayMine(true);
						} else {
							if (clue == MINE_PLACEHOLDER) {
								gameEnv.mistake(next.getNextPoint().getX(), next.getNextPoint().getY());
								clue = gameEnv.iWannaInfo(next.getNextPoint().getX(), next.getNextPoint().getY());
								gameAgent.actionFeedback(new Action(next.getNextPoint(), clue));								
								map[next.getNextPoint().getX()][next.getNextPoint().getY()].setWrong();
							} else {
								gameAgent.actionFeedback(new Action(next.getNextPoint(), clue));
								map[next.getNextPoint().getX()][next.getNextPoint().getY()].setClue(clue);
							}
						}
						mainWindow.repaint();
					} else {
						this.timer.stop();
						this.random = gameAgent.getRandomCounter();
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
				if (info == MINE_PLACEHOLDER) {
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
				if (info == MINE_PLACEHOLDER) {
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
					if (gameEnv.iWannaInfo(row, col) == MINE_PLACEHOLDER) {
						correct += 1;
					} else {
						map[row][col].setWrong2();
					}
				}
			}
		}
		String resReport = "Random " + this.random + " times.(Include the first action)\n" + 
				"Failed counter(#failedCounter/#totalMine) : " + failedCount + " / " + MINE_QUANTITY +
				".\nMine marked by AI(#correctCounter/#totalMarkCounter): " + correct + " / " + total;
		return resReport;
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
		JButton basicPlay = new JButton("BP");
		basicPlay.addActionListener(ae -> {
			startGameBasic();
		});
		displayPanel.add(basicPlay);
		JButton improvePlay = new JButton("IP");
		improvePlay.addActionListener(ae -> {
			startGameImpoved();
		});
		displayPanel.add(improvePlay);
		
		JButton stop = new JButton("s");
		stop.addActionListener(ae -> {
			this.timer.stop();
		});
		displayPanel.add(stop);
		
		JButton resume = new JButton("r");
		resume.addActionListener(ae -> {
			this.timer.start();
		});
		displayPanel.add(resume);
		
		
		
		
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