package src.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Battle}.
 *
 * @author Group 7 (John Nguyen)
 */
class BattleTest {

    /** A Random with a fixed seed, so every run gives the same numbers. */
    private Random myRng;

    /** An Ogre with the SRS statistics (attack speed 2). */
    private Ogre myOgre;

    @BeforeEach
    void setUp() {
        myRng = new Random(42);
        myOgre = new Ogre("Ogre", new MonsterStats(200, 30, 60, 2, 0.6, 0.1, 30, 60));
    }

    /** Checks that a null hero, monster or Random is rejected. */
    @Test
    void nullArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Battle(null, myOgre, myRng));
        assertThrows(IllegalArgumentException.class,
                () -> new Battle(new Warrior("A"), null, myRng));
        assertThrows(IllegalArgumentException.class,
                () -> new Battle(new Warrior("A"), myOgre, null));
    }

    /** Checks that a null action is rejected. */
    @Test
    void nullActionIsRejected() {
        final Battle battle = new Battle(new Warrior("Conan"), myOgre, myRng);
        assertThrows(IllegalArgumentException.class,
                () -> battle.playRound(null));
    }

    /** Checks that a new battle has no winner and no rounds. */
    @Test
    void battleIsNotOverAtTheStart() {
        final Battle battle = new Battle(new Warrior("Conan"), myOgre, myRng);
        assertFalse(battle.isOver());
        assertTrue(battle.getWinner() == null);
        assertEquals(0, battle.getRounds());
    }

    /** Checks that a faster hero attacks more than once. */
    @Test
    void fasterHeroMakesMoreAttacks() {
        // Thief speed 6, Ogre speed 2: the Thief attacks 3 times
        final Battle battle = new Battle(new Thief("Shade"), myOgre, myRng);
        assertEquals(3, battle.heroAttacksPerRound());
    }

    /** Checks that a slower hero still attacks once. */
    @Test
    void slowerHeroStillMakesOneAttack() {
        // Warrior speed 4, Gremlin speed 5: 4 / 5 rounds down to 0, so 1
        final Gremlin gremlin = new Gremlin("Gremlin",
                new MonsterStats(70, 15, 30, 5, 0.8, 0.4, 20, 40));
        final Battle battle = new Battle(new Warrior("Conan"), gremlin, myRng);
        assertEquals(1, battle.heroAttacksPerRound());
    }

    /** Checks that a special skill replaces the normal attacks. */
    @Test
    void specialSkillReplacesTheNormalAttacks() {
        final Battle battle = new Battle(new Warrior("Conan"), myOgre, myRng);
        final String log = battle.playRound(Battle.Action.SPECIAL);
        assertFalse(log.contains("Conan hits"));
        assertFalse(log.contains("Conan misses"));
        assertTrue(log.contains("Crushing Blow"));
    }

    /** Checks that a healing potion replaces the normal attacks. */
    @Test
    void healingPotionReplacesTheNormalAttacks() {
        final Warrior warrior = new Warrior("Conan");
        warrior.takeDamage(50);
        warrior.addHealingPotion();
        final Battle battle = new Battle(warrior, myOgre, myRng);
        final String log = battle.playRound(Battle.Action.POTION);
        assertTrue(log.contains("drinks a healing potion"));
        assertFalse(log.contains("Conan hits"));
        assertEquals(0, warrior.getHealingPotions());
    }

    /** Checks that choosing a potion with none left does not use the round. */
    @Test
    void choosingAPotionWithNoneDoesNotUseTheRound() {
        final Battle battle = new Battle(new Warrior("Conan"), myOgre, myRng);
        final String log = battle.playRound(Battle.Action.POTION);
        assertTrue(log.contains("no healing potions"));
        assertEquals(0, battle.getRounds());
    }

    /** Checks that the log shows all three attacks of a fast hero. */
    @Test
    void thiefLogShowsThreeAttacksInOneRound() {
        final Battle battle = new Battle(new Thief("Shade"), myOgre, myRng);
        final String log = battle.playRound(Battle.Action.ATTACK);
        int count = 0;
        for (final String line : log.split("\n")) {
            if (line.startsWith("Shade hits") || line.startsWith("Shade misses")) {
                count++;
            }
        }
        assertEquals(3, count);
    }

    /** Checks that a fight ends with a living winner. */
    @Test
    void fightEndsWithExactlyOneWinner() {
        final Battle battle = new Battle(new Warrior("Conan"), myOgre, myRng);
        int safety = 0;
        while (!battle.isOver() && safety < 500) {
            battle.playRound(Battle.Action.ATTACK);
            safety++;
        }
        assertTrue(battle.isOver());
        assertTrue(battle.getWinner().isAlive());
        assertTrue(battle.getRounds() > 0);
    }

    /** Checks that a defeated monster does not attack. */
    @Test
    void defeatedMonsterDoesNotAttackBack() {
        // a 1 hit point monster that always hits, tried with 50 different seeds
        for (int seed = 0; seed < 50; seed++) {
            final Warrior warrior = new Warrior("Conan");
            final Ogre weak = new Ogre("Ogre", new MonsterStats(1, 30, 60, 2, 1.0, 0.0, 0, 0));
            final Battle battle = new Battle(warrior, weak, new Random(seed));
            battle.playRound(Battle.Action.ATTACK);
            if (!weak.isAlive()) {
                assertEquals(125, warrior.getHitPoints());
            }
        }
    }

    /** Checks that no round can be played after the battle ends. */
    @Test
    void cannotPlayARoundAfterTheBattleIsOver() {
        final Warrior warrior = new Warrior("Conan");
        final Battle battle = new Battle(warrior, myOgre, myRng);
        warrior.takeDamage(500);
        assertTrue(battle.isOver());
        assertThrows(IllegalStateException.class,
                () -> battle.playRound(Battle.Action.ATTACK));
    }

    /** Checks that the Priestess can heal in a round. */
    @Test
    void priestessHealCanBeUsedInARound() {
        final Priestess priestess = new Priestess("Mira");
        priestess.takeDamage(30);
        final Battle battle = new Battle(priestess, myOgre, myRng);
        final String log = battle.playRound(Battle.Action.SPECIAL);
        assertTrue(log.contains("Mira heals"));
    }

    // ---------- blocking, monster healing and the battle log ----------

    /** A monster with 1000 hit points that always hits for exactly 10 and never heals. */
    private static Ogre steadyMonster() {
        return new Ogre("Ogre", new MonsterStats(1000, 10, 10, 1, 1.0, 0.0, 0, 0));
    }

    /** Checks that a blocked attack deals no damage. */
    @Test
    void blockedAttackDealsNoDamage() {
        int blocked = 0;
        int notBlocked = 0;
        for (int seed = 0; seed < 100; seed++) {
            final Warrior warrior = new Warrior("Conan");
            final Battle battle = new Battle(warrior, steadyMonster(), new Random(seed));
            final String log = battle.playRound(Battle.Action.ATTACK);
            if (log.contains("Conan blocks Ogre's attack.")) {
                assertEquals(125, warrior.getHitPoints());
                blocked++;
            } else {
                assertEquals(115, warrior.getHitPoints());
                notBlocked++;
            }
        }
        assertTrue(blocked > 0 && notBlocked > 0);
    }

    /** Checks that the monster heals after each hit it survives. */
    @Test
    void monsterHealsAfterEveryHitItSurvives() {
        for (int seed = 0; seed < 50; seed++) {
            // this monster always heals exactly 10 when it is hurt and standing
            final Ogre healer = new Ogre("Ogre",
                    new MonsterStats(1000, 10, 10, 1, 1.0, 1.0, 10, 10));
            final Battle battle = new Battle(new Warrior("Conan"), healer, new Random(seed));
            final String log = battle.playRound(Battle.Action.ATTACK);
            int hits = 0;
            int heals = 0;
            for (final String line : log.split("\n")) {
                if (line.startsWith("Conan hits")) {
                    hits++;
                }
                if (line.equals("Ogre heals for 10 hit points.")) {
                    heals++;
                }
            }
            assertEquals(hits, heals);
        }
    }

    /** Checks that a monster that faints does not heal. */
    @Test
    void monsterThatFaintsDoesNotHeal() {
        for (int seed = 0; seed < 50; seed++) {
            final Ogre frail = new Ogre("Ogre", new MonsterStats(1, 10, 10, 1, 1.0, 1.0, 10, 10));
            final Battle battle = new Battle(new Warrior("Conan"), frail, new Random(seed));
            final String log = battle.playRound(Battle.Action.ATTACK);
            if (!frail.isAlive()) {
                assertFalse(log.contains("heals"));
            }
        }
    }

    /** Checks that the log is empty before the first round. */
    @Test
    void logIsEmptyBeforeTheFirstRound() {
        final Battle battle = new Battle(new Warrior("Conan"), myOgre, myRng);
        assertEquals("", battle.getLog());
    }

    /** Checks that the log holds every round. */
    @Test
    void logCollectsEveryRound() {
        final Battle battle = new Battle(new Warrior("Conan"), steadyMonster(), myRng);
        final String first = battle.playRound(Battle.Action.ATTACK);
        final String second = battle.playRound(Battle.Action.ATTACK);
        assertEquals(first + second, battle.getLog());
        assertTrue(battle.getLog().contains("Round 1"));
        assertTrue(battle.getLog().contains("Round 2"));
    }

    /** Checks that the log says who won. */
    @Test
    void logRecordsTheResult() {
        final Battle battle = new Battle(new Warrior("Conan"), myOgre, myRng);
        while (!battle.isOver()) {
            battle.playRound(Battle.Action.ATTACK);
        }
        assertTrue(battle.getLog().contains(battle.getWinner().getName() + " wins!"));
    }

    /** Checks that a potion choice with none left is not logged. */
    @Test
    void potionChoiceWithNoPotionsIsNotLogged() {
        final Battle battle = new Battle(new Warrior("Conan"), myOgre, myRng);
        battle.playRound(Battle.Action.POTION);
        assertEquals("", battle.getLog());
    }
}