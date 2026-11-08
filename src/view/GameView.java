package src.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * The main window of Dungeon Adventure. It holds the menu bar, the dungeon
 * map on the left and the hero, room and battle panels on the right.
 *
 * @author Group 7 (Liiban Isse)
 * @version 0.2
 */
public class GameView extends JFrame {

    /** The width of the window in pixels. */
    private static final int WINDOW_WIDTH = 1100;

    /** The height of the window in pixels. */
    private static final int WINDOW_HEIGHT = 750;

    /** The width of the side panel area in pixels. */
    private static final int SIDE_PANEL_WIDTH = 400;

    /** The text shown in Help > About. */
    private static final String ABOUT_TEXT = "Dungeon Adventure v0.1\n"
            + "Group 7: Alex Douk, John Nguyen, Nathan Chiu, Liiban Isse\n"
            + "TCSS 360, Autumn 2026";

    /** The text shown in Help > Game Play Instructions. */
    private static final String INSTRUCTIONS_TEXT =
            "Find the four Pillars of OO (A, E, I, P) and reach the exit.\n"
                    + "Move with the direction buttons. Avoid pits, collect potions, "
                    + "and defeat monsters.";

    /**
     * Creates the main window with its menu bar and its two areas: the map
     * in the center and the side panels on the right.
     */
    public GameView() {
        super("Dungeon Adventure");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setJMenuBar(createMenuBar());
        setLayout(new BorderLayout());

        // Map area: the center grows when the window gets bigger.
        final JPanel mapArea = new JPanel();
        mapArea.setBorder(BorderFactory.createTitledBorder("Dungeon Map"));
        add(mapArea, BorderLayout.CENTER);

        // Side area: a fixed-width column on the right for the other panels.
        final JPanel sideArea = new JPanel();
        sideArea.setBorder(BorderFactory.createTitledBorder("Hero / Room / Battle"));
        sideArea.setPreferredSize(new Dimension(SIDE_PANEL_WIDTH, 0));
        add(sideArea, BorderLayout.EAST);
    }

    /**
     * Builds the File and Help menus. About, Game Play Instructions and Exit
     * work now; New Game, Save Game and Load Game will be connected once the
     * game model is ready.
     *
     * @return the finished menu bar
     */
    private JMenuBar createMenuBar() {
        final JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("New Game"));
        fileMenu.add(new JMenuItem("Save Game"));
        fileMenu.add(new JMenuItem("Load Game"));
        fileMenu.addSeparator();

        // Exit closes the program.
        final JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        final JMenu helpMenu = new JMenu("Help");

        // About and Instructions each open a popup with their text.
        final JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this, ABOUT_TEXT));
        helpMenu.add(aboutItem);

        final JMenuItem instructionsItem = new JMenuItem("Game Play Instructions");
        instructionsItem.addActionListener(
                e -> JOptionPane.showMessageDialog(this, INSTRUCTIONS_TEXT));
        helpMenu.add(instructionsItem);

        final JMenuBar bar = new JMenuBar();
        bar.add(fileMenu);
        bar.add(helpMenu);
        return bar;
    }


    public static void main(final String[] args) {
        final GameView window = new GameView();
        window.setVisible(true);
    }
}