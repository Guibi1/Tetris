package ca.guibi.tetris;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.GroupLayout;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Game extends JPanel {
    Game(Window window)
    {
        nextBlockShowcase = new BlockShowcase(this, "Next blocks", 3, true);
        holdBlockShowcase = new BlockShowcase(this, "Hold", 1, false);
        gameStats = new GameStats();
        board = new Board(window, nextBlockShowcase, holdBlockShowcase, gameStats);
        addKeyListener(board);

        
        pauseButton = new JButton("Pause");
        FontManager.setComponentFont(pauseButton);
        pauseButton.setPreferredSize(new Dimension(200, 0));
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                board.togglePause();
            }
        });

        // Layout
        JPanel centeredPanel = new JPanel();
        GroupLayout layout = new GroupLayout(centeredPanel);
        centeredPanel.setLayout(layout);
        setLayout(new GridBagLayout());
        add(centeredPanel);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(holdBlockShowcase, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(gameStats, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pauseButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                )
                .addComponent(board, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(nextBlockShowcase, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        );
        
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(holdBlockShowcase, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(gameStats, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pauseButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                )
                .addComponent(board, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(nextBlockShowcase, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
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


    private JButton pauseButton;

    private Board board;
    private BlockShowcase nextBlockShowcase;
    private BlockShowcase holdBlockShowcase;
    private GameStats gameStats;
}
