package src.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link HeroFactory}.
 *
 * @author Group 7 (Alex Douk)
 */
class HeroFactoryTest {

    private final HeroFactory factory = new HeroFactory();

    @Test
    void createsEachClassWithItsStartingStats() {
        final Hero warrior = factory.create("Warrior", "Conan");
        assertTrue(warrior instanceof Warrior);
        assertEquals(125, warrior.getHitPoints());
        assertEquals(4, warrior.getAttackSpeed());

        final Hero priestess = factory.create("Priestess", "Mira");
        assertTrue(priestess instanceof Priestess);
        assertEquals(75, priestess.getHitPoints());
        assertEquals(5, priestess.getAttackSpeed());

        final Hero thief = factory.create("Thief", "Fox");
        assertTrue(thief instanceof Thief);
        assertEquals(75, thief.getHitPoints());
        assertEquals(6, thief.getAttackSpeed());
    }

    @Test
    void newHeroHasNoPotionsAndNoPillars() {
        final Hero hero = factory.create("Warrior", "Conan");
        assertEquals(0, hero.getHealingPotions());
        assertEquals(0, hero.getVisionPotions());
        assertTrue(hero.getPillars().isEmpty());
    }

    @Test
    void usesTheGivenNameTrimmed() {
        assertEquals("Conan", factory.create("Warrior", "  Conan  ").getName());
    }

    @Test
    void eachCallReturnsSeparateObject() {
        assertNotSame(factory.create("Thief", "Fox"), factory.create("Thief", "Fox"));
    }

    @Test
    void unknownBlankOrNullTypeThrows() {
        assertThrows(IllegalArgumentException.class, () -> factory.create("Wizard", "Gandalf"));
        assertThrows(IllegalArgumentException.class, () -> factory.create(" ", "Gandalf"));
        assertThrows(IllegalArgumentException.class, () -> factory.create(null, "Gandalf"));
    }

    @Test
    void invalidNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> factory.create("Warrior", " "));
        assertThrows(IllegalArgumentException.class, () -> factory.create("Warrior", null));
        assertThrows(IllegalArgumentException.class,
                () -> factory.create("Warrior", "A".repeat(21)));
    }

    @Test
    void supportedTypesAreTheThreeHeroClasses() {
        assertEquals(List.of("Warrior", "Priestess", "Thief"), HeroFactory.getSupportedTypes());
    }
}
