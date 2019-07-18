package app;

// import debug.DebugUI;

/**
 * Optimizer
 * TODO: Prevent Start Location from being changed in removeCrossover or afterControl
 * TODO: Sometimes handleCrossover doesn't remove all Crossover (-> recoursion)
 */
public class Optimizer {
    private static boolean isFirst = false;
    private static int crossoverCounter = 0;

    // Optimize with Strategy
    public static Graph optimize(Graph graph, int strategy, boolean removeCrossover, boolean afterControl) {
        if(removeCrossover && afterControl && strategy == Strategy.CLOSEST) {
            isFirst = true;
        }
        Graph returnGraph = null;
        if (isFirst) {
            UI.runUI(graph, "Before Opt");
        }
        switch (strategy) {
        case 0:
            returnGraph = insertFirst(graph);
            break;
        case 1:
            returnGraph = insertClosest(graph);
            break;
        case 2:
            returnGraph = insertFurthest(graph);
            break;
        default:
            break;
        }

        if (isFirst) {
            UI.runUI(returnGraph, "After Opt");
        }

        if (removeCrossover) {
            returnGraph = handleCrossover(returnGraph);
        }
        if (afterControl) {
            returnGraph = afterControl(returnGraph);
        }

        if(isFirst) {
            UI.runUI(returnGraph, "After Everything");
        }
        isFirst = false;
        return returnGraph;
    }

    // Insert Nodes in order of them appearing in the nodes Array
    // Method used: merge into path
    private static Graph insertFirst(Graph graph) {
        Node[] nodes = graph.getNodes();
        DistanceMatrix distances = graph.getDistances();
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
    private static Graph insertClosest(Graph graph) {
        Node[] nodes = graph.getNodes();
        DistanceMatrix distances = graph.getDistances();
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
    private static Graph insertFurthest(Graph graph) {
        Node[] nodes = graph.getNodes();
        DistanceMatrix distances = graph.getDistances();
        Node[] path = new Node[nodes.length];
        path[0] = nodes[0];

        // Loop at Path to fill in all Nodes
        for (int i = 1; i < path.length; i++) {
            int furthestIndex = -1;
            double furthestDistance = -1;

            // Find Node Furthest Away from path[i - 1]
            for (int j = 0; j < nodes.length; j++) {
                double currentDistance = distances.getDistanceById(nodes[j], path[0]);
                if (!pathContainsNode(path, nodes[j])
                        && (furthestIndex == -1 || (furthestDistance < currentDistance && currentDistance > 0))) {
                    furthestDistance = currentDistance;
                    furthestIndex = j;
                }
            }

            // Find best place to merge node into
            int shortestIndex = -1;
            double shortestDistance = -1;

            // Loop at nodes Already in Path to find the optimal place to merge into
            for (int j = 1; j < path.length && path[j - 1] != null; j++) {

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
    private static Graph handleCrossover(Graph graph) {
        // UI.runUI(graph, "Before Handle Crossover");
        Node[] nodes = graph.getNodes();
        for (Node n : nodes) {
            if (n == null) {
                System.out.println("(Partly) Empty graph");
                System.exit(1);
            }
        }
        for (int i = 1; i < nodes.length; i++) {
            for (int j = 1; j < nodes.length; j++) {
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
                        graph = removeCrossover(graph, nodeA2, nodeB1);
                        crossoverCounter++;
                    }
                }
            }
        }
        // UI.runUI(graph, "After Crossover");
        System.out.println(crossoverCounter);
        crossoverCounter = 0;
        return graph;
    }

    // Receives a Graph, that contains a Crossover
    // This Method will return the Graph with the Crossover resolved
    private static Graph removeCrossover(Graph graph, Node nodeFrom, Node nodeTo) {
        int indexFrom = graph.findNode(nodeFrom);
        int indexTo = graph.findNode(nodeTo);

        if (indexFrom > indexTo) {
            Node tempNode = nodeFrom;
            nodeFrom = nodeTo;
            nodeTo = tempNode;
        }
        while (nodeFrom != null && nodeTo != null && nodeFrom.getID() != nodeTo.getID()
                && graph.findNode(nodeFrom) < graph.findNode(nodeTo)) {
            graph.swapNodes(nodeFrom, nodeTo);
            indexTo = graph.findNode(nodeFrom);
            nodeFrom = graph.getNextNode(nodeTo);
            nodeTo = graph.getNodes()[indexTo - 1];
        }
        return graph;
    }

    // Check if for any given Node a shorter total Distance can be achieved by
    // merging it into another path on the Graph
    private static Graph afterControl(Graph graph) {
        // Loop through all Nodes
        for (int i = 1; i < graph.getNodes().length - 1; i++) {

            // Loop through all "routes" (route meaning the connection between two Nodes)
            // Example: Current Node is A between the Nodes B and C (meaning the path is B
            // -> A -> C)
            // This is compared to the route of the current Loop, in this example X and Y
            // The goal is to check if merging A between X and Y would reduce the overall
            // distance
            // Meaning:
            // Is dist(B -> A -> C) + dist (X -> Y) > dist(B -> C) + dist (X -> A -> Y)?
            for (int j = 2; j < graph.getNodes().length; j++) {
                // if (i == j || i == j - 1 || i - 1 == j || i - 1 == j - 1 || i + 1 == j || i + 1 == j - 1) {
                    if(i == j) {
                    continue;
                }
                Node node = graph.getNodes()[i];
                // Check if new graph would be shorter
                if (graph.getDistances().getDistanceById(graph.getNodes()[i - 1], node)
                        + graph.getDistances().getDistanceById(node, graph.getNodes()[i + 1])
                        + graph.getDistances().getDistanceById(graph.getNodes()[j - 1], graph.getNodes()[j]) > graph
                                .getDistances().getDistanceById(graph.getNodes()[i - 1], graph.getNodes()[i + 1])
                                + graph.getDistances().getDistanceById(graph.getNodes()[j], node)
                                + graph.getDistances().getDistanceById(node, graph.getNodes()[j - 1])) {

                    // Create the new Route
                    if (i < j) {
                        // Loop from node (i) to node (j)
                        for (int k = i + 1; k < j; k++) {
                            graph.swapNodes(node, graph.getNodes()[k]);
                        }
                    } else {
                        for (int k = i; k > j - 1; k--) {
                            graph.swapNodes(node, graph.getNodes()[k]);
                        }
                    }
                }
            }
        }

        // Get all the distance Data needed to check if changing the route would have a
        // positive effect
        return graph;
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