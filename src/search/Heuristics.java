package search;

import model.GameState;
import model.Position;

/**
 * Collection of heuristic helpers.
 */
public final class Heuristics {

    private Heuristics() {
    }

    /**
     * Requirement 3: This is the Hamming distance heuristic.
     * I simply count how many tiles (R, G, B) are not in their goal positions.
     */
    public static int hamming(GameState current, GameState goal) {
        int distance = 0;

        Position rCurr = current.getRPos();
        Position gCurr = current.getGPos();
        Position bCurr = current.getBPos();

        if (!rCurr.equals(goal.getRPos())) {
            distance++;
        }
        if (!gCurr.equals(goal.getGPos())) {
            distance++;
        }
        if (!bCurr.equals(goal.getBPos())) {
            distance++;
        }

        return distance;
    }
}
