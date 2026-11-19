package src.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Pit}.
 *
 * @author Group 7 (Alex Douk)
 */
class PitTest {

    @Test
    void isDrawnAsXAndIsNotCollectible() {
        final Pit pit = new Pit();
        assertEquals('X', pit.getSymbol());
        assertFalse(pit.isCollectible());
    }

    @Test
    void damageIsBetweenOneAndTwentyAndCoversTheRange() {
        final Pit pit = new Pit();
        final Random rng = new Random(1);
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (int i = 0; i < 2000; i++) {
            final Hero hero = new Warrior("Hero");
            pit.applyTo(hero, rng);
            final int lost = hero.getMaxHitPoints() - hero.getHitPoints();
            min = Math.min(min, lost);
            max = Math.max(max, lost);
        }
        assertEquals(Pit.MIN_DAMAGE, min);
        assertEquals(Pit.MAX_DAMAGE, max);
    }

    @Test
    void messageNamesTheDamage() {
        final Hero hero = new Warrior("Hero");
        final String message = new Pit().applyTo(hero, new Random(3));
        final int lost = hero.getMaxHitPoints() - hero.getHitPoints();
        assertTrue(message.contains(String.valueOf(lost)), message);
    }

    @Test
    void nullArgumentsRejected() {
        final Pit pit = new Pit();
        assertThrows(IllegalArgumentException.class, () -> pit.applyTo(null, new Random(1)));
        assertThrows(IllegalArgumentException.class,
                () -> pit.applyTo(new Warrior("Hero"), null));
    }

    @Test
    void canKillAWeakHero() {
        final Hero hero = new Thief("Hero");
        hero.takeDamage(hero.getHitPoints() - 1);
        new Pit().applyTo(hero, new Random(1));
        assertFalse(hero.isAlive());
    }
}
