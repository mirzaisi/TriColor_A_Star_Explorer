package UI;

import model.GameState;
import model.TileColor;
import search.Node;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple tree/graph visualizer for the search.
 */
public class StateTreePanel extends JPanel {

    private static final int NODE_WIDTH = 170;
    private static final int NODE_HEIGHT = 80;
    private static final int H_SPACING = 40;
    private static final int V_SPACING = 100;
    private static final int MARGIN = 40;

    private final Map<GameState, VizNode> nodes = new HashMap<>();
    private final List<VizEdge> edges = new ArrayList<>();
    private int maxDepth = 0;
    private double scale = 1.0;
    private GameState initialState;
    private GameState goalState;

    public StateTreePanel() {
        setBackground(Color.WHITE);
    }

    public synchronized void ensureRoot(Node root) {
        if (!nodes.containsKey(root.getState())) {
            VizNode vn = new VizNode(root, 0);
            nodes.put(root.getState(), vn);
            maxDepth = 0;
            relayout();
        }
    }

    public synchronized void setInitialGoal(GameState initial, GameState goal) {
        this.initialState = initial;
        this.goalState = goal;
    }

    public synchronized void addExpansion(Node parent, List<Node> successors) {
        ensureRoot(parent);
        VizNode parentViz = nodes.get(parent.getState());
        int depth = parentViz.depth + 1;

        for (Node succ : successors) {
            VizNode child = nodes.get(succ.getState());
            if (child == null) {
                child = new VizNode(succ, depth);
                nodes.put(succ.getState(), child);
            }
            edges.add(new VizEdge(parentViz, child));
            maxDepth = Math.max(maxDepth, child.depth);
        }
        relayout();
        repaint();
    }

    private void relayout() {
        // For each depth, spread nodes horizontally.
        Map<Integer, List<VizNode>> levels = new HashMap<>();
        for (VizNode n : nodes.values()) {
            levels.computeIfAbsent(n.depth, d -> new ArrayList<>()).add(n);
        }
        int width = getWidth() == 0 ? 900 : getWidth();
        for (Map.Entry<Integer, List<VizNode>> entry : levels.entrySet()) {
            int depth = entry.getKey();
            List<VizNode> levelNodes = entry.getValue();
            int count = levelNodes.size();
            int totalWidth = count * NODE_WIDTH + (count - 1) * H_SPACING;
            int startX = Math.max(MARGIN, (width - totalWidth) / 2);
            for (int i = 0; i < levelNodes.size(); i++) {
                VizNode n = levelNodes.get(i);
                int x = startX + i * (NODE_WIDTH + H_SPACING);
                int y = MARGIN + depth * (NODE_HEIGHT + V_SPACING);
                n.bounds = new Rectangle(x, y, NODE_WIDTH, NODE_HEIGHT);
            }
        }
        int neededHeight = MARGIN * 2 + (maxDepth + 1) * (NODE_HEIGHT + V_SPACING);
        int neededWidth = Math.max(width, MARGIN * 2 + getMaxPerLevel(levels) * (NODE_WIDTH + H_SPACING));
        int scaledW = (int) Math.round(neededWidth * scale);
        int scaledH = (int) Math.round(neededHeight * scale);
        setPreferredSize(new Dimension(scaledW, scaledH));
        revalidate();
    }

    private int getMaxPerLevel(Map<Integer, List<VizNode>> levels) {
        int max = 1;
        for (List<VizNode> l : levels.values()) {
            max = Math.max(max, l.size());
        }
        return max;
    }

    @Override
    protected synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.scale(scale, scale);

        // Draw edges first
        g2.setStroke(new BasicStroke(1.2f));
        g2.setColor(new Color(90, 90, 90));
        for (VizEdge e : edges) {
            Rectangle a = e.from.bounds;
            Rectangle b = e.to.bounds;
            if (a == null || b == null) continue;
            int x1 = a.x + a.width / 2;
            int y1 = a.y + a.height;
            int x2 = b.x + b.width / 2;
            int y2 = b.y;
            int midX = (x1 + x2) / 2;
            int ctrlOffset = (x2 - x1) / 4;
            int ctrlY = (y1 + y2) / 2 - 20;
            java.awt.geom.QuadCurve2D q = new java.awt.geom.QuadCurve2D.Float();
            q.setCurve(x1, y1, midX + ctrlOffset, ctrlY, x2, y2);
            g2.draw(q);
            drawArrowHead(g2, midX + ctrlOffset, ctrlY, x2, y2);
        }

        // Draw nodes
        for (VizNode n : nodes.values()) {
            if (n.bounds == null) continue;
            paintNode(g2, n);
        }
        g2.dispose();
    }

    private void paintNode(Graphics2D g2, VizNode n) {
        Rectangle r = n.bounds;
        Color frame = new Color(60, 100, 160);
        Color fill = new Color(230, 240, 255);
        if (initialState != null && n.node.getState().equals(initialState)) {
            frame = new Color(0, 123, 255);
            fill = new Color(220, 235, 255);
        }
        if (goalState != null && n.node.getState().equals(goalState)) {
            frame = new Color(40, 167, 69);
            fill = new Color(223, 240, 223);
        }
        g2.setColor(fill);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g2.setColor(frame);
        g2.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);

        String title = "g=" + n.node.getGCost() + " h=" + n.node.getHCost() + " f=" + n.node.getFCost();
        FontMetrics fm = g2.getFontMetrics();
        int tx = r.x + 8;
        int ty = r.y + fm.getAscent() + 6;
        g2.drawString(title, tx, ty);
        drawMiniBoard(g2, n.node.getState(), r.x + 8, ty + 8);
    }

    private void drawMiniBoard(Graphics2D g2, GameState s, int x, int y) {
        int cell = 18;
        g2.setColor(new Color(180, 180, 180));
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int cx = x + c * (cell + 2);
                int cy = y + r * (cell + 2);
                g2.drawRect(cx, cy, cell, cell);
                TileColor t = s.getTileAt(r, c);
                if (t != null) {
                    g2.setColor(colorFor(t));
                    g2.fillRect(cx + 2, cy + 2, cell - 3, cell - 3);
                    g2.setColor(Color.BLACK);
                    g2.drawString(t.name(), cx + 4, cy + cell - 4);
                    g2.setColor(new Color(180, 180, 180));
                }
            }
        }
        g2.setColor(new Color(90, 90, 90));
        g2.drawString("next=" + s.getNextTile(), x + 3, y + 3 * (cell + 2) + 14);
    }

    private Color colorFor(TileColor t) {
        switch (t) {
            case R: return new Color(220, 53, 69);
            case G: return new Color(40, 167, 69);
            case B: return new Color(0, 123, 255);
            default: return Color.LIGHT_GRAY;
        }
    }

    private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2) {
        double phi = Math.toRadians(25);
        int barb = 10;
        double dy = y2 - y1;
        double dx = x2 - x1;
        double theta = Math.atan2(dy, dx);
        double rho = theta + phi;
        for (int j = 0; j < 2; j++) {
            double x = x2 - barb * Math.cos(rho);
            double y = y2 - barb * Math.sin(rho);
            g2.drawLine(x2, y2, (int) x, (int) y);
            rho = theta - phi;
        }
    }

    public synchronized void zoom(double factor) {
        scale = Math.max(0.5, Math.min(2.0, scale * factor));
        relayout();
        repaint();
    }

    private static class VizNode {
        final Node node;
        final int depth;
        Rectangle bounds;

            VizNode(Node node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }

    private static class VizEdge {
        final VizNode from;
        final VizNode to;
        VizEdge(VizNode from, VizNode to) {
            this.from = from;
            this.to = to;
        }
    }
}
