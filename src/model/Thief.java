package src.model;

import java.util.Random;

/**
 * A Thief hero. Thieves are fast and good at blocking, and they fight with
 * a Surprise Attack that can earn an extra attack or fail completely.
 *
 * @author Group 7
 * @version 0.1
 */
public class Thief extends Hero {

    /** The Thief's starting and maximum hit points. */
    private static final int HIT_POINTS = 75;

    /** The Thief's lowest normal attack damage. */
    private static final int MIN_DAMAGE = 20;

    /** The Thief's highest normal attack damage. */
    private static final int MAX_DAMAGE = 40;

    /** The Thief's attack speed. */
    private static final int ATTACK_SPEED = 6;

    /** The chance the Thief's normal attack hits. */
    private static final double CHANCE_TO_HIT = 0.8;

    /** The chance the Thief blocks an attack. */
    private static final double CHANCE_TO_BLOCK = 0.4;

    /** The chance the Surprise Attack succeeds and earns an extra attack. */
    private static final double SURPRISE_CHANCE = 0.4;

    /** The chance the Thief is caught and makes no attack. */
    private static final double CAUGHT_CHANCE = 0.2;

    /**
     * Creates a Thief with the class's starting statistics.
     *
     * @param name the Thief's name, not null or blank
     * @throws IllegalArgumentException if the name is blank
     */
    public Thief(final String name) {
        super(name, HIT_POINTS, MIN_DAMAGE, MAX_DAMAGE, ATTACK_SPEED,
                CHANCE_TO_HIT, CHANCE_TO_BLOCK);
    }

    /**
     * Uses Surprise Attack. There is a 40 percent chance it succeeds and the
     * Thief makes two attacks, a 20 percent chance the Thief is caught and
     * makes no attack, and a 40 percent chance of one normal attack. The
     * second attack of a success is skipped if the first one defeated the
     * target.
     *
     * @param target the monster the skill is used on
     * @param rng the shared source of randomness
     * @return a sentence describing the result, for the battle log
     */
    @Override
    public String specialSkill(final Monster target, final Random rng) {
        final double roll = rng.nextDouble();
        String result;
        if (roll < SURPRISE_CHANCE) {
            result = getName() + " surprises " + target.getName() + "! "
                    + attack(target, rng);
            if (target.isAlive()) {
                result = result + " " + attack(target, rng);
            }
        } else if (roll < SURPRISE_CHANCE + CAUGHT_CHANCE) {
            result = getName() + " is caught by " + target.getName()
                    + " and makes no attack.";
        } else {
            result = attack(target, rng);
        }
        return result;
    }
}