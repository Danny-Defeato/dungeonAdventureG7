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
 * <p>Every attack, miss, block, heal and the result of the battle is added
 * to the log.
 *
 * @author Group 7 (John Nguyen)
 * @version 0.3
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
    private final Hero myHero;

    /** The monster in the fight. */
    private final Monster myMonster;

    /** The shared source of randomness. */
    private final Random myRng;

    /** The number of rounds played so far. */
    private int myRounds;

    /** All battle events recorded line per line */
    private final StringBuilder myLog;

    /**
     * Creates a battle. Nothing happens until playRound is called.
     *
     * @param theHero the hero, not null
     * @param theMonster the monster, not null
     * @param theRng the shared source of randomness, not null
     * @throws IllegalArgumentException if any argument is null
     */
    public Battle(final Hero theHero, final Monster theMonster, final Random theRng) {
        if (theHero == null || theMonster == null || theRng == null) {
            throw new IllegalArgumentException("hero, monster and rng must not be null");
        }
        this.myHero = theHero;
        this.myMonster = theMonster;
        this.myRng = theRng;
        this.myRounds = 0;
        myLog = new StringBuilder();
    }

    /**
     * Plays one round. The hero acts first and then the monster attacks
     * once. Attacks stop as soon as either side faints, so a monster the
     * hero defeats does not get to attack. If the player chooses a potion
     * and the hero has none, nothing happens and the round is not used.
     *
     * @param theAction what the player chooses for the hero this round
     * @return the battle log for the round, one event per line
     * @throws IllegalStateException if the battle is already over
     */
    public String playRound(final Action theAction) {
        if (theAction == null) {
            throw new IllegalArgumentException("action must not be null");
        }
        if (isOver()) {
            throw new IllegalStateException("the battle is already over");
        }
        final StringBuilder log = new StringBuilder();
        if (theAction == Action.POTION && myHero.getHealingPotions() == 0) {
            log.append(myHero.getName()).append(" has no healing potions.\n");
        } else {
            myRounds++;
            log.append("Round ").append(myRounds).append('\n');
            heroTurn(theAction, log);
            if (myHero.isAlive() && myMonster.isAlive()) {
                monsterTurn(log);
            }
            if (isOver()) {
                log.append(getWinner().getName()).append(" wins!\n");
            }
            myLog.append(log);
        }
        return log.toString();
    }

    /**
     * Returns every event of the battle so far, one event per line. It
     * records every attack, miss, block, heal and the result (FR-COM-7).
     *
     * @return the whole battle log, or an empty string before the first round
     */
    public String getLog() {
        return myLog.toString();
    }

    /**
     * Tells whether the hero or the monster has fainted.
     *
     * @return true if the battle is over
     */
    public boolean isOver() {
        return !myHero.isAlive() || !myMonster.isAlive();
    }

    /**
     * Returns the winner of the battle.
     *
     * @return the hero or the monster, or null if the battle is not over
     */
    public DungeonCharacter getWinner() {
        DungeonCharacter winner = null;
        if (isOver()) {
            if (myHero.isAlive()) {
                winner = myHero;
            } else {
                winner = myMonster;
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
        return myRounds;
    }

    /**
     * Returns how many normal attacks the hero makes in a round. The hero
     * makes as many attacks as its speed divided by the monster's speed,
     * rounded down, and always at least one (FR-COM-3).
     *
     * @return the number of hero attacks per round
     */
    int heroAttacksPerRound() {
        return Math.max(1, myHero.getAttackSpeed() / myMonster.getAttackSpeed());
    }

    /**
     * Carries out the hero's choice for the round. After each action that
     * hurts the monster, the monster may heal.
     *
     * @param theAction the player's choice
     * @param theLog the log to add the events to
     */
    private void heroTurn(final Action theAction, final StringBuilder theLog) {
        if (theAction == Action.SPECIAL) {
            final int before = myMonster.getHitPoints();
            theLog.append(myHero.specialSkill(myMonster, myRng)).append('\n');
            monsterTriesToHeal(before, theLog);
        } else if (theAction == Action.POTION) {
            final int before = myHero.getHitPoints();
            myHero.useHealingPotion(myRng);
            theLog.append(myHero.getName()).append(" drinks a healing potion and heals ")
                    .append(myHero.getHitPoints() - before).append(" hit points.\n");
        } else {
            final int attacks = heroAttacksPerRound();
            for (int i = 0; i < attacks && myMonster.isAlive(); i++) {
                final int before = myMonster.getHitPoints();
                theLog.append(myHero.attack(myMonster, myRng)).append('\n');
                monsterTriesToHeal(before, theLog);
            }
        }
    }

    /**
     * Makes the monster's attack on the hero. The hero first rolls to
     * block it, and a blocked attack deals no damage (FR-COM-5).
     *
     * @param theLog the log to add the events to
     */
    private void monsterTurn(final StringBuilder theLog) {
        if (myHero.block(myRng)) {
            theLog.append(myHero.getName()).append(" blocks ")
                    .append(myMonster.getName()).append("'s attack.\n");
        } else {
            theLog.append(myMonster.attack(myHero, myRng)).append("\n");
        }
    }

    /**
     * Makes it so that the monster attempts to heal so long as if was hit before
     * and if it is still alive (FR-COM-6).
     *
     * @param hitsPointsBefore the monster's hit points before the action
     * @param theLog the log to add the events to
     */
    private void monsterTriesToHeal(final int hitsPointsBefore, final StringBuilder theLog) {
        if (myMonster.isAlive() && myMonster.getHitPoints() < hitsPointsBefore) {
            final int healed = myMonster.tryHeal(myRng);
            if (healed > 0) {
                theLog.append(myMonster.getName()).append(" heals for ")
                        .append(healed).append(" hitpoints.\n");
            }
        }
    }
}