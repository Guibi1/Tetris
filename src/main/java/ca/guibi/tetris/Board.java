package ca.guibi.tetris;

import java.util.Arrays;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.CardLayout;
import java.awt.BasicStroke;
import java.awt.AlphaComposite;
import java.awt.geom.Rectangle2D;


public class Board extends JPanel implements KeyListener {
    Board(Window window, BlockShowcase nextBlockShowcase, BlockShowcase holdBlockShowcase, GameStats statsPanel)
    {
        this.window = window;
        this.nextBlockShowcase = nextBlockShowcase;
        this.holdBlockShowcase = holdBlockShowcase;
        this.gameStats = statsPanel;
        settings = window.getSettings();
        resumeScheduler = new ScheduledThreadPoolExecutor(1);

        // Fill the gameBoard
        for (Blocks.Color[] a : gameBoard)
            Arrays.fill(a, Blocks.Color.None);

        // Layout
        layout = new CardLayout();
        layout.setVgap(0);
        setLayout(layout);
        
        drawPanel = new DrawPanel();
        add(drawPanel, "game");

        pausedPanel = new JPanel();
        JLabel labelPause = new JLabel("Game paused");
        FontManager.setComponentFont(labelPause, 50f);
        pausedPanel.add(labelPause);
        add(pausedPanel, "pause");
    }

    public void newGame()
    {
        // Reset the gameBoard
        for (Blocks.Color[] a : gameBoard)
            Arrays.fill(a, Blocks.Color.None);

        gameOver = false;
        gameStats.setScore(0);
        gameStats.setLinesCleared(0);
        generateBlock = true;
        currentBlock = Blocks.Type.None;
        isFirstBlock = true;

        Run();
        RunBlockIndicator();
    }

    public void togglePause()
    {
        gamePaused = !gamePaused;
        
        if (gamePaused)
        {
            layout.show(this, "pause");
        }
        
        else
        {
            layout.show(this, "game");
            
            resumeScheduler.shutdownNow();
            resumeScheduler = new ScheduledThreadPoolExecutor(1);
            resumeScheduler.schedule(new Thread(() -> Run()), 1200, TimeUnit.MILLISECONDS);
            RunBlockIndicator();
        }
    }

    private void Run()
    {
        new Thread(() -> {
            while (!gameOver && !gamePaused)
            {
                // Checks for completed line
                int clearedLines = 0;
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
        
                        clearedLines += 1;
                    }

                    else i -= 1;
                }

                if (clearedLines > 0)
                {
                    // Give points based on the number of line completed
                    switch (clearedLines) {
                        case 1:
                            gameStats.addScore(gameStats.getLevel() * 40);
                            break;
    
                        case 2:
                            gameStats.addScore(gameStats.getLevel() * 100);
                            break;
    
                        case 3:
                            gameStats.addScore(gameStats.getLevel() * 300);
                            break;
    
                        default:
                            gameStats.addScore(gameStats.getLevel() * 1200);
                            break;
                    }
    
                    gameStats.addLinesCleared(clearedLines);
                    repaintBoard(true);
                }

                else
                {
                    // Adds a new block
                    if (generateBlock)
                    {
                        generateBlock = false;
                        currentBlock = nextBlockShowcase.getBlockAt(0);
                        currentBlockRotation = nextBlockShowcase.getRotationAt(0);
                        currentBlockOffset.setLocation((boardX - currentBlock.getSize(currentBlockRotation).width) / 2, 0 - currentBlock.getSize(currentBlockRotation).height);
                        
                        if (isFirstBlock)
                        {
                            nextBlockShowcase.removeBlockAt(0);
                            isFirstBlock = false;
                        }

                        else
                            nextBlockShowcase.removeFirstBlock();
                        
                        nextBlockShowcase.addRandomBlock();
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
                                    gameOver = true;
                                
                                gameBoard[currentBlockOffset.y + p.y][currentBlockOffset.x + p.x] = currentBlock.getColor();
                            }

                            currentBlock = Blocks.Type.None;
                            generateBlock = true;
                            repaintBoard(true);
                        }
                    }
                }
                
                // Sleep for a time based on the current level
                try {
                    Thread.sleep((gameStats.getLevel() < 150) ? Math.round(80 * Math.cos(gameStats.getLevel() / (15 * Math.PI)) + 120) : 40);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            if (gameOver)
            {
                gameStats.saveBestScore();
                window.showMenu();

                // TODO: show an end screen
            }
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
            
            while (!gameOver && !gamePaused)
            {
                // If the alpha needs to go up
                if (alphaGoingUp)
                {
                    alphaBlockIndicator += steps;

                    if (alphaBlockIndicator >= maxAlpha)
                    {
                        alphaGoingUp = false;
                        alphaBlockIndicator = maxAlpha;
                    }
                }
                
                // If the alpha needs to go down
                else
                {
                    alphaBlockIndicator -= steps;

                    if (alphaBlockIndicator <= minAlpha)
                    {
                        alphaGoingUp = true;
                        alphaBlockIndicator = minAlpha;
                    }
                }

                // Repaint the indicator
                java.awt.EventQueue.invokeLater(new Thread(() -> 
                    drawPanel.paintImmediately(
                        blockIndicatorOffset.x * drawPanel.getSize().width / boardX,
                        blockIndicatorOffset.y * drawPanel.getSize().height / boardY,
                        currentBlock.getSize(currentBlockRotation).width * drawPanel.getSize().width / boardX,
                        currentBlock.getSize(currentBlockRotation).height * drawPanel.getSize().height / boardY
                    )
                ));
                
                // Sleep until next alpha update
                try {
                    Thread.sleep(stepsTiming);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    private void moveCurrentBlockRight()
    {
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
    }

    private void moveCurrentBlockLeft()
    {
        boolean canMove = true;
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
    }

    private void moveCurrentBlockHardFall()
    {
        int blocksFallen = blockIndicatorOffset.y - currentBlockOffset.y;
        currentBlockOffset.setLocation(blockIndicatorOffset);

        if (blocksFallen >= 5)
            gameStats.addScore(blocksFallen * 2);

        repaintBoard(true);
    }

    private void rotateCurrentBlock()
    {
        // Breaks if the rotation makes the block go outside of the board on the Y axis
        if (currentBlockOffset.y + currentBlock.getSize(currentBlockRotation + 90).height > boardY - 1)
            return;

        // Shifts the block to the left if it goes outside of the screen to the right
        int newX = Math.min(currentBlockOffset.x, boardX - currentBlock.getSize(currentBlockRotation + 90).width);

        boolean canMove = true;
        for (Point p : currentBlock.getPoints(currentBlockRotation + 90))
        {
            // Ignore the point if it is too high
            if (currentBlockOffset.y + p.y < 0)
                continue;

            // Checks if the rotated point isn't empty
            if (gameBoard[currentBlockOffset.y + p.y][newX + p.x] != Blocks.Color.None)
                canMove = false;
        }

        // TODO: Smart offset to fit

        if (canMove)
        {
            currentBlockRotation += 90;
            currentBlockOffset.x = newX;
            repaintBoard(true);
        }
    }

    private void holdCurrentBlock()
    {
        // Don't switch if there is no current block
        if (currentBlock == Blocks.Type.None)
            return;
    
        // If there is no holded block
        if (holdBlockShowcase.getBlockAt(0) == Blocks.Type.None)
        {
            holdBlockShowcase.addBlock(currentBlock, currentBlockRotation);
            generateBlock = true;
        }

        else
        {
            // Shifts the block to the left if it goes outside of the screen to the right
            int newX = Math.min(currentBlockOffset.x, boardX - holdBlockShowcase.getBlockAt(0).getSize(holdBlockShowcase.getRotationAt(0)).width);
            
            // Test if the holded block's points can fit in the game
            boolean canSwitch = true;
            for (Point p : holdBlockShowcase.getBlockAt(0).getPoints(holdBlockShowcase.getRotationAt(0)))
            {
                // Ignore the point if it is too high
                if (currentBlockOffset.y + p.y < 0)
                    continue;
                
                // Checks if the switched point isn't empty
                if (gameBoard[currentBlockOffset.y + p.y][newX + p.x] != Blocks.Color.None)
                    canSwitch = false;
            }

            // TODO: Smart offset to fit

            // Switch the blocks if it can
            if (canSwitch)
            {
                Blocks.Type tempBlock = holdBlockShowcase.getBlockAt(0);
                int tempRotation = holdBlockShowcase.getRotationAt(0);

                holdBlockShowcase.removeBlockAt(0);
                holdBlockShowcase.addBlock(currentBlock, currentBlockRotation);
                
                currentBlock = tempBlock;
                currentBlockRotation = tempRotation;
                currentBlockOffset.x = newX;
                
                repaintBoard(true);
            }
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
        // Do nothing if the game is over
        if (gameOver)
            return;

        // Key to toggle pause
        else if (e.getKeyCode() == settings.keyPause.getValue() || e.getKeyCode() == KeyEvent.VK_ESCAPE)
            togglePause();

        // Don't move the blocks if the game is paused
        else if (gamePaused)
            return;

        else
        {
            if (e.getKeyCode() == settings.keyRight.getValue())
                moveCurrentBlockRight();

            else if (e.getKeyCode() == settings.keyLeft.getValue())
                moveCurrentBlockLeft();

            else if (e.getKeyCode() == settings.keyHardFall.getValue())
                moveCurrentBlockHardFall();

            else if (e.getKeyCode() == settings.keyHold.getValue())
                holdCurrentBlock();

            else if (e.getKeyCode() == settings.keyRotate.getValue())
                rotateCurrentBlock();
        }

        e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) { return; }
    
    @Override
    public void keyTyped(KeyEvent e) { return; }
    
    @Override
    public Dimension getPreferredSize()
    {
        double aspectRatio = 1.0 / 2.0;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.height -= 20;

        return new Dimension((int) Math.round(screenSize.height * aspectRatio), screenSize.height);
    }

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


    private Window window;
    private Settings settings;

    public final int boardX = 10;
    public final int boardY = 20;

    private CardLayout layout;
    private DrawPanel drawPanel;
    private Blocks.Color[][] gameBoard = new Blocks.Color[boardY][boardX];

    private JPanel pausedPanel;
    private ScheduledThreadPoolExecutor resumeScheduler;

    private boolean gamePaused = false;
    private boolean gameOver = false;

    private boolean generateBlock = true;
    private boolean isFirstBlock = true;
    private final BlockShowcase nextBlockShowcase;
    private final BlockShowcase holdBlockShowcase;
    private final GameStats gameStats;

    private Blocks.Type currentBlock = Blocks.Type.None;
    private int currentBlockRotation = 0;
    private Point currentBlockOffset = new Point(0, 0);

    private Point blockIndicatorOffset = new Point(0, 0);
    private float alphaBlockIndicator = 0f;


    private class DrawPanel extends JPanel
    {
        DrawPanel()
        {
            setBorder(new LineBorder(Color.decode("#373D43"), strokeSize));
            setBackground(Color.decode("#121417"));
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
