
package app;

/**
 * This Class handles all of the different Algorithms applied to a Graph. <br>
 * Current Algorithms include:
 * <p>
 * <ul>
 * <li>insertFirst {@link Optimizer#insertFirst(Graph)}
 * <li>insertFurthest {@link Optimizer#insertFurthest(Graph)}
 * <li>insertClosest {@link Optimizer#insertClosest(Graph)}
 * <li>handleCrossover {@link Optimizer#handleCrossover(Graph)}
 * <li>afterControl {@link Optimizer#afterControl(Graph)}
 */
public class Optimizer {
    // private static boolean isFirst = false;
    // private static int crossoverCounter = 0;
    private static boolean graphChanged = false;
    private static int recursionCounter = 0;

    /**
     * Main function of the {@code Optimizer} Class handling all other function
     * calls
     * 
     * @param graph           A graph containing an array of Nodes
     * @param strategy        Optimization Strategy (see {@link Strategy})
     * @param removeCrossover Controls whether crossovers are removed via the
     *                        {@link Optimizer#handleCrossover(Graph)}
     * @param afterControl    Controls whether the
     *                        {@link Optimizer#afterControl(Graph)} is applied
     * @return Returns an heuristically optimized Graph as the result of the
     *         function call
     */
    public static Graph optimize(Graph graph, int strategy, boolean removeCrossover, boolean afterControl) {
        Graph returnGraph = null;
        recursionCounter = 0;

        // Call Strategy according to strategy parameter
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

        // Call additional algorithms
        if (afterControl) {
            returnGraph = afterControl(returnGraph);
        }
        if (removeCrossover) {

            returnGraph = handleCrossover(returnGraph);
            // if(!Debugging.isCrossoverFree(returnGraph)) {
            // returnGraph = handleCrossover(returnGraph);
            // }
        }
        return returnGraph;
    }

    /**
     * Uses the insertFirst Algorithm to heuristically optimize the graph passed as
     * its parameter
     * 
     * @param graph Graph to be heuristically optimized
     * @return Returns {@code graph} with the same Nodes as the Input parameter just
     *         in a different order
     */
    private static Graph insertFirst(Graph graph) {
        Node[] nodes = graph.getNodes();
        Node[] path = new Node[nodes.length];
        path[0] = nodes[0];
        path[1] = nodes[1];

        // Loop at all Nodes, except first two since they are already part of the graph
        for (int i = 2; i < nodes.length; i++) {

            int shortestIndex = -1;
            double shortestDistance = -1;

            // Find best place to merge current Node into
            // Again skip first one since this one is fixed as the origin
            for (int j = 1; j < path.length && path[j - 1] != null; j++) {
                double currentDistance = simulateMerge(path, j, nodes[i]).getTotalDistance();
                if (shortestIndex == -1 || (currentDistance < shortestDistance)) {
                    shortestIndex = j;
                    shortestDistance = currentDistance;
                }
            }

            // Merge into best spot in the sequence
            path = mergeNodeIntoGraph(path, nodes[i], shortestIndex);
        }

        Graph returnGraph = new Graph(path);
        return returnGraph;
    }

    /**
     * Uses the insertClosest Algorithm to heuristically optimize the graph passed
     * as its parameter
     * 
     * @param graph Graph to be heuristically optimized
     * @return Returns {@code graph} with the same Nodes as the Input parameter just
     *         in a different order
     */
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
            for (int j = 0; j < nodes.length; j++) {
                double currentDistance = distances.getDistanceById(path[i - 1], nodes[j]);
                if (!pathContainsNode(path, nodes[j])
                        && (shortestIndex == -1 || (currentDistance < shortestDistance && currentDistance > 0))) {
                    shortestIndex = j;
                    shortestDistance = currentDistance;
                }
            }

            int shortestIndex2 = -1;
            double shortestDistance2 = -1;

            // Loop at nodes Already in Path to find the optimal place to merge into
            for (int j = 1; j < path.length && path[j - 1] != null; j++) {
                double currentDistance = simulateMerge(path, j, nodes[shortestIndex]).getTotalDistance();

                if (shortestIndex2 == -1 || (shortestDistance2 > currentDistance && currentDistance > 0)) {
                    shortestDistance2 = currentDistance;
                    shortestIndex2 = j;
                }
            }
            path = mergeNodeIntoGraph(path, nodes[shortestIndex], shortestIndex2);
        }

        Graph returnGraph = new Graph(path);
        return returnGraph;
    }

    /**
     * Uses the insertFurthest Algorithm to heuristically optimize the graph passed
     * as its parameter
     * 
     * @param graph Graph to be heuristically optimized
     * @return Returns {@code graph} with the same Nodes as the Input parameter just
     *         in a different order
     */
    private static Graph insertFurthest(Graph graph) {
        Node[] nodes = graph.getNodes();
        DistanceMatrix distances = graph.getDistances();
        Node[] path = new Node[nodes.length];
        int lastNodeIndex = 0;
        path[0] = nodes[0];
        // Debugging.runUIWithEmptyGraph(path);
        // Loop at Path to fill in all Nodes
        for (int i = 1; i < path.length; i++) {
            int furthestIndex = -1;
            double furthestDistance = -1;

            // Find Node Furthest Away from path[i - 1]
            for (int j = 1; j < nodes.length; j++) {
                double currentDistance = distances.getDistanceById(nodes[j], path[lastNodeIndex]);
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
                double currentDistance = simulateMerge(path, j, nodes[furthestIndex]).getTotalDistance();
                if (shortestIndex == -1 || (shortestDistance > currentDistance && currentDistance > 0)) {
                    shortestDistance = currentDistance;
                    shortestIndex = j;
                }
            }
            path = mergeNodeIntoGraph(path, nodes[furthestIndex], shortestIndex);
            lastNodeIndex = shortestIndex;
            // Debugging.runUIWithEmptyGraph(path);
        }
        Graph returnGraph = new Graph(path);
        return returnGraph;
    }

    /**
     * Method responsible for handling crossovers. Should return a crossover free
     * graph
     * 
     * @param graph Graph with (or without crossovers)
     * @return Returns a Graph (hopefully without crossover)
     */
    private static Graph handleCrossover(Graph graph) {
        graphChanged = false;
        Node[] nodes = graph.getNodes();

        // Probably unnecessary
        for (Node n : nodes) {
            if (n == null) {
                System.out.println("(Partly) Empty graph");
                System.exit(1);
            }
        }

        for (int i = 1; i < nodes.length; i++) {
            for (int j = 1; j < nodes.length; j++) {
                nodes = graph.getNodes();

                Node nodeA1 = nodes[i - 1];
                Node nodeA2 = nodes[i];
                Node nodeB1 = nodes[j - 1];
                Node nodeB2 = nodes[j];

                // Skipping some constellation of Nodes since they cant contain a crossover (4
                // unique Nodes are Required)
                if (nodeA1.compareTo(nodeB1) != 0 && nodeA1.compareTo(nodeB2) != 0 && nodeA2.compareTo(nodeB1) != 0
                        && nodeA2.compareTo(nodeB2) != 0 && nodeB1.compareTo(graph.getFirstNode()) != 0) {
                    // if(i != j) {

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
                    Point intersect = new Point(intersectX, intersectY);

                    if (crossoverInRect(nodeA1, nodeA2, intersect) && crossoverInRect(nodeB1, nodeB2, intersect)) {
                        graph = removeCrossover(graph, nodeA2, nodeB1);
                        graphChanged = true;
                    }
                }
            }
        }
        recursionCounter++;
        // Avoid endless recursion (Happened sometimes)
        if (graphChanged && recursionCounter < 100) {
            graph = handleCrossover(graph);
        }
        return graph;
    }

    /**
     * Responsible for removing a crossover from a Graph detected by
     * {@link Optimizer#handleCrossover(Graph)}
     * 
     * @param graph    Graph that the crossover is on
     * @param nodeFrom First relevant Node of the Crossover
     * @param nodeTo   Last relevant Node of the crossover
     * @return Returns the graph passed to it with the crossover resolved
     */
    private static Graph removeCrossover(Graph graph, Node nodeFrom, Node nodeTo) {
        int indexFrom = graph.findNode(nodeFrom);
        int indexTo = graph.findNode(nodeTo);

        // Make sure that indexFrom is smaller than indexTo
        if (indexFrom > indexTo) {
            Node tempNode = nodeFrom;
            nodeFrom = nodeTo;
            nodeTo = tempNode;
        }

        // reverse the order of all Nodes between the two relevant Nodes (nodeFrom,
        // nodeTo)
        while (nodeFrom != null && nodeTo != null && nodeFrom.getID() != nodeTo.getID()
                && graph.findNode(nodeFrom) < graph.findNode(nodeTo)) {

            graph.swapNodes(nodeFrom, nodeTo);
            indexTo = graph.findNode(nodeFrom);
            nodeFrom = graph.getNextNode(nodeTo);
            nodeTo = graph.getNodes()[indexTo - 1];

        }
        return graph;
    }

    /**
     * Applies the afterControl Algorithm to a graph
     * 
     * @param graph Graph the algorithm is applied to
     * @return Returns a Graph with lower distance then before
     */
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
                if (i == j) {
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
                        // Loop backwards
                        for (int k = i; k > j - 1; k--) {
                            graph.swapNodes(node, graph.getNodes()[k]);
                        }
                    }
                }
            }
        }

        return graph;
    }

    /**
     * Merges a given Node into the path (Node array)
     * 
     * @param path  Node array that {@code node} is merged into
     * @param node  Node to be merged
     * @param index Index {@code node} will end up at
     * @return returns path with the merged Node
     */
    private static Node[] mergeNodeIntoGraph(Node[] path, Node node, int index) {
        for (int i = path.length - 2; i >= index; i--) {
            path[i + 1] = path[i];
        }
        path[index] = node;
        return path;
    }

    /**
     * Checks whether a Node Array contains a certain Node (check is done via the
     * {@code ID} property)
     * 
     * @param path Nodes Array
     * @param node Node to be checked
     * @return Returns true if {@code path} contains {@code node}
     */
    private static boolean pathContainsNode(Node[] path, Node node) {
        for (Node n : path) {
            if (node.getID() == (n != null ? n.getID() : -1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Does the same as {@code Optimizer#mergeNodeIntoGraph(Node[], Node, int)}
     * 
     * @param nodes Node Array
     * @param index Index to simulate merge at (see
     *              {@code Optimizer.mergeNodeIntoGraph(...)})
     * @param node  Node to merge into nodes (see above)
     * @return returns a Graph with the simulated merged Nodes Array
     */
    private static Graph simulateMerge(Node[] nodes, int index, Node node) {
        int numberOfNodes = 0;
        for (; numberOfNodes < nodes.length && nodes[numberOfNodes] != null; numberOfNodes++) {
        }
        Node[] path = new Node[numberOfNodes + 1];
        for (int i = 0; i < numberOfNodes; i++) {
            path[i] = nodes[i];
        }
        path = mergeNodeIntoGraph(path, node, index);
        return new Graph(path);
    }

    /**
     * Checks if a Point p is inside the rectangle created by Nodes a and b
     * 
     * @param a First Node of the rectangle
     * @param b Second Node of the rectangle
     * @param p Point to be checked for
     * @return returns true if p is inside the rectangle otherwise returns false
     */
    private static boolean crossoverInRect(Node a, Node b, Point p) {
        int score = 0;
        // Check X
        if (a.getX() > b.getX()) {
            // a is further to the right than b
            if (p.getX() > b.getX() && p.getX() < a.getX()) {
                score++;
            }
        } else {
            // a is further to the left than b
            if (p.getX() < b.getX() && p.getX() > a.getX()) {
                score++;
            }
        }

        if (a.getY() > b.getY()) {
            // a is further down than b
            if (p.getY() < a.getY() && p.getY() > b.getY()) {
                score++;
            }
        } else {
            // a is further up than b
            if (p.getY() > a.getY() && p.getY() < b.getY()) {
                score++;
            }
        }
        // Little bit weird but it works
        // Avoids doing overly complicated checks
        // Of course all of the above statements could be the return value
        // But that isn't readable
        return score == 2;
    }
}