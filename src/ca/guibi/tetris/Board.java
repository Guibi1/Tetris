package ca.guibi.tetris;

import java.awt.Dimension;
import javax.swing.JPanel;
import java.util.Arrays;

import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.Rectangle2D;

public class Board extends JPanel {
    Board()
    {
        super();
        setPreferredSize(new Dimension(400, 800));
        setBackground(Color.decode("#121417"));
        setBorder(new LineBorder(Color.decode("#373D43"), 2));

        for (Blocks.Color[] a : gameBoard)
            Arrays.fill(a, Blocks.Color.None);
    }

    void NewGame()
    {
        for (Blocks.Color[] a : gameBoard)
            Arrays.fill(a, Blocks.Color.None);

        paused = false;
        score = 0;

        Run();
    }

    void Run()
    {
        while (!paused)
        {
            // Checks for completed line
            boolean completedLine = false;
            for (int i = gameBoard.length - 1; i >= 0; i--)
                if (!Arrays.stream(gameBoard[i]).anyMatch(Blocks.Color.None::equals))
                {
                    Blocks.Color[] last = new Blocks.Color[10];
                    Arrays.fill(last, Blocks.Color.None);
                    
                    for (int j = 0; j <= i; j++)
                    {
                        Blocks.Color[] temp = last;
                        last = gameBoard[j];
                        gameBoard[j] = temp;
                    }
    
                    completedLine = true;
                    break;
                }

            if (!completedLine)
            {
                
            }
    
            try {
                paintImmediately(getVisibleRect());
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;

        int width = getSize().width;
        int height = getSize().height;
        int strokeSize = 2;

        g2D.setStroke(new BasicStroke(strokeSize));
        g2D.setColor(Color.decode("#373D43"));
        for (int i = 1; i <= gameBoard[0].length; i++)
        {
            g2D.drawLine(i * width / gameBoard[0].length, 0, i * width / gameBoard[0].length, height);
        }
        
        for (int i = 1; i <= gameBoard.length; i++)
        {
            g2D.drawLine(0, i * height / gameBoard.length, width, i * height / gameBoard.length);
        }

        for (int i = 0; i < gameBoard.length; i++)
            for (int j = 0; j < gameBoard[i].length; j++)
            {
                if (gameBoard[i][j] != Blocks.Color.None)
                {
                    g2D.setColor(gameBoard[i][j].getColor());
                    g2D.fill(new Rectangle2D.Double(
                        (j * width / gameBoard[i].length) + (strokeSize / 2),
                        (i * height / gameBoard.length) + (strokeSize / 2),
                        width / gameBoard[i].length - strokeSize,
                        height / gameBoard.length - strokeSize
                    ));
                }
            }
    }

    private Blocks.Color[][] gameBoard = new Blocks.Color[20][10];
    private boolean paused = false;
    private int score = 0;
}
