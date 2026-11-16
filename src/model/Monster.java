package src.model;

import java.util.Random;

/**
 * Abstract parent of the monsters. A monster adds a chance to heal itself
 * after being hit to a {@link DungeonCharacter}. Each monster class (Ogre,
 * Gremlin and Skeleton) supplies its own statistics.
 *
 * @author Group 7 (John Nguyen)
 * @version 0.2
 */
public abstract class Monster extends DungeonCharacter {

    /** The chance the monster heals after being hit, from 0.0 to 1.0. */
    private final double myChanceToHeal;

    /** The fewest hit points a heal restores. */
    private final int myMinHeal;

    /** The most hit points a heal restores. */
    private final int myMaxHeal;

    /**
     * Creates a monster.
     *
     * @param theName the monster's name, not null or blank
     * @param theHitPoints the starting and maximum hit points, above 0
     * @param theMinDamage the lowest attack damage, 0 or more
     * @param theMaxDamage the highest attack damage, at least minDamage
     * @param theAttackSpeed how fast the monster acts, 1 or more
     * @param theChanceToHit the chance an attack hits, from 0.0 to 1.0
     * @param theChanceToHeal the chance to heal after being hit, from 0.0 to 1.0
     * @param theMinHeal the fewest hit points a heal restores, 0 or more
     * @param theMaxHeal the most hit points a heal restores, at least minHeal
     * @throws IllegalArgumentException if any value is out of range
     */
    protected Monster(final String theName, final int theHitPoints,
                      final int theMinDamage, final int theMaxDamage,
                      final int theAttackSpeed, final double theChanceToHit,
                      final double theChanceToHeal,
                      final int theMinHeal, final int theMaxHeal) {
        super(theName, theHitPoints, theMinDamage, theMaxDamage, theAttackSpeed, theChanceToHit);
        if (theChanceToHeal < MIN_CHANCE || theChanceToHeal > MAX_CHANCE) {
            throw new IllegalArgumentException("chanceToHeal must be 0.0 to 1.0");
        }
        if (theMinHeal < 0 || theMaxHeal < theMinHeal) {
            throw new IllegalArgumentException("invalid heal range");
        }
        this.myChanceToHeal = theChanceToHeal;
        this.myMinHeal = theMinHeal;
        this.myMaxHeal = theMaxHeal;
    }

    /**
     * Creates a monster using stats from MonsterStats.
     *
     * @param theName the monster's name, not null or blank
     * @param theStats the statistics read from the database, not null
     * @throws IllegalArgumentException if any value is out of range
     */
    protected Monster(final String theName, final MonsterStats theStats) {
        this(theName, checkStats(theStats).getHitPoints(), theStats.getMinDamage(),
                theStats.getMaxDamage(), theStats.getAttackSpeed(),
                theStats.getChanceToHit(), theStats.getChanceToHeal(),
                theStats.getMinHeal(), theStats.getMaxHeal());
    }

    /**
     * Does a check to see if the monster stats exist or not.
     * Used up in the Monster constructor when it first gets hit points.
     *
     * @param theStats the monster stats to check
     * @return the same stats
     * @throws IllegalArgumentException if theStats are null
     */
    private static MonsterStats checkStats(final MonsterStats theStats) {
        if (theStats == null) {
            throw new IllegalArgumentException("stats must not be null");
        }
        return theStats;
    }

    /**
     * Tries to heal after the monster was hit. A monster that has fainted
     * cannot heal, and a heal never raises hit points above the maximum.
     * The game model should call this only when the monster was hit and
     * survived.
     *
     * @param theRng the shared source of randomness
     * @return the hit points restored, or 0 if the monster did not heal
     */
    public int tryHeal(final Random theRng) {
        if (theRng == null) {
            throw new IllegalArgumentException("rng must not be null");
        }
        int healed = 0;
        if (isAlive() && theRng.nextDouble() < myChanceToHeal) {
            final int before = getHitPoints();
            setHitPoints(before + myMinHeal + theRng.nextInt(myMaxHeal - myMinHeal + 1));
            healed = getHitPoints() - before;
        }
        return healed;
    }

    /**
     * Returns a one-line summary of the monster: name and hit points.
     *
     * @return the summary text
     */
    @Override
    public String toString() {
        return getName() + " | HP " + getHitPoints() + "/" + getMaxHitPoints();
    }
}