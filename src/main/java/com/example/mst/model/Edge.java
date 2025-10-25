package com.example.mst.model;

/**
 * Represents an edge in an undirected weighted graph.
 * Each edge connects two nodes and has a weight representing the cost.
 *
 * This class implements proper OOP principles:
 * - Encapsulation: Private fields with public getters
 * - Immutability: All fields are final
 * - Comparable: Natural ordering by weight for sorting
 *
 * @author MST Assignment
 * @version 1.0
 */
public class Edge implements Comparable<Edge> {
    private final String from;
    private final String to;
    private final int weight;

    /**
     * Constructor to create an edge.
     * Validates input to ensure edge integrity.
     *
     * @param from Starting node (cannot be null or empty)
     * @param to Ending node (cannot be null or empty)
     * @param weight Cost of the edge (must be positive)
     * @throws IllegalArgumentException if parameters are invalid
     */
    public Edge(String from, String to, int weight) {
        if (from == null || from.trim().isEmpty()) {
            throw new IllegalArgumentException("From node cannot be null or empty");
        }
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("To node cannot be null or empty");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Weight must be non-negative");
        }
        if (from.equals(to)) {
            throw new IllegalArgumentException("Self-loops are not allowed");
        }

        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    /**
     * Get the starting node of this edge.
     * @return Starting node name
     */
    public String getFrom() {
        return from;
    }

    /**
     * Get the ending node of this edge.
     * @return Ending node name
     */
    public String getTo() {
        return to;
    }

    /**
     * Get the weight (cost) of this edge.
     * @return Edge weight
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Get the other endpoint of this edge.
     * Useful for traversing undirected graphs.
     *
     * @param node One endpoint of the edge
     * @return The other endpoint
     * @throws IllegalArgumentException if node is not part of this edge
     */
    public String getOther(String node) {
        if (from.equals(node)) {
            return to;
        } else if (to.equals(node)) {
            return from;
        }
        throw new IllegalArgumentException("Node " + node + " is not part of this edge");
    }

    /**
     * Check if this edge connects to the specified node.
     *
     * @param node Node to check
     * @return true if edge connects to node, false otherwise
     */
    public boolean connectsTo(String node) {
        return from.equals(node) || to.equals(node);
    }

    /**
     * Check if this edge connects two specific nodes (in any order).
     *
     * @param node1 First node
     * @param node2 Second node
     * @return true if edge connects these nodes, false otherwise
     */
    public boolean connects(String node1, String node2) {
        return (from.equals(node1) && to.equals(node2)) ||
                (from.equals(node2) && to.equals(node1));
    }

    /**
     * Compare edges by weight for sorting (used in Kruskal's algorithm).
     * Edges with lower weights come first.
     *
     * @param other Another edge to compare
     * @return Negative if this edge is lighter, positive if heavier, 0 if equal
     */
    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    /**
     * String representation of the edge for debugging and output.
     * Format: "from -- to [weight: X]"
     *
     * @return String representation
     */
    @Override
    public String toString() {
        return from + " -- " + to + " [weight: " + weight + "]";
    }

    /**
     * Check equality based on endpoints and weight.
     * Since graph is undirected, (A,B,5) equals (B,A,5).
     *
     * @param obj Object to compare
     * @return true if edges are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Edge edge = (Edge) obj;
        return weight == edge.weight &&
                ((from.equals(edge.from) && to.equals(edge.to)) ||
                        (from.equals(edge.to) && to.equals(edge.from)));
    }

    /**
     * Hash code for edge.
     * Order-independent hash for undirected edges.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        // Order-independent hash for undirected edges
        return from.hashCode() + to.hashCode() + weight * 31;
    }

    /**
     * Create a copy of this edge.
     *
     * @return New edge with same properties
     */
    public Edge copy() {
        return new Edge(from, to, weight);
    }

    /**
     * Create a reversed edge (swap from and to).
     * Useful for building adjacency lists in undirected graphs.
     *
     * @return New edge with reversed endpoints
     */
    public Edge reverse() {
        return new Edge(to, from, weight);
    }
}