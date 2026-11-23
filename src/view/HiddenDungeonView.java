package src.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import src.model.Dungeon;
import src.model.Monster;
import src.model.Room;

/**
 * The hidden testing view of the whole dungeon (SRS 3.11, FR-MNU-2). It shows
 * every room with its doors and contents, marks the hero's room with @ and
 * lists every monster with its location and hit points. It only reads the
 * dungeon, so opening it never changes the game.
 *
 * <p>It is opened with the secret shortcut {@link GameView#HIDDEN_VIEW_SHORTCUT}
 * and does not appear in any menu.</p>
 *
 * @author Group 7 (Alex Douk)
 * @version 0.1
 */
public class HiddenDungeonView extends JDialog {

    /** The symbol that marks the hero's room. */
    public static final char HERO_SYMBOL = '@';

    /** The number of text lines in one room's picture. */
    private static final int ROOM_LINES = 3;

    /** The line of a room's picture that holds its contents symbol. */
    private static final int MIDDLE_LINE = 1;

    /** The column of a room's picture that holds its contents symbol. */
    private static final int MIDDLE_COLUMN = 1;

    /** The size of the map font in points. */
    private static final int FONT_SIZE = 16;

    /** The space around the text in pixels. */
    private static final int PADDING = 10;

    /** The line separator used in the text. */
    private static final String LINE_BREAK = "\n";

    /** The explanation of the symbols, shown under the map. */
    private static final String LEGEND = "@ hero   i entrance   O exit   "
            + "A E I P pillars" + LINE_BREAK
            + "H healing potion   V vision potion   X pit   M multiple items"
            + LINE_BREAK + "* wall   - | door";

    /**
     * Creates the hidden view for the given dungeon.
     *
     * @param theOwner the window the view is centered on, may be null
     * @param theDungeon the dungeon to show, not null
     * @throws IllegalArgumentException if theDungeon is null
     */
    public HiddenDungeonView(final Frame theOwner, final Dungeon theDungeon) {
        super(theOwner, "Hidden Dungeon View (testing)", false);
        if (theDungeon == null) {
            throw new IllegalArgumentException("dungeon must not be null");
        }
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        final JTextArea text = new JTextArea(buildText(theDungeon));
        text.setEditable(false);
        text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE));
        text.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING,
                PADDING, PADDING));
        add(new JScrollPane(text), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(theOwner);
    }

    /**
     * Builds the text shown in the view: the map of every room with the
     * hero's room marked, the list of monsters, and the legend.
     *
     * @param theDungeon the dungeon to describe, not null
     * @return the full text of the view
     * @throws IllegalArgumentException if theDungeon is null
     */
    public static String buildText(final Dungeon theDungeon) {
        if (theDungeon == null) {
            throw new IllegalArgumentException("dungeon must not be null");
        }
        return buildMap(theDungeon) + LINE_BREAK + buildHeroLine(theDungeon)
                + LINE_BREAK + buildMonsterList(theDungeon)
                + LINE_BREAK + LEGEND + LINE_BREAK;
    }

    /**
     * Draws every room side by side, row by row, using each room's 3 x 3
     * picture. The hero's room shows {@link #HERO_SYMBOL} in its center.
     *
     * @param theDungeon the dungeon to draw
     * @return the map text
     */
    private static String buildMap(final Dungeon theDungeon) {
        final StringBuilder map = new StringBuilder();
        for (int row = 0; row < theDungeon.getRows(); row++) {
            final String[][] pictures = new String[theDungeon.getCols()][];
            for (int col = 0; col < theDungeon.getCols(); col++) {
                pictures[col] = theDungeon.getRoom(row, col).toString().split(LINE_BREAK);
                if (row == theDungeon.getHeroRow() && col == theDungeon.getHeroCol()) {
                    pictures[col][MIDDLE_LINE] = markHero(pictures[col][MIDDLE_LINE]);
                }
            }
            for (int line = 0; line < ROOM_LINES; line++) {
                for (final String[] picture : pictures) {
                    map.append(picture[line]);
                }
                map.append(LINE_BREAK);
            }
        }
        return map.toString();
    }

    /**
     * Replaces the contents symbol in the middle line of a room's picture
     * with the hero symbol.
     *
     * @param theLine the middle line of a room's picture
     * @return the line with the hero symbol in the center
     */
    private static String markHero(final String theLine) {
        final StringBuilder line = new StringBuilder(theLine);
        line.setCharAt(MIDDLE_COLUMN, HERO_SYMBOL);
        return line.toString();
    }

    /**
     * Describes where the hero is and what the hero's room holds, because the
     * hero symbol covers that room's own symbol on the map.
     *
     * @param theDungeon the dungeon to describe
     * @return one line about the hero's room
     */
    private static String buildHeroLine(final Dungeon theDungeon) {
        final char symbol = theDungeon.getHeroRoom().getSymbol();
        final String contents = symbol == ' ' ? "empty" : "contains " + symbol;
        return "Hero (" + HERO_SYMBOL + "): row " + theDungeon.getHeroRow() + ", col "
                + theDungeon.getHeroCol() + " (room " + contents + ")" + LINE_BREAK;
    }

    /**
     * Lists every living monster with its room and hit points.
     *
     * @param theDungeon the dungeon to search
     * @return one line per monster, or a line saying there are none
     */
    private static String buildMonsterList(final Dungeon theDungeon) {
        final StringBuilder list = new StringBuilder("Monsters:" + LINE_BREAK);
        int count = 0;
        for (int row = 0; row < theDungeon.getRows(); row++) {
            for (int col = 0; col < theDungeon.getCols(); col++) {
                final Room room = theDungeon.getRoom(row, col);
                final Monster monster = room.getMonster();
                if (monster != null && monster.isAlive()) {
                    list.append("  row ").append(row).append(", col ").append(col)
                            .append(": ").append(monster.getName()).append(" (")
                            .append(monster.getHitPoints()).append(" HP)")
                            .append(LINE_BREAK);
                    count++;
                }
            }
        }
        if (count == 0) {
            list.append("  none").append(LINE_BREAK);
        }
        return list.toString();
    }
}
