package ca.guibi.tetris;


public class Main {

    public static void main(String[] args) {
        // TODO: Add menus

        if (!FontManager.loadFont("resources/Merubot-regular.ttf", 32.0f))
            return;

        GameWindow gw = new GameWindow();
        gw.setVisible(true);
        gw.game.NewGame();
    }
}