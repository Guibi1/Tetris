package ca.guibi.tetris;

import javax.swing.GroupLayout;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Game extends StyledPanel
{
    Game(Window window)
    {
        nextBlockShowcase = new BlockShowcase(this, "Next blocks", 3, true);
        holdBlockShowcase = new BlockShowcase(this, "Hold", 1, false);
        gameStats = new GameStats();
        board = new Board(window, nextBlockShowcase, holdBlockShowcase, gameStats);

        StyledButton pauseButton = new StyledButton("Pause");
        pauseButton.setPreferredSize(new Dimension(200, 0));
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                board.togglePause();
            }
        });

        // Layout
        StyledPanel centeredPanel = new StyledPanel();
        GroupLayout layout = new GroupLayout(centeredPanel);
        centeredPanel.setLayout(layout);
        setLayout(new GridBagLayout());
        add(centeredPanel);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(holdBlockShowcase, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(gameStats, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pauseButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(20)
                .addComponent(board, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(25)
                .addComponent(nextBlockShowcase, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
        );
        
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGap(20)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(holdBlockShowcase, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(gameStats, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pauseButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(20)
                .addComponent(board, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(25)
                .addComponent(nextBlockShowcase, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
        );
    }

    public void newGame()
    {
        board.newGame();
    }

    public int getBlockSizePixel()
    {
        if (board != null)
            return board.getPreferredSize().width / board.boardX;

        return 30;
    }


    private Board board;
    private BlockShowcase nextBlockShowcase;
    private BlockShowcase holdBlockShowcase;
    private GameStats gameStats;
}
