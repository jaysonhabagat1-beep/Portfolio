import java.awt.*;
import java.util.Random;

import javax.swing.*;

public class TicTacToe extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private JLabel statusLabel;

    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private boolean isVsAI = false;
    private boolean xTurn = true;
    private int movesCount = 0;
    private boolean gameActive = false;

    private JTextField p1Field, p2Field;
    private JComboBox<String> modeBox;
    private JPanel configPanel;
    private JPanel gamePanel;

    public TicTacToe() {
        setTitle("Tic-Tac-Toe - Multiplayer & AI");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildConfigPanel();
        buildGamePanel();

        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Configure player and click 'Start Game'", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(statusLabel, BorderLayout.CENTER);

        JButton restartButton = new JButton("Back to Settings");
        restartButton.setFont(new Font("Arial", Font.BOLD, 14));
        restartButton.addActionListener(e -> showConfigPanel());
        bottomPanel.add(restartButton, BorderLayout.SOUTH);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        add(bottomPanel, BorderLayout.SOUTH);
        showConfigPanel();
    }

    private void buildConfigPanel() {
        configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("Game Mode:"), gbc);

        modeBox = new JComboBox<>(new String[] {"Player vs Player", "Player vs AI (Computer)"});
        gbc.gridx = 1;
        modeBox.addActionListener(e -> {
            boolean vsAI = modeBox.getSelectedIndex() == 1;
            if (vsAI) p2Field.setText("Computer");
            else p2Field.setText("Player 2");
        });

        configPanel.add(modeBox, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        configPanel.add(new JLabel("Player 1 Name (X):"), gbc);
        p1Field = new JTextField("Player 1" , 15);
        gbc.gridx = 1;
        configPanel.add(p1Field, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        configPanel.add(new JLabel("Player 2 Name (0):"), gbc);
        p2Field = new JTextField("Player 2", 15);
        gbc.gridx = 1;
        configPanel.add(p2Field, gbc);

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e ->startGame());

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth =2;
        configPanel.add(startButton, gbc);
    }

    private void buildGamePanel() {
        gamePanel = new JPanel(new GridLayout(3, 3, 5, 5));
        gamePanel.setBackground(Color.DARK_GRAY);
        gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Font buttonFont = new Font("Arial", Font.BOLD, 60);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
            buttons[i][j] = new JButton("");
            buttons[i][j].setFont(buttonFont);
            buttons[i][j].setFocusPainted(false);
            buttons[i][j].setBackground(Color.WHITE);

            final int row = i;
            final int col = j;

            buttons[i][j].addActionListener(e -> handleCellClick(row, col));
            gamePanel.add(buttons[i][j]);
            }
        }
    }

    private void showConfigPanel() {
        gameActive = false;
        remove(gamePanel);
        add(configPanel, BorderLayout.CENTER);
        statusLabel.setText("Configure player and click 'Start Game'");
        revalidate();
        repaint();
    }

    private void startGame() {
        player1Name = p1Field.getText().trim().isEmpty() ? "Player 1" : p1Field.getText().trim();
        player2Name = p2Field.getText().trim().isEmpty() ? "Player 2" : p2Field.getText().trim();
        isVsAI = modeBox.getSelectedIndex() == 1;
        remove(configPanel);
        add(gamePanel, BorderLayout.CENTER);

        resetBoard();
        gameActive = true;
        revalidate();
        repaint();
    }

    private void resetBoard() {
        xTurn = true;
        movesCount = 0;
        statusLabel.setText(player1Name + "'s turn (X)'" );
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
                buttons[i][j].setBackground(Color.WHITE);
            }
        }
    }

    private void handleCellClick(int row, int col) {
        if (!gameActive) return;
        JButton btn = buttons[row][col];
        if (!btn.getText().equals("")) return;

        makeMove(row, col);

        if (!gameActive) return;
        if (isVsAI && !xTurn && gameActive) {
            Timer aiTimer = new Timer(400, e -> {
                makeAIMove();
                ((Timer)e.getSource()).stop();
            });
            aiTimer.setRepeats(false);
            aiTimer.start();
        }
    }

    private void makeMove(int row, int col) {
        JButton btn = buttons[row][col];
        String symbol = xTurn ? "X" : "0";
        btn.setText(symbol);
        btn.setForeground(xTurn ? Color.BLUE : Color.RED);
        movesCount++;

        String currentName = xTurn ? player1Name : player2Name;

        if (checkWin(symbol)) {
            statusLabel.setText(currentName + " (" + symbol +") Wins!" );
            disableBoard();
            gameActive = false;
        } else if (movesCount == 9) {
            statusLabel.setText("It's a Draw!");
            gameActive = false;
        } else {
            xTurn = !xTurn;
            String nextName = xTurn ? player1Name : player2Name;
            statusLabel.setText(nextName + "'s turn (" + (xTurn ? "X" : "0") + ")");
            }
    }

    private void makeAIMove() {
        if (!gameActive) return;
        int[] bestMove = findBestAIMove();
        if (bestMove != null) {
            makeMove(bestMove[0], bestMove[1]);
        }
    }

    private int[] findBestAIMove() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    buttons[i][j].setText("0");
                    boolean wins = checkWin("0");
                    buttons[i][j].setText("");
                    if (wins) return new int[]{i, j};
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    buttons[i][j].setText("X");
                    boolean blocksWin = checkWin("X");
                    buttons[i][j].setText("");
                    if (blocksWin) return new int[]{i, j};
                }
            }
        }

        if (buttons[1][1].getText().equals("")) {
            return new int[]{1, 1};
        }

        java.util.List<int[]> emptyCells = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        if (!emptyCells.isEmpty()) {
            Random rand = new Random();
            return emptyCells.get(rand.nextInt(emptyCells.size()));
        }
        return null;
    }

    private boolean checkWin(String symbol) {
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(symbol)
                && buttons[i][1].getText().equals(symbol)
                && buttons[i][2].getText().equals(symbol)) return true;

            if (buttons[0][i].getText().equals(symbol)
                && buttons[1][i].getText().equals(symbol)
                && buttons[2][i].getText().equals(symbol)) return true;
        }

        if (buttons[0][0].getText().equals(symbol)
            && buttons[1][1].getText().equals(symbol)
            && buttons[2][2].getText().equals(symbol)) return true;

        if (buttons[0][2].getText().equals(symbol)
            && buttons[1][1].getText().equals(symbol)
            && buttons[2][0].getText().equals(symbol)) return true;

        return false;
    }

    private void disableBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TicTacToe().setVisible(true);
        });
    }

    

}