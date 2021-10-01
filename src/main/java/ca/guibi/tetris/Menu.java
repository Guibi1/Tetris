package ca.guibi.tetris;

import javax.swing.JLabel;
import javax.swing.GroupLayout;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Menu extends StyledPanel {
    Menu(Window window)
    {
        titleLabel = new JLabel("Tetris");
        FontManager.setComponentFont(titleLabel, 160f);

        startButton = new StyledButton("New game");
        FontManager.setComponentFont(startButton);
        startButton.setPreferredSize(new Dimension(200, 0));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                window.showGame();
                window.newGame();
            }
        });
        
        settingsButton = new StyledButton("Settings");
        FontManager.setComponentFont(settingsButton);
        settingsButton.setPreferredSize(new Dimension(200, 0));
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                window.showSettings();
            }
        });

        quitButton = new StyledButton("Quit");
        FontManager.setComponentFont(quitButton);
        quitButton.setPreferredSize(new Dimension(200, 0));
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });

        // Layout
        StyledPanel centeredPanel = new StyledPanel();
        GroupLayout layout = new GroupLayout(centeredPanel);
        centeredPanel.setLayout(layout);
        setLayout(new GridBagLayout());
        add(centeredPanel);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(titleLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(40)
                .addComponent(startButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(settingsButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(quitButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        );
        
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(titleLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(40)
                .addComponent(startButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(settingsButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(quitButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        );
    }


    private JLabel titleLabel;
    private StyledButton startButton;
    private StyledButton settingsButton;
    private StyledButton quitButton;
}
