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
 * @version 0.2
 */
public abstract class DungeonCharacter implements Serializable {

    /** Lowest value a chance can have. */
    protected static final double MIN_CHANCE = 0.0;

    /** Highest value a chance can have. */
    protected static final double MAX_CHANCE = 1.0;

    /** Lowest attack speed a character can have. */
    protected static final int MIN_ATTACK_SPEED = 1;

    /** The character's name. */
    private final String myName;

    /** The most hit points the character can have. */
    private final int myMaxHitPoints;

    /** The lowest damage a successful attack deals. */
    private final int myMinDamage;

    /** The highest damage a successful attack deals. */
    private final int myMaxDamage;

    /** How fast the character acts, where 1 is slowest. */
    private final int myAttackSpeed;

    /** The chance an attack hits, from 0.0 to 1.0. */
    private final double myChanceToHit;

    /** The character's current hit points, from 0 to the maximum. */
    private int myHitPoints;

    /**
     * Creates a character at full health.
     *
     * @param theName the character's name, not null or blank
     * @param theHitPoints the starting and maximum hit points, above 0
     * @param theMinDamage the lowest attack damage, 0 or more
     * @param theMaxDamage the highest attack damage, at least minDamage
     * @param theAttackSpeed how fast the character acts, 1 or more
     * @param theChanceToHit the chance an attack hits, from 0.0 to 1.0
     * @throws IllegalArgumentException if any value is out of range
     */
    protected DungeonCharacter(final String theName, final int theHitPoints,
                               final int theMinDamage, final int theMaxDamage,
                               final int theAttackSpeed, final double theChanceToHit) {
        if (theName == null || theName.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (theHitPoints <= 0) {
            throw new IllegalArgumentException("hitPoints must be above 0");
        }
        if (theMinDamage < 0 || theMaxDamage < theMinDamage) {
            throw new IllegalArgumentException("invalid damage range");
        }
        if (theAttackSpeed < MIN_ATTACK_SPEED) {
            throw new IllegalArgumentException("attackSpeed must be at least 1");
        }
        if (theChanceToHit < MIN_CHANCE || theChanceToHit > MAX_CHANCE) {
            throw new IllegalArgumentException("chanceToHit must be 0.0 to 1.0");
        }
        this.myName = theName;
        this.myHitPoints = theHitPoints;
        this.myMaxHitPoints = theHitPoints;
        this.myMinDamage = theMinDamage;
        this.myMaxDamage = theMaxDamage;
        this.myAttackSpeed = theAttackSpeed;
        this.myChanceToHit = theChanceToHit;
    }

    /**
     * Returns the character's name.
     *
     * @return the name
     */
    public String getName() {
        return myName;
    }

    /**
     * Returns the character's current hit points.
     *
     * @return the current hit points, from 0 to the maximum
     */
    public int getHitPoints() {
        return myHitPoints;
    }

    /**
     * Returns the most hit points the character can have.
     *
     * @return the maximum hit points
     */
    public int getMaxHitPoints() {
        return myMaxHitPoints;
    }

    /**
     * Returns how fast the character acts.
     *
     * @return the attack speed, where 1 is slowest
     */
    public int getAttackSpeed() {
        return myAttackSpeed;
    }

    /**
     * Tells whether the character can still fight.
     *
     * @return true if the character has more than 0 hit points
     */
    public boolean isAlive() {
        return myHitPoints > 0;
    }

    /**
     * Lowers the character's hit points by the given amount. Hit points
     * never drop below 0.
     *
     * @param theAmount the damage to take, 0 or more
     * @throws IllegalArgumentException if amount is negative
     */
    public void takeDamage(final int theAmount) {
        if (theAmount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        setHitPoints(myHitPoints - theAmount);
    }

    /**
     * Sets the character's hit points, keeping them between 0 and the
     * maximum. Subclasses use this for healing.
     *
     * @param theHp the new hit points, which are clamped to 0 through the maximum
     */
    protected void setHitPoints(final int theHp) {
        myHitPoints = Math.max(0, Math.min(theHp, myMaxHitPoints));
    }

    /**
     * Makes one attack on a foe. The attack hits if a random value is below
     * the chance to hit. A hit deals random damage between the minimum and
     * maximum damage and is applied to the foe.
     *
     * @param theFoe the opponent to attack
     * @param theRng the shared source of randomness
     * @return a sentence describing the result, for the battle log
     */
    public String attack(final DungeonCharacter theFoe, final Random theRng) {
        if (theFoe == null || theRng == null) {
            throw new IllegalArgumentException("foe and rng must not be null");
        }
        final String result;
        if (theRng.nextDouble() < myChanceToHit) {
            final int damage = myMinDamage + theRng.nextInt(myMaxDamage - myMinDamage + 1);
            theFoe.takeDamage(damage);
            result = myName + " hits " + theFoe.getName() + " for " + damage + " damage.";
        } else {
            result = myName + " misses " + theFoe.getName() + ".";
        }
        return result;
    }
}