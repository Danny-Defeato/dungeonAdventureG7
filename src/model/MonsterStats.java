package src.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable set of statistics for one kind of monster. Instances are built
 * from rows of the SQLite monster table and handed to a monster factory.
 *
 * @author Group 7 (Alex Douk)
 * @version 0.2
 */
public final class MonsterStats implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The smallest chance, meaning never. */
    private static final double MIN_CHANCE = 0.0;

    /** The largest chance, meaning always. */
    private static final double MAX_CHANCE = 1.0;

    /** The slowest attack speed. */
    private static final int MIN_ATTACK_SPEED = 1;

    /** The monster's starting hit points. */
    private final int myHitPoints;

    /** The lowest damage an attack deals. */
    private final int myMinDamage;

    /** The highest damage an attack deals. */
    private final int myMaxDamage;

    /** How fast the monster acts, where 1 is slowest. */
    private final int myAttackSpeed;

    /** The chance an attack hits, from 0.0 to 1.0. */
    private final double myChanceToHit;

    /** The chance the monster heals after being hit, from 0.0 to 1.0. */
    private final double myChanceToHeal;

    /** The fewest hit points a heal restores. */
    private final int myMinHeal;

    /** The most hit points a heal restores. */
    private final int myMaxHeal;

    /**
     * Creates a set of monster statistics.
     *
     * @param theHitPoints starting hit points, above 0
     * @param theMinDamage lowest attack damage, 0 or more
     * @param theMaxDamage highest attack damage, at least theMinDamage
     * @param theAttackSpeed attack speed, 1 or more
     * @param theChanceToHit chance to hit, from 0.0 to 1.0
     * @param theChanceToHeal chance to heal, from 0.0 to 1.0
     * @param theMinHeal fewest hit points healed, 0 or more
     * @param theMaxHeal most hit points healed, at least theMinHeal
     * @throws IllegalArgumentException if any value is out of range
     */
    public MonsterStats(final int theHitPoints, final int theMinDamage,
                        final int theMaxDamage, final int theAttackSpeed,
                        final double theChanceToHit, final double theChanceToHeal,
                        final int theMinHeal, final int theMaxHeal) {
        if (theHitPoints <= 0) {
            throw new IllegalArgumentException("hitPoints must be above 0");
        }
        if (theMinDamage < 0 || theMaxDamage < theMinDamage) {
            throw new IllegalArgumentException("invalid damage range");
        }
        if (theAttackSpeed < MIN_ATTACK_SPEED) {
            throw new IllegalArgumentException(
                    "attackSpeed must be at least " + MIN_ATTACK_SPEED);
        }
        if (theChanceToHit < MIN_CHANCE || theChanceToHit > MAX_CHANCE) {
            throw new IllegalArgumentException("chanceToHit must be 0.0 to 1.0");
        }
        if (theChanceToHeal < MIN_CHANCE || theChanceToHeal > MAX_CHANCE) {
            throw new IllegalArgumentException("chanceToHeal must be 0.0 to 1.0");
        }
        if (theMinHeal < 0 || theMaxHeal < theMinHeal) {
            throw new IllegalArgumentException("invalid heal range");
        }
        myHitPoints = theHitPoints;
        myMinDamage = theMinDamage;
        myMaxDamage = theMaxDamage;
        myAttackSpeed = theAttackSpeed;
        myChanceToHit = theChanceToHit;
        myChanceToHeal = theChanceToHeal;
        myMinHeal = theMinHeal;
        myMaxHeal = theMaxHeal;
    }

    /** @return the starting hit points */
    public int getHitPoints() {
        return myHitPoints;
    }

    /** @return the lowest attack damage */
    public int getMinDamage() {
        return myMinDamage;
    }

    /** @return the highest attack damage */
    public int getMaxDamage() {
        return myMaxDamage;
    }

    /** @return the attack speed */
    public int getAttackSpeed() {
        return myAttackSpeed;
    }

    /** @return the chance to hit, from 0.0 to 1.0 */
    public double getChanceToHit() {
        return myChanceToHit;
    }

    /** @return the chance to heal, from 0.0 to 1.0 */
    public double getChanceToHeal() {
        return myChanceToHeal;
    }

    /** @return the fewest hit points a heal restores */
    public int getMinHeal() {
        return myMinHeal;
    }

    /** @return the most hit points a heal restores */
    public int getMaxHeal() {
        return myMaxHeal;
    }

    @Override
    public boolean equals(final Object theOther) {
        boolean result = this == theOther;
        if (!result && theOther instanceof MonsterStats) {
            final MonsterStats o = (MonsterStats) theOther;
            result = myHitPoints == o.myHitPoints && myMinDamage == o.myMinDamage
                    && myMaxDamage == o.myMaxDamage && myAttackSpeed == o.myAttackSpeed
                    && Double.compare(myChanceToHit, o.myChanceToHit) == 0
                    && Double.compare(myChanceToHeal, o.myChanceToHeal) == 0
                    && myMinHeal == o.myMinHeal && myMaxHeal == o.myMaxHeal;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(myHitPoints, myMinDamage, myMaxDamage, myAttackSpeed,
                myChanceToHit, myChanceToHeal, myMinHeal, myMaxHeal);
    }

    @Override
    public String toString() {
        return "MonsterStats[hp=" + myHitPoints + ", damage=" + myMinDamage + "-"
                + myMaxDamage + ", speed=" + myAttackSpeed + ", hit=" + myChanceToHit
                + ", healChance=" + myChanceToHeal + ", heal=" + myMinHeal + "-"
                + myMaxHeal + "]";
    }
}
