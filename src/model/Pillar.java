package src.model;

import java.util.List;
import java.util.Random;
import java.io.Serial;

/** One of the four Pillars of OO. The symbol is the first letter of the name (A, E, I, P).
 *
 *
 * @author Nathan Chiu
 */
public class Pillar extends RoomItem {
    @Serial
    private static final long serialVersionUID = 1L;
    /** The four pillar names, in order. */
    public static final List<String> NAMES =
            List.of("Abstraction", "Encapsulation", "Inheritance", "Polymorphism");
    /** Number of pillars a hero must collect. */
    public static final int TOTAL = NAMES.size();

    private final String myName;

    /**
     * @param theName one of {@link #NAMES}
     */
    public Pillar(final String theName) {
        super(theName.charAt(0));
        myName = theName;
    }

    /** @return the pillar's name */
    public String getName() {
        return myName;
    }

    @Override
    public String applyTo(final Hero theHero, final Random theRng) {
        theHero.addPillar(this);
        return "You found the Pillar of " + myName + "!";
    }
}
