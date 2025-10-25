package com.example.mst.io;

import com.example.mst.algorithms.KruskalAlgorithm;
import com.example.mst.algorithms.PrimAlgorithm;
import com.example.mst.model.Edge;
import com.example.mst.model.Graph;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for writing results to JSON and CSV files.
 */
public class JsonWriter {
    private final Gson gson;

    public JsonWriter() {
        // Use pretty printing for readable output
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Write all results to a JSON file.
     *
     * @param results List of result objects
     * @param filename Path to output JSON file
     * @throws IOException If file cannot be written
     */
    public void writeResults(List<ResultData> results, String filename) throws IOException {
        // Create output directory if it doesn't exist
        createDirectoryIfNeeded(filename);

        JsonObject root = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        for (ResultData result : results) {
            JsonObject resultObj = buildResultObject(result);
            resultsArray.add(resultObj);
        }

        root.add("results", resultsArray);

        // Write to file
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(root, writer);
        }
    }

    /**
     * Build a JSON object for a single result.
     */
    private JsonObject buildResultObject(ResultData result) {
        JsonObject obj = new JsonObject();

        obj.addProperty("graph_id", result.graphId);

        // Input statistics
        JsonObject inputStats = new JsonObject();
        inputStats.addProperty("vertices", result.vertices);
        inputStats.addProperty("edges", result.edges);
        obj.add("input_stats", inputStats);

        // Prim's algorithm results
        JsonObject primObj = new JsonObject();
        primObj.add("mst_edges", edgesToJsonArray(result.primEdges));
        primObj.addProperty("total_cost", result.primCost);
        primObj.addProperty("operations_count", result.primOperations);
        primObj.addProperty("execution_time_ms", round(result.primTime, 2));
        obj.add("prim", primObj);

        // Kruskal's algorithm results
        JsonObject kruskalObj = new JsonObject();
        kruskalObj.add("mst_edges", edgesToJsonArray(result.kruskalEdges));
        kruskalObj.addProperty("total_cost", result.kruskalCost);
        kruskalObj.addProperty("operations_count", result.kruskalOperations);
        kruskalObj.addProperty("execution_time_ms", round(result.kruskalTime, 2));
        obj.add("kruskal", kruskalObj);

        return obj;
    }

    /**
     * Convert list of edges to JSON array.
     */
    private JsonArray edgesToJsonArray(List<Edge> edges) {
        JsonArray array = new JsonArray();
        for (Edge edge : edges) {
            JsonObject edgeObj = new JsonObject();
            edgeObj.addProperty("from", edge.getFrom());
            edgeObj.addProperty("to", edge.getTo());
            edgeObj.addProperty("weight", edge.getWeight());
            array.add(edgeObj);
        }
        return array;
    }

    /**
     * Write summary results to CSV file.
     *
     * @param results List of result objects
     * @param filename Path to output CSV file
     * @throws IOException If file cannot be written
     */
    public void writeCsvSummary(List<ResultData> results, String filename) throws IOException {
        createDirectoryIfNeeded(filename);

        StringBuilder csv = new StringBuilder();

        // CSV Header
        csv.append("Graph ID,Vertices,Edges,");
        csv.append("Prim Cost,Prim Time (ms),Prim Operations,");
        csv.append("Kruskal Cost,Kruskal Time (ms),Kruskal Operations\n");

        // Data rows
        for (ResultData result : results) {
            csv.append(result.graphId).append(",");
            csv.append(result.vertices).append(",");
            csv.append(result.edges).append(",");
            csv.append(result.primCost).append(",");
            csv.append(round(result.primTime, 2)).append(",");
            csv.append(result.primOperations).append(",");
            csv.append(result.kruskalCost).append(",");
            csv.append(round(result.kruskalTime, 2)).append(",");
            csv.append(result.kruskalOperations).append("\n");
        }

        // Write to file
        Files.write(Paths.get(filename), csv.toString().getBytes());
    }

    /**
     * Create directory for output file if it doesn't exist.
     */
    private void createDirectoryIfNeeded(String filename) throws IOException {
        String directory = Paths.get(filename).getParent().toString();
        if (!Files.exists(Paths.get(directory))) {
            Files.createDirectories(Paths.get(directory));
        }
    }

    /**
     * Round a double to specified decimal places.
     */
    private double round(double value, int decimals) {
        double scale = Math.pow(10, decimals);
        return Math.round(value * scale) / scale;
    }

    /**
     * Data class to hold results for one graph processing.
     */
    public static class ResultData {
        public int graphId;
        public int vertices;
        public int edges;

        public List<Edge> primEdges;
        public int primCost;
        public int primOperations;
        public double primTime;

        public List<Edge> kruskalEdges;
        public int kruskalCost;
        public int kruskalOperations;
        public double kruskalTime;

        public ResultData(Graph graph, PrimAlgorithm prim, KruskalAlgorithm kruskal) {
            this.graphId = graph.getId();
            this.vertices = graph.getNodeCount();
            this.edges = graph.getEdgeCount();

            this.primEdges = prim.getMstEdges();
            this.primCost = prim.getTotalCost();
            this.primOperations = prim.getOperationsCount();
            this.primTime = prim.getExecutionTimeMs();

            this.kruskalEdges = kruskal.getMstEdges();
            this.kruskalCost = kruskal.getTotalCost();
            this.kruskalOperations = kruskal.getOperationsCount();
            this.kruskalTime = kruskal.getExecutionTimeMs();
        }
    }
}