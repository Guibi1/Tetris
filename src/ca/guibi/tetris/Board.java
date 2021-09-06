package ca.guibi.tetris;

import java.util.Arrays;
import java.util.Random;

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
    Board()
    {
        setPreferredSize(new Dimension(300, 650));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        boardLines = new BoardLines();
        add(boardLines);
        validate();
        setBackground(Color.decode("#121417"));

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
        currentBlock = Blocks.Type.None;
        nextBlock = Blocks.Type.values()[random.nextInt(Blocks.Type.values().length - 1)];

        Run();
    }

    void Run()
    {
        RunBlockIndicator();
        
        new Thread(() -> {
            while (!paused)
            {
                // Checks for completed line
                boolean completedLine = false;
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
        
                        completedLine = true;

                        // TODO: Calculate score
                    }

                    else i -= 1;
                }

                if (completedLine)
                    repaintBoard(true);

                else
                {
                    // Adds a new block
                    if (generateBlock)
                    {
                        generateBlock = false;
                        currentBlock = nextBlock;
                        currentBlock.rotateBlock(random.nextInt(4) * 90);
                        currentBlockOffset.setLocation((boardX - currentBlock.getSize().width) / 2, 0 - currentBlock.getSize().height);
                        nextBlock = Blocks.Type.values()[random.nextInt(Blocks.Type.values().length - 1)];
                        repaintBoard();
                    }

                    // Drags the block down if it can
                    else
                    {
                        boolean canFall = true;
                        for (Point p : currentBlock.getPoints())
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
                            for (Point p : currentBlock.getPoints())
                            {
                                if (currentBlockOffset.y + p.y < 0)
                                    continue;

                                if (currentBlockOffset.y + p.y <= 0)
                                    paused = true;
                                
                                gameBoard[currentBlockOffset.y + p.y][currentBlockOffset.x + p.x] = currentBlock.getColor();
                            }

                            currentBlock.resetAngle();
                            currentBlock = Blocks.Type.None;
                            generateBlock = true;
                            repaintBoard(true);
                        }
                    }
                }
                
                try {
                    Thread.sleep(200);
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
                        (blockIndicatorOffset.x) * (getSize().width / boardX),
                        (blockIndicatorOffset.y) * (getSize().height / boardY),
                        (currentBlock.getSize().width) * (getSize().width / boardX),
                        (currentBlock.getSize().height) * (getSize().height / boardY)
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
    
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(KeyEvent.getKeyText(e.getKeyCode()));

        switch (e.getKeyCode())
        {
            case KeyEvent.VK_RIGHT:
                boolean canMove = true;
                for (Point p : currentBlock.getPoints())
                {
                    if (currentBlockOffset.y + p.y < 0)
                        continue;
                    
                    else if (currentBlockOffset.x + p.x >= boardX - 1 || gameBoard[currentBlockOffset.y + p.y][currentBlockOffset.x + p.x + 1] != Blocks.Color.None)
                        canMove = false;
                }
                
                if (canMove)
                {
                    currentBlockOffset.translate(1, 0);
                    repaintBoard();
                }

                System.out.println(canMove);

                break;

            case KeyEvent.VK_LEFT:
                canMove = true;
                for (Point p : currentBlock.getPoints())
                {
                    if (currentBlockOffset.y + p.y < 0)
                        continue;

                    else if (currentBlockOffset.x + p.x <= 0 || gameBoard[currentBlockOffset.y + p.y][currentBlockOffset.x + p.x - 1] != Blocks.Color.None)
                        canMove = false;
                }
                
                if (canMove)
                {
                    currentBlockOffset.translate(-1, 0);
                    repaintBoard();
                }
            
                break;
        
            case KeyEvent.VK_DOWN:
                canMove = true;
                for (Point p : currentBlock.getPoints())
                {
                    if (currentBlockOffset.y + p.y + 1 < 0)
                        continue;
                    
                    if (currentBlockOffset.y + p.y >= boardY - 1 || gameBoard[currentBlockOffset.y + p.y + 1][currentBlockOffset.x + p.x] != Blocks.Color.None)
                        canMove = false;
                }
                
                if (canMove)
                {
                    currentBlockOffset.translate(0, 1);
                    repaintBoard();
                }

                break;
        
            case KeyEvent.VK_SPACE:
                if (currentBlockOffset.y + currentBlock.getSize().width > boardY - 1)
                    break;

                currentBlock.rotateBlock(90);
                int newX = currentBlockOffset.x;

                if (currentBlockOffset.x + currentBlock.getSize().width >= boardX)
                    newX = boardX - currentBlock.getSize().width - 1;
                    
                canMove = true;
                for (Point p : currentBlock.getPoints())
                {
                    if (currentBlockOffset.y + p.y < 0)
                        continue;
                    
                    if (gameBoard[currentBlockOffset.y + p.y][newX + p.x] != Blocks.Color.None)
                        canMove = false;
                }
                
                if (canMove)
                {
                    currentBlockOffset.x = newX;
                    repaintBoard(true);
                }
                
                else
                    currentBlock.rotateBlock(-90);
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
        blockIndicatorOffset = new Point((int) currentBlockOffset.getX(), (int) currentBlockOffset.getY());
        boolean canFall = true;

        while (currentBlock != Blocks.Type.None && canFall)
        {
            for (Point p : currentBlock.getPoints())
            {
                if (blockIndicatorOffset.y + p.y + 1 < 0)
                    continue;
                
                if (blockIndicatorOffset.y + p.y >= boardY - 1 || gameBoard[blockIndicatorOffset.y + p.y + 1][blockIndicatorOffset.x + p.x] != Blocks.Color.None)
                    canFall = false;
            }
            
            if (canFall)
                blockIndicatorOffset.translate(0, 1);
        }
        
        if (paintAll)
            java.awt.EventQueue.invokeLater(new Thread(() -> paintImmediately(getVisibleRect())));
        
        else
        {
            java.awt.EventQueue.invokeLater(new Thread(() ->
                {
                    repaint(
                        (currentBlockOffset.x - 2) * (getSize().width / boardX),
                        (currentBlockOffset.y - 2) * (getSize().height / boardY),
                        (currentBlock.getSize().width + 4) * (getSize().width / boardX),
                        (currentBlock.getSize().height + 4) * (getSize().height / boardY)
                    );

                    paintImmediately(
                        (blockIndicatorOffset.x - 2) * (getSize().width / boardX),
                        (blockIndicatorOffset.y - 2) * (getSize().height / boardY),
                        (currentBlock.getSize().width + 4) * (getSize().width / boardX),
                        (currentBlock.getSize().height + 4) * (getSize().height / boardY)
                    );
                }
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

        for (Point p : currentBlock.getPoints())
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
    private int score = 0;
    private Random random = new Random(System.currentTimeMillis());
    private boolean generateBlock = true;
    private Blocks.Type nextBlock = Blocks.Type.None;
    private Blocks.Type currentBlock = Blocks.Type.None;
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
