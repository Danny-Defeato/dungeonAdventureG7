package src.controller;

import java.util.Random;
import javax.swing.SwingUtilities;
import src.model.Dungeon;
import src.model.DungeonBuilder;
import src.model.Hero;
import src.model.HeroFactory;
import src.model.MonsterFactory;
import src.model.SQLiteDatabase;
import src.view.GameView;
import src.view.NewGameDialog;

/**
 * Starts Dungeon Adventure: asks for the hero's name and class in the
 * new-game dialog, builds the model, the window and the controller and
 * connects them.
 *
 * <p><b>Hidden dungeon view (for testing and grading):</b> press
 * <b>Ctrl+Shift+D</b> anywhere in the game window to open a window that shows
 * the entire dungeon, including every room's items, every monster and the
 * hero's position. It does not change the game and is not listed in any menu
 * (SRS FR-MNU-2). See {@link GameView#HIDDEN_VIEW_SHORTCUT}.</p>
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
            // New game: the player names the hero and picks a class (SRS 3.1).
            final NewGameDialog newGame = new NewGameDialog(null);
            if (!newGame.showDialog()) {
                return;     // the player closed the dialog, so the program ends
            }
            final Hero hero = new HeroFactory().create(newGame.getHeroType(),
                    newGame.getHeroName());

            // Model: database -> monster factory -> dungeon builder -> dungeon.
            final SQLiteDatabase db = new SQLiteDatabase(SQLiteDatabase.DEFAULT_URL);
            final MonsterFactory factory = new MonsterFactory(db);
            final Dungeon dungeon = new DungeonBuilder(factory, new Random()).build();

            // View and controller.
            final GameView view = new GameView();
            new GameController(view, hero, dungeon);
            view.setVisible(true);
        });
    }
}