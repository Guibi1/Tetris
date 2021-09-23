package ca.guibi.tetris;

import java.util.HashSet;

import javax.swing.JFrame;

import java.awt.CardLayout;
import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;


public class Window extends JFrame {
    Window()
    {
        menu = new Menu(this);
        settings = new Settings(this);
        game = new Game(this);

        // Layout
        layout = new CardLayout();
        layout.setVgap(0);
        setLayout(layout);

        add(menu, "menu");
        add(settings, "settings");
        add(game, "game");
        
        // Window parameters
        setTitle("Tetris");

        setResizable(false);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        validate();

        // Disable "tab" key to change the focus
        for (int id : new int[] {KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS})
            setFocusTraversalKeys(id, new HashSet<AWTKeyStroke>());
    }

    public void showMenu()
    {
        layout.show(getContentPane(), "menu");
        menu.requestFocusInWindow();
    }

    public void showGame()
    {
        layout.show(getContentPane(), "game");
        game.requestFocusInWindow();
    }

    public void showSettings()
    {
        layout.show(getContentPane(), "settings");
        settings.requestFocusInWindow();
    }

    public void newGame()
    {
        game.newGame();
    }

    public Settings getSettings()
    {
        return settings;
    }


    private Menu menu;
    private Settings settings;
    private Game game;

    private CardLayout layout;
}
