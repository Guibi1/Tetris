package ca.guibi.tetris;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;

import javax.swing.JFrame;


public class GameWindow extends JFrame {
    GameWindow()
    {
        // Window parameters
        setSize(350, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Tetris");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height) / 2);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Layout
        setLayout(new BorderLayout());
        nextBlockPanel = new InformationPanel(30);
        add(nextBlockPanel, BorderLayout.LINE_START);

        game = new Board(nextBlockPanel);
        addKeyListener(game);
        add(game, BorderLayout.CENTER);

        validate();
        setVisible(true);
    }


    Board game;
    InformationPanel nextBlockPanel;
}
