package search;

import model.GameState;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the outcome of running A*.
 */
public class SearchResult {

    private final boolean solved;
    private final List<GameState> path;
    private final String message;

    public SearchResult(boolean solved, List<GameState> path, String message) {
        this.solved = solved;
        this.path = path == null ? Collections.emptyList() : path;
        this.message = message;
    }

    public boolean isSolved() {
        return solved;
    }

    public List<GameState> getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }
}
