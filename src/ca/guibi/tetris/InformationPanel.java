package ca.guibi.tetris;

import java.util.Random;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;


public class InformationPanel extends JPanel {
    InformationPanel(int blockSize)
    {
        this.blockSize = blockSize;
        
        setPreferredSize(new Dimension(blockSize * boardX, blockSize * boardY));
        setBackground(Color.decode("#121417"));

        setNextBlock(Blocks.Type.I);
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public void addScore(int score)
    {
        this.score += score;
    }

    public int getScore()
    {
        return score;
    }

    public void setNextBlock(Blocks.Type nextBlock)
    {
        this.nextBlock = nextBlock;
        offset.x = (boardX - nextBlock.getSize(nextBlockRotation).width) * blockSize / 2;
        offset.y = (boardY - nextBlock.getSize(nextBlockRotation).height) * blockSize / 2;

        java.awt.EventQueue.invokeLater(new Thread( () -> paintImmediately(0, 0, getSize().width, getSize().height) ));
    }

    public Blocks.Type generateNextBlock()
    {
        setNextBlockRotation(random.nextInt(4) * 90);
        setNextBlock(Blocks.Type.values()[random.nextInt(Blocks.Type.values().length - 1)]);
        return nextBlock;
    }

    public Blocks.Type getNextBlock()
    {
        return nextBlock;
    }

    public void setNextBlockRotation(int angle)
    {
        nextBlockRotation = angle;
    }

    public int getNextBlockRotation()
    {
        return nextBlockRotation;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;

        // TODO: Add a grid

        for (Point p : nextBlock.getPoints(nextBlockRotation))
        {
            g2D.setColor(nextBlock.getJavaColor());
            g2D.fillRect(
                p.x * blockSize + offset.x,
                p.y * blockSize + offset.y,
                blockSize,
                blockSize
            );
        }
    }


    private int boardX = 6;
    private int boardY = 6;

    private int score = 0;

    private int blockSize = 0;

    private Random random = new Random(System.currentTimeMillis());
    private Blocks.Type nextBlock = Blocks.Type.None;
    private int nextBlockRotation = 0;
    private Point offset = new Point(1, 1);
}
