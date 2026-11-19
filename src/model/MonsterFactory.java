package src.model;

import java.util.List;
import java.util.Random;

/**
 * Creates monsters from the statistics stored in the SQLite database
 * (SRS FR-MON-3). Every call returns a new, separate monster object.
 *
 * <p>Guardian monsters, which stand in rooms with a pillar or the exit, are
 * stronger: their hit points and damage range are raised by
 * {@link #GUARDIAN_MULTIPLIER}. Attack speed, chance to hit and the heal
 * statistics are unchanged.
 *
 * @author Group 7 (Alex Douk)
 * @version 0.2
 */
public class MonsterFactory {

    /** How much stronger a guardian is: +25% hit points and damage. */
    public static final double GUARDIAN_MULTIPLIER = 1.25;

    /** The type name of an Ogre, as stored in the database. */
    private static final String OGRE = "Ogre";

    /** The type name of a Gremlin, as stored in the database. */
    private static final String GREMLIN = "Gremlin";

    /** The type name of a Skeleton, as stored in the database. */
    private static final String SKELETON = "Skeleton";

    /** The monster types the factory knows how to build. */
    private static final List<String> TYPES = List.of(GREMLIN, OGRE, SKELETON);

    /** The database the statistics are read from. */
    private final SQLiteDatabase myDatabase;

    /**
     * Creates a factory that reads statistics from the given database.
     *
     * @param theDatabase the open database, not null
     * @throws IllegalArgumentException if the database is null
     */
    public MonsterFactory(final SQLiteDatabase theDatabase) {
        if (theDatabase == null) {
            throw new IllegalArgumentException("database must not be null");
        }
        myDatabase = theDatabase;
    }

    /**
     * Returns the monster types this factory can build.
     *
     * @return an unmodifiable list of type names
     */
    public static List<String> getSupportedTypes() {
        return TYPES;
    }

    /**
     * Creates a monster of the given type. The monster's name is its type.
     *
     * @param theType the type, such as "Ogre"; not blank
     * @param theGuardian true to build the stronger guardian version
     * @return a new monster
     * @throws IllegalArgumentException if the type is blank, unsupported, or
     *         has no row in the database
     */
    public Monster create(final String theType, final boolean theGuardian) {
        if (theType == null || theType.isBlank()) {
            throw new IllegalArgumentException("type must not be blank");
        }
        if (!TYPES.contains(theType)) {
            throw new IllegalArgumentException("Unsupported monster type: " + theType);
        }
        MonsterStats stats = myDatabase.loadMonsterStats(theType);
        if (theGuardian) {
            stats = boost(stats);
        }
        final Monster monster;
        switch (theType) {
            case OGRE:
                monster = new Ogre(theType, stats);
                break;
            case GREMLIN:
                monster = new Gremlin(theType, stats);
                break;
            default:
                monster = new Skeleton(theType, stats);
                break;
        }
        return monster;
    }

    /**
     * Creates a monster of a randomly chosen type.
     *
     * @param theRng the shared source of randomness, not null
     * @param theGuardian true to build the stronger guardian version
     * @return a new monster
     * @throws IllegalArgumentException if the random source is null
     */
    public Monster createRandom(final Random theRng, final boolean theGuardian) {
        if (theRng == null) {
            throw new IllegalArgumentException("rng must not be null");
        }
        return create(TYPES.get(theRng.nextInt(TYPES.size())), theGuardian);
    }

    private static MonsterStats boost(final MonsterStats theStats) {
        return new MonsterStats(scale(theStats.getHitPoints()),
                scale(theStats.getMinDamage()), scale(theStats.getMaxDamage()),
                theStats.getAttackSpeed(), theStats.getChanceToHit(),
                theStats.getChanceToHeal(), theStats.getMinHeal(),
                theStats.getMaxHeal());
    }

    private static int scale(final int theValue) {
        return (int) Math.round(theValue * GUARDIAN_MULTIPLIER);
    }
}
