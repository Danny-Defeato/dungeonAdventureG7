package src.model;

import java.util.Random;
import java.io.Serial;

/** Marks the dungeon exit. The win check is made by the game model.
 *
 *
 * @author Nathan Chiu
 */
public class Exit extends RoomItem {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Creates an exit marker. */
    public Exit() {
        super('O');
    }

    @Override
    public String applyTo(final Hero theHero, final Random theRng) {
        return "";
    }

    @Override
    public boolean isCollectible() {
        return false;
    }
}
