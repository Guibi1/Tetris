package ca.guibi.tetris;

import javax.swing.JPanel;

import java.awt.Color;


public class StyledPanel extends JPanel
{
    StyledPanel()
    {
        setBackground(backgroundColor);
    }

    private final Color backgroundColor = Color.decode("#0B0902");
}
