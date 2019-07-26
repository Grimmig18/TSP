package app;

/**
 * Debugging
 */
public class Debugging {

    public static boolean isCrossoverFree(Graph graph) {
        // UI.runUI(graph, "Before Handle Crossover");
        Node[] nodes = graph.getNodes();
        for (Node n : nodes) {
            if (n == null) {
                System.out.println("(Partly) Empty graph");
                System.exit(1);
            }
        }
        for (int i = 1; i < nodes.length; i++) {
            for (int j = 2; j < nodes.length; j++) {
                nodes = graph.getNodes();
                // Skip if any of the Nodes would be the same Node, since that can't be a
                // crossover
                // if (!(i - 1 == j - 1 || i - 1 == j || i == j - 1 || i == j)) {
                // if(!(i == j)) {
                Node nodeA1 = nodes[i - 1];
                Node nodeA2 = nodes[i];
                Node nodeB1 = nodes[j - 1];
                Node nodeB2 = nodes[j];

                if (nodeA1.compareTo(nodeB1) != 0 && nodeA1.compareTo(nodeB2) != 0 && nodeA2.compareTo(nodeB1) != 0
                        && nodeA2.compareTo(nodeB2) != 0) {

                    // Construct routes of these nodes (A1 -> A2, B1 -> B2)
                    // f(x) = mx + n

                    // m = dy / dx
                    double mA = (nodeA2.getY() - nodeA1.getY()) / (nodeA2.getX() - nodeA1.getX());
                    double mB = (nodeB2.getY() - nodeB1.getY()) / (nodeB2.getX() - nodeB1.getX());

                    // n = f(x) - mx
                    double nA = nodeA1.getY() - (nodeA1.getX() * mA);
                    double nB = nodeB1.getY() - (nodeB1.getX() * mB);

                    // Construct crossing point of these functions to see where they intersect
                    double intersectX = (nB - nA) / (mA - mB);
                    double intersectY = (mA * intersectX) + nA;

                    // New approach for detecting relevant crossovers
                    // Check if intersection lies within the rectangle of nodes A1 and A2 and inside
                    // the rectangle created by B1 and B2
                    // Checking Order:
                    // X A1, A2 DONE
                    // Y A1, A2
                    // X B1, B2
                    // Y B1, B2
                    if ((intersectX < (nodeA1.getX() < nodeA2.getX() ? nodeA2.getX() : nodeA1.getX())
                            && intersectX > (nodeA1.getX() < nodeA2.getX() ? nodeA1.getX() : nodeA2.getX()))
                            && (intersectY < (nodeA1.getY() < nodeA2.getY() ? nodeA2.getY() : nodeA1.getY())
                                    && intersectY > (nodeA1.getY() < nodeA2.getY() ? nodeA1.getY() : nodeA2.getY()))
                            && (intersectX < (nodeB1.getX() < nodeB2.getX() ? nodeB2.getX() : nodeB1.getX())
                                    && intersectX > (nodeB1.getX() < nodeB2.getX() ? nodeB1.getX() : nodeB2.getX()))
                            && (intersectY < (nodeB1.getY() < nodeB2.getY() ? nodeB2.getY() : nodeB1.getY())
                                    && intersectY > (nodeB1.getY() < nodeB2.getY() ? nodeB1.getY() : nodeB2.getY()))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void runUIWithEmptyGraph(Node[] nodes) {
        int counter = 0;
        for(int i = 0; i < nodes.length && nodes[i] != null; i++) {
            counter++;
        }

        Node[] newNodes = new Node[counter];
        for(int i = 0; i < counter; i++) {
            newNodes[i] = nodes[i];
        }
        UI.runUI(new Graph(newNodes), "Partly empty Graph");
    }
}