package app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * UI
 */
public class UI {
    public static void runUI(Graph graph, String title) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initializeUI(graph.getNodes(), graph.getTotalDistance(), title);
            }
        });
    }

    private static void initializeUI(Node[] nodes, double totalDistance, String title) {
        SwingUtilities.isEventDispatchThread();
        JFrame f = new JFrame(title);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new MyPanel(nodes, totalDistance));
        f.pack();
        f.setVisible(true);
    }
    
}