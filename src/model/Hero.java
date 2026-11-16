package src.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Abstract parent of the playable heroes. A hero adds a chance to block,
 * potions and a special skill to a {@link DungeonCharacter}. Each hero class
 * (Warrior, Priestess and Thief) supplies its own starting statistics and
 * its own special skill.
 *
 * <p>A hero also keeps the list of Pillars of OO it has found (FR-HERO-6,
 * FR-MOV-4), which is included in {@link #toString()}.
 *
 * @author Group 7 (John Nguyen)
 * @version 0.4
 */
public abstract class Hero extends DungeonCharacter {

    /** The most characters a hero's name can have. */
    private static final int MAX_NAME_LENGTH = 20;

    /** The fewest hit points a healing potion restores. */
    private static final int HEAL_MIN = 5;

    /** The most hit points a healing potion restores. */
    private static final int HEAL_MAX = 15;

    /** The chance the hero blocks an incoming attack, from 0.0 to 1.0. */
    private final double myChanceToBlock;

    /** The number of healing potions the hero carries. */
    private int myHealingPotions;

    /** The number of vision potions the hero carries. */
    private int myVisionPotions;

    /** The Pillars of OO the hero has found, in the order found. */
    private final List<Pillar> myPillars;

    /**
     * Creates a hero with no potions and no pillars. Leading and trailing
     * spaces are removed from the name.
     *
     * @param theName the hero's name, 1 to 20 characters after trimming
     * @param theHitPoints the starting and maximum hit points, above 0
     * @param theMinDamage the lowest attack damage, 0 or more
     * @param theMaxDamage the highest attack damage, at least minDamage
     * @param theAttackSpeed how fast the hero acts, 1 or more
     * @param theChanceToHit the chance an attack hits, from 0.0 to 1.0
     * @param theChanceToBlock the chance to block an attack, from 0.0 to 1.0
     * @throws IllegalArgumentException if any value is out of range
     */
    protected Hero(final String theName, final int theHitPoints,
                   final int theMinDamage, final int theMaxDamage,
                   final int theAttackSpeed, final double theChanceToHit,
                   final double theChanceToBlock) {
        super(trimmed(theName), theHitPoints, theMinDamage, theMaxDamage, theAttackSpeed,
                theChanceToHit);
        if (getName().length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "name must be at most " + MAX_NAME_LENGTH + " characters");
        }
        if (theChanceToBlock < MIN_CHANCE || theChanceToBlock > MAX_CHANCE) {
            throw new IllegalArgumentException("chanceToBlock must be 0.0 to 1.0");
        }
        this.myChanceToBlock = theChanceToBlock;
        this.myHealingPotions = 0;
        this.myVisionPotions = 0;
        this.myPillars = new ArrayList<>();
    }

    /**
     * Removes leading and trailing spaces from a name.
     *
     * @param theName the name to clean, which may be null
     * @return the trimmed name, or null if the name was null
     */
    private static String trimmed(final String theName) {
        String result = null;
        if (theName != null) {
            result = theName.trim();
        }
        return result;
    }

    /**
     * Uses the hero's special skill on a monster. Each hero class defines
     * its own skill.
     *
     * @param theTarget the monster the skill is used on
     * @param theRng the shared source of randomness
     * @return a sentence describing the result, for the battle log
     */
    public abstract String specialSkill(final Monster theTarget, final Random theRng);

    /**
     * Rolls to block an incoming attack.
     *
     * @param theRng the shared source of randomness
     * @return true if the hero blocks the attack
     */
    public boolean block(final Random theRng) {
        if (theRng == null) {
            throw new IllegalArgumentException("rng must not be null");
        }
        return theRng.nextDouble() < myChanceToBlock;
    }

    /**
     * Restores hit points. The total never goes above the maximum.
     *
     * @param theAmount the hit points to restore
     */
    public void heal(final int theAmount) {
        if (theAmount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        setHitPoints(getHitPoints() + theAmount);
    }

    /**
     * Uses one healing potion to restore 5 to 15 hit points.
     *
     * @param theRng the shared source of randomness
     * @return true if a potion was used, false if the hero had none
     */
    public boolean useHealingPotion(final Random theRng) {
        if (theRng == null) {
            throw new IllegalArgumentException("rng must not be null");
        }
        final boolean used = myHealingPotions > 0;
        if (used) {
            myHealingPotions--;
            heal(HEAL_MIN + theRng.nextInt(HEAL_MAX - HEAL_MIN + 1));
        }
        return used;
    }

    /**
     * Uses one vision potion. The dungeon reveals the surrounding rooms.
     *
     * @return true if a potion was used, false if the hero had none
     */
    public boolean useVisionPotion() {
        final boolean used = myVisionPotions > 0;
        if (used) {
            myVisionPotions--;
        }
        return used;
    }

    /**
     * Adds one healing potion to the hero's inventory.
     */
    public void addHealingPotion() {
        myHealingPotions++;
    }

    /**
     * Adds one vision potion to the hero's inventory.
     */
    public void addVisionPotion() {
        myVisionPotions++;
    }

    /**
     * Returns the number of healing potions the hero carries.
     *
     * @return the healing potion count
     */
    public int getHealingPotions() {
        return myHealingPotions;
    }

    /**
     * Returns the number of vision potions the hero carries.
     *
     * @return the vision potion count
     */
    public int getVisionPotions() {
        return myVisionPotions;
    }

    /**
     * Adds a pillar to the hero's collection. A pillar the hero already has
     * (same name) is not added a second time.
     *
     * @param thePillar the pillar the hero found, not null
     * @return true if the pillar was added, false if the hero already had it
     * @throws IllegalArgumentException if the pillar is null
     */
    public boolean addPillar(final Pillar thePillar) {
        if (thePillar == null) {
            throw new IllegalArgumentException("pillar must not be null");
        }
        boolean added = true;
        for (final Pillar found : myPillars) {
            if (found.getName().equals(thePillar.getName())) {
                added = false;
                break;
            }
        }
        if (added) {
            myPillars.add(thePillar);
        }
        return added;
    }

    /**
     * Returns the pillars the hero has found. The list cannot be changed;
     * use {@link #addPillar(Pillar)} to add one.
     *
     * @return an unmodifiable list of the pillars found
     */
    public List<Pillar> getPillars() {
        return Collections.unmodifiableList(myPillars);
    }

    /**
     * Tells whether the hero has found all four Pillars of OO.
     *
     * @return true if the hero holds every pillar
     */
    public boolean hasAllPillars() {
        return myPillars.size() == Pillar.TOTAL;
    }

    /**
     * Returns a one-line summary of the hero: name, hit points, potions and
     * the names of the pillars found.
     *
     * @return the summary text
     */
    @Override
    public String toString() {
        return getName() + " | HP " + getHitPoints() + "/" + getMaxHitPoints()
                + " | Healing potions: " + myHealingPotions
                + " | Vision potions: " + myVisionPotions
                + " | Pillars: " + pillarNames();
    }

    /**
     * Builds the list of pillar names for {@link #toString()}.
     *
     * @return the pillar names separated by commas, or "none"
     */
    private String pillarNames() {
        String result = "none";
        if (!myPillars.isEmpty()) {
            final StringBuilder names = new StringBuilder();
            for (final Pillar found : myPillars) {
                if (!names.isEmpty()) {
                    names.append(", ");
                }
                names.append(found.getName());
            }
            result = names.toString();
        }
        return result;
    }
}