package src.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import src.model.Dungeon;
import src.model.Room;

/**
 * Shows the dungeon as a grid of rooms. Visited rooms show their symbol,
 * unvisited rooms are gray, and the hero's room shows @ (SRS FR-MOV-3).
 *
 * @author Group 7 (Liiban Isse)
 * @version 0.1
 */
public class MapPanel extends JPanel {

    /** Background of the room the hero is in. */
    private static final Color HERO_COLOR = new Color(255, 225, 140);

    /** Background of a room the hero has visited. */
    private static final Color VISITED_COLOR = Color.WHITE;

    /** Background of a room the hero has not seen yet. */
    private static final Color HIDDEN_COLOR = Color.DARK_GRAY;

    /** Font for the room symbols. */
    private static final Font ROOM_FONT = new Font(Font.MONOSPACED, Font.BOLD, 28);

    /** Symbol shown in the hero's room. */
    private static final String HERO_SYMBOL = "@";

    /** Space between rooms in pixels. */
    private static final int GAP = 4;

    /**
     * Creates an empty map panel.
     */
    public MapPanel() {
        super();
        setBorder(BorderFactory.createTitledBorder("Dungeon Map"));
    }

    /**
     * Redraws the map for the given dungeon.
     *
     * @param theDungeon the dungeon to show
     * @throws IllegalArgumentException if theDungeon is null
     */
    public void showDungeon(final Dungeon theDungeon) {
        if (theDungeon == null) {
            throw new IllegalArgumentException("dungeon must not be null");
        }
        removeAll();
        setLayout(new GridLayout(theDungeon.getRows(), theDungeon.getCols(), GAP, GAP));
        for (int row = 0; row < theDungeon.getRows(); row++) {
            for (int col = 0; col < theDungeon.getCols(); col++) {
                final boolean hasHero = row == theDungeon.getHeroRow()
                        && col == theDungeon.getHeroCol();
                add(createRoomLabel(theDungeon.getRoom(row, col), hasHero));
            }
        }
        revalidate();
        repaint();
    }

    /**
     * Builds one square of the map.
     *
     * @param theRoom the room to draw
     * @param theHasHero whether the hero is in this room
     * @return the square for this room
     */
    private JLabel createRoomLabel(final Room theRoom, final boolean theHasHero) {
        final JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setFont(ROOM_FONT);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        if (theHasHero) {
            label.setText(HERO_SYMBOL);
            label.setBackground(HERO_COLOR);
        } else if (theRoom.isVisited() || theRoom.isRevealed()) {
            label.setText(String.valueOf(theRoom.getSymbol()));
            label.setBackground(VISITED_COLOR);
        } else {
            label.setBackground(HIDDEN_COLOR);
        }
        return label;
    }
}