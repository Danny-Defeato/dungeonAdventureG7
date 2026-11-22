package src.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Random;

/**
 * The rules of one game in progress: the hero, the dungeon and what happens
 * when the hero walks into a room. It never touches the GUI. Instead it tells
 * listeners (the view) when something changes, using
 * {@link PropertyChangeSupport} (Observer).
 *
 * @author Group 7 (John Nguyen)
 * @version 0.1
 */
public class GameModel {

    /** The property fired when the hero moves into a room. The new value is the room. */
    public static final String ROOM_PROPERTY = "room";

    /** The hero the player controls. */
    private final Hero myHero;

    /** The dungeon the hero is exploring. */
    private final Dungeon myDungeon;

    /** The shared source of randomness. */
    private final Random myRng;

    /** Tells the listeners when the game changes. */
    private final PropertyChangeSupport mySupport;

    /** The monster in the room the hero is in, or null. */
    private Monster myCurrentMonster;

    /** True while the hero is in a battle. */
    private boolean myInBattle;

    /** True once the hero has fainted. */
    private boolean myGameOver;

    /**
     * Creates a game with the hero standing in the dungeon's entrance.
     *
     * @param theHero the player's hero, not null
     * @param theDungeon the dungeon to explore, not null
     * @param theRng the shared source of randomness, not null
     * @throws IllegalArgumentException if any argument is null
     */
    public GameModel(final Hero theHero, final Dungeon theDungeon, final Random theRng) {
        if (theHero == null || theDungeon == null || theRng == null) {
            throw new IllegalArgumentException("hero, dungeon and rng must not be null");
        }
        myHero = theHero;
        myDungeon = theDungeon;
        myRng = theRng;
        mySupport = new PropertyChangeSupport(this);
        myCurrentMonster = null;
        myInBattle = false;
        myGameOver = false;
    }

    /**
     * Returns the hero.
     *
     * @return the player's hero
     */
    public Hero getHero() {
        return myHero;
    }

    /**
     * Returns the dungeon.
     *
     * @return the dungeon being explored
     */
    public Dungeon getDungeon() {
        return myDungeon;
    }

    /**
     * Tells whether the hero is fighting a monster.
     *
     * @return true if a battle is in progress
     */
    public boolean isInBattle() {
        return myInBattle;
    }

    /**
     * Tells whether the hero has fainted.
     *
     * @return true if the game is over
     */
    public boolean isGameOver() {
        return myGameOver;
    }

    /**
     * Adds a listener that is told when the game changes.
     *
     * @param theListener the listener, not null
     * @throws IllegalArgumentException if the listener is null
     */
    public void addPropertyChangeListener(final PropertyChangeListener theListener) {
        if (theListener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        mySupport.addPropertyChangeListener(theListener);
    }

    /**
     * Moves the hero through a door and tells the listeners about the new
     * room. Nothing happens if the room has no door that way.
     *
     * @param theDirection the way to move, not null
     * @throws IllegalArgumentException if the direction is null
     */
    public void move(final Direction theDirection) {
        if (theDirection == null) {
            throw new IllegalArgumentException("direction must not be null");
        }
        final Room room = myDungeon.moveHero(theDirection);
        if (room != null) {
            // TODO: handle the room the hero entered: apply its items (potions,
            //       pillars, pits), start a battle if it has a monster, and
            //       end the game if the hero faints.
            // TODO: do not let the hero move during a battle or after fainting.
            mySupport.firePropertyChange(ROOM_PROPERTY, null, room);
        }
    }

    // TODO (from the UML): heroAttack(): String, createMemento(): GameMemento
    //      and restore(theMemento).
}