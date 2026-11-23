package src.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import src.model.Direction;
import src.model.Room;

/**
 * Shows the current room: a message and the North, South, East and West
 * buttons. Only directions with a door are enabled (SRS FR-MOV-1).
 *
 * @author Group 7 (Liiban Isse)
 * @version 0.2
 */
public class RoomPanel extends JPanel {

    /** Shows what just happened in the room. */
    private final JLabel myMessageLabel = new JLabel(" ");

    /** Moves the hero north. */
    private final JButton myNorthButton = new JButton("North");

    /** Moves the hero south. */
    private final JButton mySouthButton = new JButton("South");

    /** Moves the hero east. */
    private final JButton myEastButton = new JButton("East");

    /** Moves the hero west. */
    private final JButton myWestButton = new JButton("West");

    /** Uses a healing potion. */
    private final JButton myHealingButton = new JButton("Use Healing Potion");

    /** Uses a vision potion. */
    private final JButton myVisionButton = new JButton("Use Vision Potion");

    /**
     * Creates the room panel: a message on top and the direction buttons
     * arranged like a compass below it.
     */
    public RoomPanel() {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Current Room"));

        // A 3 x 3 grid: buttons on the edges, empty labels as spacers.
        final JPanel grid = new JPanel(new GridLayout(3, 3));
        grid.add(new JLabel());
        grid.add(myNorthButton);
        grid.add(new JLabel());
        grid.add(myWestButton);
        grid.add(new JLabel());
        grid.add(myEastButton);
        grid.add(new JLabel());
        grid.add(mySouthButton);
        grid.add(new JLabel());

        // FlowLayout keeps the grid at its natural size instead of stretching.
        final JPanel holder = new JPanel(new FlowLayout());
        holder.add(grid);

        // Potion buttons side by side, below the direction buttons.
        final JPanel potions = new JPanel(new FlowLayout());
        potions.add(myHealingButton);
        potions.add(myVisionButton);

        add(myMessageLabel, BorderLayout.NORTH);
        add(holder, BorderLayout.CENTER);
        add(potions, BorderLayout.SOUTH);
    }

    /**
     * Sets what happens when a direction button is clicked.
     *
     * @param theAction receives the direction that was clicked
     */
    public void setMoveAction(final Consumer<Direction> theAction) {
        myNorthButton.addActionListener(e -> theAction.accept(Direction.NORTH));
        mySouthButton.addActionListener(e -> theAction.accept(Direction.SOUTH));
        myEastButton.addActionListener(e -> theAction.accept(Direction.EAST));
        myWestButton.addActionListener(e -> theAction.accept(Direction.WEST));
    }
    /**
     * Sets what happens when the potion buttons are clicked.
     *
     * @param theHealingAction runs when Use Healing Potion is clicked
     * @param theVisionAction runs when Use Vision Potion is clicked
     * @throws IllegalArgumentException if either action is null
     */
    public void setPotionActions(final Runnable theHealingAction,
                                 final Runnable theVisionAction) {
        if (theHealingAction == null || theVisionAction == null) {
            throw new IllegalArgumentException("potion actions must not be null");
        }
        myHealingButton.addActionListener(e -> theHealingAction.run());
        myVisionButton.addActionListener(e -> theVisionAction.run());
    }

    /**
     * Enables each potion button only if the hero has that potion.
     *
     * @param theHasHealing whether the hero has a healing potion
     * @param theHasVision whether the hero has a vision potion
     */
    public void setPotionsEnabled(final boolean theHasHealing,
                                  final boolean theHasVision) {
        myHealingButton.setEnabled(theHasHealing);
        myVisionButton.setEnabled(theHasVision);
    }

    /**
     * Shows the given room: enables only the directions that have a door.
     *
     * @param theRoom the room the hero is in
     */
    public void showRoom(final Room theRoom) {
        myNorthButton.setEnabled(theRoom.hasDoor(Direction.NORTH));
        mySouthButton.setEnabled(theRoom.hasDoor(Direction.SOUTH));
        myEastButton.setEnabled(theRoom.hasDoor(Direction.EAST));
        myWestButton.setEnabled(theRoom.hasDoor(Direction.WEST));
    }

    /**
     * Shows a message about what just happened.
     *
     * @param theMessage the text to show
     */
    public void showMessage(final String theMessage) {
        myMessageLabel.setText(theMessage);
    }
}