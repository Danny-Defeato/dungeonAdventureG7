package src.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link SQLiteDatabase} and {@link MonsterStats}.
 *
 * @author Group 7 (Alex Douk)
 */
class SQLiteDatabaseTest {

    @TempDir
    Path tempDir;

    private SQLiteDatabase db;

    @BeforeEach
    void setUp() {
        db = new SQLiteDatabase("jdbc:sqlite:" + tempDir.resolve("test.db"));
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    @Test
    void databaseFileIsCreated() {
        assertTrue(Files.exists(tempDir.resolve("test.db")));
    }

    @Test
    void defaultOgreMatchesSrs() {
        assertEquals(new MonsterStats(200, 30, 60, 2, 0.6, 0.1, 30, 60),
                db.loadMonsterStats("Ogre"));
    }

    @Test
    void defaultGremlinMatchesSrs() {
        assertEquals(new MonsterStats(70, 15, 30, 5, 0.8, 0.4, 20, 40),
                db.loadMonsterStats("Gremlin"));
    }

    @Test
    void defaultSkeletonMatchesSrs() {
        assertEquals(new MonsterStats(100, 30, 50, 3, 0.8, 0.3, 30, 50),
                db.loadMonsterStats("Skeleton"));
    }

    @Test
    void insertedStatsMapBackToEveryField() {
        final MonsterStats in = new MonsterStats(11, 2, 5, 4, 0.25, 0.75, 1, 3);
        db.insertMonsterStats("Imp", in);
        final MonsterStats out = db.loadMonsterStats("Imp");
        assertEquals(11, out.getHitPoints());
        assertEquals(2, out.getMinDamage());
        assertEquals(5, out.getMaxDamage());
        assertEquals(4, out.getAttackSpeed());
        assertEquals(0.25, out.getChanceToHit());
        assertEquals(0.75, out.getChanceToHeal());
        assertEquals(1, out.getMinHeal());
        assertEquals(3, out.getMaxHeal());
    }

    @Test
    void insertingSameTypeReplacesRow() {
        db.insertMonsterStats("Ogre", new MonsterStats(1, 0, 0, 1, 0, 0, 0, 0));
        assertEquals(1, db.loadMonsterStats("Ogre").getHitPoints());
    }

    @Test
    void unknownTypeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> db.loadMonsterStats("Dragon"));
    }

    @Test
    void blankOrNullTypeThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.loadMonsterStats(" "));
        assertThrows(IllegalArgumentException.class, () -> db.loadMonsterStats(null));
        assertThrows(IllegalArgumentException.class, () -> db.insertMonsterStats("",
                new MonsterStats(1, 0, 0, 1, 0, 0, 0, 0)));
    }

    @Test
    void sqlInjectionTextIsTreatedAsData() {
        assertThrows(IllegalArgumentException.class,
                () -> db.loadMonsterStats("Ogre' OR '1'='1"));
    }

    @Test
    void dataPersistsAcrossReopen() {
        db.insertMonsterStats("Imp", new MonsterStats(11, 2, 5, 4, 0.25, 0.75, 1, 3));
        db.close();
        try (SQLiteDatabase again =
                new SQLiteDatabase("jdbc:sqlite:" + tempDir.resolve("test.db"))) {
            assertEquals(11, again.loadMonsterStats("Imp").getHitPoints());
            assertEquals(200, again.loadMonsterStats("Ogre").getHitPoints());
        }
    }

    @Test
    void reopenDoesNotOverwriteEditedDefaults() {
        db.insertMonsterStats("Ogre", new MonsterStats(5, 1, 2, 1, 0.5, 0, 0, 0));
        db.close();
        try (SQLiteDatabase again =
                new SQLiteDatabase("jdbc:sqlite:" + tempDir.resolve("test.db"))) {
            assertEquals(5, again.loadMonsterStats("Ogre").getHitPoints());
        }
    }

    @Test
    void closeTwiceIsSafe() {
        db.close();
        db.close();
    }

    @Test
    void operationsAfterCloseFail() {
        db.close();
        assertThrows(RuntimeException.class, () -> db.loadMonsterStats("Ogre"));
    }

    @Test
    void unopenablePathThrowsIllegalState() {
        assertThrows(IllegalStateException.class, () -> new SQLiteDatabase(
                "jdbc:sqlite:" + tempDir.resolve("no/such/dir/x.db")));
    }

    @Test
    void getAllTypesListsSeededTypesAlphabetically() {
        assertEquals(java.util.List.of("Gremlin", "Ogre", "Skeleton"),
                db.getAllTypes());
    }

    @Test
    void getAllTypesIncludesInsertedType() {
        db.insertMonsterStats("Imp", new MonsterStats(11, 2, 5, 4, 0.25, 0.75, 1, 3));
        assertTrue(db.getAllTypes().contains("Imp"));
        assertEquals(4, db.getAllTypes().size());
    }

    @Test
    void openExistingOpensExistingFile() {
        try (SQLiteDatabase again = SQLiteDatabase.openExisting(tempDir.resolve("test.db"))) {
            assertEquals(200, again.loadMonsterStats("Ogre").getHitPoints());
        }
    }

    @Test
    void openExistingMissingFileNamesPathAndDoesNotCreateIt() {
        final Path missing = tempDir.resolve("dungeon.db");
        final IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> SQLiteDatabase.openExisting(missing));
        assertTrue(e.getMessage().contains(missing.toAbsolutePath().toString()));
        assertTrue(Files.notExists(missing));
    }

    @Test
    void openExistingRejectsNonDatabaseFile() throws IOException {
        final Path junk = tempDir.resolve("junk.db");
        Files.writeString(junk, "this is not a sqlite database at all, just text");
        assertThrows(IllegalStateException.class,
                () -> SQLiteDatabase.openExisting(junk));
    }

    @Test
    void invalidStatsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new MonsterStats(0, 1, 2, 1, 0.5, 0.5, 1, 2));
        assertThrows(IllegalArgumentException.class,
                () -> new MonsterStats(10, 5, 2, 1, 0.5, 0.5, 1, 2));
        assertThrows(IllegalArgumentException.class,
                () -> new MonsterStats(10, 1, 2, 0, 0.5, 0.5, 1, 2));
        assertThrows(IllegalArgumentException.class,
                () -> new MonsterStats(10, 1, 2, 1, 1.5, 0.5, 1, 2));
        assertThrows(IllegalArgumentException.class,
                () -> new MonsterStats(10, 1, 2, 1, 0.5, -0.1, 1, 2));
        assertThrows(IllegalArgumentException.class,
                () -> new MonsterStats(10, 1, 2, 1, 0.5, 0.5, 3, 2));
    }
}
