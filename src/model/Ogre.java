package src.model;

/**
 * An Ogre monster. Ogres are slow and have a lot of hit points,
 * and they hit hard, but they only rarely heal themselves.
 * Their statistics are read from the database and passed in
 * by the monster factory, so none are stored in this class.
 *
 * @author Group 7 (John Nguyen)
 * @version 0.1
 */
public class Ogre extends Monster {

    /**
     * Creates an Ogre from statistics read from the database.
     *
     * @param name the Ogre's name as stored in the database, not null or blank
     * @param stats the Ogre's statistics, not null
     * @throws IllegalArgumentException if any value is out of range
     */
    public Ogre(final String name, final MonsterStats stats) {
        super(name, stats);
    }
}