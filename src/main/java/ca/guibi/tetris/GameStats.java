package ca.guibi.tetris;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.nio.file.Files;

import javax.swing.JLabel;
import javax.swing.BoxLayout;


public class GameStats extends StyledPanel
{
    GameStats()
    {
        // Layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        scoreLabel = new JLabel();
        scoreLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        FontManager.setComponentFont(scoreLabel);
        setScore(0);
        add(scoreLabel);
        
        bestScoreLabel = new JLabel();
        bestScoreLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        FontManager.setComponentFont(bestScoreLabel);
        setBestScore(0);
        add(bestScoreLabel);

        loadBestScore();
    }

    public void loadBestScore()
    {
        try
        {
            ByteBuffer bb = ByteBuffer.wrap(Files.readAllBytes(Paths.get(scoreFilePath)));
            setBestScore(bb.getInt());

        } catch (IOException e) {
            System.out.println("Can't read best score.");
        }
    }

    public void saveBestScore()
    {
        if (score > bestScore)
        {
            setBestScore(score);
            
            try
            {
                ByteBuffer bb = ByteBuffer.allocate(4);
                bb.putInt(score);
                
                Files.write(Paths.get(scoreFilePath), bb.array());
                
            } catch (IOException e) {
                System.out.println("Can't save best score.");
                e.printStackTrace();
            }
        }
    }

    public void setBestScore(int newBestScore)
    {
        bestScore = newBestScore;
        bestScoreLabel.setText("Best score: " + Integer.toString(bestScore));
    }

    public void setScore(int newScore)
    {
        score = newScore;
        scoreLabel.setText("Score: " + Integer.toString(score));
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


    private final String scoreFilePath = "bestScore.dat";

    private int linesClearedCount = 0;

    private int score = 0;
    private JLabel scoreLabel;

    private int bestScore = 0;
    private JLabel bestScoreLabel;
}
