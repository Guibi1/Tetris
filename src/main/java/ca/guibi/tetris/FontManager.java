package ca.guibi.tetris;

import java.io.IOException;
import java.util.Collections;

import javax.swing.JComponent;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;


public abstract class FontManager {
    public static boolean loadFont(String fontFile, Float defaultSize)
    {
        try
        {
            font = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemClassLoader().getResourceAsStream(fontFile));
            font = font.deriveFont(
                Collections.singletonMap(
                    TextAttribute.WEIGHT, TextAttribute.WEIGHT_HEAVY
                )
            ).deriveFont(defaultSize);

            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        } catch (IOException|FontFormatException e) {
            System.out.println("Error while loading the font.");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void setComponentFont(JComponent component)
    {
        component.setFont(font);
    }

    public static void setComponentFont(JComponent component, Float fontSize)
    {
        component.setFont(font.deriveFont(fontSize));
    }


    private static Font font;
}
