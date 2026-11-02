package src.model;

/**
 * The four compass directions a hero can move through a door.
 *
 * @author Nathan Chiu
 */
public enum Direction {
    /** Up the map (row - 1). */
    NORTH(-1, 0),
    /** Down the map (row + 1). */
    SOUTH(1, 0),
    /** Right on the map (col + 1). */
    EAST(0, 1),
    /** Left on the map (col - 1). */
    WEST(0, -1);

    private final int myRowDelta;
    private final int myColDelta;

    Direction(final int theRowDelta, final int theColDelta) {
        myRowDelta = theRowDelta;
        myColDelta = theColDelta;
    }

    /** @return the change in row when moving in this direction */
    public int getRowDelta() {
        return myRowDelta;
    }

    /** @return the change in column when moving in this direction */
    public int getColDelta() {
        return myColDelta;
    }

    /** @return the direction pointing the opposite way */
    public Direction opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }
}