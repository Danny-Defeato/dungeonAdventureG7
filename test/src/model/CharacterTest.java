package src.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the hero and monster classes.
 *
 * @author Group 7 (John Nguyen)
 */
class CharacterTest {

    /** A Random with a fixed seed, so every run gives the same numbers. */
    private Random myRng;

    /** A Warrior used by most tests. */
    private Warrior myWarrior;

    /**
     * A monster that always hits for exactly 10, always heals exactly 10,
     * and has 100 hit points, so its results never depend on luck.
     */
    private Ogre myMonster;

    @BeforeEach
    void setUp() {
        myRng = new Random(42);
        myWarrior = new Warrior("Conan");
        myMonster = new Ogre("Ogre", new MonsterStats(100, 10, 10, 1, 1.0, 1.0, 10, 10));
    }

    // ---------- creating characters ----------

    /** Checks the Warrior's starting statistics. */
    @Test
    void warriorStartsWithItsStatistics() {
        assertEquals("Conan", myWarrior.getName());
        assertEquals(125, myWarrior.getHitPoints());
        assertEquals(4, myWarrior.getAttackSpeed());
    }

    /** Checks that extra spaces are removed from names. */
    @Test
    void nameIsTrimmed() {
        assertEquals("Bob", new Warrior("  Bob  ").getName());
    }

    /** Checks that empty, null and too long names are rejected. */
    @Test
    void badNamesAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Warrior(""));
        assertThrows(IllegalArgumentException.class,
                () -> new Warrior(null));
        assertThrows(IllegalArgumentException.class,
                () -> new Warrior("A".repeat(21)));
    }

    /** Checks that a monster uses its MonsterStats. */
    @Test
    void monsterTakesItsStatisticsFromMonsterStats() {
        assertEquals("Ogre", myMonster.getName());
        assertEquals(100, myMonster.getHitPoints());
    }

    // ---------- hit points ----------

    /** Checks that hit points never go below zero. */
    @Test
    void takeDamageStopsAtZero() {
        myWarrior.takeDamage(25);
        assertEquals(100, myWarrior.getHitPoints());
        myWarrior.takeDamage(500);
        assertEquals(0, myWarrior.getHitPoints());
        assertFalse(myWarrior.isAlive());
    }

    /** Checks that negative damage is rejected. */
    @Test
    void negativeDamageIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> myWarrior.takeDamage(-1));
    }

    /** Checks that the test monster always hits for 10. */
    @Test
    void monsterAttackAlwaysHitsForTen() {
        myMonster.attack(myWarrior, myRng);
        assertEquals(115, myWarrior.getHitPoints());
    }

    /** Checks that a hero attack misses or deals damage in range. */
    @Test
    void heroAttackDealsDamageInRangeOrMisses() {
        myWarrior.attack(myMonster, myRng);
        final int lost = 100 - myMonster.getHitPoints();
        assertTrue(lost == 0 || (lost >= 35 && lost <= 60));
    }

    // ---------- potions ----------

    /** Checks that a healing potion heals 5 to 15 and is used up. */
    @Test
    void healingPotionHealsFiveToFifteenAndIsUsedUp() {
        myWarrior.takeDamage(50);
        myWarrior.addHealingPotion();
        assertTrue(myWarrior.useHealingPotion(myRng));
        final int healed = myWarrior.getHitPoints() - 75;
        assertTrue(healed >= 5 && healed <= 15);
        assertEquals(0, myWarrior.getHealingPotions());
    }

    /** Checks that healing stops at maximum hit points. */
    @Test
    void healingPotionCannotRaiseHitPointsAboveMaximum() {
        myWarrior.takeDamage(1);
        myWarrior.addHealingPotion();
        myWarrior.useHealingPotion(myRng);
        assertEquals(125, myWarrior.getHitPoints());
    }

    /** Checks that using a potion with none left returns false. */
    @Test
    void usingPotionsWithNoneReturnsFalse() {
        assertFalse(myWarrior.useHealingPotion(myRng));
        assertFalse(myWarrior.useVisionPotion());
    }

    /** Checks that vision potions are counted and used up. */
    @Test
    void visionPotionsAreCounted() {
        myWarrior.addVisionPotion();
        assertEquals(1, myWarrior.getVisionPotions());
        assertTrue(myWarrior.useVisionPotion());
        assertEquals(0, myWarrior.getVisionPotions());
    }

    // ---------- pillars ----------

    /** Checks that a new hero has no pillars. */
    @Test
    void heroStartsWithNoPillars() {
        assertTrue(myWarrior.getPillars().isEmpty());
        assertFalse(myWarrior.hasAllPillars());
    }

    /** Checks that pillars stay in order and duplicates are ignored. */
    @Test
    void addPillarKeepsPillarsInOrderAndIgnoresDuplicates() {
        assertTrue(myWarrior.addPillar(new Pillar("Inheritance")));
        assertTrue(myWarrior.addPillar(new Pillar("Abstraction")));
        assertFalse(myWarrior.addPillar(new Pillar("Abstraction")));
        assertEquals(2, myWarrior.getPillars().size());
        assertEquals("Inheritance", myWarrior.getPillars().get(0).getName());
    }

    /** Checks that a null pillar is rejected. */
    @Test
    void nullPillarIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> myWarrior.addPillar(null));
    }

    /** Checks that the hero has all pillars after collecting four. */
    @Test
    void hasAllPillarsIsTrueAfterAllFour() {
        for (final String name : Pillar.NAMES) {
            myWarrior.addPillar(new Pillar(name));
        }
        assertTrue(myWarrior.hasAllPillars());
    }

    /** Checks that toString shows the pillars. */
    @Test
    void toStringListsThePillars() {
        assertTrue(myWarrior.toString().contains("Pillars: none"));
        myWarrior.addPillar(new Pillar("Abstraction"));
        assertTrue(myWarrior.toString().contains("Pillars: Abstraction"));
    }

    // ---------- special skills ----------

    /** Checks that Crushing Blow misses or deals 75 to 175. */
    @Test
    void crushingBlowDealsNothingOrSeventyFiveToOneSeventyFive() {
        final Ogre big = new Ogre("Ogre", new MonsterStats(200, 30, 60, 2, 0.6, 0.1, 30, 60));
        myWarrior.specialSkill(big, myRng);
        final int lost = 200 - big.getHitPoints();
        assertTrue(lost == 0 || (lost >= 75 && lost <= 175));
    }

    /** Checks that the Priestess heals herself, not the monster. */
    @Test
    void priestessHealRestoresOnlyHerself() {
        final Priestess priestess = new Priestess("Mira");
        priestess.takeDamage(60);
        priestess.specialSkill(myMonster, myRng);
        final int healed = priestess.getHitPoints() - 15;
        assertTrue(healed >= 25 && healed <= 50);
        assertEquals(100, myMonster.getHitPoints());
    }

    /** Checks that the Thief's special skill never heals the monster. */
    @Test
    void surpriseAttackNeverHealsTheMonster() {
        final Thief thief = new Thief("Shade");
        thief.specialSkill(myMonster, myRng);
        assertTrue(myMonster.getHitPoints() <= 100);
    }

    // ---------- monster healing ----------

    /** Checks that a hurt monster heals. */
    @Test
    void monsterHealsAfterBeingHit() {
        myMonster.takeDamage(50);
        assertEquals(10, myMonster.tryHeal(myRng));
        assertEquals(60, myMonster.getHitPoints());
    }

    /** Checks that monster healing stops at maximum hit points. */
    @Test
    void monsterHealNeverGoesAboveMaximum() {
        myMonster.takeDamage(4);
        assertEquals(4, myMonster.tryHeal(myRng));
        assertEquals(100, myMonster.getHitPoints());
    }

    /** Checks that a fainted monster cannot heal. */
    @Test
    void faintedMonsterCannotHeal() {
        myMonster.takeDamage(500);
        assertEquals(0, myMonster.tryHeal(myRng));
        assertEquals(0, myMonster.getHitPoints());
    }

    // ---------- null and negative arguments ----------

    /** Checks that null arguments are rejected. */
    @Test
    void nullArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> myWarrior.attack(null, myRng));
        assertThrows(IllegalArgumentException.class,
                () -> myWarrior.attack(myMonster, null));
        assertThrows(IllegalArgumentException.class,
                () -> myWarrior.block(null));
        assertThrows(IllegalArgumentException.class,
                () -> myWarrior.useHealingPotion(null));
        assertThrows(IllegalArgumentException.class,
                () -> myWarrior.specialSkill(null, myRng));
        assertThrows(IllegalArgumentException.class,
                () -> myMonster.tryHeal(null));
    }

    /** Checks that a monster without stats is rejected. */
    @Test
    void monsterWithoutStatsIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Ogre("Ogre", null));
    }

    /** Checks that a negative heal amount is rejected. */
    @Test
    void negativeHealAmountIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> myWarrior.heal(-1));
    }
}