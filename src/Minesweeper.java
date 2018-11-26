import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Minesweeper {
    private JFrame frame;
    private JPanel board;
    private JButton[][] buttons;
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menu = new JMenu("Choose difficulty");
    private JMenuItem easy = new JMenuItem("Easy");
    private JMenuItem medium = new JMenuItem("Medium");
    private JMenuItem hard = new JMenuItem("Hard");
    private JLabel status;
    private ImageIcon flag;
    private int bombAmount;
    private int boardSize;
    public enum Difficulty{EASY, MEDIUM, HARD;}
    private Difficulty difficulty;
    private MinesweeperModel mm;

    public Minesweeper(Difficulty d){
        setDifficulty(d);
        setFrame();
        setBoard();
        setMenu();
        setStatus();
        setButtons();
        setFlagImage();
        frame.setVisible(true);
        gameBoard();
    }

    private void setFlagImage() {
        flag = new ImageIcon("src\\resources\\flag.png");
        Image img = flag.getImage();
        Image newimg = img.getScaledInstance( buttons[0][0].getWidth(), buttons[0][0].getHeight(),  java.awt.Image.SCALE_SMOOTH );
        flag = new ImageIcon(newimg);
    }

    private void setButtons() {
        buttons = new JButton[boardSize][boardSize];
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setSize(frame.getWidth() / boardSize, frame.getHeight() / boardSize);
                board.add(buttons[row][col]);
                int finalCol = col;
                int finalRow = row;
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
            }
        }
    }

    private void setStatus() {
        status = new JLabel(bombAmount+"");
        status.setHorizontalAlignment(JLabel.CENTER);
        frame.add(status,BorderLayout.NORTH);
    }

    private void setBoard() {
        board = new JPanel();
        board.setLayout(new GridLayout(boardSize, boardSize));
        frame.add(board);
    }

    private void setFrame() {
        frame = new JFrame();
        frame.setSize(1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setDifficulty(Difficulty d){
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
    }

    private void setMenu() {
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
    }

    public void gameBoard() {
        mm = new MinesweeperModel(boardSize,bombAmount,buttons,difficulty,frame,this);
        mm.setBoard();
    }

    public static void main(String[] args) {
        Minesweeper game = new Minesweeper(Difficulty.EASY);
    }
}