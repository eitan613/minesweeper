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
    private ImageIcon bomb;
    private int bombAmount;
    private int boardSize;

    public void placeBombIcons() {
        Point location = new Point();
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                location.setLocation(c,r);
                if (mm.isBomb(location))
                    buttons[c][r].setIcon(bomb);
            }
        }
    }

    public enum Difficulty{EASY, MEDIUM, HARD;}
    private Difficulty difficulty;
    private MinesweeperModel mm;

    public Minesweeper(Difficulty d){
        mm = new MinesweeperModel(d);
        bombAmount = mm.getBombAmount();
        boardSize = mm.getBoardSize();
        setFrame();
        setBoard();
        setMenu();
        setStatus();
        setButtons();
        flag = setImage("src\\resources\\flag.png");
        bomb = setImage("src\\resources\\bomb.png");
        frame.setVisible(true);
        mm.getGUI(frame,buttons,this);
        mm.setBoard();
    }

    private ImageIcon setImage(String filename){
        ImageIcon image = new ImageIcon(filename);
        Image img = image.getImage();
        Image newimg = img.getScaledInstance( buttons[0][0].getWidth(), buttons[0][0].getHeight(),  java.awt.Image.SCALE_SMOOTH );
        return new ImageIcon(newimg);
    }

    private void setButtons() {
        buttons = new JButton[boardSize][boardSize];
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                buttons[col][row] = new JButton();
                buttons[col][row].setSize(frame.getWidth() / boardSize, frame.getHeight() / boardSize);
                board.add(buttons[col][row]);
                int finalCol = col;
                int finalRow = row;
                buttons[col][row].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        if (SwingUtilities.isRightMouseButton(e)){
                            if (buttons[finalCol][finalRow].getIcon() == null){
                                buttons[finalCol][finalRow].setIcon(flag);
                                bombAmount--;
                                status.setText(bombAmount+"");
                            }
                            else{
                                buttons[finalCol][finalRow].setIcon(null);
                                bombAmount++;
                                status.setText(bombAmount+"");
                            }
                        }
                        else{
                            if (buttons[finalCol][finalRow].getIcon() != null){
                                buttons[finalCol][finalRow].setIcon(null);
                                mm.buttonPressed(new Point(finalCol,finalRow));
                            }
                            else
                                mm.buttonPressed(new Point(finalCol,finalRow));
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

    public static void main(String[] args) {
        Minesweeper game = new Minesweeper(Difficulty.EASY);
    }
}