package com.example.mst.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an undirected weighted graph using adjacency list representation.
 *
 * This class demonstrates proper OOP design:
 * - Encapsulation: Private data structures with controlled access
 * - Single Responsibility: Graph structure management
 * - Validation: Input validation in constructor and methods
 * - Utility Methods: Rich API for graph operations
 *
 * Data Structure:
 * - Adjacency List: HashMap<Node, List<Edge>> for O(1) neighbor lookup
 * - Edge List: List<Edge> for algorithms that need all edges
 * - Node Set: List<String> for vertex enumeration
 *
 * @author MST Assignment
 * @version 1.0
 */
public class Graph {
    private final int id;
    private final List<String> nodes;
    private final List<Edge> edges;
    private final Map<String, List<Edge>> adjacencyList;
    private final Map<String, Integer> nodeIndices;

    /**
     * Constructor to create a graph from nodes and edges.
     * Validates input and builds internal data structures.
     *
     * @param id Unique identifier for this graph
     * @param nodes List of node names (must be non-empty, no duplicates)
     * @param edges List of edges connecting nodes
     * @throws IllegalArgumentException if input is invalid
     */
    public Graph(int id, List<String> nodes, List<Edge> edges) {
        validateInput(id, nodes, edges);

        this.id = id;
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>(edges);
        this.adjacencyList = new HashMap<>();
        this.nodeIndices = new HashMap<>();

        buildDataStructures();
    }

    /**
     * Validate input parameters for graph construction.
     */
    private void validateInput(int id, List<String> nodes, List<Edge> edges) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("Graph must have at least one node");
        }

        // Check for duplicate nodes
        Set<String> uniqueNodes = new HashSet<>(nodes);
        if (uniqueNodes.size() != nodes.size()) {
            throw new IllegalArgumentException("Duplicate nodes are not allowed");
        }

        if (edges == null) {
            throw new IllegalArgumentException("Edges list cannot be null");
        }

        // Validate that all edges reference existing nodes
        Set<String> nodeSet = new HashSet<>(nodes);
        for (Edge edge : edges) {
            if (!nodeSet.contains(edge.getFrom())) {
                throw new IllegalArgumentException("Edge references non-existent node: " + edge.getFrom());
            }
            if (!nodeSet.contains(edge.getTo())) {
                throw new IllegalArgumentException("Edge references non-existent node: " + edge.getTo());
            }
        }
    }

    /**
     * Build internal data structures for efficient graph operations.
     * - Adjacency list for neighbor lookup
     * - Node indices for array-based operations
     */
    private void buildDataStructures() {
        // Initialize adjacency list and node indices
        for (int i = 0; i < nodes.size(); i++) {
            String node = nodes.get(i);
            adjacencyList.put(node, new ArrayList<>());
            nodeIndices.put(node, i);
        }

        // Build adjacency list (both directions for undirected graph)
        for (Edge edge : edges) {
            // Add edge from source to destination
            adjacencyList.get(edge.getFrom()).add(edge);

            // Add reverse edge for undirected graph
            adjacencyList.get(edge.getTo()).add(edge.reverse());
        }
    }

    // ==================== Getters ====================

    /**
     * Get the unique identifier of this graph.
     * @return Graph ID
     */
    public int getId() {
        return id;
    }

    /**
     * Get all nodes in the graph.
     * Returns a defensive copy to prevent external modification.
     *
     * @return List of node names
     */
    public List<String> getNodes() {
        return new ArrayList<>(nodes);
    }

    /**
     * Get all edges in the graph.
     * Returns a defensive copy to prevent external modification.
     *
     * @return List of edges
     */
    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    /**
     * Get all edges connected to a specific node.
     *
     * @param node Node name
     * @return List of edges incident to the node (empty list if node doesn't exist)
     */
    public List<Edge> getAdjacentEdges(String node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    /**
     * Get all neighbors of a node.
     *
     * @param node Node name
     * @return List of neighboring node names
     */
    public List<String> getNeighbors(String node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>())
                .stream()
                .map(edge -> edge.getOther(node))
                .collect(Collectors.toList());
    }

    /**
     * Get the number of nodes (vertices) in the graph.
     * @return Vertex count
     */
    public int getNodeCount() {
        return nodes.size();
    }

    /**
     * Get the number of edges in the graph.
     * @return Edge count
     */
    public int getEdgeCount() {
        return edges.size();
    }

    /**
     * Get the degree (number of connections) of a node.
     *
     * @param node Node name
     * @return Degree of the node
     */
    public int getDegree(String node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>()).size();
    }

    // ==================== Graph Properties ====================

    /**
     * Check if the graph is connected.
     * A graph is connected if there is a path between any two nodes.
     * Uses Breadth-First Search (BFS) for traversal.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        if (nodes.isEmpty()) return true;

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        // Start BFS from first node
        String start = nodes.get(0);
        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            for (Edge edge : getAdjacentEdges(current)) {
                String neighbor = edge.getOther(current);
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }

        return visited.size() == nodes.size();
    }

    /**
     * Check if graph contains a specific node.
     *
     * @param node Node name to check
     * @return true if node exists, false otherwise
     */
    public boolean hasNode(String node) {
        return adjacencyList.containsKey(node);
    }

    /**
     * Check if there is an edge between two nodes.
     *
     * @param from First node
     * @param to Second node
     * @return true if edge exists, false otherwise
     */
    public boolean hasEdge(String from, String to) {
        return adjacencyList.getOrDefault(from, new ArrayList<>())
                .stream()
                .anyMatch(edge -> edge.connects(from, to));
    }

    /**
     * Get the edge between two nodes if it exists.
     *
     * @param from First node
     * @param to Second node
     * @return Edge object or null if no edge exists
     */
    public Edge getEdge(String from, String to) {
        return adjacencyList.getOrDefault(from, new ArrayList<>())
                .stream()
                .filter(edge -> edge.connects(from, to))
                .findFirst()
                .orElse(null);
    }

    /**
     * Calculate the density of the graph.
     * Density = 2 * |E| / (|V| * (|V| - 1))
     * Range: [0, 1] where 1 means complete graph
     *
     * @return Graph density
     */
    public double getDensity() {
        int v = getNodeCount();
        if (v <= 1) return 0.0;
        return (2.0 * getEdgeCount()) / (v * (v - 1));
    }

    /**
     * Get the total weight of all edges in the graph.
     *
     * @return Sum of all edge weights
     */
    public int getTotalWeight() {
        return edges.stream()
                .mapToInt(Edge::getWeight)
                .sum();
    }

    /**
     * Get the minimum edge weight in the graph.
     *
     * @return Minimum weight, or Integer.MAX_VALUE if no edges
     */
    public int getMinEdgeWeight() {
        return edges.stream()
                .mapToInt(Edge::getWeight)
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    /**
     * Get the maximum edge weight in the graph.
     *
     * @return Maximum weight, or Integer.MIN_VALUE if no edges
     */
    public int getMaxEdgeWeight() {
        return edges.stream()
                .mapToInt(Edge::getWeight)
                .max()
                .orElse(Integer.MIN_VALUE);
    }

    // ==================== Visualization Support ====================

    /**
     * Get a detailed string representation of the graph structure.
     * Useful for debugging and visualization.
     *
     * @return Detailed graph information
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph ").append(id).append(":\n");
        sb.append("  Nodes: ").append(nodes.size()).append(" ").append(nodes).append("\n");
        sb.append("  Edges: ").append(edges.size()).append("\n");
        sb.append("  Connected: ").append(isConnected()).append("\n");
        sb.append("  Density: ").append(String.format("%.2f", getDensity())).append("\n");
        sb.append("  Total Weight: ").append(getTotalWeight()).append("\n");
        sb.append("\n  Adjacency List:\n");

        for (String node : nodes) {
            sb.append("    ").append(node).append(" -> ");
            List<Edge> adjacent = getAdjacentEdges(node);
            sb.append(adjacent.stream()
                    .map(e -> e.getOther(node) + "(" + e.getWeight() + ")")
                    .collect(Collectors.joining(", ")));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Standard string representation.
     * Format: "Graph ID [nodes=N, edges=E]"
     */
    @Override
    public String toString() {
        return "Graph " + id + " [nodes=" + nodes.size() +
                ", edges=" + edges.size() +
                ", connected=" + isConnected() + "]";
    }

    /**
     * Get graph statistics as a formatted string.
     *
     * @return Statistics summary
     */
    public String getStatistics() {
        return String.format(
                "Graph Statistics:\n" +
                        "  ID: %d\n" +
                        "  Vertices: %d\n" +
                        "  Edges: %d\n" +
                        "  Connected: %s\n" +
                        "  Density: %.2f\n" +
                        "  Min Edge Weight: %d\n" +
                        "  Max Edge Weight: %d\n" +
                        "  Total Weight: %d",
                id, getNodeCount(), getEdgeCount(), isConnected(),
                getDensity(), getMinEdgeWeight(), getMaxEdgeWeight(), getTotalWeight()
        );
    }
}