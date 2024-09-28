package src.step3_DFA.graphics;

import src.step2_NDFA.NDFA_Struct;
import src.step2_NDFA.graphics.NDFA_JSON_Exporter;

import java.util.*;

/**
 * The {@code DFA_JSON_Exporter} class inherits from {@code NDFA_JSON_Exporter} and
 * is responsible for exporting a DFA structure to a JSON file for visualization or analysis.
 */
public class DFAMinimized_JSON_Exporter extends NDFA_JSON_Exporter {

    /**
     * Overrides the method to export the DFA structure to a JSON file.
     *
     * @param dfa      The DFA structure to export.
     * @param filename The name of the file to which the JSON format will be written.
     */
    @Override
    public void toJsonFile(NDFA_Struct dfa, String filename) {
        // Call the method from NDFA_JSON_Exporter with appropriate DFA handling
        super.toJsonFile(dfa, filename);
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
    public void collectEtatForJson(NDFA_Struct.Etat etat, List<Map<String, String>> nodes, List<Map<String, String>> links,
                                   Set<NDFA_Struct.Etat> visites, NDFA_Struct dfa) {
        if (!visites.add(etat)) {
            return; // Skip already visited states
        }

        // Add the current state to the nodes list
        Map<String, String> node = new HashMap<>();
        node.put("id", String.valueOf(etat.id)); // Ensure the ID is a String
        nodes.add(node);

        // Add deterministic transitions (without epsilon transitions) for DFA
        for (Map.Entry<Integer, Set<NDFA_Struct.Etat>> entry : etat.transitions.entrySet()) {
            int symbol = entry.getKey();
            for (NDFA_Struct.Etat suivant : entry.getValue()) {
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
