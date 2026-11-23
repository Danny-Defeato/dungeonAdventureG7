package src.controller;

import java.util.Objects;
import src.model.Direction;
import src.model.Dungeon;
import src.model.Hero;
import src.model.Room;
import src.view.GameView;
import java.util.Random;

/**
 * Connects the game model to the window (the Controller in MVC). It holds
 * the hero and the dungeon, changes them when the player acts, and tells the
 * view to show the new state.
 *
 * @author Group 7 (Liiban Isse)
 * @version 0.1
 */
public class GameController {
    /** How many rooms around the hero a vision potion reveals. */
    private static final int VISION_RANGE = 1;

    /** Source of randomness for healing amounts. */
    private final Random myRandom = new Random();

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
        myView.getRoomPanel().setPotionActions(this::useHealingPotion,
                this::useVisionPotion);
        myView.setHiddenViewAction(() -> myView.showHiddenDungeon(myDungeon));
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
     * Uses one healing potion, if the hero has one, and updates the window.
     */
    public void useHealingPotion() {
        final int before = myHero.getHitPoints();
        if (myHero.useHealingPotion(myRandom)) {
            final int healed = myHero.getHitPoints() - before;
            myView.getRoomPanel().showMessage("You drank a healing potion and recovered "
                    + healed + " HP.");
            refreshView();
        }
    }

    /**
     * Uses one vision potion, if the hero has one, reveals the nearby rooms
     * and updates the window.
     */
    public void useVisionPotion() {
        if (myHero.useVisionPotion()) {
            revealNearbyRooms();
            myView.getRoomPanel().showMessage("The vision potion reveals the rooms around you.");
            refreshView();
        }
    }

    /**
     * Reveals the hero's room and the rooms around it (up to 8), staying
     * inside the dungeon. TODO: move into GameModel once it exists.
     */
    private void revealNearbyRooms() {
        final int heroRow = myDungeon.getHeroRow();
        final int heroCol = myDungeon.getHeroCol();
        for (int row = heroRow - VISION_RANGE; row <= heroRow + VISION_RANGE; row++) {
            for (int col = heroCol - VISION_RANGE; col <= heroCol + VISION_RANGE; col++) {
                if (row >= 0 && row < myDungeon.getRows()
                        && col >= 0 && col < myDungeon.getCols()) {
                    myDungeon.getRoom(row, col).setRevealed(true);
                }
            }
        }
    }

    /**
     * Shows the current hero, room and map in the window.
     */
    private void refreshView() {
        myView.getHeroPanel().showHero(myHero);
        myView.getRoomPanel().showRoom(myDungeon.getHeroRoom());
        myView.getMapPanel().showDungeon(myDungeon);
        myView.getRoomPanel().setPotionsEnabled(myHero.getHealingPotions() > 0,
                myHero.getVisionPotions() > 0);
    }
}