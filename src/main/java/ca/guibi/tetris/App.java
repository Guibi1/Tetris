package ca.guibi.tetris;


public class App
{
    public static void main(String[] args)
    {
        if (!FontManager.loadFont("Merubot-Regular.ttf", 32.0f))
            return;

        Window w = new Window();
        w.setVisible(true);
    }
}
