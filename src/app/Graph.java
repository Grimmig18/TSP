package app;


/**
 * Graph
 */
public class Graph {
    private Node[] nodes;
    private DistanceMatrix distances;

    public Graph(Node[] nodes) {
        this.nodes = nodes;
        this.distances = new DistanceMatrix(this);
    }

    public Graph(int length) {
        this.nodes = new Node[length];
    }

    public Node getFirstNode(){
        if(nodes.length == 0) {
            return null;
        }
        return nodes[0];
    }
 
    public Node[] getNodes() {
        if(nodes.length == 0) {
            return null;
        }
        return nodes;
    }


    public double getTotalDistance() {
        if(nodes.length == 0) {
            return -1;
        }
        double totalDistance = 0;
        for(int i = 1; i < nodes.length; i++) {
            totalDistance += distances.getDistanceById(nodes[i-1], nodes[i]);
        }

        return totalDistance;
    }


    /**
     * Returns the Index of a given node. <br />
     * If the Node can't be found -1 is returned
     */
    public int findNode(Node node) {
        for(int i = 0; i < nodes.length; i++) {
            if(node.getID() == nodes[i].getID()) {
                return i;
            }
        }
        return -1;
    }

    /* Add Some stuff to manipulate the nodes */

    public void swapNodes(Node a, Node b) {
        int indexOfA = findNode(a);
        int indexOfB = findNode(b);

        Node tempA = nodes[indexOfA];
        nodes[indexOfA] = nodes[indexOfB];
        nodes[indexOfB] = tempA;
    }

    public Node getNextNode(Node prevNode) {
        int index = findNode(prevNode);
        return index != nodes.length - 1 ? nodes[index + 1] : null;
    }

    public Node getPrevNode(Node sucNode) {
        int index = findNode(sucNode);
        return index != 0 ? nodes[index - 1] : null;
    }
    
    public DistanceMatrix getDistances() {
        return this.distances;
    }

}