package src.model;

import java.util.List;

/**
 * Creates the hero the player chose (SRS FR-NEW-4). Each hero class sets its
 * own starting statistics, so the factory only picks the class and passes on
 * the name. Every call returns a new, separate hero.
 *
 * @author Group 7 (Alex Douk)
 * @version 0.1
 */
public class HeroFactory {

    /** The hero classes the factory knows how to build. */
    private static final List<String> TYPES = List.of("Warrior", "Priestess", "Thief");

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
     * @param type the class, one of {@link #getSupportedTypes()}; not blank
     * @param name the hero's name, 1 to 20 characters after trimming
     * @return a new hero
     * @throws IllegalArgumentException if the type is blank or unsupported, or
     *         the name is not valid for a hero
     */
    public Hero create(final String type, final String name) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type must not be blank");
        }
        final Hero hero;
        switch (type) {
            case "Warrior":
                hero = new Warrior(name);
                break;
            case "Priestess":
                hero = new Priestess(name);
                break;
            case "Thief":
                hero = new Thief(name);
                break;
            default:
                throw new IllegalArgumentException("Unsupported hero type: " + type);
        }
        return hero;
    }
}
