package src.model;

import java.util.Random;

/** Marks the dungeon exit. The win check is made by the game model. */
public class Exit extends RoomItem {
    private static final long serialVersionUID = 1L;

    /** Creates an exit marker. */
    public Exit() {
        super('O');
    }

    @Override
    public String applyTo(final Hero theHero, final Random theRng) {
        return "";
    }

    @Override
    public boolean isCollectible() {
        return false;
    }
}
