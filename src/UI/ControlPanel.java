package UI;

import javax.swing.*;
import java.awt.*;

/**
 * Small panel holding the main control buttons.
 */
public class ControlPanel extends JPanel {

    private final JButton solveButton;
    private final JButton resetButton;

    public ControlPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        solveButton = new JButton("Solve with A*");
        resetButton = new JButton("Reset boards");

        add(solveButton);
        add(resetButton);

        JLabel info = new JLabel("Move limit: 10");
        add(info);

        JLabel howTo = new JLabel("How to use: Click a cell to cycle between R (Red), G (Green), B (Blue), and empty. Set exactly one R, one G, and one B on each board, then press 'Solve with A*'.");
        howTo.setForeground(new java.awt.Color(80, 80, 80));
        add(howTo);
    }

    public JButton getSolveButton() {
        return solveButton;
    }

    public JButton getResetButton() {
        return resetButton;
    }
}
