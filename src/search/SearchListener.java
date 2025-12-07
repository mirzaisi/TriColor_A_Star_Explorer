package search;

import java.util.List;

/**
 * Requirement 6: I use this to feed the GUI information about the chosen state
 * and the alternative successors at every expansion.
 */
public interface SearchListener {
    void onNodeExpanded(Node chosen, List<Node> successors);
}
