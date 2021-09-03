package ca.guibi.tetris;

import java.awt.Point;

public final class Blocks
{
    public enum Type {
        I(new Point[] { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(0, 3) }, Color.Cyan),
        O(new Point[] { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) }, Color.Yellow),
        T(new Point[] { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 1) }, Color.Purple),
        S(new Point[] { new Point(0, 1), new Point(0, 2), new Point(1, 0), new Point(1, 1) }, Color.Green),
        Z(new Point[] { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) }, Color.Red),
        J(new Point[] { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 2) }, Color.Blue),
        L(new Point[] { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 0) }, Color.Orange);

        private final Point[] points;
        private final Color color;
        private int angle;

        Type(Point[] points, Color color)
        {
            this.points = points;
            this.color = color;
            angle = 0;
        }
        
        public void rotateBlock(int angle)
        {
            this.angle += angle;
        }

        public void setAngle(int angle)
        {
            this.angle = angle;
        }

        public int getAngle()
        {
            return angle;
        }

        public Point[] getPoints()
        {
            Point[] truePoints = new Point[4];
            
            for (int i = 0; i < points.length; i++)
            {
                truePoints[i] = new Point((int)(points[i].getX() * Math.cos(angle) - points[i].getY() * Math.sin(angle)), (int)(points[i].getY() * Math.cos(angle) + points[i].getX() * Math.sin(angle)));
            }

            return truePoints;
        }

        public Color getColor()
        {
            return color;
        }

        public java.awt.Color getJavaColor()
        {
            return color.getJavaColor();
        }
    }
    
    public enum Color {
        Cyan(java.awt.Color.cyan),
        Yellow(java.awt.Color.yellow),
        Purple(java.awt.Color.magenta),
        Green(java.awt.Color.green),
        Red(java.awt.Color.red),
        Blue(java.awt.Color.blue),
        Orange(java.awt.Color.orange),
        None(java.awt.Color.black);

        private final java.awt.Color color;

        Color(final java.awt.Color color)
        {
            this.color = color;
        }

        public java.awt.Color getJavaColor()
        {
            return color;
        }
    }
}
