package src.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import src.model.Direction;
import src.model.Dungeon;
import src.model.DungeonBuilder;
import src.model.Monster;
import src.model.MonsterFactory;
import src.model.SQLiteDatabase;

/**
 * Tests for the text of {@link HiddenDungeonView} (SRS 3.11, FR-MNU-2). The
 * window itself is not opened, so these tests also run without a screen.
 *
 * @author Group 7 (Alex Douk)
 */
class HiddenDungeonViewTest {

    /** Lines in one room's picture. */
    private static final int ROOM_LINES = 3;

    /** Characters across one room's picture. */
    private static final int ROOM_WIDTH = 3;

    @TempDir
    private Path myTempDir;

    private SQLiteDatabase myDatabase;

    private Dungeon myDungeon;

    @BeforeEach
    void setUp() {
        myDatabase = new SQLiteDatabase("jdbc:sqlite:" + myTempDir.resolve("test.db"));
        myDungeon = new DungeonBuilder(new MonsterFactory(myDatabase), new Random(7))
                .build();
    }

    @AfterEach
    void tearDown() {
        myDatabase.close();
    }

    @Test
    void mapHasThreeLinesPerRowAndThreeCharactersPerRoom() {
        final String[] lines = HiddenDungeonView.buildText(myDungeon).split("\n");
        for (int i = 0; i < myDungeon.getRows() * ROOM_LINES; i++) {
            assertEquals(myDungeon.getCols() * ROOM_WIDTH, lines[i].length(), lines[i]);
        }
    }

    @Test
    void heroRoomIsMarked() {
        final String[] lines = HiddenDungeonView.buildText(myDungeon).split("\n");
        final int line = myDungeon.getHeroRow() * ROOM_LINES + 1;
        final int column = myDungeon.getHeroCol() * ROOM_WIDTH + 1;
        assertEquals(HiddenDungeonView.HERO_SYMBOL, lines[line].charAt(column));
    }

    @Test
    void heroLineNamesTheHeroRoom() {
        final String text = HiddenDungeonView.buildText(myDungeon);
        assertTrue(text.contains("Hero (@): row " + myDungeon.getHeroRow()
                + ", col " + myDungeon.getHeroCol()), text);
    }

    @Test
    void markMovesWithTheHero() {
        final Direction door = myDungeon.getHeroRoom().getDoors().iterator().next();
        myDungeon.moveHero(door);
        heroRoomIsMarked();
    }

    @Test
    void everyMonsterIsListed() {
        final String text = HiddenDungeonView.buildText(myDungeon);
        int monsters = 0;
        for (int row = 0; row < myDungeon.getRows(); row++) {
            for (int col = 0; col < myDungeon.getCols(); col++) {
                final Monster monster = myDungeon.getRoom(row, col).getMonster();
                if (monster != null) {
                    monsters++;
                    assertTrue(text.contains("row " + row + ", col " + col + ": "
                            + monster.getName()), text);
                }
            }
        }
        // Four pillar guardians and the exit guardian are always there.
        assertTrue(monsters >= 5, "expected at least 5 monsters, found " + monsters);
    }

    @Test
    void buildingTheTextDoesNotChangeTheGame() {
        final String before = myDungeon.toString();
        final int heroRow = myDungeon.getHeroRow();
        final int heroCol = myDungeon.getHeroCol();
        HiddenDungeonView.buildText(myDungeon);
        assertEquals(before, myDungeon.toString());
        assertEquals(heroRow, myDungeon.getHeroRow());
        assertEquals(heroCol, myDungeon.getHeroCol());
    }

    @Test
    void nullDungeonIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> HiddenDungeonView.buildText(null));
    }
}
