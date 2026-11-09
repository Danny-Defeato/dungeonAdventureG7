package src.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link MonsterFactory}.
 *
 * @author Group 7 (Alex Douk)
 */
class MonsterFactoryTest {

    @TempDir
    Path tempDir;

    private SQLiteDatabase db;
    private MonsterFactory factory;

    @BeforeEach
    void setUp() {
        db = new SQLiteDatabase("jdbc:sqlite:" + tempDir.resolve("test.db"));
        factory = new MonsterFactory(db);
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    @Test
    void createsEachTypeWithDatabaseStats() {
        final Monster ogre = factory.create("Ogre", false);
        assertTrue(ogre instanceof Ogre);
        assertEquals("Ogre", ogre.getName());
        assertEquals(200, ogre.getHitPoints());
        assertEquals(2, ogre.getAttackSpeed());

        final Monster gremlin = factory.create("Gremlin", false);
        assertTrue(gremlin instanceof Gremlin);
        assertEquals(70, gremlin.getHitPoints());
        assertEquals(5, gremlin.getAttackSpeed());

        final Monster skeleton = factory.create("Skeleton", false);
        assertTrue(skeleton instanceof Skeleton);
        assertEquals(100, skeleton.getHitPoints());
        assertEquals(3, skeleton.getAttackSpeed());
    }

    @Test
    void usesEditedDatabaseValues() {
        db.insertMonsterStats("Ogre", new MonsterStats(10, 1, 2, 1, 0.5, 0, 0, 0));
        assertEquals(10, factory.create("Ogre", false).getHitPoints());
    }

    @Test
    void guardianHasQuarterMoreHitPoints() {
        assertEquals(250, factory.create("Ogre", true).getHitPoints());
        assertEquals(88, factory.create("Gremlin", true).getHitPoints());
        assertEquals(125, factory.create("Skeleton", true).getHitPoints());
    }

    @Test
    void guardianDamageRangeIsQuarterHigher() {
        // Ogre damage is 30-60, so a guardian deals 38-75 (37.5 rounds up).
        final Monster guardian = factory.create("Ogre", true);
        final Random seeded = new Random(1);
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (int i = 0; i < 2000; i++) {
            final Monster dummy = factory.create("Ogre", true);
            guardian.attack(dummy, seeded);
            final int dealt = dummy.getMaxHitPoints() - dummy.getHitPoints();
            if (dealt > 0) {
                min = Math.min(min, dealt);
                max = Math.max(max, dealt);
            }
        }
        assertTrue(min >= 38, "min was " + min);
        assertTrue(max <= 75 && max > 60, "max was " + max);
    }

    @Test
    void guardianKeepsAttackSpeed() {
        final Monster normal = factory.create("Gremlin", false);
        final Monster guardian = factory.create("Gremlin", true);
        assertEquals(normal.getAttackSpeed(), guardian.getAttackSpeed());
    }

    @Test
    void eachCallReturnsSeparateObject() {
        assertNotSame(factory.create("Ogre", false), factory.create("Ogre", false));
    }

    @Test
    void unknownBlankOrNullTypeThrows() {
        assertThrows(IllegalArgumentException.class, () -> factory.create("Dragon", false));
        assertThrows(IllegalArgumentException.class, () -> factory.create(" ", false));
        assertThrows(IllegalArgumentException.class, () -> factory.create(null, false));
    }

    @Test
    void typeInDatabaseButNotSupportedThrows() {
        db.insertMonsterStats("Imp", new MonsterStats(11, 2, 5, 4, 0.25, 0.75, 1, 3));
        assertThrows(IllegalArgumentException.class, () -> factory.create("Imp", false));
    }

    @Test
    void nullDatabaseRejected() {
        assertThrows(NullPointerException.class, () -> new MonsterFactory(null));
    }

    @Test
    void createRandomIsRepeatableWithSameSeedAndCoversAllTypes() {
        final Set<String> seen = new HashSet<>();
        final Random a = new Random(42);
        final Random b = new Random(42);
        for (int i = 0; i < 100; i++) {
            final String first = factory.createRandom(a, false).getName();
            assertEquals(first, factory.createRandom(b, false).getName());
            seen.add(first);
        }
        assertEquals(Set.of("Ogre", "Gremlin", "Skeleton"), seen);
    }

    @Test
    void createRandomGuardianIsStronger() {
        final Monster m = factory.createRandom(new Random(7), true);
        assertTrue(m.getHitPoints() > db.loadMonsterStats(m.getName()).getHitPoints());
    }

    @Test
    void supportedTypesMatchDatabaseSeed() {
        assertEquals(new HashSet<>(db.getAllTypes()),
                new HashSet<>(MonsterFactory.getSupportedTypes()));
    }
}
