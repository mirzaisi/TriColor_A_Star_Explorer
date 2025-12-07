package search;

import model.GameState;

/**
 * Internal node for A* search.
 */
public class Node implements Comparable<Node> {

    private final GameState state;
    private final Node parent;
    private final int gCost;  // path cost so far
    private final int hCost;  // heuristic
    private final int fCost;  // g + h

    public Node(GameState state, Node parent, int gCost, int hCost) {
        this.state = state;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
    }

    public GameState getState() {
        return state;
    }

    public Node getParent() {
        return parent;
    }

    public int getGCost() {
        return gCost;
    }

    public int getHCost() {
        return hCost;
    }

    public int getFCost() {
        return fCost;
    }

    @Override
    public int compareTo(Node other) {
        int cmp = Integer.compare(this.fCost, other.fCost);
        if (cmp != 0) return cmp;
        // Tie-breaker: prefer lower h, then lower g.
        cmp = Integer.compare(this.hCost, other.hCost);
        if (cmp != 0) return cmp;
        return Integer.compare(this.gCost, other.gCost);
    }
}
