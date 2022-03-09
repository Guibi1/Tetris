package ca.guibi.tetris;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.AbstractAction;
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
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Board extends StyledPanel
{
    Board(Window window, BlockShowcase nextBlockShowcase, BlockShowcase holdBlockShowcase, GameStats statsPanel)
    {
        this.window = window;
        this.nextBlockShowcase = nextBlockShowcase;
        this.holdBlockShowcase = holdBlockShowcase;
        this.gameStats = statsPanel;
        schedulerBlockIndicator = new ScheduledThreadPoolExecutor(1);

        // Fill the gameBoard
        for (Blocks.Color[] a : gameBoard)
            Arrays.fill(a, Blocks.Color.None);

        // Layout
        layout = new CardLayout();
        layout.setVgap(0);
        setLayout(layout);

        // Draw panel
        drawPanel = new DrawPanel();
        add(drawPanel, "game");

        // Pause panel
        pausedPanel = new StyledPanel();
        pausedPanel.setLayout(new BoxLayout(pausedPanel, BoxLayout.Y_AXIS));
        StyledLabel pauseLabel = new StyledLabel("Game paused", 50f);
        pauseLabel.setAlignmentX(CENTER_ALIGNMENT);
        pausedPanel.add(pauseLabel);

        StyledButton resumeButton = new StyledButton("Resume");
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                togglePause();
            }
        });
        resumeButton.setAlignmentX(CENTER_ALIGNMENT);
        pausedPanel.add(resumeButton);

        StyledButton quitButton = new StyledButton("Quit Game");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                window.showMenu();
            }
        });
        quitButton.setAlignmentX(CENTER_ALIGNMENT);
        pausedPanel.add(quitButton);
        add(pausedPanel, "pause");


        // Set actions
        ActionMap actionMap = getActionMap();
        actionMap.put("pause", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });
        actionMap.put("moveRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                moveCurrentBlockRight();
            }
        });
        actionMap.put("moveLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                moveCurrentBlockLeft();
            }
        });
        actionMap.put("fall", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                fallCurrentBlock();
            }
        });
        actionMap.put("hold", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                holdCurrentBlock();
            }
        });
        actionMap.put("rotate", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                rotateCurrentBlock();
            }
        });
    }

    public void newGame()
    {
        // Set keybinds
        Settings settings = window.getSettings();
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        inputMap.clear();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "pause");
        inputMap.put(KeyStroke.getKeyStroke(settings.pauseSetting.getValue(), 0), "pause");
        inputMap.put(KeyStroke.getKeyStroke(settings.rightSetting.getValue(), 0), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke(settings.leftSetting.getValue(), 0), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke(settings.fallSetting.getValue(), 0), "fall");
        inputMap.put(KeyStroke.getKeyStroke(settings.holdSetting.getValue(), 0), "hold");
        inputMap.put(KeyStroke.getKeyStroke(settings.rotateSetting.getValue(), 0), "rotate");

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
    }

    public void togglePause()
    {
        gamePaused = !gamePaused;

        if (gamePaused)
        {
            layout.show(this, "pause");
            scheduledFutureBlockIndicator.cancel(false);
        }
        
        else
        {
            layout.show(this, "game");
            RunBlockIndicator();
            Run(1200);
        }
    }

    private void Run()
    {
        gamePaused = false;
        RunBlockIndicator();

        Run(0);
    }

    private void Run(long delay)
    {
        scheduler.schedule(() -> {
            if (gameOver)
            {
                scheduledFutureBlockIndicator.cancel(false);
                gameStats.saveBestScore();
                window.showMenu();
                
                // TODO: show an end screen
                return;
            }
            if (gamePaused)
                return;

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
                    rotationCurrentBlock = nextBlockShowcase.getRotationAt(0);
                    offsetCurrentBlock.setLocation((boardX - currentBlock.getSize(rotationCurrentBlock).width) / 2, 0 - currentBlock.getSize(rotationCurrentBlock).height);
                    
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
                    for (Point p : currentBlock.getPoints(rotationCurrentBlock))
                    {
                        if (offsetCurrentBlock.y + p.y + 1 < 0)
                            continue;
                        
                        if (offsetCurrentBlock.y + p.y == boardY - 1 || gameBoard[offsetCurrentBlock.y + p.y + 1][offsetCurrentBlock.x + p.x] != Blocks.Color.None)
                            canFall = false;
                    }

                    if (canFall)
                    {
                        offsetCurrentBlock.translate(0, 1);
                        repaintBoard();
                    }
                    
                    else
                    {
                        // Check if game is over
                        for (Point p : currentBlock.getPoints(rotationCurrentBlock))
                        {
                            if (offsetCurrentBlock.y + p.y < 0)
                                continue;

                            if (offsetCurrentBlock.y + p.y <= 0)
                                gameOver = true;
                            
                            gameBoard[offsetCurrentBlock.y + p.y][offsetCurrentBlock.x + p.x] = currentBlock.getColor();
                        }

                        currentBlock = Blocks.Type.None;
                        generateBlock = true;
                        repaintBoard(true);
                    }
                }
            }

            // Schedule next tick
            Run(Math.round(160 / (1 + Math.pow(2.7, 0.05 * gameStats.getLevel() - 4)) + 40));
        }, delay, TimeUnit.MILLISECONDS);
    }

    private void RunBlockIndicator()
    {
        float maxAlpha = 0.4f;
        float minAlpha = 0.1f;
        int stepDelay = 20;
        alphaBlockIndicator = minAlpha;
        float steps = (maxAlpha - minAlpha) / stepDelay;

        scheduledFutureBlockIndicator = schedulerBlockIndicator.scheduleAtFixedRate(() -> {
            if (isAlphaIncreasing)
            {
                alphaBlockIndicator += steps;

                if (alphaBlockIndicator >= maxAlpha)
                {
                    isAlphaIncreasing = false;
                    alphaBlockIndicator = maxAlpha;
                }
            }
            
            // If the alpha needs to go down
            else
            {
                alphaBlockIndicator -= steps;

                if (alphaBlockIndicator <= minAlpha)
                {
                    isAlphaIncreasing = true;
                    alphaBlockIndicator = minAlpha;
                }
            }

            // Repaint the indicator
            java.awt.EventQueue.invokeLater(new Thread(() -> 
                drawPanel.paintImmediately(
                    offsetBlockIndicator.x * drawPanel.getSize().width / boardX,
                    offsetBlockIndicator.y * drawPanel.getSize().height / boardY,
                    currentBlock.getSize(rotationCurrentBlock).width * drawPanel.getSize().width / boardX,
                    currentBlock.getSize(rotationCurrentBlock).height * drawPanel.getSize().height / boardY
                )
            ));
        }, 0, stepDelay, TimeUnit.MILLISECONDS);
    }

    private void moveCurrentBlockRight()
    {
        // Do nothing if the game is over or paused
        if (gameOver || gamePaused)
            return;

        boolean canMove = true;
        for (Point p : currentBlock.getPoints(rotationCurrentBlock))
        {
            // Can't move to the right if it makes it move outside of the board
            if (offsetCurrentBlock.x + p.x >= boardX - 1)
                canMove = false;

            // Ignore the point if it is too high up
            else if (offsetCurrentBlock.y + p.y < 0)
                continue;
            
            // Check if the point at the right isn't empty
            else if (gameBoard[offsetCurrentBlock.y + p.y][offsetCurrentBlock.x + p.x + 1] != Blocks.Color.None)
                canMove = false;
        }
        
        if (canMove)
        {
            offsetCurrentBlock.translate(1, 0);
            repaintBoard();
        }
    }

    private void moveCurrentBlockLeft()
    {
        // Do nothing if the game is over or paused
        if (gameOver || gamePaused)
            return;
            
        boolean canMove = true;
        for (Point p : currentBlock.getPoints(rotationCurrentBlock))
        {
            // Can't move to the left if it makes it move out of the board
            if (offsetCurrentBlock.x + p.x <= 0)
                canMove = false;
            
            // Ignore the point if it is too high up
            else if (offsetCurrentBlock.y + p.y < 0)
                continue;

            // Check if the point at the left isn't empty
            else if (gameBoard[offsetCurrentBlock.y + p.y][offsetCurrentBlock.x + p.x - 1] != Blocks.Color.None)
                canMove = false;
        }
        
        if (canMove)
        {
            offsetCurrentBlock.translate(-1, 0);
            repaintBoard();
        }
    }

    private void fallCurrentBlock()
    {
        // Do nothing if the game is over or paused
        if (gameOver || gamePaused)
            return;
            
        int blocksFallen = offsetBlockIndicator.y - offsetCurrentBlock.y;
        offsetCurrentBlock.setLocation(offsetBlockIndicator);

        if (blocksFallen >= 5)
            gameStats.addScore(blocksFallen * 2);

        repaintBoard(true);
    }

    private void rotateCurrentBlock()
    {
        // Do nothing if the game is over or paused
        if (gameOver || gamePaused)
            return;
            
        // Breaks if the rotation makes the block go outside of the board on the Y axis
        if (offsetCurrentBlock.y + currentBlock.getSize(rotationCurrentBlock + 90).height > boardY - 1)
            return;

        // Shifts the block to the left if it goes outside of the screen to the right
        int newX = Math.min(offsetCurrentBlock.x, boardX - currentBlock.getSize(rotationCurrentBlock + 90).width);

        boolean canMove = true;
        for (Point p : currentBlock.getPoints(rotationCurrentBlock + 90))
        {
            // Ignore the point if it is too high
            if (offsetCurrentBlock.y + p.y < 0)
                continue;

            // Checks if the rotated point isn't empty
            if (gameBoard[offsetCurrentBlock.y + p.y][newX + p.x] != Blocks.Color.None)
                canMove = false;
        }

        // TODO: Smart offset to fit

        if (canMove)
        {
            rotationCurrentBlock += 90;
            offsetCurrentBlock.x = newX;
            repaintBoard(true);
        }
    }

    private void holdCurrentBlock()
    {
        // Do nothing if the game is over or paused
        if (gameOver || gamePaused)
            return;
        
        // Don't switch if there is no current block
        if (currentBlock == Blocks.Type.None)
            return;
    
        // If there is no holded block
        if (holdBlockShowcase.getBlockAt(0) == Blocks.Type.None)
        {
            holdBlockShowcase.addBlock(currentBlock, rotationCurrentBlock);
            generateBlock = true;
        }

        else
        {
            // Shifts the block to the left if it goes outside of the screen to the right
            int newX = Math.min(offsetCurrentBlock.x, boardX - holdBlockShowcase.getBlockAt(0).getSize(holdBlockShowcase.getRotationAt(0)).width);
            
            // Test if the holded block's points can fit in the game
            boolean canSwitch = true;
            for (Point p : holdBlockShowcase.getBlockAt(0).getPoints(holdBlockShowcase.getRotationAt(0)))
            {
                // Ignore the point if it is too high
                if (offsetCurrentBlock.y + p.y < 0)
                    continue;
                
                // Checks if the switched point isn't empty
                if (gameBoard[offsetCurrentBlock.y + p.y][newX + p.x] != Blocks.Color.None)
                    canSwitch = false;
            }

            // TODO: Smart offset to fit

            // Switch the blocks if it can
            if (canSwitch)
            {
                Blocks.Type tempBlock = holdBlockShowcase.getBlockAt(0);
                int tempRotation = holdBlockShowcase.getRotationAt(0);

                holdBlockShowcase.removeBlockAt(0);
                holdBlockShowcase.addBlock(currentBlock, rotationCurrentBlock);
                
                currentBlock = tempBlock;
                rotationCurrentBlock = tempRotation;
                offsetCurrentBlock.x = newX;
                
                repaintBoard(true);
            }
        }
    }

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
        Point oldIndicatorOffset = new Point(offsetBlockIndicator);
        offsetBlockIndicator = new Point(offsetCurrentBlock);
        boolean canFall = true;

        // Repeat as long as the indicator block fell
        while (currentBlock != Blocks.Type.None && canFall)
        {
            for (Point p : currentBlock.getPoints(rotationCurrentBlock))
            {
                // Ignore the point if it is outside of the board
                if (offsetBlockIndicator.x + p.x < 0 || offsetBlockIndicator.x  + p.x > boardX - 1)
                    continue;
                
                // Ignore the point if it is too high up to be in the board
                if (offsetBlockIndicator.y + p.y + 1 < 0)
                    continue;
                
                // The block can't fall if the point under it isn't empty
                if (offsetBlockIndicator.y + p.y >= boardY - 1 || gameBoard[offsetBlockIndicator.y + p.y + 1][offsetBlockIndicator.x + p.x] != Blocks.Color.None)
                    canFall = false;
            }
            
            // Drags down the block indicator by 1
            if (canFall)
                offsetBlockIndicator.translate(0, 1);
        }
        
        // Repaints all the board
        if (paintAll)
            java.awt.EventQueue.invokeLater(new Thread(() -> drawPanel.paintImmediately(0, 0, drawPanel.getSize().width, drawPanel.getSize().height)));
        
        // Repaint the indicator if it moved
        else if (oldIndicatorOffset != offsetBlockIndicator)
        {
            java.awt.EventQueue.invokeLater(new Thread(() ->
                {
                    // Repaints around the current block
                    drawPanel.repaint(
                        (offsetCurrentBlock.x - 2) * drawPanel.getSize().width / boardX,
                        (offsetCurrentBlock.y - 2) * drawPanel.getSize().height / boardY,
                        (currentBlock.getSize(rotationCurrentBlock).width + 4) * drawPanel.getSize().width / boardX,
                        (currentBlock.getSize(rotationCurrentBlock).height + 4) * drawPanel.getSize().height / boardY
                    );

                    // Repaints where the block indicator was
                    drawPanel.repaint(
                        oldIndicatorOffset.x * drawPanel.getSize().width / boardX,
                        oldIndicatorOffset.y * drawPanel.getSize().height / boardY,
                        currentBlock.getSize(rotationCurrentBlock).width * drawPanel.getSize().width / boardX,
                        currentBlock.getSize(rotationCurrentBlock).height * drawPanel.getSize().height / boardY
                    );

                    // Repaints the block indicator
                    drawPanel.paintImmediately(
                        offsetBlockIndicator.x * drawPanel.getSize().width / boardX,
                        offsetBlockIndicator.y * drawPanel.getSize().height / boardY,
                        currentBlock.getSize(rotationCurrentBlock).width * drawPanel.getSize().width / boardX,
                        currentBlock.getSize(rotationCurrentBlock).height * drawPanel.getSize().height / boardY
                    );
                }
            ));
        }

        // Repaints only around the current block
        else
        {
            java.awt.EventQueue.invokeLater(new Thread(() ->
                drawPanel.repaint(
                    (offsetCurrentBlock.x - 2) * drawPanel.getSize().width / boardX,
                    (offsetCurrentBlock.y - 2) * drawPanel.getSize().height / boardY,
                    (currentBlock.getSize(rotationCurrentBlock).width + 4) * drawPanel.getSize().width / boardX,
                    (currentBlock.getSize(rotationCurrentBlock).height + 4) * drawPanel.getSize().height / boardY
                )
            ));
        }
    }


    private final Window window;

    public final int boardX = 10;
    public final int boardY = 20;

    private Blocks.Color[][] gameBoard = new Blocks.Color[boardY][boardX];
    private boolean gamePaused = false;
    private boolean gameOver = false;
    private boolean generateBlock = true;
    private boolean isFirstBlock = true;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Blocks.Type currentBlock = Blocks.Type.None;
    private int rotationCurrentBlock = 0;
    private Point offsetCurrentBlock = new Point(0, 0);

    private ScheduledExecutorService schedulerBlockIndicator = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFutureBlockIndicator;
    private Point offsetBlockIndicator = new Point(0, 0);
    private float alphaBlockIndicator = 0f;
    private boolean isAlphaIncreasing = true;

    // GUI
    private final CardLayout layout;
    private final StyledPanel pausedPanel;
    private final BlockShowcase nextBlockShowcase;
    private final BlockShowcase holdBlockShowcase;
    private final GameStats gameStats;
    private final DrawPanel drawPanel;


    private class DrawPanel extends StyledPanel
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

            for (Point p : currentBlock.getPoints(rotationCurrentBlock))
            {
                // Draw current block
                g2D.setColor(currentBlock.getColor().getJavaColor());
                g2D.fill(new Rectangle2D.Double(
                    (p.x + offsetCurrentBlock.x) * width / boardX,
                    (p.y + offsetCurrentBlock.y) * height / boardY,
                    width / boardX,
                    height / boardY
                ));

                // Draw current block indicator
                g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaBlockIndicator));
                g2D.fill(new Rectangle2D.Double(
                    (p.x + offsetBlockIndicator.x) * width / boardX,
                    (p.y + offsetBlockIndicator.y) * height / boardY,
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
                g2D.drawLine(i * width / boardX, 0, i * width / boardX, height);
            
            for (int i = 1; i <= boardY; i++)
                g2D.drawLine(0, i * height / boardY, width, i * height / boardY);
        }


        private int strokeSize = 2;
    }
}
