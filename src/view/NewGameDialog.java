package src.view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * The new-game dialog (SRS 3.1, FR-NEW-1 to FR-NEW-3). It introduces the game,
 * asks for the hero's name and lets the player pick Warrior, Priestess or
 * Thief, each shown with its statistics and special skill. The dialog is
 * modal: {@link #showDialog()} waits until the player clicks Start or closes
 * the dialog.
 *
 * <p>The dialog only collects the player's choices. The caller creates the
 * hero from them with the HeroFactory.</p>
 *
 * @author Group 7 (Alex Douk)
 * @version 0.1
 */
public class NewGameDialog extends JDialog {

    /** The longest hero name allowed, after trimming (FR-NEW-2). */
    public static final int MAX_NAME_LENGTH = 20;

    /** The hero class selected when the dialog opens. */
    private static final String DEFAULT_TYPE = "Warrior";

    /** The width of the name field in columns. */
    private static final int NAME_COLUMNS = 20;

    /** The space around the dialog's content in pixels. */
    private static final int PADDING = 10;

    /** The start of each card's text; it wraps the text at a fixed width. */
    private static final String CARD_START = "<html><body style='width:150px'>";

    /** The space between parts of the dialog in pixels. */
    private static final int GAP = 8;

    /** The introduction shown at the top of the dialog (FR-NEW-1). */
    private static final String INTRO_TEXT = "<html><body style='width:480px'>"
            + "<b>Welcome to Dungeon Adventure!</b><br>"
            + "Explore a randomly built dungeon, find the four Pillars of OO "
            + "(A, E, I, P) and reach the exit alive.<br>"
            + "Move through doors with the direction buttons, pick up potions, "
            + "avoid pits and fight the monsters that guard the pillars.</html>";

    /**
     * The hero classes in display order, each with the text shown on its card.
     * The statistics come from SRS FR-HERO-1 to FR-HERO-4.
     */
    private static final Map<String, String> HERO_CARDS = createHeroCards();

    /** The field where the player types the hero's name. */
    private final JTextField myNameField = new JTextField(NAME_COLUMNS);

    /** The radio buttons for the hero classes, keyed by class name. */
    private final Map<String, JRadioButton> myTypeButtons = new LinkedHashMap<>();

    /** Whether the player clicked Start with a valid name. */
    private boolean myConfirmed;

    /**
     * Creates the new-game dialog. It is not shown until
     * {@link #showDialog()} is called.
     *
     * @param theOwner the window the dialog is centered on, or null to center
     *                 it on the screen
     */
    public NewGameDialog(final Frame theOwner) {
        super(theOwner, "New Game", true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        final JPanel content = new JPanel(new BorderLayout(GAP, GAP));
        content.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING,
                PADDING, PADDING));
        content.add(new JLabel(INTRO_TEXT), BorderLayout.NORTH);
        content.add(createHeroCardPanel(), BorderLayout.CENTER);
        content.add(createBottomPanel(), BorderLayout.SOUTH);
        setContentPane(content);

        pack();
        setResizable(false);
        setLocationRelativeTo(theOwner);
    }

    /**
     * Checks a hero name the way FR-NEW-2 requires: after removing leading and
     * trailing spaces it must be 1 to {@link #MAX_NAME_LENGTH} characters.
     *
     * @param theName the name the player typed, may be null
     * @return a message describing the problem, or null if the name is valid
     */
    public static String checkName(final String theName) {
        final String trimmed = theName == null ? "" : theName.trim();
        String problem = null;
        if (trimmed.isEmpty()) {
            problem = "Please enter a name for your hero.";
        } else if (trimmed.length() > MAX_NAME_LENGTH) {
            problem = "The name must be at most " + MAX_NAME_LENGTH + " characters.";
        }
        return problem;
    }

    /**
     * Shows the dialog and waits until the player clicks Start or closes it.
     *
     * @return true if the player clicked Start with a valid name, false if
     *         the dialog was closed
     */
    public boolean showDialog() {
        myConfirmed = false;
        setVisible(true);
        return myConfirmed;
    }

    /**
     * Returns the name the player entered, without leading or trailing spaces.
     *
     * @return the hero's name
     */
    public String getHeroName() {
        return myNameField.getText().trim();
    }

    /**
     * Returns the hero class the player selected, such as "Warrior".
     *
     * @return the selected hero class name
     */
    public String getHeroType() {
        String selected = DEFAULT_TYPE;
        for (final Map.Entry<String, JRadioButton> entry : myTypeButtons.entrySet()) {
            if (entry.getValue().isSelected()) {
                selected = entry.getKey();
            }
        }
        return selected;
    }

    /**
     * Builds one card per hero class, each with a radio button and the
     * class's statistics (FR-NEW-3).
     *
     * @return the panel holding the cards
     */
    private JPanel createHeroCardPanel() {
        final JPanel cards = new JPanel(new GridLayout(1, HERO_CARDS.size(), GAP, GAP));
        final ButtonGroup group = new ButtonGroup();
        for (final Map.Entry<String, String> entry : HERO_CARDS.entrySet()) {
            final JRadioButton button = new JRadioButton(entry.getKey());
            button.setSelected(DEFAULT_TYPE.equals(entry.getKey()));
            group.add(button);
            myTypeButtons.put(entry.getKey(), button);

            final JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createEtchedBorder());
            card.add(button, BorderLayout.NORTH);
            final JLabel stats = new JLabel(entry.getValue());
            stats.setBorder(BorderFactory.createEmptyBorder(0, PADDING, PADDING, PADDING));
            card.add(stats, BorderLayout.CENTER);
            cards.add(card);
        }
        return cards;
    }

    /**
     * Builds the name field and the Start button. Pressing Enter in the name
     * field also starts the game.
     *
     * @return the panel holding the name field and the button
     */
    private JPanel createBottomPanel() {
        final JPanel bottom = new JPanel(new BorderLayout(GAP, GAP));
        final JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("Hero name (1 to " + MAX_NAME_LENGTH + " characters):"));
        namePanel.add(myNameField);
        bottom.add(namePanel, BorderLayout.CENTER);

        final JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> tryStart());
        myNameField.addActionListener(e -> tryStart());
        getRootPane().setDefaultButton(startButton);
        bottom.add(startButton, BorderLayout.EAST);
        return bottom;
    }

    /**
     * Closes the dialog if the name is valid. Otherwise shows the problem and
     * keeps the dialog open (SRS 3.1).
     */
    private void tryStart() {
        final String problem = checkName(myNameField.getText());
        if (problem == null) {
            myConfirmed = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, problem, "Invalid name",
                    JOptionPane.WARNING_MESSAGE);
            myNameField.requestFocusInWindow();
        }
    }

    /**
     * Returns the hero classes the dialog offers, in display order. Tests use
     * this to check that every card matches a type the HeroFactory supports.
     *
     * @return the hero class names on the cards
     */
    static List<String> getCardTypes() {
        return List.copyOf(HERO_CARDS.keySet());
    }

    /**
     * Builds the card text for each hero class from SRS FR-HERO-1 to FR-HERO-4.
     *
     * @return the hero classes in display order, mapped to their card text
     */
    private static Map<String, String> createHeroCards() {
        final Map<String, String> cards = new LinkedHashMap<>();
        cards.put("Warrior", CARD_START + "HP 125<br>Speed 4<br>Hit chance 80%<br>"
                + "Damage 35-60<br>Block chance 20%<br><br>"
                + "<b>Crushing Blow:</b> 40% chance to deal 75-175 damage</html>");
        cards.put("Priestess", CARD_START + "HP 75<br>Speed 5<br>Hit chance 70%<br>"
                + "Damage 25-45<br>Block chance 30%<br><br>"
                + "<b>Heal:</b> restores 25-50 hit points</html>");
        cards.put("Thief", CARD_START + "HP 75<br>Speed 6<br>Hit chance 80%<br>"
                + "Damage 20-40<br>Block chance 40%<br><br>"
                + "<b>Surprise Attack:</b> 40% chance of an extra attack, "
                + "20% chance of being caught</html>");
        return cards;
    }
}
