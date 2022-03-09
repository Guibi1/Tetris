package ca.guibi.tetris;

import javax.swing.JLabel;


public class StyledLabel extends JLabel
{
    StyledLabel()
    {
        super();
        FontManager.setComponentFont(this);
    }

    StyledLabel(String text)
    {
        super(text);
        FontManager.setComponentFont(this);
    }

    StyledLabel(String text, Float fontSize)
    {
        super(text);
        FontManager.setComponentFont(this, fontSize);
    }
}
