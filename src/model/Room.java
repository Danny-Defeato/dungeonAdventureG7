package src.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.io.Serial;

/** One room of the dungeon: doors, items, an optional monster and what the player
 * knows about it.
 *
 * @author Nathan Chiu
 */
public class Room implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int myRow;
    private final int myCol;
    private final Set<Direction> myDoors = EnumSet.noneOf(Direction.class);
    private final List<RoomItem> myItems = new ArrayList<>();
    private Monster myMonster;
    private boolean myVisited;
    private boolean myRevealed;

    /**
     * @param theRow the row in the dungeon
     * @param theCol the column in the dungeon
     */
    public Room(final int theRow, final int theCol) {
        myRow = theRow;
        myCol = theCol;
    }

    /** @return row */
    public int getRow() {
        return myRow;
    }

    /** @return column */
    public int getCol() {
        return myCol;
    }

    /**
     * Adds a door on one side.
     *
     * @param theDirection the side
     */
    public void addDoor(final Direction theDirection) {
        myDoors.add(theDirection);
    }

    /**
     * @param theDirection a side
     * @return true if there is a door on that side
     */
    public boolean hasDoor(final Direction theDirection) {
        return myDoors.contains(theDirection);
    }

    /** @return number of doors */
    public int getDoorCount() {
        return myDoors.size();
    }

    /** @return a copy of the set of doors */
    public Set<Direction> getDoors() {
        return myDoors.isEmpty() ? EnumSet.noneOf(Direction.class) : EnumSet.copyOf(myDoors);
    }

    /**
     * @param theItem the item to place in this room
     */
    public void addItem(final RoomItem theItem) {
        myItems.add(theItem);
    }

    /**
     * @param theItem the item to remove
     */
    public void removeItem(final RoomItem theItem) {
        myItems.remove(theItem);
    }

    /** @return a copy of the item list */
    public List<RoomItem> getItems() {
        return new ArrayList<>(myItems);
    }

    /** @return true if this is the entrance */
    public boolean hasEntrance() {
        return hasItemOfType(Entrance.class);
    }

    /** @return true if this is the exit */
    public boolean hasExit() {
        return hasItemOfType(Exit.class);
    }

    /** @return true if this room holds a pillar */
    public boolean hasPillar() {
        return hasItemOfType(Pillar.class);
    }

    private boolean hasItemOfType(final Class<? extends RoomItem> theType) {
        for (final RoomItem item : myItems) {
            if (theType.isInstance(item)) {
                return true;
            }
        }
        return false;
    }

    /** @return the monster in this room, or null */
    public Monster getMonster() {
        return myMonster;
    }

    /**
     * @param theMonster the monster, or null to remove it
     */
    public void setMonster(final Monster theMonster) {
        myMonster = theMonster;
    }

    /** @return true once the hero has been here */
    public boolean isVisited() {
        return myVisited;
    }

    /**
     * @param theVisited whether the hero has been here
     */
    public void setVisited(final boolean theVisited) {
        myVisited = theVisited;
    }

    /** @return true if a vision potion has revealed this room */
    public boolean isRevealed() {
        return myRevealed;
    }

    /**
     * @param theRevealed whether the room is revealed
     */
    public void setRevealed(final boolean theRevealed) {
        myRevealed = theRevealed;
    }

    /**
     * @return M for several items, the item's symbol for one item, or a space when empty
     */
    public char getSymbol() {
        if (myItems.isEmpty()) {
            return ' ';
        }
        return myItems.size() > 1 ? 'M' : myItems.get(0).getSymbol();
    }

    /**
     * Builds the 3 x 3 text picture of this room. A wall is drawn as * and a door
     * as - (north/south) or | (east/west).
     *
     * @return three lines separated by newlines
     */
    @Override
    public String toString() {
        return "*" + (hasDoor(Direction.NORTH) ? '-' : '*') + "*\n"
                + (hasDoor(Direction.WEST) ? '|' : '*') + getSymbol()
                + (hasDoor(Direction.EAST) ? '|' : '*') + "\n"
                + "*" + (hasDoor(Direction.SOUTH) ? '-' : '*') + "*";
    }
}
