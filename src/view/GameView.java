package src.view;

import javax.swing.JFrame;

public class GameView extends JFrame {

    public GameView() {
        super("Dungeon Adventure");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        GameView window = new GameView();
        window.setVisible(true);
    }
}
