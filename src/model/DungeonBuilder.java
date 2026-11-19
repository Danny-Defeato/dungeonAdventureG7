package src.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Builds a random {@link Dungeon} (Builder pattern).
 *
 * <p>The maze is carved with a randomized depth-first search, which connects
 * every room and leaves dead ends. A few extra walls are then opened so
 * there are some loops. The entrance, exit and four pillars go in six
 * different rooms. The entrance room holds nothing else. Every pillar room
 * and the exit room get a guardian monster; other rooms get an ordinary
 * monster with probability {@link #MONSTER_CHANCE}.
 *
 * <p>Every room other than the entrance, exit and pillar rooms independently
 * has a {@link #ITEM_CHANCE} chance of a healing potion, of a vision potion and
 * of a pit (FR-GEN-4), so one room can hold several. Pillar rooms hold only
 * their pillar, so the pillar's letter always shows on the map.
 *
 * <p>All randomness comes from the injected {@link Random}, so a fixed seed
 * gives the same dungeon.
 *
 * @author Group 7 (Alex Douk)
 * @version 0.2
 */
public class DungeonBuilder {

    /** Default number of rows and columns. */
    public static final int DEFAULT_SIZE = 5;

    /** Fewest rows and fewest columns a dungeon can have (FR-GEN-1). */
    public static final int MIN_SIZE = 4;

    /** Chance that an ordinary room holds a monster. */
    public static final double MONSTER_CHANCE = 0.15;

    /** Chance that an ordinary room holds each of a healing potion, a vision potion and a pit. */
    public static final double ITEM_CHANCE = 0.10;

    /** Chance that a wall between two rooms is opened after carving. */
    public static final double EXTRA_DOOR_CHANCE = 0.08;

    /** Position in the shuffled room list of the entrance. */
    private static final int ENTRANCE_INDEX = 0;

    /** Position in the shuffled room list of the exit. */
    private static final int EXIT_INDEX = 1;

    /** Position in the shuffled room list of the first pillar room. */
    private static final int FIRST_PILLAR_INDEX = 2;

    /** Position in the shuffled room list of the first ordinary room. */
    private static final int FIRST_ORDINARY_INDEX = FIRST_PILLAR_INDEX + Pillar.TOTAL;

    /** How many mazes to try before giving up. */
    private static final int MAX_ATTEMPTS = 100;

    /** The factory that makes the monsters. */
    private final MonsterFactory myMonsterFactory;

    /** The shared source of randomness. */
    private final Random myRng;

    /** The number of rows of the dungeon to build. */
    private int myRows = DEFAULT_SIZE;

    /** The number of columns of the dungeon to build. */
    private int myCols = DEFAULT_SIZE;

    /**
     * Creates a builder.
     *
     * @param theMonsterFactory the factory used to make monsters, not null
     * @param theRng the shared source of randomness, not null
     * @throws IllegalArgumentException if either argument is null
     */
    public DungeonBuilder(final MonsterFactory theMonsterFactory, final Random theRng) {
        if (theMonsterFactory == null) {
            throw new IllegalArgumentException("monsterFactory must not be null");
        }
        if (theRng == null) {
            throw new IllegalArgumentException("rng must not be null");
        }
        myMonsterFactory = theMonsterFactory;
        myRng = theRng;
    }

    /**
     * Sets the size of the dungeon.
     *
     * @param theRows the number of rows, at least {@link #MIN_SIZE}
     * @param theCols the number of columns, at least {@link #MIN_SIZE}
     * @return this builder
     * @throws IllegalArgumentException if the size is too small
     */
    public DungeonBuilder setSize(final int theRows, final int theCols) {
        if (theRows < MIN_SIZE || theCols < MIN_SIZE) {
            throw new IllegalArgumentException(
                    "dungeon must be at least " + MIN_SIZE + " x " + MIN_SIZE);
        }
        myRows = theRows;
        myCols = theCols;
        return this;
    }

    /**
     * Builds a new dungeon. If a generated maze cannot be completed or has no
     * dead end (FR-GEN-1, FR-GEN-2), a new one is generated.
     *
     * @return the dungeon, with the hero at the entrance
     * @throws IllegalStateException if no usable dungeon could be built
     */
    public Dungeon build() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            final Dungeon dungeon = tryBuild();
            if (dungeon.isTraversable() && dungeon.hasDeadEnd()) {
                return dungeon;
            }
        }
        throw new IllegalStateException("could not build a traversable dungeon");
    }

    private Dungeon tryBuild() {
        final Room[][] grid = new Room[myRows][myCols];
        for (int r = 0; r < myRows; r++) {
            for (int c = 0; c < myCols; c++) {
                grid[r][c] = new Room(r, c);
            }
        }
        carve(grid);
        openExtraDoors(grid);

        final List<Room> all = new ArrayList<>();
        for (final Room[] row : grid) {
            Collections.addAll(all, row);
        }
        Collections.shuffle(all, myRng);
        final Room entrance = all.get(ENTRANCE_INDEX);
        final Room exit = all.get(EXIT_INDEX);
        entrance.addItem(new Entrance());
        exit.addItem(new Exit());
        exit.setMonster(myMonsterFactory.createRandom(myRng, true));
        for (int i = 0; i < Pillar.TOTAL; i++) {
            final Room room = all.get(FIRST_PILLAR_INDEX + i);
            room.addItem(new Pillar(Pillar.NAMES.get(i)));
            room.setMonster(myMonsterFactory.createRandom(myRng, true));
        }
        for (int i = FIRST_ORDINARY_INDEX; i < all.size(); i++) {
            placeItems(all.get(i));
        }
        for (int i = FIRST_ORDINARY_INDEX; i < all.size(); i++) {
            if (myRng.nextDouble() < MONSTER_CHANCE) {
                all.get(i).setMonster(myMonsterFactory.createRandom(myRng, false));
            }
        }
        return new Dungeon(grid, entrance, exit);
    }

    /** Each of a healing potion, a vision potion and a pit is placed independently. */
    private void placeItems(final Room theRoom) {
        if (myRng.nextDouble() < ITEM_CHANCE) {
            theRoom.addItem(new HealthPotion());
        }
        if (myRng.nextDouble() < ITEM_CHANCE) {
            theRoom.addItem(new VisionPotion());
        }
        if (myRng.nextDouble() < ITEM_CHANCE) {
            theRoom.addItem(new Pit());
        }
    }

    /** Randomized depth-first search: opens a door between neighbors. */
    private void carve(final Room[][] theGrid) {
        final boolean[][] seen = new boolean[myRows][myCols];
        final Deque<Room> stack = new ArrayDeque<>();
        final Room start = theGrid[myRng.nextInt(myRows)][myRng.nextInt(myCols)];
        seen[start.getRow()][start.getCol()] = true;
        stack.push(start);
        while (!stack.isEmpty()) {
            final Room room = stack.peek();
            final List<Direction> options = new ArrayList<>();
            for (final Direction d : Direction.values()) {
                final int r = room.getRow() + d.getRowDelta();
                final int c = room.getCol() + d.getColDelta();
                if (inBounds(r, c) && !seen[r][c]) {
                    options.add(d);
                }
            }
            if (options.isEmpty()) {
                stack.pop();
            } else {
                final Direction d = options.get(myRng.nextInt(options.size()));
                final Room next = theGrid[room.getRow() + d.getRowDelta()]
                        [room.getCol() + d.getColDelta()];
                connect(room, d, next);
                seen[next.getRow()][next.getCol()] = true;
                stack.push(next);
            }
        }
    }

    /** Opens some extra walls (south and east only, so each wall is tried once). */
    private void openExtraDoors(final Room[][] theGrid) {
        for (int r = 0; r < myRows; r++) {
            for (int c = 0; c < myCols; c++) {
                if (r + 1 < myRows && myRng.nextDouble() < EXTRA_DOOR_CHANCE) {
                    connect(theGrid[r][c], Direction.SOUTH, theGrid[r + 1][c]);
                }
                if (c + 1 < myCols && myRng.nextDouble() < EXTRA_DOOR_CHANCE) {
                    connect(theGrid[r][c], Direction.EAST, theGrid[r][c + 1]);
                }
            }
        }
    }

    private static void connect(final Room theFrom, final Direction theDirection,
                                final Room theTo) {
        theFrom.addDoor(theDirection);
        theTo.addDoor(theDirection.opposite());
    }

    private boolean inBounds(final int theRow, final int theCol) {
        return theRow >= 0 && theRow < myRows && theCol >= 0 && theCol < myCols;
    }
}
