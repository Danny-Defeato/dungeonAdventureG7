package src.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Builds a random {@link Dungeon} (Builder pattern).
 *
 * <p>The maze is carved with a randomized depth-first search, which connects
 * every room and leaves dead ends. A few extra walls are then opened so
 * there are some loops. The entrance, exit and four pillars go in six
 * different rooms. The entrance room holds nothing else. Every pillar room
 * and the exit room get a guardian monster; other rooms get an ordinary
 * monster with probability {@link #MONSTER_CHANCE}. All randomness comes from
 * the injected {@link Random}, so a fixed seed gives the same dungeon.
 *
 * <p>Not placed yet: healing potions, vision potions and pits (SRS 3.4),
 * because those item classes do not exist yet.
 *
 * @author Group 7 (Alex Douk)
 * @version 0.1
 */
public class DungeonBuilder {

    /** Default number of rows and columns. */
    public static final int DEFAULT_SIZE = 5;

    /** Chance that an ordinary room holds a monster. */
    public static final double MONSTER_CHANCE = 0.15;

    /** Chance that a wall between two rooms is opened after carving. */
    public static final double EXTRA_DOOR_CHANCE = 0.08;

    /** Rooms needed: entrance, exit and four pillars. */
    private static final int MIN_ROOMS = 2 + Pillar.TOTAL;

    /** How many mazes to try before giving up. */
    private static final int MAX_ATTEMPTS = 100;

    private final MonsterFactory monsterFactory;
    private final Random rng;
    private int rows = DEFAULT_SIZE;
    private int cols = DEFAULT_SIZE;

    /**
     * Creates a builder.
     *
     * @param monsterFactory the factory used to make monsters, not null
     * @param rng the shared source of randomness, not null
     */
    public DungeonBuilder(final MonsterFactory monsterFactory, final Random rng) {
        this.monsterFactory = Objects.requireNonNull(monsterFactory, "monsterFactory");
        this.rng = Objects.requireNonNull(rng, "rng");
    }

    /**
     * Sets the size of the dungeon.
     *
     * @param rows the number of rows, at least 1
     * @param cols the number of columns, at least 1; rows times columns must be
     *        at least 6 so the entrance, exit and pillars each get a room
     * @return this builder
     * @throws IllegalArgumentException if the size is too small
     */
    public DungeonBuilder setSize(final int rows, final int cols) {
        if (rows < 1 || cols < 1 || rows * cols < MIN_ROOMS) {
            throw new IllegalArgumentException(
                    "dungeon needs at least " + MIN_ROOMS + " rooms");
        }
        this.rows = rows;
        this.cols = cols;
        return this;
    }

    /**
     * Builds a new dungeon. If a generated maze cannot be completed, a new one
     * is generated.
     *
     * @return the dungeon, with the hero at the entrance
     * @throws IllegalStateException if no usable dungeon could be built
     */
    public Dungeon build() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            final Dungeon dungeon = tryBuild();
            if (dungeon.isTraversable()) {
                return dungeon;
            }
        }
        throw new IllegalStateException("could not build a traversable dungeon");
    }

    private Dungeon tryBuild() {
        final Room[][] grid = new Room[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Room(r, c);
            }
        }
        carve(grid);
        openExtraDoors(grid);

        final List<Room> all = new ArrayList<>();
        for (final Room[] row : grid) {
            Collections.addAll(all, row);
        }
        Collections.shuffle(all, rng);
        final Room entrance = all.get(0);
        final Room exit = all.get(1);
        entrance.addItem(new Entrance());
        exit.addItem(new Exit());
        exit.setMonster(monsterFactory.createRandom(rng, true));
        for (int i = 0; i < Pillar.TOTAL; i++) {
            final Room room = all.get(2 + i);
            room.addItem(new Pillar(Pillar.NAMES.get(i)));
            room.setMonster(monsterFactory.createRandom(rng, true));
        }
        for (int i = 2 + Pillar.TOTAL; i < all.size(); i++) {
            if (rng.nextDouble() < MONSTER_CHANCE) {
                all.get(i).setMonster(monsterFactory.createRandom(rng, false));
            }
        }
        return new Dungeon(grid, entrance, exit);
    }

    /** Randomized depth-first search: opens a door between neighbors. */
    private void carve(final Room[][] grid) {
        final boolean[][] seen = new boolean[rows][cols];
        final Deque<Room> stack = new ArrayDeque<>();
        final Room start = grid[rng.nextInt(rows)][rng.nextInt(cols)];
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
                final Direction d = options.get(rng.nextInt(options.size()));
                final Room next = grid[room.getRow() + d.getRowDelta()]
                        [room.getCol() + d.getColDelta()];
                connect(room, d, next);
                seen[next.getRow()][next.getCol()] = true;
                stack.push(next);
            }
        }
    }

    /** Opens some extra walls (south and east only, so each wall is tried once). */
    private void openExtraDoors(final Room[][] grid) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r + 1 < rows && rng.nextDouble() < EXTRA_DOOR_CHANCE) {
                    connect(grid[r][c], Direction.SOUTH, grid[r + 1][c]);
                }
                if (c + 1 < cols && rng.nextDouble() < EXTRA_DOOR_CHANCE) {
                    connect(grid[r][c], Direction.EAST, grid[r][c + 1]);
                }
            }
        }
    }

    private static void connect(final Room from, final Direction d, final Room to) {
        from.addDoor(d);
        to.addDoor(d.opposite());
    }

    private boolean inBounds(final int r, final int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }
}
