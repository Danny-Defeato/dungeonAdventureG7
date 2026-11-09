package src.view;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import src.model.Hero;
import javax.swing.JProgressBar;

/**
 * Shows the hero's name, hit points, potions and pillars (SRS FR-MOV-4).
 *
 * @author Group 7 (Liiban Isse)
 * @version 0.1
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
        myPillarLabel.setText("Pillars: none yet");   // TODO: use getPillars() once john adds it to Hero class

    }

}