package src.model;

import java.util.Random;

/** Marks the dungeon entrance. Never removed and never has an effect. */
public class Entrance extends RoomItem {
    private static final long serialVersionUID = 1L;

    /** Creates an entrance marker. */
    public Entrance() {
        super('i');
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
