package ca.guibi.tetris;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.FlowLayout;

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
        setLayout(new FlowLayout());
        nextBlockPanel = new InformationPanel(30);
        add(nextBlockPanel);

        game = new Board(nextBlockPanel);
        addKeyListener(game);
        add(game);
        
        // TODO: Show the next block and the score

        validate();
        setVisible(true);
    }


    Board game;
    InformationPanel nextBlockPanel;
}
