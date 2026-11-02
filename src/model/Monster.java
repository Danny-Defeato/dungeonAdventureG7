package src.model;

import java.util.Random;

/**
 * Abstract parent of the monsters. A monster adds a chance to heal itself
 * after being hit to a {@link DungeonCharacter}. Each monster class (Ogre,
 * Gremlin and Skeleton) supplies its own statistics.
 *
 * @author Group 7 (John Nguyen)
 * @version 0.1
 */
public abstract class Monster extends DungeonCharacter {

    /** The chance the monster heals after being hit, from 0.0 to 1.0. */
    private final double chanceToHeal;

    /** The fewest hit points a heal restores. */
    private final int minHeal;

    /** The most hit points a heal restores. */
    private final int maxHeal;

    /**
     * Creates a monster.
     *
     * @param name the monster's name, not null or blank
     * @param hitPoints the starting and maximum hit points, above 0
     * @param minDamage the lowest attack damage, 0 or more
     * @param maxDamage the highest attack damage, at least minDamage
     * @param attackSpeed how fast the monster acts, 1 or more
     * @param chanceToHit the chance an attack hits, from 0.0 to 1.0
     * @param chanceToHeal the chance to heal after being hit, from 0.0 to 1.0
     * @param minHeal the fewest hit points a heal restores, 0 or more
     * @param maxHeal the most hit points a heal restores, at least minHeal
     * @throws IllegalArgumentException if any value is out of range
     */
    protected Monster(final String name, final int hitPoints,
                      final int minDamage, final int maxDamage,
                      final int attackSpeed, final double chanceToHit,
                      final double chanceToHeal,
                      final int minHeal, final int maxHeal) {
        super(name, hitPoints, minDamage, maxDamage, attackSpeed, chanceToHit);
        if (chanceToHeal < 0.0 || chanceToHeal > 1.0) {
            throw new IllegalArgumentException("chanceToHeal must be 0.0 to 1.0");
        }
        if (minHeal < 0 || maxHeal < minHeal) {
            throw new IllegalArgumentException("invalid heal range");
        }
        this.chanceToHeal = chanceToHeal;
        this.minHeal = minHeal;
        this.maxHeal = maxHeal;
    }

    /**
     * Creates a monster using stats from MonsterStats.
     *
     * @param name the monster's name, not null or blank
     * @param stats the statistics read from the database, not null
     * @throws IllegalArgumentException if any value is out of range
     */
    protected Monster(final String name, final MonsterStats stats) {
        this(name, stats.getHitPoints(), stats.getMinDamage(),
                stats.getMaxDamage(), stats.getAttackSpeed(),
                stats.getChanceToHit(), stats.getChanceToHeal(),
                stats.getMinHeal(), stats.getMaxHeal());
    }

    /**
     * Tries to heal after the monster was hit. A monster that has fainted
     * cannot heal, and a heal never raises hit points above the maximum.
     * The game model should call this only when the monster was hit and
     * survived.
     *
     * @param rng the shared source of randomness
     * @return the hit points restored, or 0 if the monster did not heal
     */
    public int tryHeal(final Random rng) {
        int healed = 0;
        if (isAlive() && rng.nextDouble() < chanceToHeal) {
            final int before = getHitPoints();
            setHitPoints(before + minHeal + rng.nextInt(maxHeal - minHeal + 1));
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