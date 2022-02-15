package ca.guibi.tetris;

import javax.swing.JButton;
import javax.swing.BorderFactory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class StyledButton extends JButton implements MouseListener
{
    StyledButton(String text)
    {
        setText(text);

        setBackground(backgroundColor);
        setFocusPainted(false);
        setBorder(BorderFactory.createRaisedBevelBorder());
        setOpaque(true);
     
        FontManager.setComponentFont(this);
        addMouseListener(this);
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(250, 60);
    }

    @Override
    public void mouseEntered(MouseEvent e)
    { 
        if (e.getSource() == this)
            this.setBackground(hoverColor); 
    }

    @Override
    public void mouseExited(MouseEvent e)
    { 
        if (e.getSource() == this)
            this.setBackground(backgroundColor); 
    }
    
    @Override
    public void mouseClicked(MouseEvent e) { return; }

    @Override
    public void mouseReleased(MouseEvent e) { return; }

    @Override
    public void mousePressed(MouseEvent e) { return; }


    private final Color hoverColor = Color.decode("#4F5CA8");
    private final Color backgroundColor = Color.decode("#243A81");
}
