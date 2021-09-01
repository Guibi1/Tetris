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
        add(game);

        // Window parameters
        setSize(430, 850);
        setResizable(false);
        setTitle("Tetris");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height) / 2);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    Board game;
}
