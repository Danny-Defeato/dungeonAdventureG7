package src.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable set of statistics for one kind of monster. Instances are built
 * from rows of the SQLite monster table and handed to a monster factory.
 *
 * @author Group 7 (Alex Douk)
 * @version 0.1
 */
public final class MonsterStats implements Serializable {

    /** The monster's starting hit points. */
    private final int hitPoints;

    /** The lowest damage an attack deals. */
    private final int minDamage;

    /** The highest damage an attack deals. */
    private final int maxDamage;

    /** How fast the monster acts, where 1 is slowest. */
    private final int attackSpeed;

    /** The chance an attack hits, from 0.0 to 1.0. */
    private final double chanceToHit;

    /** The chance the monster heals after being hit, from 0.0 to 1.0. */
    private final double chanceToHeal;

    /** The fewest hit points a heal restores. */
    private final int minHeal;

    /** The most hit points a heal restores. */
    private final int maxHeal;

    /**
     * Creates a set of monster statistics.
     *
     * @param hitPoints starting hit points, above 0
     * @param minDamage lowest attack damage, 0 or more
     * @param maxDamage highest attack damage, at least minDamage
     * @param attackSpeed attack speed, 1 or more
     * @param chanceToHit chance to hit, from 0.0 to 1.0
     * @param chanceToHeal chance to heal, from 0.0 to 1.0
     * @param minHeal fewest hit points healed, 0 or more
     * @param maxHeal most hit points healed, at least minHeal
     * @throws IllegalArgumentException if any value is out of range
     */
    public MonsterStats(final int hitPoints, final int minDamage,
                        final int maxDamage, final int attackSpeed,
                        final double chanceToHit, final double chanceToHeal,
                        final int minHeal, final int maxHeal) {
        if (hitPoints <= 0) {
            throw new IllegalArgumentException("hitPoints must be above 0");
        }
        if (minDamage < 0 || maxDamage < minDamage) {
            throw new IllegalArgumentException("invalid damage range");
        }
        if (attackSpeed < 1) {
            throw new IllegalArgumentException("attackSpeed must be at least 1");
        }
        if (chanceToHit < 0.0 || chanceToHit > 1.0) {
            throw new IllegalArgumentException("chanceToHit must be 0.0 to 1.0");
        }
        if (chanceToHeal < 0.0 || chanceToHeal > 1.0) {
            throw new IllegalArgumentException("chanceToHeal must be 0.0 to 1.0");
        }
        if (minHeal < 0 || maxHeal < minHeal) {
            throw new IllegalArgumentException("invalid heal range");
        }
        this.hitPoints = hitPoints;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.attackSpeed = attackSpeed;
        this.chanceToHit = chanceToHit;
        this.chanceToHeal = chanceToHeal;
        this.minHeal = minHeal;
        this.maxHeal = maxHeal;
    }

    /** @return the starting hit points */
    public int getHitPoints() {
        return hitPoints;
    }

    /** @return the lowest attack damage */
    public int getMinDamage() {
        return minDamage;
    }

    /** @return the highest attack damage */
    public int getMaxDamage() {
        return maxDamage;
    }

    /** @return the attack speed */
    public int getAttackSpeed() {
        return attackSpeed;
    }

    /** @return the chance to hit, from 0.0 to 1.0 */
    public double getChanceToHit() {
        return chanceToHit;
    }

    /** @return the chance to heal, from 0.0 to 1.0 */
    public double getChanceToHeal() {
        return chanceToHeal;
    }

    /** @return the fewest hit points a heal restores */
    public int getMinHeal() {
        return minHeal;
    }

    /** @return the most hit points a heal restores */
    public int getMaxHeal() {
        return maxHeal;
    }

    @Override
    public boolean equals(final Object other) {
        boolean result = this == other;
        if (!result && other instanceof MonsterStats) {
            final MonsterStats o = (MonsterStats) other;
            result = hitPoints == o.hitPoints && minDamage == o.minDamage
                    && maxDamage == o.maxDamage && attackSpeed == o.attackSpeed
                    && Double.compare(chanceToHit, o.chanceToHit) == 0
                    && Double.compare(chanceToHeal, o.chanceToHeal) == 0
                    && minHeal == o.minHeal && maxHeal == o.maxHeal;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hitPoints, minDamage, maxDamage, attackSpeed,
                chanceToHit, chanceToHeal, minHeal, maxHeal);
    }

    @Override
    public String toString() {
        return "MonsterStats[hp=" + hitPoints + ", damage=" + minDamage + "-"
                + maxDamage + ", speed=" + attackSpeed + ", hit=" + chanceToHit
                + ", healChance=" + chanceToHeal + ", heal=" + minHeal + "-"
                + maxHeal + "]";
    }
}
