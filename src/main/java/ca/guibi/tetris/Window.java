package ca.guibi.tetris;

import javax.swing.JFrame;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.CardLayout;


public class Window extends JFrame {
    Window()
    {
        menu = new Menu(this);
        game = new Game(this);

        // Layout
        layout = new CardLayout();
        setLayout(layout);

        add(menu, "menu");
        add(game, "game");
        
        // Window parameters
        setMinimumSize(new Dimension(600, 800));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Tetris");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height) / 2);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        validate();
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
    }

    public void newGame()
    {
        game.newGame();
    }


    private Menu menu;
    private Game game;

    private CardLayout layout;
}
