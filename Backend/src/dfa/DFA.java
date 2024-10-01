package src.dfa;

import src.ndfa.NDFA;

import java.util.*;

/**
 * The {@code DFA} class represents a Deterministic Finite Automaton (DFA).
 * It extends the functionality of {@code NDFA} by enforcing deterministic transitions.
 */
public class DFA extends NDFA {

    public Set<Etat> etatAcceptant; // Set of accepting states

    /**
     * Constructs a DFA with the specified initial state and accepting states.
     *
     * @param etatInitial     The initial state of the DFA.
     * @param etatsAcceptants The set of accepting states in the DFA.
     */
    public DFA(Etat etatInitial, Set<Etat> etatsAcceptants) {
        super(etatInitial, etatsAcceptants.iterator().next()); // Use the NDFA constructor with one accepting state
        this.etatAcceptant = etatsAcceptants;  // Set of accepting states
    }

    /**
     * The {@code Etat} class represents a state in the DFA.
     * Inherits from {@code NDFA.Etat}, but transitions are deterministic.
     */
    public static class Etat extends NDFA.Etat {

        private String name;

        // No-argument constructor for Etat
        public Etat() {
            super(); // Call the parent constructor to set the ID
            this.name = "State_" + id;  // Default name based on the state ID
        }

        // Constructor that accepts a name for the state
        public Etat(String name) {
            super(); // Call the parent constructor to set the ID
            this.name = name; // Set the custom name
        }

        @Override
        public String toString() {
            return "State ID: " + id + ", Name: " + name;
        }

        /**
         * Adds a deterministic transition from this state to another state based on a specific symbol.
         * Overrides NDFA's method to ensure the transition is deterministic (only one target state per symbol).
         *
         * @param symbole The ASCII symbol for the transition.
         * @param suivant The state to transition to when the symbol is encountered.
         */
        @Override
        public void ajouterTransition(int symbole, NDFA.Etat suivant) {
            // In DFA, there can only be one transition per symbol
            transitions.put(symbole, Collections.singleton(suivant));
        }

        /**
         * Retrieves the state that this state can transition to based on the given symbol.
         * This ensures deterministic behavior as it returns only one state per symbol.
         *
         * @param symbole The ASCII symbol for which to find the transition.
         * @return The state to which this state can transition using the given symbol, or {@code null} if no such transition exists.
         */
        @Override
        public Set<NDFA.Etat> obtenirTransition(int symbole) {
            Set<NDFA.Etat> etats = transitions.get(symbole);
            return etats != null && !etats.isEmpty() ? Collections.singleton(etats.iterator().next()) : null; // Return a single state (deterministic)
        }

        // Remove epsilon transitions for DFA (no epsilon transitions in DFA)
        @Override
        public void ajouterTransition(NDFA.Etat suivant) {
            throw new UnsupportedOperationException("DFA does not support epsilon transitions.");
        }
    }

    /**
     * Overrides the NDFA toString method to provide the string representation of the DFA.
     *
     * @return A string representation of the DFA structure.
     */
    @Override
    public String toString() {
        return etatInitial.toString();
    }
}
