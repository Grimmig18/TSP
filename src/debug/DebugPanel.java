package debug;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import app.App;
import app.Node;

/**
 * DebugsPanel
 */
public class DebugPanel extends JPanel{

    Node[] nodes;
    int dimensionX = 750;
    int dimensionY = 750;
    int[][] linePath; 

    private static final int width = 16;
    private static final int height = 16;

    public DebugPanel(Node[] nodes) {
        setBorder(BorderFactory.createLineBorder(Color.black));
        this.nodes = nodes;
        this.linePath = new int[nodes.length][2];
    }

    public Dimension getPreferredSize() {
        return new Dimension(dimensionX, dimensionY);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw Nodes
        for(int i = 0; i < nodes.length; i++) {
            // A - B
            // D - C
            // X -> Y
            // Y = (X-A)/(B-A) * (D-C) + C
            int newX = (int) Math.round((nodes[i].getX() - 0) / (App.range - 0) * (dimensionX - 0) + 0);
            int newY = (int) Math.round((nodes[i].getY() - 0) / (App.range - 0) * (dimensionY - 0) + 0);
            // System.out.println(nodes[i].getX());
            linePath[i][0] = newX + 8;
            linePath[i][1] = newY + 8;
            // System.out.println("X: " + newX + ", Y: " + newY);
            // g.drawOval(newX, newY, width, height);
            g.fillOval(newX, newY, width, height);

        }

        // Draw Lines
        for(int i = 1; i < linePath.length; i++) {
            if(i != 2) {
                g.drawLine(linePath[i - 1][0], linePath[i - 1][1], linePath[i][0], linePath[i][1]);
            }
        }

        // Draw Text
        // g.drawOval(400, 400, 16, 16);
        // Round Total Distance
        // totalDistance = Math.round(totalDistance * 1000.0) / 1000.0;
        // g.drawString("Total Distance: " + totalDistance, 10, 20);
        Scanner waiter = new Scanner(System.in);
        waiter.next();
        waiter.close();
        this.setVisible(false);
    }
}