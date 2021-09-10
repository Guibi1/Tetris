package ca.guibi.tetris;

import java.util.Arrays;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.AlphaComposite;
import java.awt.geom.Rectangle2D;


public class Board extends JPanel implements KeyListener {
    Board(InformationPanel nextBlockPanel)
    {
        setPreferredSize(new Dimension(300, 650));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        boardLines = new BoardLines();
        add(boardLines);
        validate();
        setBackground(Color.decode("#121417"));

        this.infoPanel = nextBlockPanel;

        for (Blocks.Color[] a : gameBoard)
            Arrays.fill(a, Blocks.Color.None);
    }

    void NewGame()
    {
        for (Blocks.Color[] a : gameBoard)
            Arrays.fill(a, Blocks.Color.None);

        paused = false;
        infoPanel.setScore(0);
        generateBlock = true;
        currentBlock = Blocks.Type.None;
        infoPanel.generateNextBlock();

        Run();
    }

    void Run()
    {
        RunBlockIndicator();
        
        new Thread(() -> {
            while (!paused)
            {
                // Checks for completed line
                int completedLines = 0;
                for (int i = boardY - 1; i >= 0;)
                {
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
        
                        completedLines += 1;
                    }

                    else i -= 1;
                }

                switch (completedLines) {
                    case 0:
                        break;

                    case 1:
                        infoPanel.addScore(getLevel() * 40);
                        break;

                    case 2:
                        infoPanel.addScore(getLevel() * 100);
                        break;

                    case 3:
                        infoPanel.addScore(getLevel() * 300);
                        break;

                    default:
                        infoPanel.addScore(getLevel() * 1200);
                        break;
                }

                linesCompleted += completedLines;

                if (completedLines != 0)
                    repaintBoard(true);

                else
                {
                    // Adds a new block
                    if (generateBlock)
                    {
                        generateBlock = false;
                        currentBlock = infoPanel.getNextBlock();
                        currentBlockRotation = infoPanel.getNextBlockRotation();
                        currentBlockOffset.setLocation((boardX - currentBlock.getSize(currentBlockRotation).width) / 2, 0 - currentBlock.getSize(currentBlockRotation).height);
                        infoPanel.generateNextBlock();
                        repaintBoard();
                    }

                    // Drags the block down if it can
                    else
                    {
                        boolean canFall = true;
                        for (Point p : currentBlock.getPoints(currentBlockRotation))
                        {
                            if (currentBlockOffset.y + p.y + 1 < 0)
                                continue;
                            
                            if (currentBlockOffset.y + p.y == boardY - 1 || gameBoard[currentBlockOffset.y + p.y + 1][currentBlockOffset.x + p.x] != Blocks.Color.None)
                                canFall = false;
                        }

                        if (canFall)
                        {
                            currentBlockOffset.translate(0, 1);
                            repaintBoard();
                        }
                        
                        else
                        {
                            // Check if game is over
                            for (Point p : currentBlock.getPoints(currentBlockRotation))
                            {
                                if (currentBlockOffset.y + p.y < 0)
                                    continue;

                                if (currentBlockOffset.y + p.y <= 0)
                                    paused = true;
                                
                                gameBoard[currentBlockOffset.y + p.y][currentBlockOffset.x + p.x] = currentBlock.getColor();
                            }

                            currentBlock = Blocks.Type.None;
                            generateBlock = true;
                            repaintBoard(true);
                        }
                    }
                }
                
                try {
                    Thread.sleep((getLevel() < 150) ? Math.round(80 * Math.cos(getLevel() / (15 * Math.PI)) + 120) : 40);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            System.out.println("Game over.");
        }).start();
    }

    private void RunBlockIndicator()
    {
        new Thread(() -> {
            float minAlpha = 0.1f;
            float maxAlpha = 0.4f;
            boolean alphaGoingUp = true;
            int animationTime = 400;

            int stepsTiming = animationTime / 20;
            float steps = (maxAlpha - minAlpha) / stepsTiming;
            alphaBlockIndicator = minAlpha;
            
            while (!paused)
            {
                if (alphaGoingUp)
                {
                    alphaBlockIndicator += steps;

                    if (alphaBlockIndicator >= maxAlpha)
                    {
                        alphaGoingUp = false;
                        alphaBlockIndicator = maxAlpha;
                    }
                }
                
                else
                {
                    alphaBlockIndicator -= steps;

                    if (alphaBlockIndicator <= minAlpha)
                    {
                        alphaGoingUp = true;
                        alphaBlockIndicator = minAlpha;
                    }
                }

                java.awt.EventQueue.invokeLater(new Thread(() -> 
                    paintImmediately(
                        blockIndicatorOffset.x * getSize().width / boardX,
                        blockIndicatorOffset.y * getSize().height / boardY,
                        currentBlock.getSize(currentBlockRotation).width * getSize().width / boardX,
                        currentBlock.getSize(currentBlockRotation).height * getSize().height / boardY
                    )
                ));
                
                try {
                    Thread.sleep(stepsTiming);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public int getLevel()
    {
        return 1 + linesCompleted % 10;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_RIGHT:
                boolean canMove = true;
                for (Point p : currentBlock.getPoints(currentBlockRotation))
                {
                    // Can't move to the right if it makes it move outside of the board
                    if (currentBlockOffset.x + p.x >= boardX - 1)
                        canMove = false;

                    // Ignore the point if it is too high up
                    else if (currentBlockOffset.y + p.y < 0)
                        continue;
                    
                    // Check if the point at the right isn't empty
                    else if (gameBoard[currentBlockOffset.y + p.y][currentBlockOffset.x + p.x + 1] != Blocks.Color.None)
                        canMove = false;
                }
                
                if (canMove)
                {
                    currentBlockOffset.translate(1, 0);
                    repaintBoard();
                }

                break;

            case KeyEvent.VK_LEFT:
                canMove = true;
                for (Point p : currentBlock.getPoints(currentBlockRotation))
                {
                    // Can't move to the left if it makes it move out of the board
                    if (currentBlockOffset.x + p.x <= 0)
                        canMove = false;
                    
                    // Ignore the point if it is too high up
                    else if (currentBlockOffset.y + p.y < 0)
                        continue;

                    // Check if the point at the left isn't empty
                    else if (gameBoard[currentBlockOffset.y + p.y][currentBlockOffset.x + p.x - 1] != Blocks.Color.None)
                        canMove = false;
                }
                
                if (canMove)
                {
                    currentBlockOffset.translate(-1, 0);
                    repaintBoard();
                }
            
                break;
        
            case KeyEvent.VK_DOWN:
                int blocksFallen = blockIndicatorOffset.y - currentBlockOffset.y;
                currentBlockOffset.setLocation(blockIndicatorOffset);

                if (blocksFallen >= 5)
                    infoPanel.addScore(blocksFallen * 2);

                repaintBoard(true);
                break;
        
            case KeyEvent.VK_SPACE:
                // Breaks if the rotation makes the block go outside of the board on the Y axis
                if (currentBlockOffset.y + currentBlock.getSize(currentBlockRotation + 90).height > boardY - 1)
                    break;

                // Shifts the block to the left if it goes outside of the screen to the right
                int newX = currentBlockOffset.x;
                if (currentBlockOffset.x + currentBlock.getSize(currentBlockRotation + 90).width - 1 >= boardX)
                    newX = boardX - currentBlock.getSize(currentBlockRotation + 90).width;
                    
                canMove = true;
                for (Point p : currentBlock.getPoints(currentBlockRotation + 90))
                {
                    // Ignore the point if it is too high
                    if (currentBlockOffset.y + p.y < 0)
                        continue;
                    
                    // Checks if the rotated point isn't empty
                    if (gameBoard[currentBlockOffset.y + p.y][newX + p.x] != Blocks.Color.None)
                        canMove = false;
                }
                
                if (canMove)
                {
                    currentBlockRotation += 90;
                    currentBlockOffset.x = newX;
                    repaintBoard(true);
                }
                break;

            default:
                return;
        }

        e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) { return; }
    
    @Override
    public void keyTyped(KeyEvent e) { return; }

    private void repaintBoard()
    {
        repaintBoard(false);
    }

    private void repaintBoard(boolean paintAll)
    {
        // Finds where the block will fall
        Point oldIndicatorOffset = new Point((int) blockIndicatorOffset.getX(), (int) blockIndicatorOffset.getY());
        blockIndicatorOffset = new Point((int) currentBlockOffset.getX(), (int) currentBlockOffset.getY());
        boolean canFall = true;

        // Repeat as long as the indicator block fell
        while (currentBlock != Blocks.Type.None && canFall)
        {
            for (Point p : currentBlock.getPoints(currentBlockRotation))
            {
                // Ignore the point if it is outside of the board
                if (blockIndicatorOffset.x + p.x < 0 || blockIndicatorOffset.x  + p.x > boardX - 1)
                    continue;
                
                // Ignore the point if it is too high up to be in the board
                if (blockIndicatorOffset.y + p.y + 1 < 0)
                    continue;
                
                // The block can't fall if the point under it isn't empty
                if (blockIndicatorOffset.y + p.y >= boardY - 1 || gameBoard[blockIndicatorOffset.y + p.y + 1][blockIndicatorOffset.x + p.x] != Blocks.Color.None)
                    canFall = false;
            }
            
            // Drags down the block indicator by 1
            if (canFall)
                blockIndicatorOffset.translate(0, 1);
        }
        
        // Repaints all the board
        if (paintAll)
            java.awt.EventQueue.invokeLater(new Thread(() -> paintImmediately(0, 0, getSize().width, getSize().height)));
        
        // Repaint the indicator if it moved
        else if (oldIndicatorOffset != blockIndicatorOffset)
        {
            java.awt.EventQueue.invokeLater(new Thread(() ->
                {
                    // Repaints around the current block
                    repaint(
                        (currentBlockOffset.x - 2) * getSize().width / boardX,
                        (currentBlockOffset.y - 2) * getSize().height / boardY,
                        (currentBlock.getSize(currentBlockRotation).width + 4) * getSize().width / boardX,
                        (currentBlock.getSize(currentBlockRotation).height + 4) * getSize().height / boardY
                    );

                    // Repaints where the block indicator was
                    repaint(
                        oldIndicatorOffset.x * getSize().width / boardX,
                        oldIndicatorOffset.y * getSize().height / boardY,
                        currentBlock.getSize(currentBlockRotation).width * getSize().width / boardX,
                        currentBlock.getSize(currentBlockRotation).height * getSize().height / boardY
                    );

                    // Repaints the block indicator
                    paintImmediately(
                        blockIndicatorOffset.x * getSize().width / boardX,
                        blockIndicatorOffset.y * getSize().height / boardY,
                        currentBlock.getSize(currentBlockRotation).width * getSize().width / boardX,
                        currentBlock.getSize(currentBlockRotation).height * getSize().height / boardY
                    );
                }
            ));
        }

        // Repaints only around the current block
        else
        {
            java.awt.EventQueue.invokeLater(new Thread(() ->
                repaint(
                    (currentBlockOffset.x - 2) * getSize().width / boardX,
                    (currentBlockOffset.y - 2) * getSize().height / boardY,
                    (currentBlock.getSize(currentBlockRotation).width + 4) * getSize().width / boardX,
                    (currentBlock.getSize(currentBlockRotation).height + 4) * getSize().height / boardY
                )
            ));
        }
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;

        int width = getSize().width;
        int height = getSize().height;

        for (Point p : currentBlock.getPoints(currentBlockRotation))
        {
            g2D.setColor(currentBlock.getJavaColor());
            g2D.fill(new Rectangle2D.Double(
                (p.x + currentBlockOffset.x) * width / boardX,
                (p.y + currentBlockOffset.y) * height / boardY,
                width / boardX,
                height / boardY
            ));

            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaBlockIndicator));
            g2D.fill(new Rectangle2D.Double(
                (p.x + blockIndicatorOffset.x) * width / boardX,
                (p.y + blockIndicatorOffset.y) * height / boardY,
                width / boardX,
                height / boardY
            ));
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        for (int i = 0; i < boardY; i++)
            for (int j = 0; j < boardX; j++)
            {
                if (gameBoard[i][j] != Blocks.Color.None)
                {
                    g2D.setColor(gameBoard[i][j].getJavaColor());
                    g2D.fill(new Rectangle2D.Double(
                        j * width / boardX,
                        i * height / boardY,
                        width / boardX,
                        height / boardY
                    ));
                }
            }
    }


    int boardX = 10;
    int boardY = 20;
    private BoardLines boardLines;
    private Blocks.Color[][] gameBoard = new Blocks.Color[boardY][boardX];

    private boolean paused = false;
    private int linesCompleted = 0;

    private boolean generateBlock = true;
    private InformationPanel infoPanel;

    private Blocks.Type currentBlock = Blocks.Type.None;
    private int currentBlockRotation = 0;
    private Point currentBlockOffset = new Point(0, 0);

    private Point blockIndicatorOffset = new Point(0, 0);
    private float alphaBlockIndicator = 0f;


    private class BoardLines extends JPanel
    {
        BoardLines()
        {
            setOpaque(false);
            setBorder(new LineBorder(Color.decode("#373D43"), strokeSize));
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2D = (Graphics2D) g;

            int width = getSize().width;
            int height = getSize().height;
    
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
        }

        private int strokeSize = 2;
    }
}
