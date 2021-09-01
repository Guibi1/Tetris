package ca.guibi;

import java.awt.Dimension;
import javax.swing.JPanel;
import java.util.Arrays;

import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.BasicStroke;

public class Board extends JPanel {
    Board()
    {
        super();
        setPreferredSize(new Dimension(400, 800));
        setBackground(Color.decode("#121417"));
        setBorder(new LineBorder(Color.decode("#373D43"), 2));
        gameBoard = new Blocks.Color[20][10];
    }

    void NewGame()
    {
        Arrays.fill(gameBoard, Blocks.Color.None);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;

        int width = getSize().width;
        int height = getSize().height;

        g2D.setStroke(new BasicStroke(2));
        g2D.setColor(Color.decode("#373D43"));
        for (int i = 1; i <= gameBoard[0].length; i++)
        {
            g2D.draw(new Line2D.Float(i * width / gameBoard[0].length, 0, i * width / gameBoard[0].length, height));
        }
        
        for (int i = 1; i <= gameBoard.length; i++)
        {
            g2D.draw(new Line2D.Float(0, i * height / gameBoard.length, width, i * height / gameBoard.length));
        }
    }

    private Blocks.Color[][] gameBoard;
}
