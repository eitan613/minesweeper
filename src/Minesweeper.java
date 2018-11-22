import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Random;

public class Minesweeper { //hashmap, point array, make them ints and bombs ++ neighbors
    private JFrame board;
    private JButton[][] buttons;
    MinesweeperModel mm;
    ImageIcon flag;

    public Minesweeper(){
        buttons = new JButton[10][10];
        board = new JFrame();
        board.setSize(500, 500);
        board.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        board.setLayout(new GridLayout(10, 10));
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setSize(board.getWidth() / 10, board.getHeight() / 10);
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
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                int finalRow = row;
                int finalCol = col;
                buttons[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        if (SwingUtilities.isRightMouseButton(e)){
                            if (buttons[finalRow][finalCol].getIcon() == null)
                                buttons[finalRow][finalCol].setIcon(flag);
                            else
                                buttons[finalRow][finalCol].setIcon(null);
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
        board.setVisible(true);
    }

    public static void main(String[] args) {
        Minesweeper game = new Minesweeper();
    }
    class MinesweeperModel {
        boolean[][] board;
        boolean[][] playedYet = new boolean[10][10];
        Random randomInt;
        boolean gameOver;
        ImageIcon bomb;

        public MinesweeperModel(){
            board = new boolean[10][10];
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
            for (int i = 0; i < 10; i++) {
                do {
                    row = randomInt.nextInt(10);
                    column = randomInt.nextInt(10);
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
            if ((row + 1 < 10) && board[row + 1][col]) //directly underneath
                total++;
            if ((row + 1 < 10) && ((col + 1 < 10)) && board[row + 1][col + 1]) //bottom right corner
                total++;
            if ((row + 1 < 10) && (col - 1 >= 0) && board[row + 1][col - 1]) //bottom left corner
                total++;
            if ((col + 1 < 10) && board[row][col + 1]) //right
                total++;
            if ((col - 1 >= 0) && board[row][col - 1]) //left
                total++;
            if ((row - 1 >= 0) && board[row - 1][col]) //directly on top
                total++;
            if ((col + 1 < 10) && (row - 1 >= 0) && board[row - 1][col + 1]) //top right corner
                total++;
            if ((row - 1 >= 0) && (col - 1 >= 0) && board[row - 1][col - 1]) //top left corner
                total++;
            return total;
        }

        public void buttonPressed(int row, int col) {
            if (board[row][col]) //bomb
            {
                for (int r = 0; r < 10; r++) {
                    for (int c = 0; c < 10; c++) {
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
                    if (col + 1 < 10 && !playedYet[row][col + 1])
                        buttonPressed(row, col + 1);//right
                    if (col - 1 >= 0 && !playedYet[row][col - 1])
                        buttonPressed(row, col - 1);//left
                    if ((col + 1 < 10) && (row + 1 < 10) && !playedYet[row + 1][col + 1])
                        buttonPressed(row + 1, col + 1);//bottom right corner
                    if ((row + 1 < 10) && !playedYet[row + 1][col])
                        buttonPressed(row + 1, col);//directly underneath
                    if ((row + 1 < 10) && (col - 1 >= 0) && !playedYet[row + 1][col - 1])
                        buttonPressed(row + 1, col - 1);//bottom left corner
                    if ((row - 1 >= 0) && (col - 1 >= 0) && !playedYet[row - 1][col - 1])
                        buttonPressed(row - 1, col - 1);//top left corner
                    if ((row - 1 >= 0) && !playedYet[row - 1][col])
                        buttonPressed(row - 1, col);//directly above
                    if ((row - 1 >= 0) && (col + 1 < 10) && !playedYet[row - 1][col + 1])
                        buttonPressed(row - 1, col + 1);//top right corner
                }
                int pressedButtons = 0;
                for (int r = 0; r < 10; r++) {
                    for (int c = 0; c < 10; c++) {
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
            String replay = JOptionPane.showInputDialog("Type Y to play again.");
            if (replay.equalsIgnoreCase("Y")) {
                board.dispose();
                Minesweeper game = new Minesweeper();
            }
            else
                board.dispose();
        } catch (NullPointerException e){
            board.dispose();
        }
    }
}