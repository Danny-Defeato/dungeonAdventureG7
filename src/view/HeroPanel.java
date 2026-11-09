package src.view;

import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import src.model.Hero;
import src.model.Pillar;

/**
 * Shows the hero's name, hit points, potions and pillars (SRS FR-MOV-4).
 *
 * @author Group 7 (Liiban Isse)
 * @version 0.3
 */
public class HeroPanel extends JPanel {

    /** Shows the hero's name and class. */
    private final JLabel myNameLabel = new JLabel();

    /** Shows the hero's hit points as a bar. */
    private final JProgressBar myHitPointBar = new JProgressBar();

    /** Shows the hero's potion counts. */
    private final JLabel myPotionLabel = new JLabel();

    /** Shows the pillars the hero has found. */
    private final JLabel myPillarLabel = new JLabel();

    /**
     * Creates the hero panel with one row per piece of information.
     */
    public HeroPanel() {
        super(new GridLayout(4, 1));
        setBorder(BorderFactory.createTitledBorder("Hero"));
        add(myNameLabel);
        myHitPointBar.setStringPainted(true);
        add(myHitPointBar);
        add(myPotionLabel);
        add(myPillarLabel);
    }

    /**
     * Updates the labels to show the given hero.
     *
     * @param theHero the hero to show
     */
    public void showHero(final Hero theHero) {
        myNameLabel.setText(theHero.getName() + " the "
                + theHero.getClass().getSimpleName());
        myHitPointBar.setMaximum(theHero.getMaxHitPoints());
        myHitPointBar.setValue(theHero.getHitPoints());
        myHitPointBar.setString("HP " + theHero.getHitPoints()
                + " / " + theHero.getMaxHitPoints());
        myPotionLabel.setText("Healing potions: " + theHero.getHealingPotions()
                + "   Vision potions: " + theHero.getVisionPotions());
        myPillarLabel.setText("Pillars: " + pillarText(theHero.getPillars()));
    }

    /**
     * Builds the text for the pillars the hero has found.
     *
     * @param thePillars the pillars found
     * @return the pillar names separated by commas, or "none yet"
     */
    private String pillarText(final List<Pillar> thePillars) {
        if (thePillars.isEmpty()) {
            return "none yet";
        }
        final StringBuilder names = new StringBuilder();
        for (final Pillar pillar : thePillars) {
            if (names.length() > 0) {
                names.append(", ");
            }
            names.append(pillar.getName());
        }
        return names.toString();
    }
}