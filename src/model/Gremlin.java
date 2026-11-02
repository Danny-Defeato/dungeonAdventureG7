package src.model;

/**
 * A Gremlin monster. Gremlins are fast enemies that heal often,
 * but their hit points are the lowest of the three.
 * Their statistics are read from the database and passed in
 * by the monster factory, so none are stored in this class.
 *
 * @author Group 7 (John Nguyen)
 * @version 0.1
 */
public class Gremlin extends Monster {

    /**
     * Creates a Gremlin from statistics read from the database.
     *
     * @param name the Gremlin's name as stored in the database, not null or blank
     * @param stats the Gremlin's statistics, not null
     * @throws IllegalArgumentException if any value is out of range
     */
    public Gremlin(final String name, final MonsterStats stats) {
        super(name, stats);
    }
}
