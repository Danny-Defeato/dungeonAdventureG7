package src.model;

import java.util.Random;

/**
 * A Priestess hero. Priestesses have fewer hit points than a Warrior but
 * can heal themselves with their special skill.
 *
 * @author Group 7 (John Nguyen)
 * @version 0.1
 */
public class Priestess extends Hero {

    /** The Priestess's starting and maximum hit points. */
    private static final int HIT_POINTS = 75;

    /** The Priestess's lowest normal attack damage. */
    private static final int MIN_DAMAGE = 25;

    /** The Priestess's highest normal attack damage. */
    private static final int MAX_DAMAGE = 45;

    /** The Priestess's attack speed. */
    private static final int ATTACK_SPEED = 5;

    /** The chance the Priestess's normal attack hits. */
    private static final double CHANCE_TO_HIT = 0.7;

    /** The chance the Priestess blocks an attack. */
    private static final double CHANCE_TO_BLOCK = 0.3;

    /** The fewest hit points the Heal skill restores. */
    private static final int MIN_HEAL = 25;

    /** The most hit points the Heal skill restores. */
    private static final int MAX_HEAL = 50;

    /**
     * Creates a Priestess with the class's starting statistics.
     *
     * @param name the Priestess's name, not null or blank
     * @throws IllegalArgumentException if the name is blank
     */
    public Priestess(final String name) {
        super(name, HIT_POINTS, MIN_DAMAGE, MAX_DAMAGE, ATTACK_SPEED,
                CHANCE_TO_HIT, CHANCE_TO_BLOCK);
    }

    /**
     * Uses Heal. The Priestess restores 25 to 50 of her own hit points, up
     * to her starting hit points. The target is not affected.
     *
     * @param target the monster in the battle, which this skill ignores
     * @param rng the shared source of randomness
     * @return a sentence describing the result, for the battle log
     */
    @Override
    public String specialSkill(final Monster target, final Random rng) {
        final int before = getHitPoints();
        heal(MIN_HEAL + rng.nextInt(MAX_HEAL - MIN_HEAL + 1));
        final int restored = getHitPoints() - before;
        return getName() + " heals for " + restored + " hit points.";
    }
}