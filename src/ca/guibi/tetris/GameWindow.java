package ca.guibi.tetris;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;


public class GameWindow extends JFrame {
    GameWindow()
    {
        // Main layout
        setLayout(new BorderLayout());

        // Left
        layoutLeft = new JPanel();
        layoutLeft.setLayout(new BoxLayout(layoutLeft, BoxLayout.Y_AXIS));
        add(layoutLeft, BorderLayout.LINE_START);

        holdBlockPanel = new BlockShowcase("Hold", 30, 1, false);
        layoutLeft.add(holdBlockPanel);
        
        scorePanel = new GameStats();
        layoutLeft.add(scorePanel);

        // Right
        nextBlockPanel = new BlockShowcase("Next blocks", 30, 3, true);
        add(nextBlockPanel, BorderLayout.LINE_END);

        // Centre
        game = new Board(nextBlockPanel, holdBlockPanel, scorePanel);
        addKeyListener(game);
        add(game, BorderLayout.CENTER);

        validate();

        // Window parameters
        setMinimumSize(new Dimension(
            game.getMinimumSize().width + holdBlockPanel.getMinimumSize().width + nextBlockPanel.getMinimumSize().width + 100,
            Math.max(game.getMinimumSize().height, Math.max(holdBlockPanel.getMinimumSize().height, nextBlockPanel.getMinimumSize().height))
        ));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Tetris");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height) / 2);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    Board game;
    BlockShowcase nextBlockPanel;
    BlockShowcase holdBlockPanel;
    GameStats scorePanel;

    JPanel layoutLeft;
}
