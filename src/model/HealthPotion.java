package src.model;

import java.util.Random;
import java.io.Serial;

/** A healing potion.
 *
 * @author Nathan Chiu
 */
public class HealthPotion extends Potion {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Creates a healing potion. */
    public HealthPotion() {
        super('H');
    }

    @Override
    public String applyTo(final Hero theHero, final Random theRng) {
        theHero.addHealingPotion();
        return "You found a Healing Potion!";
    }
}
