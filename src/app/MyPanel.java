package app;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

class MyPanel extends JPanel {

    Node[] nodes;
    double totalDistance;
    int dimensionX = 750;
    int dimensionY = 750;
    int[][] linePath; 

    private static final int width = 16;
    private static final int height = 16;

    public MyPanel(Node[] nodes, double totalDistance) {
        setBorder(BorderFactory.createLineBorder(Color.black));
        this.nodes = nodes;
        this.totalDistance = totalDistance;
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
            if(i == 0) {
                g.setColor(Color.RED);
            }
            g.fillOval(newX, newY, width, height);
            g.setColor(Color.BLUE);

        }

        // Draw Lines
        g.setColor(Color.BLACK);
        for(int i = 1; i < linePath.length; i++) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));
            g2.draw(new Line2D.Float(linePath[i - 1][0], linePath[i - 1][1], linePath[i][0], linePath[i][1]));
        }

        // Draw Text
        // g.drawOval(400, 400, 16, 16);
        // Round Total Distance
        totalDistance = Math.round(totalDistance * 1000.0) / 1000.0;
        g.drawString("Total Distance: " + totalDistance, 10, 20);
    }
}