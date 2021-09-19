package ca.guibi.tetris;

import javax.swing.JPanel;
import javax.swing.GroupLayout;


public class Game extends JPanel {
    Game(Window window)
    {
        nextBlockPanel = new BlockShowcase(this, "Next blocks", 3, true);
        holdBlockPanel = new BlockShowcase(this, "Hold", 1, false);
        statsPanel = new GameStats();
        game = new Board(window, nextBlockPanel, holdBlockPanel, statsPanel);
        addKeyListener(game);
        
        GroupLayout layout = new GroupLayout(this);
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
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(holdBlockPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                    .addComponent(statsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                )
                .addComponent(game, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addComponent(nextBlockPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
        );
    }

    public void newGame()
    {
        game.newGame();
    }

    public int getBlockSizePixel()
    {
        if (game != null)
            return game.getPreferredSize().width / game.boardX;

        return 30;
    }


    private Board game;
    private BlockShowcase nextBlockPanel;
    private BlockShowcase holdBlockPanel;
    private GameStats statsPanel;
}
