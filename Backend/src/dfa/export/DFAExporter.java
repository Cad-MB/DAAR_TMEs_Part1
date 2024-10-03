package src.dfa.export;

import src.dfa.DFA;
import src.ndfa.NDFA;
import src.ndfa.export.NDFAExporter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * The {@code DFAMinimizationExporter} class inherits from {@code NDFAExporter} and
 * is responsible for exporting a DFA structure to a JSON file for visualization or analysis.
 */
public class DFAExporter extends NDFAExporter {

    /**
     * Overrides the method to export the DFA structure to a JSON file.
     *
     * @param dfa The DFA structure to export.
     * @param filename The name of the file to which the JSON format will be written.
     */
    public void toJsonFile(DFA dfa, String filename) {
        // Call the method from NDFAExporter with appropriate DFA handling
        try (FileWriter writer = new FileWriter(filename)) {
            // Create lists for nodes and links
            List<Map<String, String>> nodes = new ArrayList<>();
            List<Map<String, String>> links = new ArrayList<>();

            // Add all states and transitions to the lists
            Set<NDFA.Etat> visites = new HashSet<>();
            collectEtatForJson(dfa.etatInitial, nodes, links, visites, dfa);
            // Start writing the JSON structure
            writer.write("{\n");

            // Write nodes
            writer.write("  \"nodes\": [\n");
            for (int i = 0; i < nodes.size(); i++) {
                Map<String, String> node = nodes.get(i);
                StringBuilder nodeJson = new StringBuilder();
                nodeJson.append("    { \"id\": \"").append(node.get("id")).append("\"");

                // Check if the node is the initial or accepting state
                if (node.get("id").equals(String.valueOf(dfa.etatInitial.id))) {
                    nodeJson.append(", \"type\": \"initial\"");
                }
                if (dfa.etatAcceptant.stream().anyMatch(e -> node.get("id").equals(String.valueOf(e.id)))) {
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
     * Helper function to recursively collect DFA states and transitions for JSON export.
     * Overrides the NDFA behavior to handle DFA's deterministic transitions.
     *
     * @param etat    The current state being processed.
     * @param nodes   The list of nodes to which the current state will be added.
     * @param links   The list of links (transitions) from this state to other states.
     * @param visites A set of visited states to prevent infinite loops.
     * @param dfa     The DFA structure, used to identify initial and accepting states.
     */
    @Override
    public void collectEtatForJson(NDFA.Etat etat, List<Map<String, String>> nodes, List<Map<String, String>> links,
                                   Set<NDFA.Etat> visites, NDFA dfa) {
        if (!visites.add(etat)) {
            return; // Skip already visited states
        }

        // Add the current state to the nodes list
        Map<String, String> node = new HashMap<>();
        node.put("id", String.valueOf(etat.id)); // Ensure the ID is a String
        nodes.add(node);

        // Add deterministic transitions (without epsilon transitions) for DFA
        for (Map.Entry<Integer, Set<NDFA.Etat>> entry : etat.transitions.entrySet()) {
            int symbol = entry.getKey();
            for (NDFA.Etat suivant : entry.getValue()) {
                if (suivant == null) {
                    // Log or handle the case where the next state is null
                    System.err.println("Null transition state detected for symbol: " + (char) symbol + " from state: " + etat.id);
                    continue; // Skip this transition
                }

                Map<String, String> link = new HashMap<>();
                link.put("source", String.valueOf(etat.id)); // Ensure source is a String
                link.put("target", String.valueOf(suivant.id)); // Ensure target is a String
                link.put("label", String.valueOf((char) symbol)); // Ensure label is a String

                // Handle loop detection: if the source and target states are the same, it's a loop
                if (etat.equals(suivant)) {
                    link.put("type", "loop");
                }

                links.add(link);

                // Avoid revisiting self-loops unnecessarily
                if (!etat.equals(suivant)) {
                    collectEtatForJson(suivant, nodes, links, visites, dfa); // Recursively process the next state
                }
            }
        }
    }

}
