package UI;

import model.GameState;
import model.TileColor;
import search.AStarSolver;
import search.SearchResult;

import javax.swing.*;
import java.awt.*;

/**
 * Main window tying everything together.
 */
public class MainFrame extends JFrame {

    private final BoardPanel initialBoardPanel;
    private final BoardPanel goalBoardPanel;
    private final ControlPanel controlPanel;

    public MainFrame() {
        super("3x3 Tile Puzzle - A* Search");
        initialBoardPanel = new BoardPanel("Initial state");
        goalBoardPanel = new BoardPanel("Goal state");
        controlPanel = new ControlPanel();

        buildUi();
        wireActions();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JSplitPane boardsSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                initialBoardPanel,
                goalBoardPanel);
        boardsSplit.setResizeWeight(0.5);

        add(boardsSplit, BorderLayout.CENTER);

        add(controlPanel, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void wireActions() {
        controlPanel.getSolveButton().addActionListener(e -> onSolve());
        controlPanel.getResetButton().addActionListener(e -> onReset());
    }

    private void onReset() {
        initialBoardPanel.clearBoard();
        goalBoardPanel.clearBoard();
    }

    /**
     * Requirement 1: This method reads both boards, validates them,
     * and starts the A* solver.
     */
    private void onSolve() {
        GameState initialState;
        GameState goalState;
        try {
            // Requirement 1: read initial/goal states from the boards.
            initialState = initialBoardPanel.buildGameState(TileColor.R);
            goalState = goalBoardPanel.buildGameState(TileColor.R);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Invalid configuration",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        controlPanel.getSolveButton().setEnabled(false);
        try {
            SearchTreeDialog treeDialog = new SearchTreeDialog(this);
            GuiSearchListener listener = new GuiSearchListener();
            listener.attachDialog(treeDialog);
            treeDialog.setInitialGoal(initialState, goalState);
            treeDialog.setVisible(true);

            // Requirement 3 & 5: A* with Hamming heuristic and 10-move limit.
            AStarSolver solver = new AStarSolver(initialState, goalState, 10);
            SearchResult result = solver.solve(listener);

            treeDialog.showMessage(result.getMessage());

            if (result.isSolved()) {
                treeDialog.appendSolutionPath(result.getPath());
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        result.getMessage(),
                        "No Solution Within Limit",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } finally {
            controlPanel.getSolveButton().setEnabled(true);
        }
    }

}
