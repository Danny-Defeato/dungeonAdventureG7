package src.model;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A grid of {@link Room}s with one entrance, one exit and the hero's current
 * position. Dungeons are normally made by {@link DungeonBuilder}.
 *
 * <p>The class is serializable so a whole game can be saved (SRS 3.10). It
 * imports no Swing or AWT classes.
 *
 * @author Group 7 (Alex Douk)
 * @version 0.1
 */
public class Dungeon implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The rooms, indexed [row][column]. */
    private final Room[][] rooms;

    /** The room that holds the entrance. */
    private final Room entrance;

    /** The room that holds the exit. */
    private final Room exit;

    /** The hero's current row. */
    private int heroRow;

    /** The hero's current column. */
    private int heroCol;

    /**
     * Creates a dungeon with the hero standing in the entrance room, which is
     * marked as visited.
     *
     * @param rooms a non-empty rectangular grid of rooms, none null
     * @param entrance the entrance room, which must be in the grid
     * @param exit the exit room, which must be in the grid
     * @throws IllegalArgumentException if the grid is empty, not rectangular,
     *         or does not contain the entrance and exit
     */
    Dungeon(final Room[][] rooms, final Room entrance, final Room exit) {
        Objects.requireNonNull(rooms, "rooms");
        if (rooms.length == 0 || rooms[0].length == 0) {
            throw new IllegalArgumentException("dungeon must not be empty");
        }
        final int cols = rooms[0].length;
        this.rooms = new Room[rooms.length][cols];
        for (int r = 0; r < rooms.length; r++) {
            if (rooms[r].length != cols) {
                throw new IllegalArgumentException("dungeon must be rectangular");
            }
            for (int c = 0; c < cols; c++) {
                this.rooms[r][c] = Objects.requireNonNull(rooms[r][c], "room");
            }
        }
        this.entrance = Objects.requireNonNull(entrance, "entrance");
        this.exit = Objects.requireNonNull(exit, "exit");
        if (!contains(entrance) || !contains(exit)) {
            throw new IllegalArgumentException("entrance and exit must be in the grid");
        }
        heroRow = entrance.getRow();
        heroCol = entrance.getCol();
        entrance.setVisited(true);
    }

    /** @return the number of rows */
    public int getRows() {
        return rooms.length;
    }

    /** @return the number of columns */
    public int getCols() {
        return rooms[0].length;
    }

    /**
     * Returns the room at a position.
     *
     * @param row the row, from 0
     * @param col the column, from 0
     * @return the room
     * @throws IllegalArgumentException if the position is outside the dungeon
     */
    public Room getRoom(final int row, final int col) {
        if (!inBounds(row, col)) {
            throw new IllegalArgumentException("no room at " + row + "," + col);
        }
        return rooms[row][col];
    }

    /** @return the room that holds the entrance */
    public Room getEntrance() {
        return entrance;
    }

    /** @return the room that holds the exit */
    public Room getExit() {
        return exit;
    }

    /** @return the hero's current row */
    public int getHeroRow() {
        return heroRow;
    }

    /** @return the hero's current column */
    public int getHeroCol() {
        return heroCol;
    }

    /** @return the room the hero is standing in */
    public Room getHeroRoom() {
        return rooms[heroRow][heroCol];
    }

    /**
     * Moves the hero through a door of the current room and marks the new room
     * as visited. Nothing changes if the room has no door that way.
     *
     * @param direction the way to move, not null
     * @return the room the hero moved into, or null if there was no door
     */
    public Room moveHero(final Direction direction) {
        Objects.requireNonNull(direction, "direction");
        Room result = null;
        final int r = heroRow + direction.getRowDelta();
        final int c = heroCol + direction.getColDelta();
        if (getHeroRoom().hasDoor(direction) && inBounds(r, c)) {
            heroRow = r;
            heroCol = c;
            result = rooms[r][c];
            result.setVisited(true);
        }
        return result;
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
        seen.add(entrance);
        todo.add(entrance);
        while (!todo.isEmpty()) {
            final Room room = todo.poll();
            for (final Direction d : room.getDoors()) {
                final int r = room.getRow() + d.getRowDelta();
                final int c = room.getCol() + d.getColDelta();
                if (inBounds(r, c) && seen.add(rooms[r][c])) {
                    todo.add(rooms[r][c]);
                }
            }
        }
        int pillars = 0;
        for (final Room room : seen) {
            if (room.hasPillar()) {
                pillars++;
            }
        }
        return seen.contains(exit) && pillars == Pillar.TOTAL;
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
        for (int r = 0; r < rooms.length; r++) {
            final String[][] lines = new String[rooms[r].length][];
            for (int c = 0; c < rooms[r].length; c++) {
                lines[c] = rooms[r][c].toString().split("\n");
            }
            for (int line = 0; line < 3; line++) {
                for (int c = 0; c < lines.length; c++) {
                    sb.append(lines[c][line]);
                }
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    private boolean inBounds(final int row, final int col) {
        return row >= 0 && row < rooms.length && col >= 0 && col < rooms[0].length;
    }

    private boolean contains(final Room room) {
        return inBounds(room.getRow(), room.getCol())
                && rooms[room.getRow()][room.getCol()] == room;
    }
}
