package com.example.mst.algorithms;

import com.example.mst.model.Edge;
import com.example.mst.model.Graph;

import java.util.*;

/**
 * Implementation of Prim's algorithm that produces a spanning forest.
 * Handles disconnected graphs by running Prim from every unvisited node.
 */
public class PrimAlgorithm {
    private final List<Edge> mstEdges;
    private int totalCost;
    private int operationsCount;
    private double executionTimeMs;

    public PrimAlgorithm() {
        this.mstEdges = new ArrayList<>();
        this.totalCost = 0;
        this.operationsCount = 0;
        this.executionTimeMs = 0.0;
    }

    /**
     * Execute Prim's algorithm on the given graph.
     * This implementation builds a Minimum Spanning Forest when the graph is disconnected.
     *
     * @param graph Input graph to find MST (or forest)
     */
    public void execute(Graph graph) {
        long startTime = System.nanoTime();

        mstEdges.clear();
        totalCost = 0;
        operationsCount = 0;

        List<String> nodes = graph.getNodes();
        if (nodes == null || nodes.isEmpty()) {
            executionTimeMs = (System.nanoTime() - startTime) / 1_000_000.0;
            return;
        }

        // Track visited nodes across all components
        Set<String> visited = new HashSet<>();

        // Process each connected component separately
        for (String startNode : nodes) {
            if (visited.contains(startNode)) {
                continue; // this node already covered by previous Prim run
            }

            // Mark start of a new component
            visited.add(startNode);
            operationsCount++; // mark visited operation

            // Min-heap of edges by weight for this component
            PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(Edge::getWeight));

            // Add all edges outgoing from the start node
            List<Edge> startAdj = graph.getAdjacentEdges(startNode);
            if (startAdj != null) {
                for (Edge e : startAdj) {
                    pq.offer(e);
                    operationsCount++; // push to queue
                }
            }

            // Run Prim for this connected component
            while (!pq.isEmpty() && visited.size() < nodes.size()) {
                Edge minEdge = pq.poll();
                operationsCount++; // poll operation

                String nextNode = minEdge.getTo();

                // If destination is already visited, skip (would form cycle)
                if (visited.contains(nextNode)) {
                    operationsCount++; // comparison
                    continue;
                }

                // Accept this edge into the MST forest
                mstEdges.add(minEdge);
                totalCost += minEdge.getWeight();
                visited.add(nextNode);
                operationsCount += 2; // add edge + update cost/visited

                // Push neighbors of the newly added vertex
                List<Edge> adj = graph.getAdjacentEdges(nextNode);
                if (adj != null) {
                    for (Edge e : adj) {
                        // Only consider edges leading to unvisited nodes
                        if (!visited.contains(e.getTo())) {
                            pq.offer(e);
                            operationsCount++; // queue operation
                        }
                        operationsCount++; // comparison
                    }
                }
            }
        }

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
     * Print the MST (or forest) result in a readable format.
     */
    public void printResult() {
        System.out.println("\n=== Prim's Algorithm Result ===");
        System.out.println("MST Edges:");
        for (Edge edge : mstEdges) {
            System.out.println("  " + edge);
        }
        System.out.println("Total Cost: " + totalCost);
        System.out.println("Operations Count: " + operationsCount);
        System.out.printf("Execution Time: %.2f ms%n", executionTimeMs);
    }
}
