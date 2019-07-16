package app;

/**
 * Optimizer
 */
public class Optimizer {

    // Optimize with Strategy
    public static Graph optimize(Node[] nodes, DistanceMatrix distances, int strategy, boolean removeCrossover,
            boolean afterControl) {
        Graph returnGraph = null;
        switch (strategy) {
        case 0:
            returnGraph = insertFirst(nodes, distances);
            break;
        case 1:
            returnGraph = insertClosest(nodes, distances);
            break;
        case 2:
            returnGraph = insertFurthest(nodes, distances);
            break;
        default:
            break;
        }

        if (removeCrossover) {
            returnGraph = handleCrossover(returnGraph, distances);
        }
        if (afterControl) {
            returnGraph = afterControl(returnGraph, distances);
        }

        return returnGraph;
    }

    // Insert Nodes in order of them appearing in the nodes Array
    // Method used: merge into path
    private static Graph insertFirst(Node[] nodes, DistanceMatrix distances) {
        Node[] path = new Node[nodes.length];
        path[0] = nodes[0];
        path[1] = nodes[1];
        // Loop at all Nodes, except first two
        for (int i = 2; i < nodes.length; i++) {

            // Find best place to merge current Node into
            // Again skip first one since this one is fixed as the origin
            int shortestIndex = -1;
            double shortestDistance = -1;

            for (int j = 1; j < path.length - 1; j++) {
                double currentDistance = ((path[j] != null) ? distances.getDistanceById(path[j], nodes[i]) : 0)
                        + (path[j + 1] != null ? distances.getDistanceById(path[j + 1], nodes[i]) : 0);
                if (shortestIndex == -1 || (currentDistance < shortestDistance && currentDistance > 0)) {
                    shortestIndex = j;
                    shortestDistance = currentDistance;
                }
            }

            // Merge into best spot in the sequence
            path = mergeNodeIntoGraph(path, nodes[i], shortestIndex);
        }

        Graph returnGraph = new Graph(nodes);
        return returnGraph;
    }

    // Append Node closest to the last inserted to the path
    // Method used: append to path
    private static Graph insertClosest(Node[] nodes, DistanceMatrix distances) {
        Node[] path = new Node[nodes.length];
        path[0] = nodes[0];

        // Loop at path to to find the closest Node to append behind the
        for (int i = 1; i < path.length; i++) {
            int shortestIndex = -1;
            double shortestDistance = -1;

            // Find closest Node to current Path Node
            // skip the first one since it already is a part of the graph
            for (int j = 1; j < nodes.length; j++) {
                double currentDistance = distances.getDistanceById(path[i - 1], nodes[j]);
                if (!pathContainsNode(path, nodes[j])
                        && (shortestIndex == -1 || (currentDistance < shortestDistance && currentDistance > 0))) {
                    shortestIndex = j;
                    shortestDistance = currentDistance;
                }
            }
            path = mergeNodeIntoGraph(path, nodes[shortestIndex], i);
        }

        Graph returnGraph = new Graph(path);
        return returnGraph;
    }

    // Merge Node furthest from the last inserted into path
    // Method used: merge into path
    private static Graph insertFurthest(Node[] nodes, DistanceMatrix distances) {
        Node[] path = new Node[nodes.length];
        path[0] = nodes[0];

        // Loop at Path to fill in all Nodes
        for (int i = 1; i < path.length; i++) {
            int furthestIndex = -1;
            double furthestDistance = -1;

            // Find Node Furthest Away from path[i - 1]
            for (int j = 0; j < nodes.length; j++) {
                double currentDistance = distances.getDistanceById(nodes[j], path[i - 1]);
                if (!pathContainsNode(path, nodes[j])
                        && (furthestIndex == -1 || (furthestDistance < currentDistance && currentDistance > 0))) {
                    furthestDistance = currentDistance;
                    furthestIndex = j;
                }
            }

            // Find best place to merge node into
            int shortestIndex = -1;
            double shortestDistance = -1;

            for (int j = 1; j < path.length; j++) {

                double currentDistance = ((path[j - 1] != null)
                        ? distances.getDistanceById(path[j - 1], nodes[furthestIndex])
                        : 0) + ((path[j] != null) ? distances.getDistanceById(path[j], nodes[furthestIndex]) : 0);

                if (shortestIndex == -1 || (shortestDistance > currentDistance && currentDistance > 0)) {
                    shortestDistance = currentDistance;
                    shortestIndex = j;
                }
            }
            path = mergeNodeIntoGraph(path, nodes[i], shortestIndex);
        }
        Graph returnGraph = new Graph(path);
        return returnGraph;
    }

    // Detects and removes Crossover in a given Graph
    private static Graph handleCrossover(Graph graph, DistanceMatrix distances) {
        Node[] nodes = graph.getNodes();
        for (Node n : nodes) {
            if (n == null) {
                System.out.println("(Partly) Empty graph");
                System.exit(1);
            }
        }
        for (int i = 1; i < nodes.length; i++) {
            for (int j = 2; j < nodes.length; j++) {
                // Skip if any of the Nodes would be the same Node, since that can't be a crossover 
                if (!(i - 1 == j - 1 || i - 1 == j || i == j - 1 || i == j)) {
                    Node nodeA1 = nodes[i - 1];
                    Node nodeA2 = nodes[i];
                    Node nodeB1 = nodes[j - 1];
                    Node nodeB2 = nodes[j];

                    // Construct routes of these nodes (A1 -> A2, B1 -> B2)
                    // f(x) = mx + n

                    // m = dy / dx
                    double mA = Math.abs(nodeA1.getY() - nodeA2.getY()) / Math.abs(nodeA1.getX() - nodeA2.getX());
                    double mB = Math.abs(nodeB1.getY() - nodeB2.getY()) / Math.abs(nodeB1.getX() - nodeB2.getX());

                    // n = f(x) - mx
                    double nA = nodeA1.getY() - (nodeA1.getX() * mA);
                    double nB = nodeB1.getY() - (nodeB1.getX() * mA);

                    // Construct crossing point of these functions to see, where they intersect
                    double intersectX = (nB - nA) / (mA - mB);
                    double intersectY = mA * intersectX + nA;

                    // Now check this point of intersection is within a relevant area
                    // TODO
                    if (((intersectX < nodeA1.getX() && intersectX > nodeA2.getX()) || (intersectX > nodeA1.getX() && intersectX < nodeA2.getX())) 
                        && ((intersectX < nodeB1.getX() && intersectX > nodeB2.getX()) || (intersectX > nodeB1.getX() && intersectX < nodeB2.getX())) 
                        && ((intersectY < nodeA1.getY() && intersectY > nodeA2.getY()) || (intersectY > nodeA1.getY() && intersectY < nodeA2.getY())) 
                        && ((intersectY < nodeB1.getY() && intersectY > nodeB2.getY()) || (intersectY > nodeB1.getY() && intersectY > nodeB2.getY()))) {
                            graph = removeCrossover(graph, nodeA2, nodeB1);
                    }
                }

            }
        }

        return graph;
    }

    // Receives a Graph, that contains a Crossover 
    // This Method will return the Graph with the Crossover resolved
    private static Graph removeCrossover(Graph graph, Node nodeFrom, Node nodeTo) {
        int indexFrom = graph.findNode(nodeFrom);
        int indexTo = graph.findNode(nodeTo);

        if(indexFrom > indexTo) {
            Node tempNode = nodeFrom;
            nodeFrom = nodeTo;
            nodeTo = tempNode;
        }

        while(nodeFrom.getID() != nodeTo.getID() && graph.findNode(nodeFrom) < graph.findNode(nodeTo)) {
            nodeFrom = graph.getNextNode(nodeFrom);
            nodeTo = graph.getPrevNode(nodeTo);
            if(nodeFrom != null && nodeTo != null) {
                graph.swapNodes(nodeFrom, nodeTo);
            } else {
                return graph;
            }
        }
        return graph;
    }

    // Check if for any given Node a shorter total Distance can be achieved by
    // merging it into another path on the Graph
    private static Graph afterControl(Graph graph, DistanceMatrix distances) {
        return null;
    }

    // Merges a Node into the Graph at index i
    // All Nodes that have an index >= i are moved up by one 
    private static Node[] mergeNodeIntoGraph(Node[] path, Node node, int index) {
        for (int i = path.length - 2; i >= index; i--) {
            // Node temp = path[i];
            path[i + 1] = path[i];
        }
        path[index] = node;

        return path;
    }

    private static boolean pathContainsNode(Node[] path, Node node) {
        for (Node n : path) {
            if (node.getID() == (n != null ? n.getID() : -1)) {
                return true;
            }
        }
        return false;
    }

}