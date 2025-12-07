package search;

import model.GameState;

import java.util.*;

/**
 * A* search over the 3x3 puzzle state space.
 */
public class AStarSolver {

    private final GameState initial;
    private final GameState goal;
    private final int maxDepth;

    /**
     * @param initial starting state
     * @param goal    goal positions
     * @param maxDepth max number of moves (hard limit 10)
     */
    public AStarSolver(GameState initial, GameState goal, int maxDepth) {
        this.initial = initial;
        this.goal = goal;
        this.maxDepth = maxDepth;
    }

    public AStarSolver(GameState initial, GameState goal) {
        this(initial, goal, 10);
    }

    /**
     * Requirement 3: This is the A* implementation, using the Hamming distance
     * as our heuristic.
     *
     * Requirement 5: I stop expanding nodes once their depth (g) reaches maxDepth
     * and, if no goal is found by then, I report that to the user.
     *
     * Requirement 6: On every expansion I call the SearchListener so the GUI can
     * show the chosen state and its alternative successors.
     */
    public SearchResult solve(SearchListener listener) {
        PriorityQueue<Node> open = new PriorityQueue<>();
        Map<GameState, Integer> bestG = new HashMap<>();
        Set<GameState> closed = new HashSet<>();

        int h0 = Heuristics.hamming(initial, goal);
        Node start = new Node(initial, null, 0, h0);
        open.add(start);
        bestG.put(initial, 0);

        while (!open.isEmpty()) {
            Node current = open.poll();

            if (closed.contains(current.getState())) {
                continue;
            }
            closed.add(current.getState());

            if (current.getState().isGoal(goal)) {
                List<GameState> path = reconstructPath(current);
                String msg = "Goal found in " + current.getGCost() + " move(s).";
                return new SearchResult(true, path, msg);
            }

            // Requirement 5: depth / move limit (10).
            if (current.getGCost() >= maxDepth) {
                // We reached the cap for this branch, don't expand it further.
                // We'll keep going with other nodes still in the queue (if any).
                continue;
            }

            List<GameState> succStates = current.getState().generateSuccessors();
            List<Node> successors = new ArrayList<>();

            for (GameState succState : succStates) {
                int tentativeG = current.getGCost() + 1;
                Integer knownBest = bestG.get(succState);

                if (knownBest != null && knownBest <= tentativeG) {
                    // We already have an equal or better path to this state.
                    continue;
                }

                int h = Heuristics.hamming(succState, goal);
                Node succNode = new Node(succState, current, tentativeG, h);
                bestG.put(succState, tentativeG);
                open.add(succNode);
                successors.add(succNode);
            }

            if (listener != null) {
                listener.onNodeExpanded(current, successors);
            }
        }

        String msg = "No solution found within " + maxDepth + " moves from the initial state.";
        return new SearchResult(false, Collections.emptyList(), msg);
    }

    private List<GameState> reconstructPath(Node goalNode) {
        List<GameState> reversed = new ArrayList<>();
        Node current = goalNode;
        while (current != null) {
            reversed.add(current.getState());
            current = current.getParent();
        }
        Collections.reverse(reversed);
        return reversed;
    }
}
