package com.example.mst.algorithms;

import com.example.mst.model.Edge;
import com.example.mst.model.Graph;

import java.util.*;

/**
 * Implementation of Kruskal's algorithm to find the Minimum Spanning Tree.
 * Uses Union-Find (Disjoint Set) data structure to detect cycles.
 */
public class KruskalAlgorithm {
    private final List<Edge> mstEdges;
    private int totalCost;
    private int operationsCount;
    private double executionTimeMs;

    public KruskalAlgorithm() {
        this.mstEdges = new ArrayList<>();
        this.totalCost = 0;
        this.operationsCount = 0;
        this.executionTimeMs = 0.0;
    }

    /**
     * Execute Kruskal's algorithm on the given graph.
     * @param graph Input graph to find MST
     */
    public void execute(Graph graph) {
        long startTime = System.nanoTime();

        mstEdges.clear();
        totalCost = 0;
        operationsCount = 0;

        List<String> nodes = graph.getNodes();
        List<Edge> edges = graph.getEdges();

        if (nodes.isEmpty() || edges.isEmpty()) {
            executionTimeMs = (System.nanoTime() - startTime) / 1_000_000.0;
            return;
        }

        // Initialize Union-Find structure
        UnionFind uf = new UnionFind(nodes);

        // Sort edges by weight (ascending order)
        List<Edge> sortedEdges = new ArrayList<>(edges);
        Collections.sort(sortedEdges);
        operationsCount += sortedEdges.size(); // Sorting operation count

        // Process edges in order of increasing weight
        for (Edge edge : sortedEdges) {
            operationsCount++; // Edge processing

            String node1 = edge.getFrom();
            String node2 = edge.getTo();

            // Check if adding this edge creates a cycle
            if (!uf.connected(node1, node2)) {
                // No cycle, add edge to MST
                mstEdges.add(edge);
                totalCost += edge.getWeight();
                uf.union(node1, node2);
                operationsCount += 2; // Union and cost calculation

                // Stop if we have V-1 edges (complete MST)
                if (mstEdges.size() == nodes.size() - 1) {
                    break;
                }
            }
            operationsCount++; // Comparison operation
        }

        operationsCount += uf.getOperationsCount();
        executionTimeMs = (System.nanoTime() - startTime) / 1_000_000.0;
    }

    public List<Edge> getMstEdges() {
        return new ArrayList<>(mstEdges);
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getOperationsCount() {
        return operationsCount;
    }

    public double getExecutionTimeMs() {
        return executionTimeMs;
    }

    /**
     * Print the MST result in a readable format.
     */
    public void printResult() {
        System.out.println("\n=== Kruskal's Algorithm Result ===");
        System.out.println("MST Edges:");
        for (Edge edge : mstEdges) {
            System.out.println("  " + edge);
        }
        System.out.println("Total Cost: " + totalCost);
        System.out.println("Operations Count: " + operationsCount);
        System.out.printf("Execution Time: %.2f ms%n", executionTimeMs);
    }

    /**
     * Union-Find (Disjoint Set) data structure for cycle detection.
     * Uses path compression and union by rank for efficiency.
     */
    private static class UnionFind {
        private final Map<String, String> parent;
        private final Map<String, Integer> rank;
        private int operationsCount;

        /**
         * Initialize Union-Find with all nodes as separate sets.
         * @param nodes List of all nodes in the graph
         */
        public UnionFind(List<String> nodes) {
            parent = new HashMap<>();
            rank = new HashMap<>();
            operationsCount = 0;

            for (String node : nodes) {
                parent.put(node, node); // Each node is its own parent initially
                rank.put(node, 0);
                operationsCount++;
            }
        }

        /**
         * Find the root of the set containing the node (with path compression).
         * @param node Node to find root for
         * @return Root of the set
         */
        public String find(String node) {
            operationsCount++;
            if (!parent.get(node).equals(node)) {
                // Path compression: make node point directly to root
                parent.put(node, find(parent.get(node)));
                operationsCount++;
            }
            return parent.get(node);
        }

        /**
         * Check if two nodes are in the same set.
         * @param node1 First node
         * @param node2 Second node
         * @return True if connected, false otherwise
         */
        public boolean connected(String node1, String node2) {
            operationsCount++;
            return find(node1).equals(find(node2));
        }

        /**
         * Union two sets (by rank).
         * @param node1 First node
         * @param node2 Second node
         */
        public void union(String node1, String node2) {
            String root1 = find(node1);
            String root2 = find(node2);
            operationsCount++;

            if (root1.equals(root2)) {
                return; // Already in same set
            }

            // Union by rank: attach smaller tree under larger tree
            int rank1 = rank.get(root1);
            int rank2 = rank.get(root2);

            if (rank1 < rank2) {
                parent.put(root1, root2);
            } else if (rank1 > rank2) {
                parent.put(root2, root1);
            } else {
                parent.put(root2, root1);
                rank.put(root1, rank1 + 1);
            }
            operationsCount += 2;
        }

        public int getOperationsCount() {
            return operationsCount;
        }
    }
}