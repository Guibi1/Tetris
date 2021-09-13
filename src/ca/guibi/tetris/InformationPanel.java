package ca.guibi.tetris;

import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.BasicStroke;


public class InformationPanel extends JPanel {
    InformationPanel(int blockSize)
    {
        this.blockSize = blockSize;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        nextBlockFrame = new NextBlockFrame();
        add(nextBlockFrame);
        
        scoreLabel = new JLabel(Integer.toString(score));
        scoreLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        add(scoreLabel);

        setNextBlock(Blocks.Type.I);
    }

    public void setScore(int score)
    {
        this.score = score;
        scoreLabel.setText(Integer.toString(this.score));
    }
    
    public void addScore(int score)
    {
        this.score += score;
        scoreLabel.setText(Integer.toString(this.score));
    }

    public int getScore()
    {
        return score;
    }

    public void setNextBlock(Blocks.Type nextBlock)
    {
        this.nextBlock = nextBlock;
        offset.x = (int) Math.round((boardX - nextBlock.getSize(nextBlockRotation).width) * blockSize / 2);
        offset.y = (int) Math.round((boardY - nextBlock.getSize(nextBlockRotation).height) * blockSize / 2);

        java.awt.EventQueue.invokeLater(new Thread( () -> nextBlockFrame.paintImmediately(0, 0, getSize().width, getSize().height) ));
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

    private double boardX = 5.5;
    private double boardY = 5.5;

    private int score = 0;
    private int blockSize = 0;
    
    private JLabel scoreLabel;
    private NextBlockFrame nextBlockFrame;

    private Random random = new Random(System.currentTimeMillis());
    private Blocks.Type nextBlock = Blocks.Type.None;
    private int nextBlockRotation = 0;
    private Point offset = new Point(1, 1);


    private class NextBlockFrame extends JPanel
    {
        NextBlockFrame()
        {
            setPreferredSize(new Dimension((int) Math.round(blockSize * boardX), (int) Math.round(blockSize * boardY)));
            setBackground(Color.decode("#121417"));
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2D = (Graphics2D) g;

            for (Point p : nextBlock.getPoints(nextBlockRotation))
            {
                g2D.setColor(nextBlock.getJavaColor());
                g2D.fillRect(
                    p.x * blockSize + offset.x,
                    p.y * blockSize + offset.y,
                    blockSize,
                    blockSize
                );

                // Lines
                g2D.setStroke(new BasicStroke(2));
                g2D.setColor(Color.decode("#373D43"));
                g2D.drawLine(
                    p.x * blockSize + offset.x,
                    p.y * blockSize + offset.y,
                    (p.x + 1) * blockSize + offset.x,
                    p.y * blockSize + offset.y
                );
                g2D.drawLine(
                    p.x * blockSize + offset.x,
                    p.y * blockSize + offset.y,
                    p.x * blockSize + offset.x,
                    (p.y + 1) * blockSize + offset.y
                );
                g2D.drawLine(
                    p.x * blockSize + offset.x + blockSize,
                    p.y * blockSize + offset.y,
                    (p.x + 1) * blockSize + offset.x,
                    (p.y + 1) * blockSize + offset.y
                );
                g2D.drawLine(
                    p.x * blockSize + offset.x,
                    p.y * blockSize + offset.y + blockSize,
                    (p.x + 1) * blockSize + offset.x,
                    (p.y + 1) * blockSize + offset.y
                );
            }
        }
    }
}
