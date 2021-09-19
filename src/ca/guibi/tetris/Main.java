package ca.guibi.tetris;


public class Main {
    public static void main(String[] args)
    {
        if (!FontManager.loadFont("resources/Merubot-regular.ttf", 32.0f))
            return;

        Window w = new Window();
        w.setVisible(true);
    }
}