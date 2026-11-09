package src.model;

import java.io.Serial;

/** Abstract parent of the potions a hero can pick up.
 *
 * @author Nathan Chiu
 */
public abstract class Potion extends RoomItem {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @param theSymbol the display symbol
     */
    protected Potion(final char theSymbol) {
        super(theSymbol);
    }
}
