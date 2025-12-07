package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a configuration of the puzzle:
 * positions of R, G, B, and whose turn it is to move next.
 */
public final class GameState {

    private final Position rPos;
    private final Position gPos;
    private final Position bPos;
    private final TileColor nextTile;

    // Directions: N, S, W, E, and 4 diagonals.
    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    public GameState(Position rPos, Position gPos, Position bPos, TileColor nextTile) {
        if (rPos == null || gPos == null || bPos == null || nextTile == null) {
            throw new IllegalArgumentException("Positions and nextTile must not be null");
        }
        // Just to be safe, makes sure tiles are not stacked on the same cell.
        if (rPos.equals(gPos) || rPos.equals(bPos) || gPos.equals(bPos)) {
            throw new IllegalArgumentException("Tiles cannot occupy the same cell");
        }
        this.rPos = rPos;
        this.gPos = gPos;
        this.bPos = bPos;
        this.nextTile = nextTile;
    }

    public Position getRPos() {
        return rPos;
    }

    public Position getGPos() {
        return gPos;
    }

    public Position getBPos() {
        return bPos;
    }

    public TileColor getNextTile() {
        return nextTile;
    }


    public TileColor getTileAt(int row, int col) {
        Position p = new Position(row, col);
        if (p.equals(rPos)) return TileColor.R;
        if (p.equals(gPos)) return TileColor.G;
        if (p.equals(bPos)) return TileColor.B;
        return null;
    }

    /**
     * Checks if this state matches the goal positions.
     */
    public boolean isGoal(GameState goal) {
        return rPos.equals(goal.rPos)
                && gPos.equals(goal.gPos)
                && bPos.equals(goal.bPos);
    }

    private boolean isOccupied(Position pos) {
        return pos.equals(rPos) || pos.equals(gPos) || pos.equals(bPos);
    }

    private static TileColor nextTurn(TileColor current) {
        // Requirement 4: this is where I enforce the R -> G -> B -> R turn order.
        switch (current) {
            case R:
                return TileColor.G;
            case G:
                return TileColor.B;
            case B:
            default:
                return TileColor.R;
        }
    }

    private Position getPositionFor(TileColor tile) {
        switch (tile) {
            case R:
                return rPos;
            case G:
                return gPos;
            case B:
            default:
                return bPos;
        }
    }

    private GameState moveTile(TileColor tile, Position newPos) {
        // Create a fresh state with that tile moved and nextTile advanced.
        Position newR = rPos;
        Position newG = gPos;
        Position newB = bPos;

        switch (tile) {
            case R:
                newR = newPos;
                break;
            case G:
                newG = newPos;
                break;
            case B:
                newB = newPos;
                break;
        }

        TileColor next = nextTurn(tile);
        return new GameState(newR, newG, newB, next);
    }

    /**
     * Generates all valid successor states for the tile whose turn it is.
     *
     * Requirement 2: this method creates moves in all 8 directions,
     * and only if the target cell is on the board and empty.
     * Requirement 4: only the tile indicated by nextTile is allowed to move.
     */
    public List<GameState> generateSuccessors() {
        List<GameState> successors = new ArrayList<>();
        Position currentPos = getPositionFor(nextTile);

        for (int[] dir : DIRECTIONS) {
            Position candidate = currentPos.translate(dir[0], dir[1]);
            if (!candidate.isInsideBoard()) {
                continue;
            }
            if (isOccupied(candidate)) {
                continue;
            }
            successors.add(moveTile(nextTile, candidate));
        }

        return successors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameState)) return false;
        GameState gameState = (GameState) o;
        return Objects.equals(rPos, gameState.rPos)
                && Objects.equals(gPos, gameState.gPos)
                && Objects.equals(bPos, gameState.bPos)
                && nextTile == gameState.nextTile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rPos, gPos, bPos, nextTile);
    }

    @Override
    public String toString() {
        return "R" + rPos + ", G" + gPos + ", B" + bPos + ", next=" + nextTile;
    }
}
