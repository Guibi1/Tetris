package ca.guibi.tetris;

import java.awt.Point;
import java.awt.Dimension;


public final class Blocks
{
    public enum Type {
        I(new Point[] { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(0, 3) }, Color.Cyan),
        O(new Point[] { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) }, Color.Yellow),
        T(new Point[] { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 1) }, Color.Purple),
        S(new Point[] { new Point(0, 1), new Point(0, 2), new Point(1, 0), new Point(1, 1) }, Color.Green),
        Z(new Point[] { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) }, Color.Red),
        J(new Point[] { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 2) }, Color.Blue),
        L(new Point[] { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 0) }, Color.Orange),
        None(new Point[0], Color.None);

        private final Color color;
        private final Point[] originalPoints;
        
        private Dimension lastSize;
        private int lastAngle;
        private Point[] lastPoints;


        Type(Point[] points, Color color)
        {
            this.color = color;
            lastSize = new Dimension();
            lastAngle = 0;
            
            originalPoints = new Point[points.length];
            for (int i = 0; i< points.length; i++)
                originalPoints[i] = new Point(points[i].x, points[i].y);
            
            lastPoints = getRotatedPoints(lastAngle);
            updateSize(lastAngle);
        }
        
        private void updateSize(int angle)
        {
            Point min = new Point(0, 0);
            Point max = new Point(0, 0);

            for (Point p : getPoints(angle))
            {
                if (p.x > max.x)
                    max.x = p.x;

                else if (p.x < min.x)
                    min.x = p.x;

                if (p.y > max.y)
                    max.y = p.y;
                
                else if (p.y < min.y)
                    min.y = p.y;
            }

            lastSize.width = 1 + max.x - min.x;
            lastSize.height = 1 + max.y - min.y;
        }

        private Point[] getRotatedPoints(int angle)
        {
            // TODO: Rotate around central point
            Point origin = new Point(0, 0);
            Point[] newPoints = new Point[originalPoints.length];

            // double rotationPoint = Math.max(getSize(angle).width, getSize(angle).height) / 2;

            for (int i = 0; i < originalPoints.length; i++)
            {
                int x = (int) Math.round(originalPoints[i].getX() * Math.cos(Math.toRadians(angle)) - originalPoints[i].getY() * Math.sin(Math.toRadians(angle)));
                int y = (int) Math.round(originalPoints[i].getY() * Math.cos(Math.toRadians(angle)) + originalPoints[i].getX() * Math.sin(Math.toRadians(angle)));
            
                newPoints[i] = new Point(x, y);

                if (x < origin.x)
                    origin.x = newPoints[i].x;

                if (y < origin.y)
                    origin.y = newPoints[i].y;
            }

            for (Point p : newPoints)
            {
                p.x -= origin.x;
                p.y -= origin.y;
            }

            return newPoints;
        }

        public Dimension getSize(int angle)
        {
            if (angle != lastAngle)
                updateSize(angle);

            return lastSize;
        }

        public Point[] getPoints(int angle)
        {
            if (angle != lastAngle)
                lastPoints = getRotatedPoints(angle);
            
            return lastPoints;
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
