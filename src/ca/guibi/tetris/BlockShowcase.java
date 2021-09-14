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


public class BlockShowcase extends JPanel {
    BlockShowcase(String title, int blockSize)
    {
        this.blockSize = blockSize;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        add(titleLabel);

        showcaseFrame = new ShowcaseFrame();
        add(showcaseFrame);
        
        validate();
        setShowcasedBlock(Blocks.Type.None);
    }

    public void setShowcasedBlock(Blocks.Type showcasedBlock)
    {
        this.showcasedBlock = showcasedBlock;
        showcasedBlockOffset.x = (int) Math.round((frameX - showcasedBlock.getSize(showcasedBlockRotation).width) * blockSize / 2);
        showcasedBlockOffset.y = (int) Math.round((frameY - showcasedBlock.getSize(showcasedBlockRotation).height) * blockSize / 2);

        java.awt.EventQueue.invokeLater(new Thread( () -> showcaseFrame.paintImmediately(0, 0, getSize().width, getSize().height) ));
    }

    public Blocks.Type showcaseRandomBlock()
    {
        setShowcasedBlockRotation(random.nextInt(4) * 90);
        setShowcasedBlock(Blocks.Type.values()[random.nextInt(Blocks.Type.values().length - 1)]);
        return showcasedBlock;
    }

    public Blocks.Type getShowcasedBlock()
    {
        return showcasedBlock;
    }

    public void setShowcasedBlockRotation(int angle)
    {
        showcasedBlockRotation = angle;
    }

    public int getShowcasedBlockRotation()
    {
        return showcasedBlockRotation;
    }

    private double frameX = 5;
    private double frameY = 5.5;

    private int blockSize = 0;
    
    private JLabel titleLabel;
    private ShowcaseFrame showcaseFrame;

    private Random random = new Random(System.currentTimeMillis());
    private Blocks.Type showcasedBlock = Blocks.Type.None;
    private int showcasedBlockRotation = 0;
    private Point showcasedBlockOffset = new Point(1, 1);


    private class ShowcaseFrame extends JPanel
    {
        ShowcaseFrame()
        {
            setPreferredSize(new Dimension((int) Math.round(blockSize * frameX), (int) Math.round(blockSize * frameY)));
            setBackground(Color.decode("#121417"));
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2D = (Graphics2D) g;

            for (Point p : showcasedBlock.getPoints(showcasedBlockRotation))
            {
                g2D.setColor(showcasedBlock.getJavaColor());
                g2D.fillRect(
                    p.x * blockSize + showcasedBlockOffset.x,
                    p.y * blockSize + showcasedBlockOffset.y,
                    blockSize,
                    blockSize
                );

                // Lines
                g2D.setStroke(new BasicStroke(2));
                g2D.setColor(Color.decode("#373D43"));
                g2D.drawLine(
                    p.x * blockSize + showcasedBlockOffset.x,
                    p.y * blockSize + showcasedBlockOffset.y,
                    (p.x + 1) * blockSize + showcasedBlockOffset.x,
                    p.y * blockSize + showcasedBlockOffset.y
                );
                g2D.drawLine(
                    p.x * blockSize + showcasedBlockOffset.x,
                    p.y * blockSize + showcasedBlockOffset.y,
                    p.x * blockSize + showcasedBlockOffset.x,
                    (p.y + 1) * blockSize + showcasedBlockOffset.y
                );
                g2D.drawLine(
                    p.x * blockSize + showcasedBlockOffset.x + blockSize,
                    p.y * blockSize + showcasedBlockOffset.y,
                    (p.x + 1) * blockSize + showcasedBlockOffset.x,
                    (p.y + 1) * blockSize + showcasedBlockOffset.y
                );
                g2D.drawLine(
                    p.x * blockSize + showcasedBlockOffset.x,
                    p.y * blockSize + showcasedBlockOffset.y + blockSize,
                    (p.x + 1) * blockSize + showcasedBlockOffset.x,
                    (p.y + 1) * blockSize + showcasedBlockOffset.y
                );
            }
        }
    }
}
