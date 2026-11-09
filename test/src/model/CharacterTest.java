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
    private Random rng;

    /** A Warrior used by most tests. */
    private Warrior warrior;

    /**
     * A monster that always hits for exactly 10, always heals exactly 10,
     * and has 100 hit points, so its results never depend on luck.
     */
    private Ogre monster;

    @BeforeEach
    void setUp() {
        rng = new Random(42);
        warrior = new Warrior("Conan");
        monster = new Ogre("Ogre", new MonsterStats(100, 10, 10, 1, 1.0, 1.0, 10, 10));
    }

    // ---------- creating characters ----------

    @Test
    void warriorStartsWithItsStatistics() {
        assertEquals("Conan", warrior.getName());
        assertEquals(125, warrior.getHitPoints());
        assertEquals(4, warrior.getAttackSpeed());
    }

    @Test
    void nameIsTrimmed() {
        assertEquals("Bob", new Warrior("  Bob  ").getName());
    }

    @Test
    void badNamesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Warrior(""));
        assertThrows(IllegalArgumentException.class, () -> new Warrior(null));
        assertThrows(IllegalArgumentException.class, () -> new Warrior("A".repeat(21)));
    }

    @Test
    void monsterTakesItsStatisticsFromMonsterStats() {
        assertEquals("Ogre", monster.getName());
        assertEquals(100, monster.getHitPoints());
    }

    // ---------- hit points ----------

    @Test
    void takeDamageStopsAtZero() {
        warrior.takeDamage(25);
        assertEquals(100, warrior.getHitPoints());
        warrior.takeDamage(500);
        assertEquals(0, warrior.getHitPoints());
        assertFalse(warrior.isAlive());
    }

    @Test
    void negativeDamageIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> warrior.takeDamage(-1));
    }

    @Test
    void monsterAttackAlwaysHitsForTen() {
        monster.attack(warrior, rng);
        assertEquals(115, warrior.getHitPoints());
    }

    @Test
    void heroAttackDealsDamageInRangeOrMisses() {
        warrior.attack(monster, rng);
        final int lost = 100 - monster.getHitPoints();
        assertTrue(lost == 0 || (lost >= 35 && lost <= 60));
    }

    // ---------- potions ----------

    @Test
    void healingPotionHealsFiveToFifteenAndIsUsedUp() {
        warrior.takeDamage(50);
        warrior.addHealingPotion();
        assertTrue(warrior.useHealingPotion(rng));
        final int healed = warrior.getHitPoints() - 75;
        assertTrue(healed >= 5 && healed <= 15);
        assertEquals(0, warrior.getHealingPotions());
    }

    @Test
    void healingPotionCannotRaiseHitPointsAboveMaximum() {
        warrior.takeDamage(1);
        warrior.addHealingPotion();
        warrior.useHealingPotion(rng);
        assertEquals(125, warrior.getHitPoints());
    }

    @Test
    void usingPotionsWithNoneReturnsFalse() {
        assertFalse(warrior.useHealingPotion(rng));
        assertFalse(warrior.useVisionPotion());
    }

    @Test
    void visionPotionsAreCounted() {
        warrior.addVisionPotion();
        assertEquals(1, warrior.getVisionPotions());
        assertTrue(warrior.useVisionPotion());
        assertEquals(0, warrior.getVisionPotions());
    }

    // ---------- pillars ----------

    @Test
    void heroStartsWithNoPillars() {
        assertTrue(warrior.getPillars().isEmpty());
        assertFalse(warrior.hasAllPillars());
    }

    @Test
    void addPillarKeepsPillarsInOrderAndIgnoresDuplicates() {
        assertTrue(warrior.addPillar(new Pillar("Inheritance")));
        assertTrue(warrior.addPillar(new Pillar("Abstraction")));
        assertFalse(warrior.addPillar(new Pillar("Abstraction")));
        assertEquals(2, warrior.getPillars().size());
        assertEquals("Inheritance", warrior.getPillars().get(0).getName());
    }

    @Test
    void nullPillarIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> warrior.addPillar(null));
    }

    @Test
    void hasAllPillarsIsTrueAfterAllFour() {
        for (final String name : Pillar.NAMES) {
            warrior.addPillar(new Pillar(name));
        }
        assertTrue(warrior.hasAllPillars());
    }

    @Test
    void toStringListsThePillars() {
        assertTrue(warrior.toString().contains("Pillars: none"));
        warrior.addPillar(new Pillar("Abstraction"));
        assertTrue(warrior.toString().contains("Pillars: Abstraction"));
    }

    // ---------- special skills ----------

    @Test
    void crushingBlowDealsNothingOrSeventyFiveToOneSeventyFive() {
        final Ogre big = new Ogre("Ogre", new MonsterStats(200, 30, 60, 2, 0.6, 0.1, 30, 60));
        warrior.specialSkill(big, rng);
        final int lost = 200 - big.getHitPoints();
        assertTrue(lost == 0 || (lost >= 75 && lost <= 175));
    }

    @Test
    void priestessHealRestoresOnlyHerself() {
        final Priestess priestess = new Priestess("Mira");
        priestess.takeDamage(60);
        priestess.specialSkill(monster, rng);
        final int healed = priestess.getHitPoints() - 15;
        assertTrue(healed >= 25 && healed <= 50);
        assertEquals(100, monster.getHitPoints());
    }

    @Test
    void surpriseAttackNeverHealsTheMonster() {
        final Thief thief = new Thief("Shade");
        thief.specialSkill(monster, rng);
        assertTrue(monster.getHitPoints() <= 100);
    }

    // ---------- monster healing ----------

    @Test
    void monsterHealsAfterBeingHit() {
        monster.takeDamage(50);
        assertEquals(10, monster.tryHeal(rng));
        assertEquals(60, monster.getHitPoints());
    }

    @Test
    void monsterHealNeverGoesAboveMaximum() {
        monster.takeDamage(4);
        assertEquals(4, monster.tryHeal(rng));
        assertEquals(100, monster.getHitPoints());
    }

    @Test
    void faintedMonsterCannotHeal() {
        monster.takeDamage(500);
        assertEquals(0, monster.tryHeal(rng));
        assertEquals(0, monster.getHitPoints());
    }
}