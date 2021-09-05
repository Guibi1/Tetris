package ca.guibi.tetris;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.FlowLayout;
import javax.swing.JFrame;

public class GameWindow extends JFrame {
    GameWindow()
    {
        setLayout(new FlowLayout());
        game = new Board();
        addKeyListener(game);
        add(game);

        // Window parameters
        setSize(350, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Tetris");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height) / 2);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    Board game;
}
