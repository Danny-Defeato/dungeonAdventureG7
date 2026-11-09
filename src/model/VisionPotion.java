package src.model;

import java.util.Random;
import java.io.Serial;

/** A vision potion.
 *
 * @author Nathan Chiu
 */
public class VisionPotion extends Potion {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Creates a vision potion. */
    public VisionPotion() {
        super('V');
    }

    @Override
    public String applyTo(final Hero theHero, final Random theRng) {
        theHero.addVisionPotion();
        return "You found a Vision Potion!";
    }
}