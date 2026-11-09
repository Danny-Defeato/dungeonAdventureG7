package src.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link DungeonBuilder} and {@link Dungeon}.
 *
 * @author Group 7 (Alex Douk)
 */
class DungeonBuilderTest {

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

    private Dungeon build(final long seed, final int rows, final int cols) {
        return new DungeonBuilder(factory, new Random(seed)).setSize(rows, cols).build();
    }

    private static int count(final Dungeon d, final Class<? extends RoomItem> type) {
        int n = 0;
        for (int r = 0; r < d.getRows(); r++) {
            for (int c = 0; c < d.getCols(); c++) {
                for (final RoomItem item : d.getRoom(r, c).getItems()) {
                    if (type.isInstance(item)) {
                        n++;
                    }
                }
            }
        }
        return n;
    }

    @Test
    void hasRequestedSize() {
        final Dungeon d = build(1, 4, 7);
        assertEquals(4, d.getRows());
        assertEquals(7, d.getCols());
    }

    @Test
    void defaultSizeIsFiveByFive() {
        final Dungeon d = new DungeonBuilder(factory, new Random(1)).build();
        assertEquals(5, d.getRows());
        assertEquals(5, d.getCols());
    }

    @Test
    void roomsKnowTheirOwnPosition() {
        final Dungeon d = build(2, 5, 6);
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 6; c++) {
                assertEquals(r, d.getRoom(r, c).getRow());
                assertEquals(c, d.getRoom(r, c).getCol());
            }
        }
    }

    @Test
    void oneEntranceOneExitFourPillarsInSeparateRooms() {
        for (long seed = 0; seed < 100; seed++) {
            final Dungeon d = build(seed, 5, 5);
            assertEquals(1, count(d, Entrance.class));
            assertEquals(1, count(d, Exit.class));
            assertEquals(4, count(d, Pillar.class));
            assertNotSame(d.getEntrance(), d.getExit());
            final Set<String> names = new HashSet<>();
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < 5; c++) {
                    final Room room = d.getRoom(r, c);
                    assertTrue(room.getItems().size() <= 1, "no room holds two items");
                    for (final RoomItem item : room.getItems()) {
                        if (item instanceof Pillar) {
                            names.add(((Pillar) item).getName());
                        }
                    }
                }
            }
            assertEquals(new HashSet<>(Pillar.NAMES), names);
        }
    }

    @Test
    void entranceHoldsNothingElse() {
        for (long seed = 0; seed < 100; seed++) {
            final Dungeon d = build(seed, 5, 5);
            assertEquals(1, d.getEntrance().getItems().size());
            assertTrue(d.getEntrance().hasEntrance());
            assertNull(d.getEntrance().getMonster());
        }
    }

    @Test
    void alwaysTraversableFromEntranceToExitAndPillars() {
        for (long seed = 0; seed < 300; seed++) {
            assertTrue(build(seed, 5, 5).isTraversable(), "seed " + seed);
        }
    }

    @Test
    void doorsAreTwoWayAndNeverLeaveTheGrid() {
        for (long seed = 0; seed < 50; seed++) {
            final Dungeon d = build(seed, 5, 6);
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < 6; c++) {
                    for (final Direction dir : d.getRoom(r, c).getDoors()) {
                        final int nr = r + dir.getRowDelta();
                        final int nc = c + dir.getColDelta();
                        assertTrue(nr >= 0 && nr < 5 && nc >= 0 && nc < 6);
                        assertTrue(d.getRoom(nr, nc).hasDoor(dir.opposite()));
                    }
                }
            }
        }
    }

    @Test
    void everyRoomHasADoor() {
        final Dungeon d = build(3, 6, 6);
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                assertTrue(d.getRoom(r, c).getDoorCount() >= 1);
            }
        }
    }

    @Test
    void hasDeadEnds() {
        for (long seed = 0; seed < 30; seed++) {
            final Dungeon d = build(seed, 6, 6);
            boolean dead = false;
            for (int r = 0; r < 6; r++) {
                for (int c = 0; c < 6; c++) {
                    dead |= d.getRoom(r, c).getDoorCount() == 1;
                }
            }
            assertTrue(dead, "seed " + seed);
        }
    }

    @Test
    void pillarAndExitRoomsHaveGuardiansOthersAreOrdinary() {
        final Set<String> types = new HashSet<>();
        for (long seed = 0; seed < 60; seed++) {
            final Dungeon d = build(seed, 6, 6);
            for (int r = 0; r < 6; r++) {
                for (int c = 0; c < 6; c++) {
                    final Room room = d.getRoom(r, c);
                    final Monster m = room.getMonster();
                    if (room.hasPillar() || room.hasExit()) {
                        assertNotNull(m, "guardian expected");
                        assertTrue(m.getHitPoints() > db.loadMonsterStats(m.getName())
                                .getHitPoints());
                    } else if (m != null) {
                        assertEquals(db.loadMonsterStats(m.getName()).getHitPoints(),
                                m.getHitPoints());
                        types.add(m.getName());
                    }
                }
            }
        }
        assertEquals(Set.of("Ogre", "Gremlin", "Skeleton"), types);
    }

    @Test
    void sameSeedGivesSameDungeon() {
        assertEquals(build(99, 6, 6).toString(), build(99, 6, 6).toString());
    }

    @Test
    void differentSeedsGiveDifferentDungeons() {
        assertFalse(build(1, 6, 6).toString().equals(build(2, 6, 6).toString()));
    }

    @Test
    void worksOnSmallestSizes() {
        assertTrue(build(1, 2, 3).isTraversable());
        assertTrue(build(1, 1, 6).isTraversable());
        assertTrue(build(1, 6, 1).isTraversable());
    }

    @Test
    void tooSmallSizeRejected() {
        final DungeonBuilder b = new DungeonBuilder(factory, new Random(1));
        assertThrows(IllegalArgumentException.class, () -> b.setSize(2, 2));
        assertThrows(IllegalArgumentException.class, () -> b.setSize(0, 10));
        assertThrows(IllegalArgumentException.class, () -> b.setSize(-1, 10));
    }

    @Test
    void nullArgumentsRejected() {
        assertThrows(NullPointerException.class,
                () -> new DungeonBuilder(null, new Random()));
        assertThrows(NullPointerException.class,
                () -> new DungeonBuilder(factory, null));
    }

    @Test
    void heroStartsInVisitedEntrance() {
        final Dungeon d = build(5, 5, 5);
        assertSame(d.getEntrance(), d.getHeroRoom());
        assertEquals(d.getEntrance().getRow(), d.getHeroRow());
        assertEquals(d.getEntrance().getCol(), d.getHeroCol());
        assertTrue(d.getEntrance().isVisited());
    }

    @Test
    void moveHeroThroughDoorUpdatesPositionAndVisited() {
        final Dungeon d = build(5, 5, 5);
        final Direction dir = d.getHeroRoom().getDoors().iterator().next();
        final int r = d.getHeroRow() + dir.getRowDelta();
        final int c = d.getHeroCol() + dir.getColDelta();
        final Room moved = d.moveHero(dir);
        assertSame(d.getRoom(r, c), moved);
        assertEquals(r, d.getHeroRow());
        assertEquals(c, d.getHeroCol());
        assertTrue(moved.isVisited());
    }

    @Test
    void moveHeroIntoWallDoesNothing() {
        final Dungeon d = build(5, 5, 5);
        for (final Direction dir : Direction.values()) {
            if (!d.getHeroRoom().hasDoor(dir)) {
                final int r = d.getHeroRow();
                final int c = d.getHeroCol();
                assertNull(d.moveHero(dir));
                assertEquals(r, d.getHeroRow());
                assertEquals(c, d.getHeroCol());
            }
        }
    }

    @Test
    void heroCanWalkToTheExit() {
        final Dungeon d = build(8, 5, 5);
        // Every room is connected, so a bounded random walk through doors
        // eventually reaches the exit.
        final Random walk = new Random(1);
        for (int i = 0; i < 100000 && !d.getHeroRoom().hasExit(); i++) {
            d.moveHero(Direction.values()[walk.nextInt(4)]);
        }
        assertTrue(d.getHeroRoom().hasExit());
    }

    @Test
    void toStringIsOneThreeByThreePicturePerRoom() {
        final Dungeon d = build(4, 4, 5);
        final String[] lines = d.toString().split("\n");
        assertEquals(12, lines.length);
        for (final String line : lines) {
            assertEquals(15, line.length());
        }
        assertTrue(d.toString().contains("i"));
        assertTrue(d.toString().contains("O"));
        for (final char pillar : new char[] {'A', 'E', 'I', 'P'}) {
            assertTrue(d.toString().indexOf(pillar) >= 0);
        }
    }

    @Test
    void invalidDungeonsAreRejected() {
        final Room a = new Room(0, 0);
        final Room stranger = new Room(0, 1);
        assertThrows(IllegalArgumentException.class,
                () -> new Dungeon(new Room[0][0], a, a));
        assertThrows(IllegalArgumentException.class,
                () -> new Dungeon(new Room[][] {{a}}, a, stranger));
        assertThrows(IllegalArgumentException.class,
                () -> new Dungeon(new Room[][] {{a}, {}}, a, a));
        assertThrows(NullPointerException.class,
                () -> new Dungeon(new Room[][] {{null}}, a, a));
    }

    @Test
    void getRoomOutsideThrows() {
        final Dungeon d = build(1, 5, 5);
        assertThrows(IllegalArgumentException.class, () -> d.getRoom(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> d.getRoom(0, 5));
        assertThrows(IllegalArgumentException.class, () -> d.getRoom(5, 0));
    }

    @Test
    void notTraversableWhenExitIsWalledOff() {
        final Room a = new Room(0, 0);
        final Room b = new Room(0, 1);
        a.addItem(new Entrance());
        b.addItem(new Exit());
        assertFalse(new Dungeon(new Room[][] {{a, b}}, a, b).isTraversable());
    }

    @Test
    void survivesSaveAndLoad() throws Exception {
        final Dungeon d = build(12, 5, 5);
        d.moveHero(d.getHeroRoom().getDoors().iterator().next());
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(d);
        }
        try (ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()))) {
            final Dungeon loaded = (Dungeon) in.readObject();
            assertEquals(d.toString(), loaded.toString());
            assertEquals(d.getHeroRow(), loaded.getHeroRow());
            assertEquals(d.getHeroCol(), loaded.getHeroCol());
            assertTrue(loaded.isTraversable());
        }
    }
}
