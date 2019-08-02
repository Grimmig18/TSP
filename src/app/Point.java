package app;

/**
 * Point
 */
public class Point {

    private double x;
    private double y;

    public Point() {
        this(0, 0);
    }
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

}