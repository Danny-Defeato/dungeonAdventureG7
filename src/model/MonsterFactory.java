package src.model;

import java.util.List;
import java.util.Objects;
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
 * @version 0.1
 */
public class MonsterFactory {

    /** How much stronger a guardian is: +25% hit points and damage. */
    public static final double GUARDIAN_MULTIPLIER = 1.25;

    /** The monster types the factory knows how to build. */
    private static final List<String> TYPES = List.of("Gremlin", "Ogre", "Skeleton");

    /** The database the statistics are read from. */
    private final SQLiteDatabase db;

    /**
     * Creates a factory that reads statistics from the given database.
     *
     * @param db the open database, not null
     */
    public MonsterFactory(final SQLiteDatabase db) {
        this.db = Objects.requireNonNull(db, "db");
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
     * @param type the type, such as "Ogre"; not blank
     * @param guardian true to build the stronger guardian version
     * @return a new monster
     * @throws IllegalArgumentException if the type is blank, unsupported, or
     *         has no row in the database
     */
    public Monster create(final String type, final boolean guardian) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type must not be blank");
        }
        if (!TYPES.contains(type)) {
            throw new IllegalArgumentException("Unsupported monster type: " + type);
        }
        MonsterStats stats = db.loadMonsterStats(type);
        if (guardian) {
            stats = boost(stats);
        }
        switch (type) {
            case "Ogre":
                return new Ogre(type, stats);
            case "Gremlin":
                return new Gremlin(type, stats);
            default:
                return new Skeleton(type, stats);
        }
    }

    /**
     * Creates a monster of a randomly chosen type.
     *
     * @param rng the shared source of randomness, not null
     * @param guardian true to build the stronger guardian version
     * @return a new monster
     */
    public Monster createRandom(final Random rng, final boolean guardian) {
        Objects.requireNonNull(rng, "rng");
        return create(TYPES.get(rng.nextInt(TYPES.size())), guardian);
    }

    private static MonsterStats boost(final MonsterStats s) {
        return new MonsterStats(scale(s.getHitPoints()), scale(s.getMinDamage()),
                scale(s.getMaxDamage()), s.getAttackSpeed(), s.getChanceToHit(),
                s.getChanceToHeal(), s.getMinHeal(), s.getMaxHeal());
    }

    private static int scale(final int value) {
        return (int) Math.round(value * GUARDIAN_MULTIPLIER);
    }
}
