package src.model;

import java.util.Random;

/**
 * One fight between a {@link Hero} and a {@link Monster}, played one round
 * at a time so the GUI can show each round as it happens (FR-COM-1 to
 * FR-COM-3).
 *
 * <p>In each round the player chooses one action. A normal attack is made
 * (hero speed divided by monster speed) times, rounded down, with a minimum
 * of 1. The special skill or a healing potion replaces the hero's normal
 * attacks for the round. The monster then attacks once.
 *
 * <p>TODO (FR-COM-5, FR-COM-6, FR-COM-7): blocking by the hero and healing
 * by the monster are not added yet.
 *
 * @author Group 7 (John Nguyen)
 * @version 0.2
 */
public class Battle {

    /** What the player chooses for the hero in a round. */
    public enum Action {
        /** One or more normal attacks. */
        ATTACK,
        /** The hero's special skill, which replaces the normal attacks. */
        SPECIAL,
        /** A healing potion, which replaces the normal attacks. */
        POTION
    }

    /** The hero in the fight. */
    private final Hero hero;

    /** The monster in the fight. */
    private final Monster monster;

    /** The shared source of randomness. */
    private final Random rng;

    /** The number of rounds played so far. */
    private int rounds;

    /**
     * Creates a battle. Nothing happens until playRound is called.
     *
     * @param hero the hero, not null
     * @param monster the monster, not null
     * @param rng the shared source of randomness, not null
     * @throws IllegalArgumentException if any argument is null
     */
    public Battle(final Hero hero, final Monster monster, final Random rng) {
        if (hero == null || monster == null || rng == null) {
            throw new IllegalArgumentException("hero, monster and rng must not be null");
        }
        this.hero = hero;
        this.monster = monster;
        this.rng = rng;
        this.rounds = 0;
    }

    /**
     * Plays one round. The hero acts first and then the monster attacks
     * once. Attacks stop as soon as either side faints, so a monster the
     * hero defeats does not get to attack. If the player chooses a potion
     * and the hero has none, nothing happens and the round is not used.
     *
     * @param action what the player chooses for the hero this round
     * @return the battle log for the round, one event per line
     * @throws IllegalStateException if the battle is already over
     */
    public String playRound(final Action action) {
        if (isOver()) {
            throw new IllegalStateException("the battle is already over");
        }
        final StringBuilder log = new StringBuilder();
        if (action == Action.POTION && hero.getHealingPotions() == 0) {
            log.append(hero.getName()).append(" has no healing potions.\n");
        } else {
            rounds++;
            log.append("Round ").append(rounds).append('\n');
            heroTurn(action, log);
            if (hero.isAlive() && monster.isAlive()) {
                log.append(monster.attack(hero, rng)).append('\n');
            }
            if (isOver()) {
                log.append(getWinner().getName()).append(" wins!\n");
            }
        }
        return log.toString();
    }

    /**
     * Tells whether the hero or the monster has fainted.
     *
     * @return true if the battle is over
     */
    public boolean isOver() {
        return !hero.isAlive() || !monster.isAlive();
    }

    /**
     * Returns the winner of the battle.
     *
     * @return the hero or the monster, or null if the battle is not over
     */
    public DungeonCharacter getWinner() {
        DungeonCharacter winner = null;
        if (isOver()) {
            if (hero.isAlive()) {
                winner = hero;
            } else {
                winner = monster;
            }
        }
        return winner;
    }

    /**
     * Returns the number of rounds played so far.
     *
     * @return the round count
     */
    public int getRounds() {
        return rounds;
    }

    /**
     * Returns how many normal attacks the hero makes in a round. The hero
     * makes as many attacks as its speed divided by the monster's speed,
     * rounded down, and always at least one (FR-COM-3).
     *
     * @return the number of hero attacks per round
     */
    int heroAttacksPerRound() {
        return Math.max(1, hero.getAttackSpeed() / monster.getAttackSpeed());
    }

    /**
     * Carries out the hero's choice for the round.
     *
     * @param action the player's choice
     * @param log the log to add the events to
     */
    private void heroTurn(final Action action, final StringBuilder log) {
        if (action == Action.SPECIAL) {
            log.append(hero.specialSkill(monster, rng)).append('\n');
        } else if (action == Action.POTION) {
            final int before = hero.getHitPoints();
            hero.useHealingPotion(rng);
            log.append(hero.getName()).append(" drinks a healing potion and heals ")
                    .append(hero.getHitPoints() - before).append(" hit points.\n");
        } else {
            final int attacks = heroAttacksPerRound();
            for (int i = 0; i < attacks && monster.isAlive(); i++) {
                log.append(hero.attack(monster, rng)).append('\n');
            }
        }
    }
}