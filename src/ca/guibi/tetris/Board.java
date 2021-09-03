package ca.guibi.tetris;

import java.awt.Dimension;
import javax.swing.JPanel;
import java.util.Arrays;
import java.util.Random;
import java.awt.Point;
import java.lang.Math;

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
        generateBlock = true;
        nextBlock = Blocks.Type.values()[random.nextInt(Blocks.Type.values().length)];

        Run();
    }

    void Run()
    {
        while (!paused)
        {
            // Checks for completed line
            boolean completedLine = false;
            for (int i = boardY - 1; i >= 0; i--)
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
                // Adds a new block

                // TODO: rotate the block
                if (generateBlock)
                {
                    currentBlock = nextBlock;
                    currentBlockOffset.setLocation(boardX / 2 - 1, 0);

                    generateBlock = false;
                    nextBlock = Blocks.Type.values()[random.nextInt(Blocks.Type.values().length)];
                }

                // Drags the block down if it can
                else
                {
                    currentBlockOffset.translate(0, 1);
                    currentBlock.rotateBlock(90);

                    System.out.println("\ntruepoints:");
                    for (Point p : currentBlock.getPoints())
                        System.out.println(p);
                }
            }
    
            try {
                paintImmediately(getVisibleRect());
                Thread.sleep(400);
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
        for (int i = 1; i <= boardX; i++)
        {
            g2D.drawLine(i * width / boardX, 0, i * width / boardX, height);
        }
        
        for (int i = 1; i <= boardY; i++)
        {
            g2D.drawLine(0, i * height / boardY, width, i * height / boardY);
        }

        for (int i = 0; i < boardY; i++)
            for (int j = 0; j < boardX; j++)
            {
                boolean isCurrentBlock = false;
                
                for (Point p : currentBlock.getPoints())
                    if (p.getX() + currentBlockOffset.getX() == j && p.getY() + currentBlockOffset.getY() == i)
                        isCurrentBlock = true;
                
                if (gameBoard[i][j] != Blocks.Color.None || isCurrentBlock)
                {
                    g2D.setColor((isCurrentBlock) ? currentBlock.getJavaColor() : gameBoard[i][j].getJavaColor());
                    g2D.fill(new Rectangle2D.Double(
                        (j * width / boardX) + (strokeSize / 2),
                        (i * height / boardY) + (strokeSize / 2),
                        width / boardX - strokeSize,
                        height / boardY - strokeSize
                    ));
                }
            }
    }

    int boardX = 10;
    int boardY = 20;
    private Blocks.Color[][] gameBoard = new Blocks.Color[boardY][boardX];
    private boolean paused = false;
    private int score = 0;
    private Random random = new Random();
    private boolean generateBlock = true;
    private Blocks.Type nextBlock = Blocks.Type.I;
    private Blocks.Type currentBlock = Blocks.Type.I;
    private Point currentBlockOffset = new Point();
}
