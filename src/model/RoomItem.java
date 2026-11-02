package src.model;

import java.io.Serializable;
import java.util.Random;

/** Abstract base class for everything that can sit in a room.
 *
 * @author Nathan Chiu
 */
public abstract class RoomItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final char mySymbol;

    /**
     * @param theSymbol the character used to draw this item
     */
    protected RoomItem(final char theSymbol) {
        mySymbol = theSymbol;
    }

    /** @return the display symbol */
    public char getSymbol() {
        return mySymbol;
    }

    /**
     * Applies this item to a hero who just entered the room.
     *
     * @param theHero the hero
     * @param theRng  the random source
     * @return a message for the player (empty if there is nothing to say)
     */
    public abstract String applyTo(Hero theHero, Random theRng);

    /** @return true if the item is picked up (removed from the room) when applied */
    public boolean isCollectible() {
        return true;
    }
}