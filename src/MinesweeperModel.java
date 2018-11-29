import javax.swing.*;
import java.awt.*;
import java.util.*;

public class MinesweeperModel {
    private ArrayList<Point> surroundingButtons = new ArrayList<>(9);
    private ArrayList<Point> mines = new ArrayList<>();
    private ArrayList<Point> alreadyClicked = new ArrayList<>(90);
    private Point neighbor = new Point();
    private JButton[][] buttons;
    private JFrame frame;
    private Random randomInt = new Random();
    private int bombAmount;
    private int boardSize;
    private Minesweeper.Difficulty difficulty;
    private Minesweeper minesweeper;

    public MinesweeperModel(Minesweeper.Difficulty d){
        setDifficulty(d);
        for (int r = -1; r < 2; r++)
            for (int c = -1; c < 2; c++)
                surroundingButtons.add(new Point(c,r));
        surroundingButtons.remove(4);//0,0=self
    }

    private void setDifficulty(Minesweeper.Difficulty d) {
        difficulty = d;
        if (difficulty == Minesweeper.Difficulty.EASY){
            boardSize = 10;
            bombAmount = 10;
        }
        else if (difficulty == Minesweeper.Difficulty.MEDIUM){
            boardSize = 16;
            bombAmount = 40;
        }
        else if (difficulty == Minesweeper.Difficulty.HARD){
            boardSize = 24;
            bombAmount = 99;
        }
    }

    public void setBoard() {
        int row, column;
        for (int i = 0; i < bombAmount; i++){
            do{
                row = randomInt.nextInt(boardSize);
                column = randomInt.nextInt(boardSize);
            }while(mines.contains(new Point(column,row)));
            Point newMine = new Point(column,row);
            mines.add(newMine);
        }
    }

    private int checkSurroundingButtons(Point pointPressed) {
        int total = 0;
        for (Point offset : surroundingButtons) {
            neighbor.setLocation(offset.getX()+pointPressed.getX(), offset.getY()+pointPressed.getY());
                if (mines.contains(neighbor))
                    total++;
        }
        return total;
    }

    private boolean withinBounds(Point point) {
        return point.getY() < boardSize && point.getY() >= 0 && point.getX() < boardSize && point.getX() >= 0;
    }

    public void buttonPressed(Point pointPressed) {
        if (mines.contains(pointPressed)){
            minesweeper.placeBombIcons();
            playAgain();
        }
        else{
            int surroundingBombs = checkSurroundingButtons(pointPressed);
            int row = (int) pointPressed.getY();
            int col = (int) pointPressed.getX();
            if (surroundingBombs > 0)
                buttons[col][row].setText(surroundingBombs + "");
            buttons[col][row].setEnabled(false);
            alreadyClicked.add(pointPressed);

            if (surroundingBombs == 0) {
                for (Point offset: surroundingButtons){
                    neighbor.setLocation(offset.getX()+pointPressed.getX(), offset.getY()+pointPressed.getY());
                    if (withinBounds(neighbor) && !alreadyClicked.contains(neighbor))
                        buttonPressed(new Point(neighbor));
                }
            }

            if (alreadyClicked.size() == boardSize-bombAmount)
                playAgain();
        }
    }

    private void playAgain() {
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

    public int getBoardSize() {
        return boardSize;
    }

    public int getBombAmount() {
        return bombAmount;
    }

    public void getGUI(JFrame frame, JButton[][] buttons, Minesweeper minesweeper) {
        this.frame = frame;
        this.buttons = buttons;
        this.minesweeper = minesweeper;
    }

    public boolean isBomb(Point location) {
        return mines.contains(location);
    }
}