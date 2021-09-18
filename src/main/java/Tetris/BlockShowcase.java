package main.java.Tetris;

import java.util.Random;
import java.util.Vector;

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
    BlockShowcase(GameWindow gw, String title, int maxBlockCount, boolean generateBlocks)
    {        
        showcasedBlocks = new Vector<Block>();
        
        // blockCount can't be less than 1
        this.maxBlockCount = Math.max(1, maxBlockCount);
        
        titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        FontManager.setComponentFont(titleLabel);

        drawPanel = new DrawPanel(gw);
        
        // Layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(titleLabel);
        add(drawPanel);
        
        
        // Fills the vector of random blocs
        if (generateBlocks)
            for (int i = 0; i < maxBlockCount; i++)
                addRandomBlock();
    }

    public void addBlock(Blocks.Type blockType, int rotation)
    {
        // Add the block to the vector
        Block newBlock = new Block(blockType, rotation);
        showcasedBlocks.add(newBlock);

        // Repaint the newly added block
        drawPanel.paintBlocks(showcasedBlocks.size() - 1);
    }

    public void addRandomBlock()
    {
        // Don't add a block if there are too many in the stacks
        if (showcasedBlocks.size() >= maxBlockCount)
            return;
        
        // Add a random block
        addBlock(Blocks.Type.values()[random.nextInt(Blocks.Type.values().length - 1)], random.nextInt(4) * 90);
        
        // Repaint the newly added block
        drawPanel.paintBlocks(showcasedBlocks.size() - 1);
    }
    
    public void removeBlockAt(int index)
    {
        showcasedBlocks.remove(index);
        drawPanel.paintBlocks(-1);

        // TODO: Make an animation
    }

    public Blocks.Type getBlockAt(int index)
    {
        if (index >= 0 && index < showcasedBlocks.size())
            return showcasedBlocks.elementAt(index).type;

        return Blocks.Type.None;
    }
    
    public int getRotationAt(int index)
    {
        if (index >= 0 && index < showcasedBlocks.size())
            return showcasedBlocks.elementAt(index).rotation;

        return 0;
    }


    private final double ratioWidth = 5;
    private final double ratioHeight = 5.5;

    private final int maxBlockCount;
    
    private JLabel titleLabel;
    private DrawPanel drawPanel;

    private Random random = new Random(System.currentTimeMillis());
    private Vector<Block> showcasedBlocks;


    private class Block
    {
        Block(Blocks.Type type, int rotation)
        {
            this.type = type;
            this.rotation = rotation;
        }

        public Blocks.Type type;
        public int rotation;
    }


    private class DrawPanel extends JPanel
    {
        DrawPanel(GameWindow gw)
        {
            this.gw = gw;
            setBackground(Color.decode("#121417"));
        }

        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension((int) Math.round(gw.getBlockSizePixel() * ratioWidth), (int) Math.round(gw.getBlockSizePixel() * ratioHeight * maxBlockCount));
        }

        @Override
        public Dimension getMaximumSize()
        {
            return new Dimension((int) Math.round(gw.getBlockSizePixel() * ratioWidth), (int) Math.round(gw.getBlockSizePixel() * ratioHeight * maxBlockCount));
        }

        @Override
        public Dimension getMinimumSize()
        {
            return new Dimension((int) Math.round(gw.getBlockSizePixel() * ratioWidth), (int) Math.round(gw.getBlockSizePixel() * ratioHeight * maxBlockCount));
        }

        public void paintBlocks(int index)
        {
            // Repaint all
            if (index == -1)
            {
                java.awt.EventQueue.invokeLater(new Thread(() -> paintImmediately(0, 0, getSize().width, getSize().height)));
            }

            // Repaint only the block at the specified index
            else
            {
                int width = (int) Math.round(gw.getBlockSizePixel() * ratioWidth);
                int height = (int) Math.round(gw.getBlockSizePixel() * ratioHeight);
                
                java.awt.EventQueue.invokeLater(new Thread(() -> paintImmediately(height * index, 0, width, height * (index + 1))));
            }
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2D = (Graphics2D) g;

            int blockSizePixel = gw.getBlockSizePixel();

            for (int i = 0; i < showcasedBlocks.size(); i++)
            {
                Blocks.Type block = showcasedBlocks.elementAt(i).type;
                int rotation = showcasedBlocks.elementAt(i).rotation;
                // Center the block
                Point offset = new Point(
                    (int) Math.round((ratioWidth - block.getSize(rotation).width) * blockSizePixel / 2),
                    (int) Math.round((ratioHeight - block.getSize(rotation).height) * blockSizePixel / 2 + (blockSizePixel * ratioHeight * i)));

                for (Point p : block.getPoints(rotation))
                {
                    g2D.setColor(block.getJavaColor());
                    g2D.fillRect(
                        p.x * blockSizePixel + offset.x,
                        p.y * blockSizePixel + offset.y,
                        blockSizePixel,
                        blockSizePixel
                    );

                    // Lines
                    g2D.setStroke(new BasicStroke(2));
                    g2D.setColor(Color.decode("#373D43"));
                    g2D.drawLine(
                        p.x * blockSizePixel + offset.x,
                        p.y * blockSizePixel + offset.y,
                        (p.x + 1) * blockSizePixel + offset.x,
                        p.y * blockSizePixel + offset.y
                    );
                    g2D.drawLine(
                        p.x * blockSizePixel + offset.x,
                        p.y * blockSizePixel + offset.y,
                        p.x * blockSizePixel + offset.x,
                        (p.y + 1) * blockSizePixel + offset.y
                    );
                    g2D.drawLine(
                        (p.x + 1) * blockSizePixel + offset.x,
                        p.y * blockSizePixel + offset.y,
                        (p.x + 1) * blockSizePixel + offset.x,
                        (p.y + 1) * blockSizePixel + offset.y
                    );
                    g2D.drawLine(
                        p.x * blockSizePixel + offset.x,
                        (p.y + 1) * blockSizePixel + offset.y,
                        (p.x + 1) * blockSizePixel + offset.x,
                        (p.y + 1) * blockSizePixel + offset.y
                    );
                }
            }
        }

        private final GameWindow gw;
    }
}
