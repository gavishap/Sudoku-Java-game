import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.Border;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

/**
 * Sudoku game class that creates a simple Sudoku board for users to play.
 */
public class SudokuGame {
    private static final int BOARD_SIZE = 9;
    private JTextField[][] inputFields = new JTextField[BOARD_SIZE][BOARD_SIZE];
    private JButton setButton;
    private JButton clearButton;
    private JPanel mainPanel;
    private JLabel errorMessage;

    /**
     * Constructor for the SudokuGame class.
     * Initializes UI components and sets up event listeners.
     */
    public SudokuGame() {
        createUIComponents();
        // Set up the "Set" button action listener
        setButton.addActionListener(e -> {
            if (hasBadInputs()) {
                errorMessage.setText("Cannot set the board. Fix invalid inputs first.");
            } else {
                errorMessage.setText("");
                for (int row = 0; row < BOARD_SIZE; row++) {
                    for (int col = 0; col < BOARD_SIZE; col++) {
                        if (!inputFields[row][col].getText().isEmpty()) {
                            inputFields[row][col].setEditable(false);
                        }
                    }
                }
            }
        });
        // Set up the "Clear" button action listener
        clearButton.addActionListener(e -> {
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    inputFields[row][col].setText("");
                    inputFields[row][col].setEditable(true);
                    inputFields[row][col].setBackground(Color.WHITE);
                }
            }
        });
        // Set up action listeners for input fields
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                final int currentRow = row;
                final int currentCol = col;
                inputFields[row][col].addActionListener(e -> {
                    int value;
                    try {
                        value = Integer.parseInt(inputFields[currentRow][currentCol].getText());
                    } catch (NumberFormatException exception) {
                        return;
                    }

                    if (value < 1 || value > 9 || !isValid(currentRow, currentCol, value)) {
                        inputFields[currentRow][currentCol].setBackground(Color.RED);
                    } else {
                        inputFields[currentRow][currentCol].setBackground(Color.GREEN);
                    }
                });
            }
        }
    }

    /**
     * Helper method to log messages to a file.
     * 
     * @param message The message to log.
     */
    private static void log(String message) {
        try {
            Files.write(Paths.get("SudokuGame.log"), (message + System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if there are any bad inputs on the Sudoku board.
     * 
     * @return true if there are bad inputs, false otherwise.
     */
    private boolean hasBadInputs() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                String text = inputFields[row][col].getText();
                if (!text.isEmpty()) {
                    int value;
                    try {
                        value = Integer.parseInt(text);
                    } catch (NumberFormatException exception) {
                        return true;
                    }

                    if (value < 1 || value > 9 || !isValid(row, col, value)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the given value can be placed in the specified cell.
     * 
     * @param row   The row of the cell.
     * @param col   The column of the cell.
     * @param value The value to check.
     * @return true if the value is valid, false otherwise.
     */
    private boolean isValid(int row, int col, int value) {
        // Check rows and columns for duplicates
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (i != col && inputFields[row][i].getText().equals(String.valueOf(value))) {
                return false;
            }
            if (i != row && inputFields[i][col].getText().equals(String.valueOf(value))) {
                return false;
            }
        }
        // Check the 3x3 box for duplicates
        int boxRow = row / 3 * 3;
        int boxCol = col / 3 * 3;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int boxRowIndex = boxRow + r;
                int boxColIndex = boxCol + c;

                if (boxRowIndex != row && boxColIndex != col
                        && inputFields[boxRowIndex][boxColIndex].getText().equals(String.valueOf(value))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * The main method that starts the Sudoku game.
     */
    public static void main(String[] args) {
        log("Starting SudokuGame...");
        JFrame frame = new JFrame("Sudoku");
        frame.setContentPane(new SudokuGame().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        log("SudokuGame is running...");
    }

    /**
     * Creates the UI components and sets up the layout.
     */
    private void createUIComponents() {
        // Set up the main panel
        mainPanel = new JPanel(new BorderLayout());
        // Set up the Sudoku grid panel
        JPanel gridPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        Color[] blockColors = { new Color(200, 200, 200), Color.WHITE };
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                inputFields[row][col] = new JTextField();
                inputFields[row][col].setPreferredSize(new Dimension(50, 50));
                inputFields[row][col].setHorizontalAlignment(JTextField.CENTER);

                int top = (row % 3 == 0) ? 2 : 1;
                int left = (col % 3 == 0) ? 2 : 1;
                int bottom = (row % 3 == 2) ? 2 : 1;
                int right = (col % 3 == 2) ? 2 : 1;

                inputFields[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
                inputFields[row][col].addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            JTextField source = (JTextField) e.getSource();
                            try {
                                int value = Integer.parseInt(source.getText());
                                if (value < 1 || value > 9) {
                                    errorMessage.setText("Invalid number. Please enter a number between 1 and 9.");
                                    e.consume();
                                } else {
                                    int currentRow = -1;
                                    int currentCol = -1;

                                    // Find the current row and col
                                    for (int row = 0; row < BOARD_SIZE; row++) {
                                        for (int col = 0; col < BOARD_SIZE; col++) {
                                            if (inputFields[row][col] == source) {
                                                currentRow = row;
                                                currentCol = col;
                                                break;
                                            }
                                        }
                                        if (currentRow != -1) {
                                            break;
                                        }
                                    }

                                    if (!isValid(currentRow, currentCol, value)) {
                                        errorMessage.setText("Invalid move. This number breaks the Sudoku rules.");
                                    } else {
                                        errorMessage.setText("");
                                    }
                                }
                            } catch (NumberFormatException exception) {
                                errorMessage.setText("Invalid input. Please enter a number between 1 and 9.");
                                e.consume();
                            }
                        }
                    }
                });
                int blockColorIndex = ((row / 3) % 2 + (col / 3)) % 2;
                inputFields[row][col].setBackground(blockColors[blockColorIndex]);
                gridPanel.add(inputFields[row][col]);
            }
        }
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        // Set up the bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Set up the button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Add the "Set" and "Clear" buttons
        setButton = new JButton("Set");
        buttonPanel.add(setButton);

        clearButton = new JButton("Clear");
        buttonPanel.add(clearButton);

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        // Set up the error message label
        errorMessage = new JLabel("");
        errorMessage.setForeground(Color.RED);

        JPanel errorMessagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        errorMessagePanel.add(errorMessage);

        bottomPanel.add(errorMessagePanel, BorderLayout.CENTER);
    }

}