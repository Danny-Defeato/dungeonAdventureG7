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
 * @version 0.3
 */
public abstract class Hero extends DungeonCharacter {

    /** The most characters a hero's name can have. */
    private static final int MAX_NAME_LENGTH = 20;

    /** The fewest hit points a healing potion restores. */
    private static final int HEAL_MIN = 5;

    /** The most hit points a healing potion restores. */
    private static final int HEAL_MAX = 15;

    /** The chance the hero blocks an incoming attack, from 0.0 to 1.0. */
    private final double chanceToBlock;

    /** The number of healing potions the hero carries. */
    private int healingPotions;

    /** The number of vision potions the hero carries. */
    private int visionPotions;

    /** The Pillars of OO the hero has found, in the order found. */
    private final List<Pillar> pillars;

    /**
     * Creates a hero with no potions and no pillars. Leading and trailing
     * spaces are removed from the name.
     *
     * @param name the hero's name, 1 to 20 characters after trimming
     * @param hitPoints the starting and maximum hit points, above 0
     * @param minDamage the lowest attack damage, 0 or more
     * @param maxDamage the highest attack damage, at least minDamage
     * @param attackSpeed how fast the hero acts, 1 or more
     * @param chanceToHit the chance an attack hits, from 0.0 to 1.0
     * @param chanceToBlock the chance to block an attack, from 0.0 to 1.0
     * @throws IllegalArgumentException if any value is out of range
     */
    protected Hero(final String name, final int hitPoints,
                   final int minDamage, final int maxDamage,
                   final int attackSpeed, final double chanceToHit,
                   final double chanceToBlock) {
        super(trimmed(name), hitPoints, minDamage, maxDamage, attackSpeed,
                chanceToHit);
        if (getName().length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "name must be at most " + MAX_NAME_LENGTH + " characters");
        }
        if (chanceToBlock < 0.0 || chanceToBlock > 1.0) {
            throw new IllegalArgumentException("chanceToBlock must be 0.0 to 1.0");
        }
        this.chanceToBlock = chanceToBlock;
        this.healingPotions = 0;
        this.visionPotions = 0;
        this.pillars = new ArrayList<>();
    }

    /**
     * Removes leading and trailing spaces from a name.
     *
     * @param name the name to clean, which may be null
     * @return the trimmed name, or null if the name was null
     */
    private static String trimmed(final String name) {
        String result = null;
        if (name != null) {
            result = name.trim();
        }
        return result;
    }

    /**
     * Uses the hero's special skill on a monster. Each hero class defines
     * its own skill.
     *
     * @param target the monster the skill is used on
     * @param rng the shared source of randomness
     * @return a sentence describing the result, for the battle log
     */
    public abstract String specialSkill(Monster target, Random rng);

    /**
     * Rolls to block an incoming attack.
     *
     * @param rng the shared source of randomness
     * @return true if the hero blocks the attack
     */
    public boolean block(final Random rng) {
        return rng.nextDouble() < chanceToBlock;
    }

    /**
     * Restores hit points. The total never goes above the maximum.
     *
     * @param amount the hit points to restore
     */
    public void heal(final int amount) {
        setHitPoints(getHitPoints() + amount);
    }

    /**
     * Uses one healing potion to restore 5 to 15 hit points.
     *
     * @param rng the shared source of randomness
     * @return true if a potion was used, false if the hero had none
     */
    public boolean useHealingPotion(final Random rng) {
        final boolean used = healingPotions > 0;
        if (used) {
            healingPotions--;
            heal(HEAL_MIN + rng.nextInt(HEAL_MAX - HEAL_MIN + 1));
        }
        return used;
    }

    /**
     * Uses one vision potion. The dungeon reveals the surrounding rooms.
     *
     * @return true if a potion was used, false if the hero had none
     */
    public boolean useVisionPotion() {
        final boolean used = visionPotions > 0;
        if (used) {
            visionPotions--;
        }
        return used;
    }

    /**
     * Adds one healing potion to the hero's inventory.
     */
    public void addHealingPotion() {
        healingPotions++;
    }

    /**
     * Adds one vision potion to the hero's inventory.
     */
    public void addVisionPotion() {
        visionPotions++;
    }

    /**
     * Returns the number of healing potions the hero carries.
     *
     * @return the healing potion count
     */
    public int getHealingPotions() {
        return healingPotions;
    }

    /**
     * Returns the number of vision potions the hero carries.
     *
     * @return the vision potion count
     */
    public int getVisionPotions() {
        return visionPotions;
    }

    /**
     * Adds a pillar to the hero's collection. A pillar the hero already has
     * (same name) is not added a second time.
     *
     * @param pillar the pillar the hero found, not null
     * @return true if the pillar was added, false if the hero already had it
     * @throws IllegalArgumentException if the pillar is null
     */
    public boolean addPillar(final Pillar pillar) {
        if (pillar == null) {
            throw new IllegalArgumentException("pillar must not be null");
        }
        boolean added = true;
        for (final Pillar found : pillars) {
            if (found.getName().equals(pillar.getName())) {
                added = false;
                break;
            }
        }
        if (added) {
            pillars.add(pillar);
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
        return Collections.unmodifiableList(pillars);
    }

    /**
     * Tells whether the hero has found all four Pillars of OO.
     *
     * @return true if the hero holds every pillar
     */
    public boolean hasAllPillars() {
        return pillars.size() == Pillar.TOTAL;
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
                + " | Healing potions: " + healingPotions
                + " | Vision potions: " + visionPotions
                + " | Pillars: " + pillarNames();
    }

    /**
     * Builds the list of pillar names for {@link #toString()}.
     *
     * @return the pillar names separated by commas, or "none"
     */
    private String pillarNames() {
        String result = "none";
        if (!pillars.isEmpty()) {
            final StringBuilder names = new StringBuilder();
            for (final Pillar found : pillars) {
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