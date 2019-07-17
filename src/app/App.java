package app;

import java.io.FileWriter;

public class App {
    public final static int range = 10;
    public static void main(String[] args) throws Exception {
        System.out.println("Hello Java");

        Node[] nodes = new Node[10];

        IdSetter.resetIdCounter();
        for(int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node();
        }
        Graph graph = new Graph(nodes);
        Graph[] solutions = new Graph[12];
        FileWriter writer = new FileWriter("C:\\Users\\d073426\\Documents\\DHBW\\Projektarbeit\\Programme\\TSP\\data.txt", true);

        // nodes[0] = new Node((Math.random() * range), (Math.random() * range), null);

        // for(int i = 1; i < nodes.length; i++) {
        //     nodes[i] = new Node();
        // }


        // Get all different solutions in one array of Graphs

        solutions[0] = Optimizer.optimize(graph, 0, false, false);
        solutions[1] = Optimizer.optimize(graph, 0, false, true);
        solutions[2] = Optimizer.optimize(graph, 0, true, false);
        solutions[3] = Optimizer.optimize(graph, 0, true, true);

        solutions[4] = Optimizer.optimize(graph, 1, false, false);
        solutions[5] = Optimizer.optimize(graph, 1, false, true);
        solutions[6] = Optimizer.optimize(graph, 1, true, false);
        solutions[7] = Optimizer.optimize(graph, 1, true, true);

        solutions[8] = Optimizer.optimize(graph, 2, false, false);
        solutions[9] = Optimizer.optimize(graph, 2, false, true);
        solutions[10] = Optimizer.optimize(graph, 2, true, false);
        solutions[11] = Optimizer.optimize(graph, 2, true, true);

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
        try {
            writer.write(dataOutput + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

        writer.close();
        UI.runUI(solutions[lowestIndex].getNodes(), lowestDistance);
    }
}