package src.view;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import javax.swing.JFrame;

public class GameView extends JFrame {

    public GameView() {
        super("Dungeon Adventure");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setJMenuBar(createMenuBar());
    }
    private JMenuBar createMenuBar() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("New Game"));
        fileMenu.add(new JMenuItem("Save Game"));
        fileMenu.add(new JMenuItem("Load Game"));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem("Exit"));

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem("About"));
        helpMenu.add(new JMenuItem("Game Play Instructions"));

        JMenuBar bar = new JMenuBar();
        bar.add(fileMenu);
        bar.add(helpMenu);
        return bar;
    }

    public static void main(String[] args) {
        GameView window = new GameView();
        window.setVisible(true);
    }
}
