package src.ndfa.export;

import src.ndfa.NDFA;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * The {@code NDFAExporter} class is responsible for exporting an NDFA structure
 * to a JSON file format. The resulting JSON represents the states (nodes) and transitions
 * (links) in the NDFA.
 *
 * <p>This format can be used for visualization in various graph-based tools that support
 * JSON-based data formats.</p>
 */
public class NDFAExporter {

    /**
     * Exports the given NDFA structure to a JSON file for visualization or analysis.
     *
     * @param ndfa The NDFA structure to export.
     * @param filename The name of the file to which the JSON format will be written.
     */
    public void toJsonFile(NDFA ndfa, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            // Create lists for nodes and links
            List<Map<String, String>> nodes = new ArrayList<>();
            List<Map<String, String>> links = new ArrayList<>();

            // Add all states and transitions to the lists
            Set<NDFA.Etat> visites = new HashSet<>();
            collectEtatForJson(ndfa.etatInitial, nodes, links, visites, ndfa);

            // Start writing the JSON structure
            writer.write("{\n");

            // Write nodes
            writer.write("  \"nodes\": [\n");
            for (int i = 0; i < nodes.size(); i++) {
                Map<String, String> node = nodes.get(i);
                StringBuilder nodeJson = new StringBuilder();
                nodeJson.append("    { \"id\": \"").append(node.get("id")).append("\"");

                // Check if the node is the initial or accepting state
                if (node.get("id").equals(String.valueOf(ndfa.etatInitial.id))) {
                    nodeJson.append(", \"type\": \"initial\"");
                }
                if (node.get("id").equals(String.valueOf(ndfa.etatAcceptant.id))) {
                    nodeJson.append(", \"type\": \"accepting\"");
                }

                nodeJson.append(" }");
                writer.write(nodeJson.toString());
                if (i < nodes.size() - 1)
                    writer.write(",");
                writer.write("\n");
            }
            writer.write("  ],\n");

            // Write links
            writer.write("  \"links\": [\n");
            for (int i = 0; i < links.size(); i++) {
                Map<String, String> link = links.get(i);
                writer.write("    { \"source\": \"" + link.get("source") + "\", \"target\": \"" + link.get("target")
                        + "\", \"label\": \"" + link.get("label") + "\" }");
                if (i < links.size() - 1)
                    writer.write(",");
                writer.write("\n");
            }
            writer.write("  ]\n");

            writer.write("}\n");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier JSON: " + e.getMessage());
        }
    }

    /**
     * Helper function to recursively collect states and transitions for JSON export.
     *
     * @param etat The current state being processed.
     * @param nodes The list of nodes to which the current state will be added.
     * @param links The list of links (transitions) from this state to other states.
     * @param visites A set of visited states to prevent infinite loops.
     * @param ndfa The NDFA structure, used to identify initial and accepting states.
     */
    public void collectEtatForJson(NDFA.Etat etat, List<Map<String, String>> nodes, List<Map<String, String>> links,
                                   Set<NDFA.Etat> visites, NDFA ndfa) {
        if (!visites.add(etat))
            return; // Skip already visited states

        // Add the current state to the nodes list
        Map<String, String> node = new HashMap<>();
        node.put("id", String.valueOf(etat.id)); // Ensure the ID is a String
        nodes.add(node);

        // Add all transitions for this state to the links list
        for (Map.Entry<Integer, Set<NDFA.Etat>> entry : etat.transitions.entrySet()) {
            int symbol = entry.getKey();
            for (NDFA.Etat suivant : entry.getValue()) {
                Map<String, String> link = new HashMap<>();
                link.put("source", String.valueOf(etat.id)); // Ensure source is a String
                link.put("target", String.valueOf(suivant.id)); // Ensure target is a String
                link.put("label", String.valueOf((char) symbol)); // Ensure label is a String
                links.add(link);
                collectEtatForJson(suivant, nodes, links, visites, ndfa); // Recursively process the next state
            }
        }

        // Add epsilon transitions to the links list
        for (NDFA.Etat suivant : etat.transitionsEpsilon) {
            Map<String, String> link = new HashMap<>();
            link.put("source", String.valueOf(etat.id)); // Ensure source is a String
            link.put("target", String.valueOf(suivant.id)); // Ensure target is a String
            link.put("label", "ε"); // Epsilon is already a String
            links.add(link);
            collectEtatForJson(suivant, nodes, links, visites, ndfa); // Recursively process the next state
        }
    }
}