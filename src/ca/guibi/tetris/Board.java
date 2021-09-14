package ca.guibi.tetris;

import java.util.Arrays;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.FlowLayout;
import java.awt.BasicStroke;
import java.awt.AlphaComposite;
import java.awt.geom.Rectangle2D;


public class Board extends JPanel implements KeyListener {
    Board(BlockShowcase nextBlockShowcase, BlockShowcase holdBlockShowcase, GameStats scorePanel)
    {
        setLayout(new FlowLayout());
        drawPanel = new DrawPanel();
        add(drawPanel);
        validate();

        this.nextBlockShowcase = nextBlockShowcase;
        this.holdBlockShowcase = holdBlockShowcase;
        this.scorePanel = scorePanel;

        for (Blocks.Color[] a : gameBoard)
            Arrays.fill(a, Blocks.Color.None);
    }

    void NewGame()
    {
        for (Blocks.Color[] a : gameBoard)
            Arrays.fill(a, Blocks.Color.None);

        paused = false;
        scorePanel.setScore(0);
        generateBlock = true;
        currentBlock = Blocks.Type.None;
        nextBlockShowcase.showcaseRandomBlock();

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
                        scorePanel.addScore(getLevel() * 40);
                        break;

                    case 2:
                        scorePanel.addScore(getLevel() * 100);
                        break;

                    case 3:
                        scorePanel.addScore(getLevel() * 300);
                        break;

                    default:
                        scorePanel.addScore(getLevel() * 1200);
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
                        currentBlock = nextBlockShowcase.getShowcasedBlock();
                        currentBlockRotation = nextBlockShowcase.getShowcasedBlockRotation();
                        currentBlockOffset.setLocation((boardX - currentBlock.getSize(currentBlockRotation).width) / 2, 0 - currentBlock.getSize(currentBlockRotation).height);
                        nextBlockShowcase.showcaseRandomBlock();
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
                    drawPanel.paintImmediately(
                        blockIndicatorOffset.x * drawPanel.getSize().width / boardX,
                        blockIndicatorOffset.y * drawPanel.getSize().height / boardY,
                        currentBlock.getSize(currentBlockRotation).width * drawPanel.getSize().width / boardX,
                        currentBlock.getSize(currentBlockRotation).height * drawPanel.getSize().height / boardY
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
                    scorePanel.addScore(blocksFallen * 2);

                repaintBoard(true);
                break;

            case KeyEvent.VK_UP:
                // Don't switch if the is no current block
                if (currentBlock == Blocks.Type.None)
                    break;
                
                // If there is no holded block
                if (holdBlockShowcase.getShowcasedBlock() == Blocks.Type.None)
                {
                    holdBlockShowcase.setShowcasedBlock(currentBlock);
                    holdBlockShowcase.setShowcasedBlockRotation(currentBlockRotation);

                    generateBlock = true;
                }

                else
                {
                    // Shifts the block to the left if it goes outside of the screen to the right
                    int newX = Math.min(currentBlockOffset.x, boardX - holdBlockShowcase.getShowcasedBlock().getSize(holdBlockShowcase.getShowcasedBlockRotation()).width);
                    
                    // Test if the holded block's points can fit in the game
                    boolean canSwitch = true;
                    for (Point p : holdBlockShowcase.getShowcasedBlock().getPoints(holdBlockShowcase.getShowcasedBlockRotation()))
                    {
                        // Ignore the point if it is too high
                        if (currentBlockOffset.y + p.y < 0)
                            continue;
                        
                        // Checks if the switched point isn't empty
                        if (gameBoard[currentBlockOffset.y + p.y][newX + p.x] != Blocks.Color.None)
                            canSwitch = false;
                    }

                    // TODO: Smart offsetto fit

                    // Switch the blocks if it can
                    if (canSwitch)
                    {
                        Blocks.Type tempBlock = holdBlockShowcase.getShowcasedBlock();
                        int tempRotation = holdBlockShowcase.getShowcasedBlockRotation();
    
                        holdBlockShowcase.setShowcasedBlock(currentBlock);
                        holdBlockShowcase.setShowcasedBlockRotation(currentBlockRotation);
                        
                        currentBlock = tempBlock;
                        currentBlockRotation = tempRotation;
                        currentBlockOffset.x = newX;
                    }
                }

                repaintBoard(true);
                break;
        
            case KeyEvent.VK_SPACE:
                // Breaks if the rotation makes the block go outside of the board on the Y axis
                if (currentBlockOffset.y + currentBlock.getSize(currentBlockRotation + 90).height > boardY - 1)
                    break;

                // Shifts the block to the left if it goes outside of the screen to the right
                int newX = Math.min(currentBlockOffset.x, boardX - currentBlock.getSize(currentBlockRotation + 90).width);
                    
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
                
                // TODO: Smart offsetto fit

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
        Point oldIndicatorOffset = new Point(blockIndicatorOffset);
        blockIndicatorOffset = new Point(currentBlockOffset);
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
            java.awt.EventQueue.invokeLater(new Thread(() -> drawPanel.paintImmediately(0, 0, drawPanel.getSize().width, drawPanel.getSize().height)));
        
        // Repaint the indicator if it moved
        else if (oldIndicatorOffset != blockIndicatorOffset)
        {
            java.awt.EventQueue.invokeLater(new Thread(() ->
                {
                    // Repaints around the current block
                    drawPanel.repaint(
                        (currentBlockOffset.x - 2) * drawPanel.getSize().width / boardX,
                        (currentBlockOffset.y - 2) * drawPanel.getSize().height / boardY,
                        (currentBlock.getSize(currentBlockRotation).width + 4) * drawPanel.getSize().width / boardX,
                        (currentBlock.getSize(currentBlockRotation).height + 4) * drawPanel.getSize().height / boardY
                    );

                    // Repaints where the block indicator was
                    drawPanel.repaint(
                        oldIndicatorOffset.x * drawPanel.getSize().width / boardX,
                        oldIndicatorOffset.y * drawPanel.getSize().height / boardY,
                        currentBlock.getSize(currentBlockRotation).width * drawPanel.getSize().width / boardX,
                        currentBlock.getSize(currentBlockRotation).height * drawPanel.getSize().height / boardY
                    );

                    // Repaints the block indicator
                    drawPanel.paintImmediately(
                        blockIndicatorOffset.x * drawPanel.getSize().width / boardX,
                        blockIndicatorOffset.y * drawPanel.getSize().height / boardY,
                        currentBlock.getSize(currentBlockRotation).width * drawPanel.getSize().width / boardX,
                        currentBlock.getSize(currentBlockRotation).height * drawPanel.getSize().height / boardY
                    );
                }
            ));
        }

        // Repaints only around the current block
        else
        {
            java.awt.EventQueue.invokeLater(new Thread(() ->
                drawPanel.repaint(
                    (currentBlockOffset.x - 2) * drawPanel.getSize().width / boardX,
                    (currentBlockOffset.y - 2) * drawPanel.getSize().height / boardY,
                    (currentBlock.getSize(currentBlockRotation).width + 4) * drawPanel.getSize().width / boardX,
                    (currentBlock.getSize(currentBlockRotation).height + 4) * drawPanel.getSize().height / boardY
                )
            ));
        }
    }

    int boardX = 10;
    int boardY = 20;
    private DrawPanel drawPanel;
    private Blocks.Color[][] gameBoard = new Blocks.Color[boardY][boardX];

    private boolean paused = false;
    private int linesCompleted = 0;

    private boolean generateBlock = true;
    private final BlockShowcase nextBlockShowcase;
    private final BlockShowcase holdBlockShowcase;
    private final GameStats scorePanel;

    private Blocks.Type currentBlock = Blocks.Type.None;
    private int currentBlockRotation = 0;
    private Point currentBlockOffset = new Point(0, 0);

    private Point blockIndicatorOffset = new Point(0, 0);
    private float alphaBlockIndicator = 0f;


    private class DrawPanel extends JPanel
    {
        DrawPanel()
        {
            setPreferredSize(new Dimension(300, 650));
            setBorder(new LineBorder(Color.decode("#373D43"), strokeSize));
            setBackground(Color.decode("#121417"));
        }

        @Override
        public Dimension getPreferredSize() {
            double aspectRatio = 1.0 / 2.0;

            Dimension d = getParent().getSize();
            int newHeight = (int) Math.round(Math.min(
                d.height - 10,
                (d.width - 10) * (1 / aspectRatio)
            ));

            return new Dimension((int) Math.round(newHeight * aspectRatio), newHeight);
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
                // Draw current block
                g2D.setColor(currentBlock.getJavaColor());
                g2D.fill(new Rectangle2D.Double(
                    (p.x + currentBlockOffset.x) * width / boardX,
                    (p.y + currentBlockOffset.y) * height / boardY,
                    width / boardX,
                    height / boardY
                ));

                // Draw current block indicator
                g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaBlockIndicator));
                g2D.fill(new Rectangle2D.Double(
                    (p.x + blockIndicatorOffset.x) * width / boardX,
                    (p.y + blockIndicatorOffset.y) * height / boardY,
                    width / boardX,
                    height / boardY
                ));
                g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }

            // Draw gameboard blocks
            for (int i = 0; i < boardY; i++)
            {
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

            // Draw board lines
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
