package src.model;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * A grid of {@link Room}s with one entrance, one exit and the hero's current
 * position. Dungeons are normally made by {@link DungeonBuilder}.
 *
 * <p>The class is serializable so a whole game can be saved (SRS 3.10). It
 * imports no Swing or AWT classes.
 *
 * @author Group 7 (Alex Douk)
 * @version 0.2
 */
public class Dungeon implements Serializable {

    private static final long serialVersionUID = 1L;

    /** How many rooms away a vision potion reveals (1 gives a 3 x 3 area). */
    private static final int VISION_RADIUS = 1;

    /** How many text lines each room's picture has (see {@link Room#toString()}). */
    private static final int ROOM_PICTURE_LINES = 3;

    /** The line break used in room pictures and in the dungeon map. */
    private static final String LINE_BREAK = "\n";

    /** The rooms, indexed [row][column]. */
    private final Room[][] myRooms;

    /** The room that holds the entrance. */
    private final Room myEntrance;

    /** The room that holds the exit. */
    private final Room myExit;

    /** The hero's current row. */
    private int myHeroRow;

    /** The hero's current column. */
    private int myHeroCol;

    /**
     * Creates a dungeon with the hero standing in the entrance room, which is
     * marked as visited.
     *
     * @param theRooms a non-empty rectangular grid of rooms, none null
     * @param theEntrance the entrance room, which must be in the grid
     * @param theExit the exit room, which must be in the grid
     * @throws IllegalArgumentException if any argument is null, or the grid is
     *         empty, not rectangular, or does not contain the entrance and exit
     */
    Dungeon(final Room[][] theRooms, final Room theEntrance, final Room theExit) {
        if (theRooms == null) {
            throw new IllegalArgumentException("rooms must not be null");
        }
        if (theRooms.length == 0 || theRooms[0] == null || theRooms[0].length == 0) {
            throw new IllegalArgumentException("dungeon must not be empty");
        }
        if (theEntrance == null || theExit == null) {
            throw new IllegalArgumentException("entrance and exit must not be null");
        }
        final int cols = theRooms[0].length;
        myRooms = new Room[theRooms.length][cols];
        for (int r = 0; r < theRooms.length; r++) {
            if (theRooms[r] == null || theRooms[r].length != cols) {
                throw new IllegalArgumentException("dungeon must be rectangular");
            }
            for (int c = 0; c < cols; c++) {
                if (theRooms[r][c] == null) {
                    throw new IllegalArgumentException("room must not be null");
                }
                myRooms[r][c] = theRooms[r][c];
            }
        }
        myEntrance = theEntrance;
        myExit = theExit;
        if (!contains(theEntrance) || !contains(theExit)) {
            throw new IllegalArgumentException("entrance and exit must be in the grid");
        }
        myHeroRow = theEntrance.getRow();
        myHeroCol = theEntrance.getCol();
        theEntrance.setVisited(true);
    }

    /** @return the number of rows */
    public int getRows() {
        return myRooms.length;
    }

    /** @return the number of columns */
    public int getCols() {
        return myRooms[0].length;
    }

    /**
     * Returns the room at a position.
     *
     * @param theRow the row, from 0
     * @param theCol the column, from 0
     * @return the room
     * @throws IllegalArgumentException if the position is outside the dungeon
     */
    public Room getRoom(final int theRow, final int theCol) {
        if (!inBounds(theRow, theCol)) {
            throw new IllegalArgumentException("no room at " + theRow + "," + theCol);
        }
        return myRooms[theRow][theCol];
    }

    /** @return the room that holds the entrance */
    public Room getEntrance() {
        return myEntrance;
    }

    /** @return the room that holds the exit */
    public Room getExit() {
        return myExit;
    }

    /** @return the hero's current row */
    public int getHeroRow() {
        return myHeroRow;
    }

    /** @return the hero's current column */
    public int getHeroCol() {
        return myHeroCol;
    }

    /** @return the room the hero is standing in */
    public Room getHeroRoom() {
        return myRooms[myHeroRow][myHeroCol];
    }

    /**
     * Moves the hero through a door of the current room and marks the new room
     * as visited. Nothing changes if the room has no door that way.
     *
     * @param theDirection the way to move, not null
     * @return the room the hero moved into, or null if there was no door
     * @throws IllegalArgumentException if the direction is null
     */
    public Room moveHero(final Direction theDirection) {
        if (theDirection == null) {
            throw new IllegalArgumentException("direction must not be null");
        }
        Room result = null;
        final int r = myHeroRow + theDirection.getRowDelta();
        final int c = myHeroCol + theDirection.getColDelta();
        if (getHeroRoom().hasDoor(theDirection) && inBounds(r, c)) {
            myHeroRow = r;
            myHeroCol = c;
            result = myRooms[r][c];
            result.setVisited(true);
        }
        return result;
    }

    /**
     * Reveals the hero's room and the rooms around it, as a vision potion does
     * (FR-POT-2). Up to eight neighbors are revealed, fewer when the hero is at
     * the edge of the dungeon.
     *
     * @return the number of rooms revealed, including the hero's own room
     */
    public int revealAroundHero() {
        int revealed = 0;
        for (int dr = -VISION_RADIUS; dr <= VISION_RADIUS; dr++) {
            for (int dc = -VISION_RADIUS; dc <= VISION_RADIUS; dc++) {
                final int r = myHeroRow + dr;
                final int c = myHeroCol + dc;
                if (inBounds(r, c)) {
                    myRooms[r][c].setRevealed(true);
                    revealed++;
                }
            }
        }
        return revealed;
    }

    /**
     * Tells whether any room is a dead end, meaning it has exactly one door.
     *
     * @return true if at least one room has a single door
     */
    public boolean hasDeadEnd() {
        for (final Room[] row : myRooms) {
            for (final Room room : row) {
                if (room.getDoorCount() == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Tells whether the dungeon can be completed: the exit and all four
     * pillars can be reached from the entrance by walking through doors.
     *
     * @return true if everything needed to win is reachable
     */
    public boolean isTraversable() {
        final Set<Room> seen = new HashSet<>();
        final Deque<Room> todo = new ArrayDeque<>();
        seen.add(myEntrance);
        todo.add(myEntrance);
        while (!todo.isEmpty()) {
            final Room room = todo.poll();
            for (final Direction d : room.getDoors()) {
                final int r = room.getRow() + d.getRowDelta();
                final int c = room.getCol() + d.getColDelta();
                if (inBounds(r, c) && seen.add(myRooms[r][c])) {
                    todo.add(myRooms[r][c]);
                }
            }
        }
        int pillars = 0;
        for (final Room room : seen) {
            if (room.hasPillar()) {
                pillars++;
            }
        }
        return seen.contains(myExit) && pillars == Pillar.TOTAL;
    }

    /**
     * Draws the whole dungeon, each room as the 3 x 3 picture from
     * {@link Room#toString()}. Used by the hidden test menu option and at
     * the end of the game.
     *
     * @return the picture, one line per row of characters
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Room[] row : myRooms) {
            final String[][] lines = new String[row.length][];
            for (int c = 0; c < row.length; c++) {
                lines[c] = row[c].toString().split(LINE_BREAK);
            }
            for (int line = 0; line < ROOM_PICTURE_LINES; line++) {
                for (final String[] roomLines : lines) {
                    sb.append(roomLines[line]);
                }
                sb.append(LINE_BREAK);
            }
        }
        return sb.toString();
    }

    private boolean inBounds(final int theRow, final int theCol) {
        return theRow >= 0 && theRow < myRooms.length
                && theCol >= 0 && theCol < myRooms[0].length;
    }

    private boolean contains(final Room theRoom) {
        return inBounds(theRoom.getRow(), theRoom.getCol())
                && myRooms[theRoom.getRow()][theRoom.getCol()] == theRoom;
    }
}
