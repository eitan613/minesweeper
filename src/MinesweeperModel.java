import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class MinesweeperModel {
    private ArrayList<Point> surroundingButtons = new ArrayList<>(9);
    private boolean[][] board;
    private boolean[][] playedYet;
    private JButton[][] buttons;
    private JFrame frame;
    private Random randomInt;
    private boolean gameOver;
    private ImageIcon bomb;
    private int bombAmount;
    private int boardSize;
    private Minesweeper.Difficulty difficulty;
    private Minesweeper m;

    public MinesweeperModel(int boardSize, int bombAmount, JButton[][] buttons, Minesweeper.Difficulty difficulty, JFrame frame, Minesweeper m){
        this.boardSize = boardSize;
        this.bombAmount = bombAmount;
        this.buttons = buttons;
        this.difficulty = difficulty;
        this.frame = frame;
        this.m = m;
        board = new boolean[boardSize][boardSize];
        playedYet= new boolean[boardSize][boardSize];
        for (int r = -1; r < 2; r++)
            for (int c = -1; c < 2; c++)
                surroundingButtons.add(new Point(r,c));
        surroundingButtons.remove(4);
        randomInt = new Random();
        gameOver = false;
        bomb = new ImageIcon("src\\resources\\bomb.png");
        Image img = bomb.getImage();
        Image newimg = img.getScaledInstance( buttons[0][0].getWidth(), buttons[0][0].getHeight(),  java.awt.Image.SCALE_SMOOTH );
        bomb = new ImageIcon(newimg);
    }

    public void setBoard() {
        int row;
        int column;
        for (int i = 0; i < bombAmount; i++) {
            do {
                row = randomInt.nextInt(boardSize);
                column = randomInt.nextInt(boardSize);
            } while (board[row][column]);
            board[row][column] = true; //true == bomb
        }
    }

    private int checkSurroundingButtons(int row, int col) {
        int total = 0;
        int x,y;
        for (Point p : surroundingButtons) {
            x = (int)p.getX()+row;
            y = (int) p.getY()+col;
            if (x >= 0 && x < boardSize && y >= 0 && y < boardSize)
                if (board[x][y])
                    total++;
        }
        return total;
    }

    public void buttonPressed(int row, int col) {
        if (board[row][col]) //bomb
        {
            for (int r = 0; r < boardSize; r++) {
                for (int c = 0; c < boardSize; c++) {
                    if (board[r][c])
                        buttons[r][c].setIcon(bomb);
                }
            }
            gameOver = true;
            playAgain();
        }
        else if (!gameOver){
            int SurroundingBombs = checkSurroundingButtons(row, col);
            if (SurroundingBombs > 0)
                buttons[row][col].setText(SurroundingBombs + "");
            buttons[row][col].setEnabled(false);
            playedYet[row][col] = true;

            if (SurroundingBombs == 0) {
                for (Point p: surroundingButtons){
                    int x = (int) p.getX()+row;
                    int y = (int) p.getY()+col;
                    if (x >= 0 && x < boardSize && y >= 0 && y < boardSize && !playedYet[x][y])
                        buttonPressed(x,y);
                }
            }
            int pressedButtons = 0;
            for (int r = 0; r < boardSize; r++) {
                for (int c = 0; c < boardSize; c++) {
                    if (!board[r][c] && playedYet[r][c])
                        pressedButtons++;
                }
            }
            if (pressedButtons == 90)
            {
                playAgain();
            }
        }
    }

    private void playAgain() {
        try {
            String replay = JOptionPane.showInputDialog("Type play again to play again.");
            if (replay.equalsIgnoreCase("play again")) {
                frame.dispose();
                m = null;
                Minesweeper minesweeper = new Minesweeper(difficulty);
            }
            else{
                frame.dispose();
                m = null;
            }
        } catch (NullPointerException e){
            frame.dispose();
            m = null;
        }
    }
}