package ca.guibi.tetris;

import javax.swing.JLabel;


public class StyledLabel extends JLabel
{
    StyledLabel(String text)
    {
        setText(text);
        FontManager.setComponentFont(this);
    }

    StyledLabel(String text, Float fontSize)
    {
        setText(text);
        FontManager.setComponentFont(this, fontSize);
    }
}
