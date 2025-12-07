package UI;

import model.GameState;
import model.Position;
import model.TileColor;

import javax.swing.*;
import java.awt.*;

/**
 * 3x3 clickable board.
 *
 * Requirement 1: the user uses this panel to define initial and goal states.
 */
public class BoardPanel extends JPanel {

    private final JButton[][] cells = new JButton[3][3];
    private final String title;

    public BoardPanel(String title) {
        this.title = title;
        setPreferredSize(new Dimension(300, 320));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout(0, 5));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // small gaps between cells so it looks less cramped
        JPanel grid = new JPanel(new GridLayout(3, 3, 2, 2));
        add(grid, BorderLayout.CENTER);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                JButton btn = new JButton("");
                btn.setFocusPainted(false);
                btn.setFont(btn.getFont().deriveFont(Font.BOLD, 18f));
                btn.setOpaque(true);
                btn.setBackground(Color.WHITE);

                // *** This is the key part: give each cell a decent size ***
                btn.setPreferredSize(new Dimension(80, 80));

                final int r = row;
                final int c = col;
                btn.addActionListener(e -> cycleCell(r, c));

                cells[row][col] = btn;
                grid.add(btn);
            }
        }
    }

    private void cycleCell(int row, int col) {
        String text = cells[row][col].getText();
        String next;
        if (text == null || text.isEmpty()) {
            next = "R";
        } else if ("R".equals(text)) {
            next = "G";
        } else if ("G".equals(text)) {
            next = "B";
        } else {
            next = "";
        }
        cells[row][col].setText(next);
        applyColor(row, col, next);
    }

    public void clearBoard() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                cells[r][c].setText("");
                applyColor(r, c, "");
            }
        }
    }

    /**
     * Reads the board and builds a GameState.
     *
     * Requirement 1: here I actually read the user's configuration and validate
     * that there is exactly one R, one G, and one B.
     *
     * @param nextTileForState nextTile value to store in the resulting GameState
     * @throws IllegalStateException when the configuration is invalid
     */
    public GameState buildGameState(TileColor nextTileForState) {
        Position rPos = null;
        Position gPos = null;
        Position bPos = null;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                String text = cells[row][col].getText();
                if ("R".equals(text)) {
                    if (rPos != null) {
                        throw new IllegalStateException("There can only be one R on the " + title + " board.");
                    }
                    rPos = new Position(row, col);
                } else if ("G".equals(text)) {
                    if (gPos != null) {
                        throw new IllegalStateException("There can only be one G on the " + title + " board.");
                    }
                    gPos = new Position(row, col);
                } else if ("B".equals(text)) {
                    if (bPos != null) {
                        throw new IllegalStateException("There can only be one B on the " + title + " board.");
                    }
                    bPos = new Position(row, col);
                }
            }
        }

        if (rPos == null || gPos == null || bPos == null) {
            throw new IllegalStateException("You need exactly one R, one G and one B on the " + title + " board.");
        }

        return new GameState(rPos, gPos, bPos, nextTileForState);
    }

    private void applyColor(int row, int col, String val) {
        JButton btn = cells[row][col];
        if (val == null || val.isEmpty()) {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.BLACK);
        } else if ("R".equals(val)) {
            btn.setBackground(new Color(220, 53, 69)); // red
            btn.setForeground(Color.WHITE);
        } else if ("G".equals(val)) {
            btn.setBackground(new Color(40, 167, 69)); // green
            btn.setForeground(Color.WHITE);
        } else if ("B".equals(val)) {
            btn.setBackground(new Color(0, 123, 255)); // blue
            btn.setForeground(Color.WHITE);
        }
    }
}
