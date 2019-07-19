package app;

/**
 * DistanceMatrix
 */
public class DistanceMatrix {

    // private static int[] rowHeader;
    // private static int[] columnHeader;

    // private static double[][] distances;

    public DistanceMatrix(Graph graph) {
        // Node[] nodes = graph.getNodes();
        // // rowHeader = new int[nodes.length];
        // // columnHeader = new int[nodes.length];
        // distances = new double[nodes.length][nodes.length];

        // for (int i = 0; i < nodes.length; i++) {
        //     for (int j = 0; j < nodes.length; j++) {
        //         double distance = Math.sqrt(Math.pow(Math.abs((nodes[i].getX() - nodes[j].getX())), 2)
        //                 + Math.pow(Math.abs((nodes[i].getY() - nodes[j].getY())), 2));
        //         distances[nodes[i].getID()][nodes[j].getID()] = distance;
        //         // if (i == 0) {
        //         //     columnHeader[j] = nodes[j].getID();
        //         // }
        //     }
        //     // rowHeader[i] = nodes[i].getID();
        // }
    }

    public double getDistanceById(Node a, Node b) {

        return Math.sqrt(Math.pow(Math.abs((a.getX() - b.getX())), 2)
            + Math.pow(Math.abs((a.getY() - b.getY())), 2));
        // try {
        //     return distances[a.getID()][b.getID()];
        // } catch (ArrayIndexOutOfBoundsException e) {
        //     e.printStackTrace();
        //     System.out.println(a.getID() + " " + b.getID());
        //     System.out.println(distances.length);
        //     System.out.println(distances[0].length);
        // }
    }

}