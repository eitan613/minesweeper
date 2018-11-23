import javafx.print.PrinterJob;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Minesweeper { //reorganize, hashmap, point array, make them ints and bombs ++ neighbors
    private JFrame frame;
    private JPanel board;
    private JButton[][] buttons;
    private int boardSize;
    private MinesweeperModel mm;
    private ImageIcon flag;
    private enum Difficulty{EASY, MEDIUM, HARD}
    private Difficulty difficulty;
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menu = new JMenu("Choose difficulty");
    private JMenuItem easy = new JMenuItem("Easy");
    private JMenuItem medium = new JMenuItem("Medium");
    private JMenuItem hard = new JMenuItem("Hard");
    private int bombAmount;
    private JLabel status;

    public Minesweeper(Difficulty d){
        difficulty = d;
        if (difficulty == Difficulty.EASY){
            boardSize = 10;
            bombAmount = 10;
        }
        else if (difficulty == Difficulty.MEDIUM){
            boardSize = 16;
            bombAmount = 40;
        }
        else if (difficulty == Difficulty.HARD){
            boardSize = 24;
            bombAmount = 99;
        }
        buttons = new JButton[boardSize][boardSize];
        frame = new JFrame();
        board = new JPanel();
        status = new JLabel(bombAmount+"");
        menuBar.add(menu);
        easy.addActionListener(e -> {
            difficulty = Difficulty.EASY;
            frame.dispose();
            Minesweeper minesweeper = new Minesweeper(difficulty);
        });
        menu.add(easy);
        medium.addActionListener(e -> {
            difficulty = Difficulty.MEDIUM;
            frame.dispose();
            Minesweeper minesweeper = new Minesweeper(difficulty);
        });
        menu.add(medium);
        hard.addActionListener(e -> {
            difficulty = Difficulty.HARD;
            frame.dispose();
            Minesweeper minesweeper = new Minesweeper(difficulty);
        });
        menu.add(hard);
        frame.setJMenuBar(menuBar);
        frame.setSize(1000, 800);
        frame.add(status,BorderLayout.NORTH);
        status.setHorizontalAlignment(JLabel.CENTER);
        frame.add(board);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        board.setLayout(new GridLayout(boardSize, boardSize));
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setSize(frame.getWidth() / boardSize, frame.getHeight() / boardSize);
            }
        }
        flag = new ImageIcon("src\\resources\\flag.png");
        Image img = flag.getImage();
        Image newimg = img.getScaledInstance( buttons[0][0].getWidth(), buttons[0][0].getHeight(),  java.awt.Image.SCALE_SMOOTH );
        flag = new ImageIcon(newimg);
        gameBoard();
    }

    public void gameBoard() {
        mm = new MinesweeperModel();
        mm.setBoard();
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                int finalRow = row;
                int finalCol = col;
                buttons[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        if (SwingUtilities.isRightMouseButton(e)){
                            if (buttons[finalRow][finalCol].getIcon() == null){
                                buttons[finalRow][finalCol].setIcon(flag);
                                bombAmount--;
                                status.setText(bombAmount+"");
                            }
                            else{
                                buttons[finalRow][finalCol].setIcon(null);
                                bombAmount++;
                                status.setText(bombAmount+"");
                            }
                        }
                        else{
                            if (buttons[finalRow][finalCol].getIcon() != null){
                                buttons[finalRow][finalCol].setIcon(null);
                                mm.buttonPressed(finalRow, finalCol);
                            }
                            else
                                mm.buttonPressed(finalRow,finalCol);
                        }
                    }
                });
                board.add(buttons[row][col]);
            }
        }
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Minesweeper game = new Minesweeper(Difficulty.EASY);
    }
    class MinesweeperModel {
        private boolean[][] board;
        private boolean[][] playedYet = new boolean[boardSize][boardSize];
        private Random randomInt;
        private boolean gameOver;
        private ImageIcon bomb;

        public MinesweeperModel(){
            board = new boolean[boardSize][boardSize];
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

            /*//double for loop (x-1 through x+1) or point array
            //https://github.com/JudahSchwartz/MineSweeper/blob/master/src/CodeBehind.java
            Point[] neighbors = new Point[8];
            neighbors[0] = new Point(-1,-1);
            neighbors[1] = new Point(-1,0);
            neighbors[2] = new Point(-1,+1);
            neighbors[3] = new Point(0,-1);
            neighbors[4] = new Point(0,+1);
            neighbors[5] = new Point(+1,-1);
            neighbors[6] = new Point(+1,0);
            neighbors[7] = new Point(+1,+1);
            for (Point p:neighbors) {
                Point currentButton = new Point(col,row);
                currentButton.translate(p.x,p.y);
                if (currentButton.y > 0 && currentButton.y < board.length && currentButton.x > 0 && currentButton.x
                        <board.length)
                    if (board[currentButton.y][currentButton.x])
                        total++;
            }*/
            int total = 0;
            if ((row + 1 < boardSize) && board[row + 1][col]) //directly underneath
                total++;
            if ((row + 1 < boardSize) && ((col + 1 < boardSize)) && board[row + 1][col + 1]) //bottom right corner
                total++;
            if ((row + 1 < boardSize) && (col - 1 >= 0) && board[row + 1][col - 1]) //bottom left corner
                total++;
            if ((col + 1 < boardSize) && board[row][col + 1]) //right
                total++;
            if ((col - 1 >= 0) && board[row][col - 1]) //left
                total++;
            if ((row - 1 >= 0) && board[row - 1][col]) //directly on top
                total++;
            if ((col + 1 < boardSize) && (row - 1 >= 0) && board[row - 1][col + 1]) //top right corner
                total++;
            if ((row - 1 >= 0) && (col - 1 >= 0) && board[row - 1][col - 1]) //top left corner
                total++;
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
                    if (col + 1 < boardSize && !playedYet[row][col + 1])
                        buttonPressed(row, col + 1);//right
                    if (col - 1 >= 0 && !playedYet[row][col - 1])
                        buttonPressed(row, col - 1);//left
                    if ((col + 1 < boardSize) && (row + 1 < boardSize) && !playedYet[row + 1][col + 1])
                        buttonPressed(row + 1, col + 1);//bottom right corner
                    if ((row + 1 < boardSize) && !playedYet[row + 1][col])
                        buttonPressed(row + 1, col);//directly underneath
                    if ((row + 1 < boardSize) && (col - 1 >= 0) && !playedYet[row + 1][col - 1])
                        buttonPressed(row + 1, col - 1);//bottom left corner
                    if ((row - 1 >= 0) && (col - 1 >= 0) && !playedYet[row - 1][col - 1])
                        buttonPressed(row - 1, col - 1);//top left corner
                    if ((row - 1 >= 0) && !playedYet[row - 1][col])
                        buttonPressed(row - 1, col);//directly above
                    if ((row - 1 >= 0) && (col + 1 < boardSize) && !playedYet[row - 1][col + 1])
                        buttonPressed(row - 1, col + 1);//top right corner
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
    }
    public void playAgain(){
        try {
            String replay = JOptionPane.showInputDialog("Type play again to play again.");
            if (replay.equalsIgnoreCase("play again")) {
                frame.dispose();
                Minesweeper minesweeper = new Minesweeper(difficulty);
            }
            else
                frame.dispose();
        } catch (NullPointerException e){
            frame.dispose();
        }
    }
}