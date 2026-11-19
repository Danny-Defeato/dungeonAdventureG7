package src.model;

import java.util.List;

/**
 * Creates the hero the player chose (SRS FR-NEW-4). Each hero class sets its
 * own starting statistics, so the factory only picks the class and passes on
 * the name. Every call returns a new, separate hero.
 *
 * @author Group 7 (Alex Douk)
 * @version 0.2
 */
public class HeroFactory {

    /** The class name of a Warrior. */
    private static final String WARRIOR = "Warrior";

    /** The class name of a Priestess. */
    private static final String PRIESTESS = "Priestess";

    /** The class name of a Thief. */
    private static final String THIEF = "Thief";

    /** The hero classes the factory knows how to build. */
    private static final List<String> TYPES = List.of(WARRIOR, PRIESTESS, THIEF);

    /**
     * Returns the hero classes this factory can build.
     *
     * @return an unmodifiable list of class names, in the order the new-game
     *         dialog should show them
     */
    public static List<String> getSupportedTypes() {
        return TYPES;
    }

    /**
     * Creates a hero of the given class with 0 healing potions, 0 vision
     * potions and no pillars.
     *
     * @param theType the class, one of {@link #getSupportedTypes()}; not blank
     * @param theName the hero's name, 1 to 20 characters after trimming
     * @return a new hero
     * @throws IllegalArgumentException if the type is blank or unsupported, or
     *         the name is not valid for a hero
     */
    public Hero create(final String theType, final String theName) {
        if (theType == null || theType.isBlank()) {
            throw new IllegalArgumentException("type must not be blank");
        }
        final Hero hero;
        switch (theType) {
            case WARRIOR:
                hero = new Warrior(theName);
                break;
            case PRIESTESS:
                hero = new Priestess(theName);
                break;
            case THIEF:
                hero = new Thief(theName);
                break;
            default:
                throw new IllegalArgumentException("Unsupported hero type: " + theType);
        }
        return hero;
    }
}
