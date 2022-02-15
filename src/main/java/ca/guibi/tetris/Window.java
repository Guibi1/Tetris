package ca.guibi.tetris;

import java.util.HashSet;

import javax.swing.JFrame;

import java.awt.CardLayout;
import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;


public class Window extends JFrame
{
    Window()
    {
        menu = new Menu(this);
        settings = new Settings(this);
        keySelector = new KeySelector(this);
        game = new Game(this);

        // Layout
        layout = new CardLayout();
        layout.setVgap(0);
        setLayout(layout);

        add(menu, "menu");
        add(settings, "settings");
        add(keySelector, "keySelector");
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
    }

    public void showGame()
    {
        layout.show(getContentPane(), "game");
    }

    public void showSettings()
    {
        layout.show(getContentPane(), "settings");
    }

    public void showKeySelector()
    {
        layout.show(getContentPane(), "keySelector");
        keySelector.requestFocusInWindow();
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
    private KeySelector keySelector;
    private Game game;

    private CardLayout layout;
}
