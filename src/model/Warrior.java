package src.model;

import java.util.Random;

/**
 * A Warrior hero. Warriors have the most hit points and fight with a
 * Crushing Blow, a rare but very strong attack.
 *
 * @author Group 7 (John Nguyen)
 * @version 0.1
 */
public class Warrior extends Hero {

    /** The Warrior's starting and maximum hit points. */
    private static final int HIT_POINTS = 125;

    /** The Warrior's lowest normal attack damage. */
    private static final int MIN_DAMAGE = 35;

    /** The Warrior's highest normal attack damage. */
    private static final int MAX_DAMAGE = 60;

    /** The Warrior's attack speed. */
    private static final int ATTACK_SPEED = 4;

    /** The chance the Warrior's normal attack hits. */
    private static final double CHANCE_TO_HIT = 0.8;

    /** The chance the Warrior blocks an attack. */
    private static final double CHANCE_TO_BLOCK = 0.2;

    /** The chance the Crushing Blow succeeds. */
    private static final double CRUSH_CHANCE = 0.4;

    /** The lowest damage of a successful Crushing Blow. */
    private static final int MIN_CRUSH = 75;

    /** The highest damage of a successful Crushing Blow. */
    private static final int MAX_CRUSH = 175;

    /**
     * Creates a Warrior with the class's starting statistics.
     *
     * @param name the Warrior's name, not null or blank
     * @throws IllegalArgumentException if the name is blank
     */
    public Warrior(final String name) {
        super(name, HIT_POINTS, MIN_DAMAGE, MAX_DAMAGE, ATTACK_SPEED,
                CHANCE_TO_HIT, CHANCE_TO_BLOCK);
    }

    /**
     * Uses Crushing Blow. It has a 40 percent chance to deal 75 to 175
     * damage. If it fails, it deals no damage.
     *
     * @param target the monster the skill is used on
     * @param rng the shared source of randomness
     * @return a sentence describing the result, for the battle log
     */
    @Override
    public String specialSkill(final Monster target, final Random rng) {
        final String result;
        if (rng.nextDouble() < CRUSH_CHANCE) {
            final int damage = MIN_CRUSH + rng.nextInt(MAX_CRUSH - MIN_CRUSH + 1);
            target.takeDamage(damage);
            result = getName() + " lands a Crushing Blow on " + target.getName()
                    + " for " + damage + " damage!";
        } else {
            result = getName() + "'s Crushing Blow misses " + target.getName() + ".";
        }
        return result;
    }
}