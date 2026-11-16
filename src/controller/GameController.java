package src.controller;

import java.util.Objects;
import src.model.Direction;
import src.model.Dungeon;
import src.model.Hero;
import src.model.Room;
import src.view.GameView;

/**
 * Connects the game model to the window (the Controller in MVC). It holds
 * the hero and the dungeon, changes them when the player acts, and tells the
 * view to show the new state.
 *
 * @author Group 7 (Liiban Isse)
 * @version 0.1
 */
public class GameController {

    /** The game window. */
    private final GameView myView;

    /** The player's hero. */
    private final Hero myHero;

    /** The dungeon the hero is exploring. */
    private final Dungeon myDungeon;

    /**
     * Creates the controller and shows the starting state in the window.
     *
     * @param theView the game window, not null
     * @param theHero the player's hero, not null
     * @param theDungeon the dungeon to explore, not null
     */
    public GameController(final GameView theView, final Hero theHero,
                          final Dungeon theDungeon) {
        myView = Objects.requireNonNull(theView, "view");
        myHero = Objects.requireNonNull(theHero, "hero");
        myDungeon = Objects.requireNonNull(theDungeon, "dungeon");
        myView.getRoomPanel().setMoveAction(this::move);
        refreshView();
        myView.getRoomPanel().showMessage("You are at the entrance. Find the four pillars!");
    }

    /**
     * Moves the hero one room in the given direction, if there is a door,
     * and updates the window.
     *
     * @param theDirection the direction to move
     */
    public void move(final Direction theDirection) {
        final Room newRoom = myDungeon.moveHero(theDirection);
        if (newRoom != null) {
            myView.getRoomPanel().showMessage("You moved "
                    + theDirection.name().toLowerCase() + ".");
            refreshView();
        }
    }

    /**
     * Shows the current hero and room in the window.
     */
    private void refreshView() {
        myView.getHeroPanel().showHero(myHero);
        myView.getRoomPanel().showRoom(myDungeon.getHeroRoom());
    }
}