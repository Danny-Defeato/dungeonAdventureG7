package src.model;

/**
 * A Skeleton monster. Skeletons are balanced fighters with moderate hit
 * points and a fair chance to heal. Their statistics are read from the
 * database and passed in by the monster factory, so none are stored in this
 * class.
 *
 * @author Group 7 (John Nguyen)
 * @version 0.1
 */
public class Skeleton extends Monster {

    /**
     * Creates a Skeleton from statistics read from the database.
     *
     * @param name the Skeleton's name as stored in the database, not null or blank
     * @param stats the Skeleton's statistics, not null
     * @throws IllegalArgumentException if any value is out of range
     */
    public Skeleton(final String name, final MonsterStats stats) {
        super(name, stats);
    }
}