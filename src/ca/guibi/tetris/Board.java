package ca.guibi.tetris;

import java.awt.Dimension;
import javax.swing.JPanel;
import java.util.Arrays;
import java.util.Random;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.Rectangle2D;

public class Board extends JPanel implements KeyListener {
    Board()
    {
        super();
        setPreferredSize(new Dimension(300, 650));
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
        currentBlock = Blocks.Type.None;
        nextBlock = Blocks.Type.values()[random.nextInt(Blocks.Type.values().length - 1)];

        Run();
    }

    void Run()
    {
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
                }

                else i -= 1;
            }

            if (completedLine)
                paintImmediately(getVisibleRect());

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
                    paintCurrentBlock();
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
                        paintCurrentBlock();
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
                        paintImmediately(getVisibleRect());
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
    }
    
    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode())
        {
            case KeyEvent.VK_RIGHT:
                boolean canMove = true;
                for (Point p : currentBlock.getPoints())
                {
                    if (currentBlockOffset.y + p.y < 0)
                        continue;
                    
                    if (currentBlockOffset.x + p.x == boardX - 1 || gameBoard[currentBlockOffset.y + p.y][currentBlockOffset.x + p.x + 1] != Blocks.Color.None)
                        canMove = false;
                }
                
                if (canMove)
                {
                    currentBlockOffset.translate(1, 0);
                    paintCurrentBlock();
                }

                break;

            case KeyEvent.VK_LEFT:
                canMove = true;
                for (Point p : currentBlock.getPoints())
                {
                    if (currentBlockOffset.y + p.y < 0)
                        continue;

                    if (currentBlockOffset.x + p.x == 0 || gameBoard[currentBlockOffset.y + p.y][currentBlockOffset.x + p.x - 1] != Blocks.Color.None)
                        canMove = false;
                }
                
                if (canMove)
                {
                    currentBlockOffset.translate(-1, 0);
                    paintCurrentBlock();
                }
            
                break;
        
            case KeyEvent.VK_DOWN:
                canMove = true;
                for (Point p : currentBlock.getPoints())
                {
                    if (currentBlockOffset.y + p.y + 1 < 0)
                        continue;
                    
                    if (currentBlockOffset.y + p.y == boardY - 1 || gameBoard[currentBlockOffset.y + p.y + 1][currentBlockOffset.x + p.x] != Blocks.Color.None)
                        canMove = false;
                }
                
                if (canMove)
                {
                    currentBlockOffset.translate(0, 1);
                    paintCurrentBlock();
                }

                break;
        
            case KeyEvent.VK_SPACE:
                currentBlock.rotateBlock(90);

                if (currentBlockOffset.x + currentBlock.getSize().width >= boardX)
                    currentBlockOffset.x = boardX - currentBlock.getSize().width - 1;
                    
                canMove = true;
                for (Point p : currentBlock.getPoints())
                {
                    if (currentBlockOffset.y + p.y < 0)
                        continue;
                    
                    if (gameBoard[currentBlockOffset.y + p.y][currentBlockOffset.x + p.x] != Blocks.Color.None)
                        canMove = false;
                }
                
                if (canMove)
                    paintImmediately(getVisibleRect());
                
                else
                    currentBlock.rotateBlock(-90);
                break;

            default: 
                return;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { return; }
    
    @Override
    public void keyTyped(KeyEvent e) { return; }

    private void paintCurrentBlock()
    {
        paintImmediately(
            (currentBlockOffset.x - 1) * (getSize().width / boardX),
            (currentBlockOffset.y - 1) * (getSize().height / boardY),
            (currentBlock.getSize().width + 3) * (getSize().width / boardX),
            (currentBlock.getSize().height + 3) * (getSize().height / boardY)
        );
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
    private Blocks.Type nextBlock = Blocks.Type.None;
    private Blocks.Type currentBlock = Blocks.Type.None;
    private Point currentBlockOffset = new Point();
}
