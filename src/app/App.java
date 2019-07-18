package app;

import java.io.FileWriter;

public class App {
    public final static int range = 10;
    public static void main(String[] args) throws Exception {
        System.out.println("Hello Java");

        Node[] nodes = new Node[30];

        IdSetter.resetIdCounter();
        for(int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node();
        }
        // Graph graph = new Graph(nodes);
        Graph[] solutions = new Graph[12];
        FileWriter writer = new FileWriter("C:\\Users\\d073426\\Documents\\DHBW\\Projektarbeit\\Programme\\TSP\\data.txt", true);

        // nodes[0] = new Node((Math.random() * range), (Math.random() * range), null);

        // for(int i = 1; i < nodes.length; i++) {
        //     nodes[i] = new Node();
        // }


        // Get all different solutions in one array of Graphs

        solutions[0] = Optimizer.optimize(new Graph(nodes), Strategy.FIRST, false, false);
        solutions[1] = Optimizer.optimize(new Graph(nodes), Strategy.FIRST, false, true);
        solutions[2] = Optimizer.optimize(new Graph(nodes), Strategy.FIRST, true, false);
        solutions[3] = Optimizer.optimize(new Graph(nodes), Strategy.FIRST, true, true);

        solutions[4] = Optimizer.optimize(new Graph(nodes), Strategy.CLOSEST, false, false);
        solutions[5] = Optimizer.optimize(new Graph(nodes), Strategy.CLOSEST, false, true);
        solutions[6] = Optimizer.optimize(new Graph(nodes), Strategy.CLOSEST, true, false);
        solutions[7] = Optimizer.optimize(new Graph(nodes), Strategy.CLOSEST, true, true);

        solutions[8] = Optimizer.optimize(new Graph(nodes), Strategy.FURTHEST, false, false);
        solutions[9] = Optimizer.optimize(new Graph(nodes), Strategy.FURTHEST, false, true);
        solutions[10] = Optimizer.optimize(new Graph(nodes), Strategy.FURTHEST, true, false);
        solutions[11] = Optimizer.optimize(new Graph(nodes), Strategy.FURTHEST, true, true);

        String dataOutput = "";
        int lowestIndex = -1;
        double lowestDistance = -1;
        for(int i = 0; i < solutions.length; i++) {
            dataOutput += solutions[i].getTotalDistance() + " ";
            if(lowestIndex == -1 || solutions[i].getTotalDistance() < lowestDistance) {
                lowestIndex = i;
                lowestDistance = solutions[i].getTotalDistance();
            }
        }
        dataOutput += " " + lowestIndex;
        try {
            writer.write(dataOutput + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

        writer.close();
        UI.runUI(solutions[lowestIndex], "Best Solution");

        // detect if the optimizing process changed any starting nodes
        for(int i = 1; i < solutions.length; i++) {
            if(solutions[i].getFirstNode().compareTo(solutions[i - 1].getFirstNode()) != 0) {
                throw new Exception("The first Nodes of the Graphs " + i + " and " + (i - 1) + " are different");
            }
        }
        // UI.runUI(solutions[0], "First");
        // UI.runUI(solutions[4], "Closest");
        // UI.runUI(solutions[8], "Furthest");
        // UI.runUI(solutions[6], "Closest, True, False");
        // UI.runUI(solutions[4], "Closest, False, False");
        // UI.runUI(solutions[5], "Closest, False, True");
        // UI.runUI(solutions[7], "Closest, True, True");
    }
}