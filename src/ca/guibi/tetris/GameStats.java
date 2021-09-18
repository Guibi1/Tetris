package ca.guibi.tetris;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;


public class GameStats extends JPanel {
    GameStats()
    {
        scoreLabel = new JLabel(Integer.toString(score));
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        FontManager.setComponentFont(scoreLabel);
        add(scoreLabel);
    }

    public void setScore(int newScore)
    {
        score = newScore;
        scoreLabel.setText(Integer.toString(score));
    }

    public int getScore()
    {
        return score;
    }

    public void addScore(int points)
    {
        setScore(score + points);
    }

    public int getLevel()
    {
        return 1 + linesClearedCount % 10;
    }

    public void setLinesCleared(int linesCompleted)
    {
        if (linesCompleted >= 0)
            linesClearedCount = linesCompleted;
    }

    public int getLinesCleared()
    {
        return linesClearedCount;
    }

    public void addLinesCleared(int linesCompleted)
    {
        setLinesCleared(getLinesCleared() + linesCompleted);
    }


    private int linesClearedCount = 0;
    private int score = 0;
    
    private JLabel scoreLabel;
}
