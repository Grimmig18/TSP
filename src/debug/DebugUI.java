package debug;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import app.DistanceMatrix;
import app.Graph;
import app.Node;

/**
 * DebugUI
 */
public class DebugUI {

    public static void runUI(Graph graph, String title, DistanceMatrix distances) {
        graph.setDistances(distances);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initializeUI(graph.getNodes(), title);
            }
        });
    }

    private static void initializeUI(Node[] nodes, String title) {
        SwingUtilities.isEventDispatchThread();
        JFrame f = new JFrame(title);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new DebugPanel(nodes));
        f.pack();
        f.setVisible(true);
    }
}