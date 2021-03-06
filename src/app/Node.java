package app;

/**
 * Node
 */
public class Node implements Comparable<Node> {
    private final double x;
    private final double y;
    private final int ID;
    // private Node pickupNode;

    public Node(double x, double y) {
        this.x = x;
        this.y = y;
        // this.pickupNode = pickupNode;
        ID = IdSetter.getNewId();
    }

    public Node() {
        this.x = Math.random() * App.range;
        this.y = Math.random() * App.range;

        ID = IdSetter.getNewId();
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public int getID() {
        return this.ID;
    }

    public int compareTo(Node other) {
        return Math.abs(this.getID() - other.getID());
    }
}