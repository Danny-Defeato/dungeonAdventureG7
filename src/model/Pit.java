package src.model;

import java.io.Serial;
import java.util.Random;

/**
 * A pit in a room. Every time the hero enters the room the pit deals a random
 * 1 to 20 points of damage (SRS FR-ROOM-3). A pit is never picked up, so it
 * stays in the room. If the damage reduces the hero to 0 hit points, the game
 * model ends the game (FR-ROOM-4).
 *
 * @author Group 7 (Alex Douk)
 * @version 0.1
 */
public class Pit extends RoomItem {

    @Serial
    private static final long serialVersionUID = 1L;

    /** The least damage a pit can deal. */
    public static final int MIN_DAMAGE = 1;

    /** The most damage a pit can deal. */
    public static final int MAX_DAMAGE = 20;

    /** Creates a pit. It is drawn as X. */
    public Pit() {
        super('X');
    }

    /**
     * Damages the hero by a random amount from {@link #MIN_DAMAGE} to
     * {@link #MAX_DAMAGE}.
     *
     * @param theHero the hero who entered the room, not null
     * @param theRng the shared source of randomness, not null
     * @return a message saying how much damage the hero took
     * @throws IllegalArgumentException if the hero or the random source is null
     */
    @Override
    public String applyTo(final Hero theHero, final Random theRng) {
        if (theHero == null || theRng == null) {
            throw new IllegalArgumentException("hero and rng must not be null");
        }
        final int damage = MIN_DAMAGE + theRng.nextInt(MAX_DAMAGE - MIN_DAMAGE + 1);
        theHero.takeDamage(damage);
        return "You fell into a pit and lost " + damage + " hit points!";
    }

    /** @return false, because a pit stays in the room */
    @Override
    public boolean isCollectible() {
        return false;
    }
}
