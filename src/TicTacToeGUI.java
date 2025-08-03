import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


interface ITicTacToe {
    void initializeBoard();
    boolean makeMove(int row, int col, char player) throws InvalidMoveException;
    boolean checkWin();
    boolean checkDraw();
    char getCurrentPlayer();
    void switchPlayer();
    char[][] getBoard();
}


class InvalidMoveException extends Exception {
    public InvalidMoveException(String message) {
        super(message);
    }
}


class TicTacToeGame implements ITicTacToe {
    private char[][] board;
    private char currentPlayer;

    public TicTacToeGame() {
        board = new char[3][3];
        currentPlayer = 'X';
        initializeBoard();
    }

    @Override
    public void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
    }

    @Override
    public boolean makeMove(int row, int col, char player) throws InvalidMoveException {
        if (row < 0 || row >= 3 || col < 0 || col >= 3) {
            throw new InvalidMoveException("Invalid position!");
        }
        if (board[row][col] != '-') {
            throw new InvalidMoveException("Position already occupied!");
        }
        if (player != currentPlayer) {
            throw new InvalidMoveException("Not your turn!");
        }

        board[row][col] = player;
        return true;
    }

    @Override
    public boolean checkWin() {

        for (int i = 0; i < 3; i++) {
            if (board[i][0] != '-' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return true;
            }
        }


        for (int j = 0; j < 3; j++) {
            if (board[0][j] != '-' && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                return true;
            }
        }


        if (board[0][0] != '-' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return true;
        }

        if (board[0][2] != '-' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return true;
        }

        return false;
    }

    @Override
    public boolean checkDraw() {
        if (checkWin()) return false;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public char getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    @Override
    public char[][] getBoard() {
        return board;
    }
}


public class TicTacToeGUI extends JFrame {
    private ITicTacToe game;
    private JButton[][] buttons;
    private JLabel statusLabel;

    public TicTacToeGUI() {
        game = new TicTacToeGame();
        createGUI();
        centerWindow();
    }

    private void centerWindow() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }

    private void createGUI() {
        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(400, 450); // Slightly taller to accommodate all components
        setResizable(false);


        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel("Player X's Turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.NORTH);


        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttons = new JButton[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBackground(Color.WHITE);
                final int row = i;
                final int col = j;
                buttons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleButtonClick(row, col);
                    }
                });
                boardPanel.add(buttons[i][j]);
            }
        }
        add(boardPanel, BorderLayout.CENTER);


        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JButton resetButton = new JButton("Reset Game");
        resetButton.setFont(new Font("Arial", Font.PLAIN, 14));
        resetButton.addActionListener(e -> resetGame());
        controlPanel.add(resetButton);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void handleButtonClick(int row, int col) {
        try {
            game.makeMove(row, col, game.getCurrentPlayer());
            buttons[row][col].setText(String.valueOf(game.getCurrentPlayer()));
            buttons[row][col].setEnabled(false);

            if (game.checkWin()) {
                showGameResult("Player " + game.getCurrentPlayer() + " wins!");
            } else if (game.checkDraw()) {
                showGameResult("The game is a draw!");
            } else {
                game.switchPlayer();
                statusLabel.setText("Player " + game.getCurrentPlayer() + "'s Turn");
            }
        } catch (InvalidMoveException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Move", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showGameResult(String message) {
        statusLabel.setText(message);
        disableAllButtons();


        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog(this, "Game Over");
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void disableAllButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    private void resetGame() {
        game = new TicTacToeGame();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
            }
        }
        statusLabel.setText("Player X's Turn");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TicTacToeGUI game = new TicTacToeGUI();
            game.setVisible(true);
        });
    }
}