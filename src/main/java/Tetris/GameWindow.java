package main.java.Tetris;

import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.GroupLayout;

import java.awt.Toolkit;
import java.awt.Dimension;


public class GameWindow extends JFrame {
    GameWindow()
    {
        nextBlockPanel = new BlockShowcase(this, "Next blocks", 3, true);
        holdBlockPanel = new BlockShowcase(this, "Hold", 1, false);
        statsPanel = new GameStats();
        game = new Board(nextBlockPanel, holdBlockPanel, statsPanel);
        addKeyListener(game);
        
        GroupLayout layout = new GroupLayout(getContentPane());
        setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(holdBlockPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                    .addComponent(statsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                )
                .addComponent(game, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addComponent(nextBlockPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
        );
        
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(holdBlockPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                    .addComponent(statsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                )
                .addComponent(game, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addComponent(nextBlockPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
        );

        // Window parameters
        setMinimumSize(new Dimension(
            game.getMinimumSize().width + holdBlockPanel.getMinimumSize().width + nextBlockPanel.getMinimumSize().width,
            Math.max(game.getMinimumSize().height, Math.max(holdBlockPanel.getMinimumSize().height, nextBlockPanel.getMinimumSize().height))
        ));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Tetris");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height) / 2);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        validate();
    }

    public int getBlockSizePixel()
    {
        if (game != null)
            return game.getPreferredSize().width / game.boardX;

        return 30;
    }

    Board game;
    BlockShowcase nextBlockPanel;
    BlockShowcase holdBlockPanel;
    GameStats statsPanel;

    JPanel layoutLeft;
}
