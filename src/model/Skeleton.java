package src.model;

/**
 * A Skeleton monster. Skeletons are balanced fighters with moderate hit
 * points and a fair chance to heal. Their statistics are read from the
 * database and passed in by the monster factory, so none are stored in this
 * class.
 *
 * @author Group 7 (John Nguyen)
 * @version 0.2
 */
public class Skeleton extends Monster {

    /**
     * Creates a Skeleton from statistics read from the database.
     *
     * @param theName the Skeleton's name as stored in the database, not null or blank
     * @param theStats the Skeleton's statistics, not null
     * @throws IllegalArgumentException if any value is out of range
     */
    public Skeleton(final String theName, final MonsterStats theStats) {
        super(theName, theStats);
    }
}