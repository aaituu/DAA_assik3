package com.example.mst;

import com.example.mst.algorithms.KruskalAlgorithm;
import com.example.mst.algorithms.PrimAlgorithm;
import com.example.mst.model.Edge;
import com.example.mst.model.Graph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MST algorithms (Prim's and Kruskal's).
 * Tests correctness, acyclicity, edge count, and handling of disconnected graphs.
 */
public class MSTTest {

    private Graph smallConnectedGraph;
    private Graph disconnectedGraph;
    private Graph triangleGraph;

    /**
     * Set up test graphs before each test.
     */
    @BeforeEach
    public void setUp() {
        // Small connected graph (5 nodes, 7 edges)
        List<String> nodes1 = Arrays.asList("A", "B", "C", "D", "E");
        List<Edge> edges1 = Arrays.asList(
                new Edge("A", "B", 4),
                new Edge("A", "C", 3),
                new Edge("B", "C", 2),
                new Edge("B", "D", 5),
                new Edge("C", "D", 7),
                new Edge("C", "E", 8),
                new Edge("D", "E", 6)
        );
        smallConnectedGraph = new Graph(1, nodes1, edges1);

        // Disconnected graph (4 nodes, 2 edges - two separate components)
        List<String> nodes2 = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges2 = Arrays.asList(
                new Edge("A", "B", 1),
                new Edge("C", "D", 2)
        );
        disconnectedGraph = new Graph(2, nodes2, edges2);

        // Triangle graph (3 nodes, 3 edges)
        List<String> nodes3 = Arrays.asList("X", "Y", "Z");
        List<Edge> edges3 = Arrays.asList(
                new Edge("X", "Y", 1),
                new Edge("Y", "Z", 2),
                new Edge("X", "Z", 3)
        );
        triangleGraph = new Graph(3, nodes3, edges3);
    }

    /**
     * Test 1: Verify that Prim's and Kruskal's algorithms produce the same total cost.
     * The MST cost must be identical for both algorithms.
     */
    @Test
    public void testPrimAndKruskalSameCost() {
        System.out.println("\n=== Test 1: Same Cost ===");

        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        // Test on small connected graph
        prim.execute(smallConnectedGraph);
        kruskal.execute(smallConnectedGraph);

        System.out.println("Graph: " + smallConnectedGraph);
        System.out.println("Prim's Cost: " + prim.getTotalCost());
        System.out.println("Kruskal's Cost: " + kruskal.getTotalCost());

        assertEquals(prim.getTotalCost(), kruskal.getTotalCost(),
                "Prim's and Kruskal's algorithms must produce the same MST cost");

        // Test on triangle graph
        prim.execute(triangleGraph);
        kruskal.execute(triangleGraph);

        System.out.println("\nGraph: " + triangleGraph);
        System.out.println("Prim's Cost: " + prim.getTotalCost());
        System.out.println("Kruskal's Cost: " + kruskal.getTotalCost());

        assertEquals(prim.getTotalCost(), kruskal.getTotalCost(),
                "Prim's and Kruskal's algorithms must produce the same MST cost for triangle graph");

        System.out.println("✓ Test Passed: Both algorithms produce the same cost\n");
    }

    /**
     * Test 2: Verify that the MST has exactly V-1 edges.
     * For a connected graph with V vertices, MST must have V-1 edges.
     */
    @Test
    public void testMSTEdgeCount() {
        System.out.println("\n=== Test 2: MST Edge Count ===");

        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        // Test on small connected graph (5 nodes -> 4 edges expected)
        prim.execute(smallConnectedGraph);
        kruskal.execute(smallConnectedGraph);

        int expectedEdges = smallConnectedGraph.getNodeCount() - 1;

        System.out.println("Graph: " + smallConnectedGraph);
        System.out.println("Expected MST edges: " + expectedEdges);
        System.out.println("Prim's MST edges: " + prim.getMstEdges().size());
        System.out.println("Kruskal's MST edges: " + kruskal.getMstEdges().size());

        assertEquals(expectedEdges, prim.getMstEdges().size(),
                "Prim's MST must have V-1 edges");
        assertEquals(expectedEdges, kruskal.getMstEdges().size(),
                "Kruskal's MST must have V-1 edges");

        // Test on triangle graph (3 nodes -> 2 edges expected)
        prim.execute(triangleGraph);
        kruskal.execute(triangleGraph);

        expectedEdges = triangleGraph.getNodeCount() - 1;

        System.out.println("\nGraph: " + triangleGraph);
        System.out.println("Expected MST edges: " + expectedEdges);
        System.out.println("Prim's MST edges: " + prim.getMstEdges().size());
        System.out.println("Kruskal's MST edges: " + kruskal.getMstEdges().size());

        assertEquals(expectedEdges, prim.getMstEdges().size(),
                "Prim's MST must have V-1 edges for triangle graph");
        assertEquals(expectedEdges, kruskal.getMstEdges().size(),
                "Kruskal's MST must have V-1 edges for triangle graph");

        System.out.println("✓ Test Passed: MST has correct number of edges (V-1)\n");
    }

    /**
     * Test 3: Verify that the MST is acyclic (contains no cycles).
     * Use Union-Find to detect if adding edges creates a cycle.
     */
    @Test
    public void testMSTIsAcyclic() {
        System.out.println("\n=== Test 3: MST is Acyclic ===");

        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        // Test Prim's MST
        prim.execute(smallConnectedGraph);
        List<Edge> primMST = prim.getMstEdges();

        System.out.println("Testing Prim's MST for cycles...");
        System.out.println("Prim's MST edges: " + primMST.size());
        boolean primAcyclic = isAcyclic(primMST, smallConnectedGraph.getNodes());
        System.out.println("Prim's MST is acyclic: " + primAcyclic);

        assertTrue(primAcyclic, "Prim's MST must be acyclic (no cycles)");

        // Test Kruskal's MST
        kruskal.execute(smallConnectedGraph);
        List<Edge> kruskalMST = kruskal.getMstEdges();

        System.out.println("\nTesting Kruskal's MST for cycles...");
        System.out.println("Kruskal's MST edges: " + kruskalMST.size());
        boolean kruskalAcyclic = isAcyclic(kruskalMST, smallConnectedGraph.getNodes());
        System.out.println("Kruskal's MST is acyclic: " + kruskalAcyclic);

        assertTrue(kruskalAcyclic, "Kruskal's MST must be acyclic (no cycles)");

        System.out.println("✓ Test Passed: MST is acyclic\n");
    }

    /**
     * Test 4: Verify that disconnected graphs are handled gracefully.
     * For disconnected graphs, MST cannot span all vertices.
     */
    @Test
    public void testDisconnectedGraphHandled() {
        System.out.println("\n=== Test 4: Disconnected Graph Handling ===");

        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        System.out.println("Graph: " + disconnectedGraph);
        System.out.println("Is connected: " + disconnectedGraph.isConnected());

        // Execute algorithms on disconnected graph
        prim.execute(disconnectedGraph);
        kruskal.execute(disconnectedGraph);

        int nodeCount = disconnectedGraph.getNodeCount();
        int primEdgeCount = prim.getMstEdges().size();
        int kruskalEdgeCount = kruskal.getMstEdges().size();

        System.out.println("Node count: " + nodeCount);
        System.out.println("Prim's MST edges: " + primEdgeCount);
        System.out.println("Kruskal's MST edges: " + kruskalEdgeCount);

        // For disconnected graph, MST should have fewer than V-1 edges
        assertTrue(primEdgeCount < nodeCount,
                "Prim's MST on disconnected graph should have fewer than V edges");
        assertTrue(kruskalEdgeCount < nodeCount,
                "Kruskal's MST on disconnected graph should have fewer than V edges");

        // Both algorithms should produce the same result even for disconnected graphs
        assertEquals(primEdgeCount, kruskalEdgeCount,
                "Both algorithms should find the same number of edges in disconnected graph");

        System.out.println("✓ Test Passed: Disconnected graphs handled correctly\n");
    }

    /**
     * Helper method: Check if a set of edges forms an acyclic graph.
     * Uses Union-Find to detect cycles.
     *
     * @param edges List of edges to check
     * @param nodes List of all nodes in the graph
     * @return true if acyclic, false if contains cycle
     */
    private boolean isAcyclic(List<Edge> edges, List<String> nodes) {
        // Union-Find structure
        Map<String, String> parent = new HashMap<>();

        // Initialize: each node is its own parent
        for (String node : nodes) {
            parent.put(node, node);
        }

        // Check each edge
        for (Edge edge : edges) {
            String root1 = find(parent, edge.getFrom());
            String root2 = find(parent, edge.getTo());

            // If both nodes are already in the same set, adding this edge creates a cycle
            if (root1.equals(root2)) {
                return false; // Cycle detected
            }

            // Union: connect the two sets
            parent.put(root1, root2);
        }

        return true; // No cycles found
    }

    /**
     * Helper method: Find root of a node in Union-Find structure.
     *
     * @param parent Parent map
     * @param node Node to find root for
     * @return Root node
     */
    private String find(Map<String, String> parent, String node) {
        if (!parent.get(node).equals(node)) {
            parent.put(node, find(parent, parent.get(node))); // Path compression
        }
        return parent.get(node);
    }
}