package src.view;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import src.model.Direction;
import src.model.Room;

/**
 * Shows the North, South, East and West buttons for the current room.
 * Only directions with a door are enabled (SRS FR-MOV-1).
 *
 * @author Group 7 (Liiban Isse)
 * @version 0.1
 */
public class RoomPanel extends JPanel {

    /** Moves the hero north. */
    private final JButton myNorthButton = new JButton("North");

    /** Moves the hero south. */
    private final JButton mySouthButton = new JButton("South");

    /** Moves the hero east. */
    private final JButton myEastButton = new JButton("East");

    /** Moves the hero west. */
    private final JButton myWestButton = new JButton("West");

    /**
     * Creates the room panel with the direction buttons arranged like a
     * compass. Empty labels fill the corners and the middle.
     */
    public RoomPanel() {
        super(new GridLayout(3, 3));
        setBorder(BorderFactory.createTitledBorder("Current Room"));
        add(new JLabel());
        add(myNorthButton);
        add(new JLabel());
        add(myWestButton);
        add(new JLabel());
        add(myEastButton);
        add(new JLabel());
        add(mySouthButton);
        add(new JLabel());
    }

    /**
     * Enables only the directions that have a door in the given room.
     *
     * @param theRoom the room the hero is in
     */
    public void showRoom(final Room theRoom) {
        myNorthButton.setEnabled(theRoom.hasDoor(Direction.NORTH));
        mySouthButton.setEnabled(theRoom.hasDoor(Direction.SOUTH));
        myEastButton.setEnabled(theRoom.hasDoor(Direction.EAST));
        myWestButton.setEnabled(theRoom.hasDoor(Direction.WEST));
    }
}