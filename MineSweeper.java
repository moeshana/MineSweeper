package MineSweeper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

public class MineSweeper {
    private final int DIM = 10;
    private final int MINE_QUANTITY = 10;
    private int random;
    final int MINE_PLACEHOLDER = Integer.MIN_VALUE;
    private final int SPOT_SIZE = 30;
    private GameEnvironment gameEnv;
    private MineSpot[][] map;
    private JFrame mainWindow;
    private JPanel mainPanel;
    private JPanel displayPanel;
    private javax.swing.Timer timer;

    private MineSpot[][] gameMap;
    private MineSpot[][] hiddenMap;
    private JFrame playWindow;
    private JPanel gamePanel;
    private JTextField safeTextField;
    private  JTextField mineTextField;
    private  JToggleButton toggleButton;


    public static void main(String... args) {
        EventQueue.invokeLater(MineSweeper::new);
    }
    public MineSweeper() {
        random = 0;
        initWindow();
        setupMap(213);
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

    private void submitSafe(int xAxis, int yAxis) {
        int clue = gameEnv.iWannaInfo(xAxis, yAxis);
        if(clue != Integer.MIN_VALUE){
            gameMap[xAxis][yAxis].setClue(clue);
            playWindow.repaint();
        }else{
            gameMap[xAxis][yAxis].setWrong();
            playWindow.repaint();
        }
    }
    private void submitMine(int xAxis, int yAxis) {
        int clue = gameEnv.iWannaInfo(xAxis, yAxis);
        if(clue == Integer.MIN_VALUE){
            gameMap[xAxis][yAxis].setMayMine(true);
            playWindow.repaint();
        }
        else{
            gameMap[xAxis][yAxis].setMayMine(true);
            playWindow.repaint();
        }
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

        int myRow = 9;
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

    private void whoIsYourDaddy(boolean revealed) {
        if(!revealed){
            hiddenMap = new MineSpot[DIM][DIM];
            for (int row = 0; row < DIM; row++) {
                for (int col = 0; col < DIM; col++) {
                    int info = gameEnv.iWannaInfo(row, col);
                    if (info == -1) {
                        MineSpot spot = new MineSpot(SPOT_SIZE);
                        hiddenMap[row][col] = spot;
                        gameMap[row][col].setMayMine(true);
                    } else {
                        MineSpot spot = new MineSpot(SPOT_SIZE);
                        hiddenMap[row][col] = spot;
                        gameMap[row][col].setClue(info);
                    }
                }
            }
            this.playWindow.repaint();
            for (int row = 0; row < DIM; row++) {
                for (int col = 0; col < DIM; col++) {
                    int info = gameEnv.iWannaInfo(row, col);
                    if (info == MINE_PLACEHOLDER) {
                        MineSpot spot = new MineSpot(SPOT_SIZE);
                        hiddenMap[row][col] = spot;
                        gameMap[row][col].setMayMine(true);
                    } else {
                        MineSpot spot = new MineSpot(SPOT_SIZE);
                        hiddenMap[row][col] = spot;
                        gameMap[row][col].setClue(info);
                        gameMap[row][col].setMayMine(false);
                    }
                }
            }
            this.playWindow.repaint();
        }

        else{
            gamePanel.removeAll();
            gamePanel.revalidate();
            gameMap = hiddenMap;
            for (int row = 0; row < DIM; row++) {
                for (int col = 0; col < DIM; col++) {
                    gamePanel.add(hiddenMap[row][col]);
                }
            }
            this.playWindow.repaint();
        }
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
    private void setupMap(int mode) {
        gameEnv = new GameEnvironment(DIM, MINE_QUANTITY, mode);
        map = new MineSpot[DIM][DIM];
        gameMap = new MineSpot[DIM][DIM];
        for (int row = 0; row < DIM; row++) {
            for (int col = 0; col < DIM; col++) {
                MineSpot spot = new MineSpot(SPOT_SIZE);
                MineSpot spot2 = new MineSpot(SPOT_SIZE);
                mainPanel.add(spot);
                map[row][col] = spot;
                gamePanel.add(spot2);
                gameMap[row][col] = spot2;
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
        mainWindow = new JFrame("MineSweeper Agent");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(DIM, DIM));
        mainPanel.setPreferredSize(new Dimension(SPOT_SIZE * DIM, SPOT_SIZE * DIM));
        displayPanel = new JPanel();
        displayPanel.setLayout(new GridLayout(3, 4));
        JButton basicPlay = new JButton("Basic");
        basicPlay.addActionListener(ae -> {
            startGameBasic();
        });
        displayPanel.add(basicPlay);
        JButton improvePlay = new JButton("Improved");
        improvePlay.addActionListener(ae -> {
            startGameImpoved();
        });
        displayPanel.add(improvePlay);
        
        JButton newMap = new JButton("NewMap");
        newMap.addActionListener(ae -> {
            newMap(213);
        });
        displayPanel.add(newMap);

        JButton stop = new JButton("stop");
        stop.addActionListener(ae -> {
            this.timer.stop();
        });
        displayPanel.add(stop);

        JButton resume = new JButton("resume");
        resume.addActionListener(ae -> {
            this.timer.start();
        });
        displayPanel.add(resume);

        JButton uncertainMap = new JButton("Uncertain Map");
        uncertainMap.addActionListener(ae -> {
            newMap(0);
        });
        displayPanel.add(uncertainMap);

        JButton falsePosMap = new JButton("P(pos) Map");
        falsePosMap.addActionListener(ae -> {
            newMap(1);
        });
        displayPanel.add(falsePosMap);

        JButton falseNegMap = new JButton("P(neg) Map");
        falseNegMap.addActionListener(ae -> {
            newMap(-1);
        });
        displayPanel.add(falseNegMap);

        displayPanel.setPreferredSize(new Dimension(SPOT_SIZE * DIM, 45));
        mainWindow.add(mainPanel, BorderLayout.CENTER);
        mainWindow.add(displayPanel, BorderLayout.NORTH);
        mainWindow.pack();
        mainWindow.setSize(new Dimension(SPOT_SIZE * DIM , SPOT_SIZE * DIM + 45 ));
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setResizable(false);
        mainWindow.setVisible(true);

        JPanel playPanel;
        JPanel playPanel2;

        toggleButton = new JToggleButton("Reveal/Hide Map");
        ItemListener itemListener = itemEvent -> {
            int state = itemEvent.getStateChange();
            if (state == ItemEvent.SELECTED) {
                whoIsYourDaddy(false);
            }
            else {
                whoIsYourDaddy(true);
            }
        };
        toggleButton.addItemListener(itemListener);


        JButton safeButton = new JButton("Choose");
        safeTextField = new JTextField("Input form: X(v),Y(h)");
        safeTextField.setColumns(15);
        safeTextField.addFocusListener(new FocusListener() {
            String info = "Input form: X(v),Y(h)";
            @Override
            public void focusGained(FocusEvent e) {
                if (safeTextField.getText().equals(info)) {
                    safeTextField.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (safeTextField.getText().equals("")) {
                    safeTextField.setText(info);
                }
            }
        });
        safeButton.addActionListener(e -> {
            String safeCord = safeTextField.getText();
            String[] coordinate = safeCord.split(",");
            try {
                int xAxis = Integer.parseInt(coordinate[0]);
                int yAxis = Integer.parseInt(coordinate[1]);
                submitSafe(xAxis, yAxis);
                safeTextField.setText("Input form: X(v),Y(h)");
            } catch(Exception eeee) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid Input", "alert", JOptionPane.ERROR_MESSAGE);

            }
        });

        JButton mineButton = new JButton("Boom");
        mineTextField = new JTextField("Input form: X(v),Y(h)");
        mineTextField.setColumns(15);
        mineTextField.addFocusListener(new FocusListener() {
            String info = "Input form: X(v),Y(h)";
            @Override
            public void focusGained(FocusEvent e) {
                if (mineTextField.getText().equals(info)) {
                    mineTextField.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (mineTextField.getText().equals("")) {
                    mineTextField.setText(info);
                }
            }
        });
        mineButton.addActionListener(e -> {
            String mineCord = mineTextField.getText();
            System.out.println(mineCord);
            String[] coordinate = mineCord.split(",");
            try {
                int xAxis = Integer.parseInt(coordinate[0]);
                int yAxis = Integer.parseInt(coordinate[1]);
                submitMine(xAxis, yAxis);
                mineTextField.setText("Input form: X(v),Y(h)");
            } catch(Exception ee) {
                JOptionPane.showMessageDialog(null,
                        "Invalid Input", "alert", JOptionPane.ERROR_MESSAGE);
            }
        });

        playWindow = new JFrame("Game User");
        playWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(DIM+1, DIM+1));
        gamePanel.setPreferredSize(new Dimension(SPOT_SIZE * (DIM+1), SPOT_SIZE * (DIM+1)));

        playWindow.pack();
        playWindow.setSize(new Dimension(SPOT_SIZE * (DIM+1) , SPOT_SIZE * (DIM+1) + 100 ));
        //playWindow.setLocationRelativeTo(null);
        playWindow.setResizable(false);
        playWindow.setVisible(true);

        playPanel = new JPanel();
        playPanel.add(toggleButton);
        playPanel.setLayout(new GridLayout(1,2));
        playPanel2 =  new JPanel();
        playPanel2.setLayout(new GridLayout(2,2));
        playPanel2.add(safeButton);
        playPanel2.add(safeTextField);
        playPanel2.add(mineButton);

        playPanel2.add(mineTextField);

        playPanel2.setPreferredSize(new Dimension(SPOT_SIZE * (DIM+1), 60));

        playWindow.add(playPanel, BorderLayout.SOUTH);
        playWindow.add(playPanel2, BorderLayout.NORTH);
        playWindow.add(gamePanel, BorderLayout.CENTER);

    }

    private void newMap(int mode) {
        if (timer != null) {
            timer.stop();
        }
        mainPanel.removeAll();
        mainPanel.revalidate();
        gamePanel.removeAll();
        gamePanel.revalidate();
        setupMap(mode);
        playWindow.repaint();
        mainWindow.repaint();
        if (toggleButton.isSelected()) {
        	toggleButton.setSelected(false);
        }
    }
}

