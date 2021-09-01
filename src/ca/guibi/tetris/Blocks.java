package ca.guibi.tetris;

public final class Blocks
{
    public enum Type {
        I,
        O,
        T,
        S,
        Z,
        J,
        L
    }
    
    public enum Color {
        Cyan(java.awt.Color.cyan),
        Yellow(java.awt.Color.yellow),
        Purple(java.awt.Color.magenta),
        Green(java.awt.Color.green),
        Red(java.awt.Color.red),
        Blue(java.awt.Color.blue),
        Orange(java.awt.Color.orange),
        None(java.awt.Color.black);

        private final java.awt.Color color;

        Color(final java.awt.Color color)
        {
            this.color = color;
        }

        public java.awt.Color getColor()
        {
            return color;
        }
    }
}
