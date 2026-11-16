package src.controller;

import java.util.Random;
import javax.swing.SwingUtilities;
import src.model.Dungeon;
import src.model.DungeonBuilder;
import src.model.Hero;
import src.model.MonsterFactory;
import src.model.SQLiteDatabase;
import src.model.Warrior;
import src.view.GameView;

/**
 * Starts Dungeon Adventure: builds the model, the window and the controller
 * and connects them.
 *
 * @author Group 7 (Liiban Isse)
 * @version 0.1
 */
public final class DungeonAdventure {

    /** Prevents creating DungeonAdventure objects; only main is used. */
    private DungeonAdventure() {
    }

    /**
     * Starts the game.
     *
     * @param args not used
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Model: database -> monster factory -> dungeon builder -> dungeon.
            final SQLiteDatabase db = new SQLiteDatabase(SQLiteDatabase.DEFAULT_URL);
            final MonsterFactory factory = new MonsterFactory(db);
            final Dungeon dungeon = new DungeonBuilder(factory, new Random()).build();
            final Hero hero = new Warrior("Gristle");   // TODO: use the New Game screen

            // View and controller.
            final GameView view = new GameView();
            new GameController(view, hero, dungeon);
            view.setVisible(true);
        });
    }
}