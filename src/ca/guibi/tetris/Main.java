package ca.guibi.tetris;

public class Main {

    public static void main(String[] args) {
        GameWindow gw = new GameWindow();
        gw.setVisible(true);
        gw.game.NewGame();
    }
}
