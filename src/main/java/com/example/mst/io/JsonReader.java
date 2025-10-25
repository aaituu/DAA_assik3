package com.example.mst.io;

import com.example.mst.model.Edge;
import com.example.mst.model.Graph;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for reading graph data from JSON files.
 * Uses Gson library for JSON parsing.
 */
public class JsonReader {
    private final Gson gson;

    public JsonReader() {
        this.gson = new Gson();
    }

    /**
     * Read all graphs from a JSON file.
     * Expected format: { "graphs": [ {...}, {...}, ... ] }
     *
     * @param filename Path to the input JSON file
     * @return List of Graph objects parsed from the file
     * @throws IOException If file cannot be read
     */
    public List<Graph> readGraphs(String filename) throws IOException {
        List<Graph> graphs = new ArrayList<>();

        // Check if file exists
        if (!Files.exists(Paths.get(filename))) {
            throw new IOException("File not found: " + filename);
        }

        // Read and parse JSON file
        try (FileReader reader = new FileReader(filename)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            if (root == null || !root.has("graphs")) {
                throw new IOException("Invalid JSON format: missing 'graphs' array");
            }

            JsonArray graphsArray = root.getAsJsonArray("graphs");

            // Parse each graph
            for (JsonElement graphElement : graphsArray) {
                JsonObject graphObj = graphElement.getAsJsonObject();
                Graph graph = parseGraph(graphObj);
                graphs.add(graph);
            }
        }

        return graphs;
    }

    /**
     * Parse a single graph object from JSON.
     *
     * @param graphObj JSON object representing a graph
     * @return Graph object
     */
    private Graph parseGraph(JsonObject graphObj) {
        // Extract graph ID
        int id = graphObj.get("id").getAsInt();

        // Extract nodes
        List<String> nodes = new ArrayList<>();
        JsonArray nodesArray = graphObj.getAsJsonArray("nodes");
        for (JsonElement nodeElement : nodesArray) {
            nodes.add(nodeElement.getAsString());
        }

        // Extract edges
        List<Edge> edges = new ArrayList<>();
        JsonArray edgesArray = graphObj.getAsJsonArray("edges");
        for (JsonElement edgeElement : edgesArray) {
            JsonObject edgeObj = edgeElement.getAsJsonObject();

            String from = edgeObj.get("from").getAsString();
            String to = edgeObj.get("to").getAsString();
            int weight = edgeObj.get("weight").getAsInt();

            edges.add(new Edge(from, to, weight));
        }

        return new Graph(id, nodes, edges);
    }

    /**
     * Read a single graph from JSON file (convenience method).
     *
     * @param filename Path to the input JSON file
     * @return First graph in the file, or null if none found
     * @throws IOException If file cannot be read
     */
    public Graph readSingleGraph(String filename) throws IOException {
        List<Graph> graphs = readGraphs(filename);
        return graphs.isEmpty() ? null : graphs.get(0);
    }
}