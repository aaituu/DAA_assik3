package com.example.mst.visualization;

import com.example.mst.model.Edge;
import com.example.mst.model.Graph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Visualizer for graphs and MST results.
 * Creates images showing graph structure and MST edges highlighted.
 *
 * Features:
 * - Circular layout for node positioning
 * - Color-coded edges (MST edges highlighted)
 * - Node labels and edge weights displayed
 * - Export to PNG format
 *
 * @author MST Assignment - Bonus Section
 * @version 1.0
 */
public class GraphVisualizer {

    // Image dimensions
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final int MARGIN = 100;

    // Visual settings
    private static final int NODE_RADIUS = 30;
    private static final int EDGE_THICKNESS = 2;
    private static final int MST_EDGE_THICKNESS = 4;

    // Colors
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color NODE_COLOR = new Color(70, 130, 180);
    private static final Color NODE_TEXT_COLOR = Color.WHITE;
    private static final Color EDGE_COLOR = new Color(200, 200, 200);
    private static final Color MST_EDGE_COLOR = new Color(255, 69, 0);
    private static final Color WEIGHT_COLOR = Color.BLACK;

    /**
     * Visualize the original graph structure.
     *
     * @param graph Graph to visualize
     * @param outputPath Path to save the image
     * @throws IOException If image cannot be saved
     */
    public void visualizeGraph(Graph graph, String outputPath) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Enable anti-aliasing for smooth rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fill background
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Calculate node positions
        Map<String, Point> nodePositions = calculateNodePositions(graph);

        // Draw title
        drawTitle(g2d, "Graph " + graph.getId() + " - Original Structure");

        // Draw edges first (so nodes appear on top)
        drawEdges(g2d, graph.getEdges(), nodePositions, EDGE_COLOR, EDGE_THICKNESS);

        // Draw nodes
        drawNodes(g2d, graph.getNodes(), nodePositions);

        // Draw graph statistics
        drawStatistics(g2d, graph);

        g2d.dispose();

        // Save image
        saveImage(image, outputPath);
    }

    /**
     * Visualize the graph with MST edges highlighted.
     *
     * @param graph Original graph
     * @param mstEdges MST edges to highlight
     * @param algorithmName Name of the algorithm (Prim/Kruskal)
     * @param totalCost Total cost of the MST
     * @param outputPath Path to save the image
     * @throws IOException If image cannot be saved
     */
    public void visualizeMST(Graph graph, List<Edge> mstEdges,
                             String algorithmName, int totalCost,
                             String outputPath) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fill background
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Calculate node positions
        Map<String, Point> nodePositions = calculateNodePositions(graph);

        // Draw title
        drawTitle(g2d, algorithmName + " MST - Cost: " + totalCost);

        // Draw all edges in gray
        drawEdges(g2d, graph.getEdges(), nodePositions, EDGE_COLOR, EDGE_THICKNESS);

        // Draw MST edges in red (highlighted)
        drawEdges(g2d, mstEdges, nodePositions, MST_EDGE_COLOR, MST_EDGE_THICKNESS);

        // Draw nodes
        drawNodes(g2d, graph.getNodes(), nodePositions);

        // Draw MST statistics
        drawMSTStatistics(g2d, mstEdges, totalCost);

        g2d.dispose();

        // Save image
        saveImage(image, outputPath);
    }

    /**
     * Calculate positions for nodes in a circular layout.
     * Nodes are evenly distributed around a circle.
     *
     * @param graph Graph to position
     * @return Map of node names to positions
     */
    private Map<String, Point> calculateNodePositions(Graph graph) {
        Map<String, Point> positions = new HashMap<>();
        List<String> nodes = graph.getNodes();
        int n = nodes.size();

        // Calculate circle parameters
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
        int radius = Math.min(WIDTH, HEIGHT) / 2 - MARGIN;

        // Position nodes evenly around the circle
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n - Math.PI / 2; // Start from top
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            positions.put(nodes.get(i), new Point(x, y));
        }

        return positions;
    }

    /**
     * Draw title at the top of the image.
     */
    private void drawTitle(Graphics2D g2d, String title) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, (WIDTH - titleWidth) / 2, 40);
    }

    /**
     * Draw edges between nodes.
     *
     * @param g2d Graphics context
     * @param edges List of edges to draw
     * @param positions Node positions
     * @param color Edge color
     * @param thickness Edge thickness
     */
    private void drawEdges(Graphics2D g2d, List<Edge> edges,
                           Map<String, Point> positions,
                           Color color, int thickness) {
        g2d.setStroke(new BasicStroke(thickness));
        g2d.setColor(color);

        for (Edge edge : edges) {
            Point p1 = positions.get(edge.getFrom());
            Point p2 = positions.get(edge.getTo());

            if (p1 != null && p2 != null) {
                // Draw line
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

                // Draw weight label
                int midX = (p1.x + p2.x) / 2;
                int midY = (p1.y + p2.y) / 2;
                drawWeightLabel(g2d, String.valueOf(edge.getWeight()), midX, midY);
            }
        }
    }

    /**
     * Draw weight label for an edge.
     */
    private void drawWeightLabel(Graphics2D g2d, String weight, int x, int y) {
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();

        // Draw background rectangle
        int labelWidth = fm.stringWidth(weight) + 8;
        int labelHeight = fm.getHeight();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x - labelWidth / 2, y - labelHeight / 2, labelWidth, labelHeight);

        // Draw weight text
        g2d.setColor(WEIGHT_COLOR);
        g2d.drawString(weight, x - fm.stringWidth(weight) / 2, y + fm.getAscent() / 2);
    }

    /**
     * Draw nodes as circles with labels.
     */
    private void drawNodes(Graphics2D g2d, List<String> nodes, Map<String, Point> positions) {
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();

        for (String node : nodes) {
            Point pos = positions.get(node);
            if (pos != null) {
                // Draw circle
                g2d.setColor(NODE_COLOR);
                g2d.fillOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                        NODE_RADIUS * 2, NODE_RADIUS * 2);

                // Draw border
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                        NODE_RADIUS * 2, NODE_RADIUS * 2);

                // Draw label
                g2d.setColor(NODE_TEXT_COLOR);
                int labelWidth = fm.stringWidth(node);
                g2d.drawString(node, pos.x - labelWidth / 2, pos.y + fm.getAscent() / 2);
            }
        }
    }

    /**
     * Draw graph statistics in the corner.
     */
    private void drawStatistics(Graphics2D g2d, Graph graph) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));

        int x = 20;
        int y = HEIGHT - 100;
        int lineHeight = 20;

        g2d.drawString("Vertices: " + graph.getNodeCount(), x, y);
        g2d.drawString("Edges: " + graph.getEdgeCount(), x, y + lineHeight);
        g2d.drawString("Connected: " + graph.isConnected(), x, y + lineHeight * 2);
        g2d.drawString("Density: " + String.format("%.2f", graph.getDensity()), x, y + lineHeight * 3);
    }

    /**
     * Draw MST statistics in the corner.
     */
    private void drawMSTStatistics(Graphics2D g2d, List<Edge> mstEdges, int totalCost) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));

        int x = 20;
        int y = HEIGHT - 80;
        int lineHeight = 20;

        g2d.drawString("MST Edges: " + mstEdges.size(), x, y);
        g2d.drawString("Total Cost: " + totalCost, x, y + lineHeight);

        // Draw legend
        g2d.setColor(EDGE_COLOR);
        g2d.setStroke(new BasicStroke(EDGE_THICKNESS));
        g2d.drawLine(WIDTH - 180, y - 10, WIDTH - 130, y - 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Original Edges", WIDTH - 120, y);

        g2d.setColor(MST_EDGE_COLOR);
        g2d.setStroke(new BasicStroke(MST_EDGE_THICKNESS));
        g2d.drawLine(WIDTH - 180, y + lineHeight - 10, WIDTH - 130, y + lineHeight - 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("MST Edges", WIDTH - 120, y + lineHeight);
    }

    /**
     * Save image to file.
     * Creates directory if it doesn't exist.
     *
     * @param image Image to save
     * @param outputPath File path
     * @throws IOException If file cannot be saved
     */
    private void saveImage(BufferedImage image, String outputPath) throws IOException {
        File outputFile = new File(outputPath);
        File parentDir = outputFile.getParentFile();

        // Create directory if needed
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        ImageIO.write(image, "PNG", outputFile);
    }

    /**
     * Create a comparison image showing both Prim's and Kruskal's MSTs side by side.
     *
     * @param graph Original graph
     * @param primEdges Prim's MST edges
     * @param kruskalEdges Kruskal's MST edges
     * @param primCost Prim's total cost
     * @param kruskalCost Kruskal's total cost
     * @param outputPath Path to save the image
     * @throws IOException If image cannot be saved
     */
    public void visualizeComparison(Graph graph,
                                    List<Edge> primEdges, List<Edge> kruskalEdges,
                                    int primCost, int kruskalCost,
                                    String outputPath) throws IOException {
        int compWidth = WIDTH * 2 + 40;
        int compHeight = HEIGHT;

        BufferedImage image = new BufferedImage(compWidth, compHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fill background
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, compWidth, compHeight);

        // Calculate node positions
        Map<String, Point> nodePositions = calculateNodePositions(graph);

        // Draw separator line
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(WIDTH + 20, 0, WIDTH + 20, compHeight);

        // Left side: Prim's MST
        drawMSTPanel(g2d, graph, primEdges, "Prim's Algorithm", primCost, nodePositions, 0);

        // Right side: Kruskal's MST
        Map<String, Point> rightPositions = new HashMap<>();
        for (Map.Entry<String, Point> entry : nodePositions.entrySet()) {
            rightPositions.put(entry.getKey(),
                    new Point(entry.getValue().x + WIDTH + 40, entry.getValue().y));
        }
        drawMSTPanel(g2d, graph, kruskalEdges, "Kruskal's Algorithm", kruskalCost, rightPositions, WIDTH + 40);

        g2d.dispose();
        saveImage(image, outputPath);
    }

    /**
     * Draw a single MST panel for comparison view.
     */
    private void drawMSTPanel(Graphics2D g2d, Graph graph, List<Edge> mstEdges,
                              String title, int cost, Map<String, Point> positions, int offsetX) {
        // Draw title
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, offsetX + (WIDTH - titleWidth) / 2, 40);

        // Draw cost
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        String costStr = "Total Cost: " + cost;
        int costWidth = g2d.getFontMetrics().stringWidth(costStr);
        g2d.drawString(costStr, offsetX + (WIDTH - costWidth) / 2, 65);

        // Draw all edges in gray
        drawEdges(g2d, graph.getEdges(), positions, EDGE_COLOR, EDGE_THICKNESS);

        // Draw MST edges highlighted
        drawEdges(g2d, mstEdges, positions, MST_EDGE_COLOR, MST_EDGE_THICKNESS);

        // Draw nodes
        drawNodes(g2d, graph.getNodes(), positions);
    }
}