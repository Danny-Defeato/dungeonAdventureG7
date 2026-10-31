package src.model;

import java.io.Serializable;
import java.util.Random;

/**
 * Abstract parent of every hero and monster in Dungeon Adventure. It holds
 * the data and behavior all characters share: a name, hit points, a damage
 * range, an attack speed, a chance to hit and a single attack.
 *
 * <p>The multiple-attacks-per-round rule and the hero's block roll are not
 * handled here. The game model applies them around calls to
 * {@link #attack(DungeonCharacter, Random)}.
 *
 * @author Group 7 (John Nguyen)
 * @version 0.1
 */
public abstract class DungeonCharacter implements Serializable {

    /** The character's name. */
    private final String name;

    /** The most hit points the character can have. */
    private final int maxHitPoints;

    /** The lowest damage a successful attack deals. */
    private final int minDamage;

    /** The highest damage a successful attack deals. */
    private final int maxDamage;

    /** How fast the character acts, where 1 is slowest. */
    private final int attackSpeed;

    /** The chance an attack hits, from 0.0 to 1.0. */
    private final double chanceToHit;

    /** The character's current hit points, from 0 to the maximum. */
    private int hitPoints;

    /**
     * Creates a character at full health.
     *
     * @param name the character's name, not null or blank
     * @param hitPoints the starting and maximum hit points, above 0
     * @param minDamage the lowest attack damage, 0 or more
     * @param maxDamage the highest attack damage, at least minDamage
     * @param attackSpeed how fast the character acts, 1 or more
     * @param chanceToHit the chance an attack hits, from 0.0 to 1.0
     * @throws IllegalArgumentException if any value is out of range
     */
    protected DungeonCharacter(final String name, final int hitPoints,
                               final int minDamage, final int maxDamage,
                               final int attackSpeed, final double chanceToHit) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
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
        this.name = name;
        this.hitPoints = hitPoints;
        this.maxHitPoints = hitPoints;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.attackSpeed = attackSpeed;
        this.chanceToHit = chanceToHit;
    }

    /**
     * Returns the character's name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the character's current hit points.
     *
     * @return the current hit points, from 0 to the maximum
     */
    public int getHitPoints() {
        return hitPoints;
    }

    /**
     * Returns the most hit points the character can have.
     *
     * @return the maximum hit points
     */
    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    /**
     * Returns how fast the character acts.
     *
     * @return the attack speed, where 1 is slowest
     */
    public int getAttackSpeed() {
        return attackSpeed;
    }

    /**
     * Tells whether the character can still fight.
     *
     * @return true if the character has more than 0 hit points
     */
    public boolean isAlive() {
        return hitPoints > 0;
    }

    /**
     * Lowers the character's hit points by the given amount. Hit points
     * never drop below 0.
     *
     * @param amount the damage to take, 0 or more
     * @throws IllegalArgumentException if amount is negative
     */
    public void takeDamage(final int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        setHitPoints(hitPoints - amount);
    }

    /**
     * Sets the character's hit points, keeping them between 0 and the
     * maximum. Subclasses use this for healing.
     *
     * @param hp the new hit points, which are clamped to 0 through the maximum
     */
    protected void setHitPoints(final int hp) {
        hitPoints = Math.max(0, Math.min(hp, maxHitPoints));
    }

    /**
     * Makes one attack on a foe. The attack hits if a random value is below
     * the chance to hit. A hit deals random damage between the minimum and
     * maximum damage and is applied to the foe.
     *
     * @param foe the opponent to attack
     * @param rng the shared source of randomness
     * @return a sentence describing the result, for the battle log
     */
    public String attack(final DungeonCharacter foe, final Random rng) {
        final String result;
        if (rng.nextDouble() < chanceToHit) {
            final int damage = minDamage + rng.nextInt(maxDamage - minDamage + 1);
            foe.takeDamage(damage);
            result = name + " hits " + foe.getName() + " for " + damage + " damage.";
        } else {
            result = name + " misses " + foe.getName() + ".";
        }
        return result;
    }
}