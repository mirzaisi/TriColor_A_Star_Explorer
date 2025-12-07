package UI;

import model.GameState;
import search.Node;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Popup window with a canvas-style tree diagram of the search.
 */
public class SearchTreeDialog extends JDialog {

    private final StateTreePanel treePanel;
    private final JTextArea messageArea;

    public SearchTreeDialog(Frame owner) {
        super(owner, "A* Search Exploration", false);
        treePanel = new StateTreePanel();
        messageArea = new JTextArea(2, 60);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout());

        JScrollPane treeScroll = new JScrollPane(treePanel);
        add(treeScroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(8, 4));
        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        JButton zoomIn = new JButton("+");
        JButton zoomOut = new JButton("-");
        zoomIn.addActionListener(e -> treePanel.zoom(1.15));
        zoomOut.addActionListener(e -> treePanel.zoom(1/1.15));
        zoomPanel.add(new JLabel("Zoom:"));
        zoomPanel.add(zoomOut);
        zoomPanel.add(zoomIn);

        JScrollPane msgScroll = new JScrollPane(messageArea);
        msgScroll.setPreferredSize(new Dimension(200, 60));
        footer.add(zoomPanel, BorderLayout.WEST);
        footer.add(msgScroll, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        setSize(1000, 700);
        setLocationRelativeTo(getOwner());
    }

    public StateTreePanel getTreePanel() {
        return treePanel;
    }

    public void showMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            messageArea.setText(msg);
            messageArea.setCaretPosition(0);
        });
    }
    public void setInitialGoal(GameState initial, GameState goal) {
        treePanel.setInitialGoal(initial, goal);
    }

    public void addExpansion(Node chosen, List<Node> successors) {
        treePanel.addExpansion(chosen, successors);
    }

    public void ensureRoot(Node start) {
        treePanel.ensureRoot(start);
    }

    public void appendSolutionPath(List<GameState> path) {
        StringBuilder sb = new StringBuilder("Solution path: ");
        for (int i = 0; i < path.size(); i++) {
            GameState s = path.get(i);
            sb.append("Step ").append(i).append(" -> R").append(s.getRPos())
                    .append(" G").append(s.getGPos()).append(" B").append(s.getBPos());
            if (i < path.size() - 1) sb.append(" | ");
        }
        showMessage(sb.toString());
    }
}
