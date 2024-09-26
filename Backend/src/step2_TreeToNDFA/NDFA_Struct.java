package src.step2_TreeToNDFA;

import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The {@code NDFA_Struct} class represents a Non-Deterministic Finite Automaton (NDFA).
 * This class provides methods to build and visualize an NDFA, as well as create states
 * and transitions based on regular expressions parsed from a syntax tree.
 *
 * <p>Each NDFA consists of an initial state and an accepting state, with various transitions between states.
 * Transitions can be based on specific symbols or epsilon (ε) transitions, which allow the automaton
 * to change states without consuming an input character.
 */
public class NDFA_Struct {

    /** Number of ASCII symbols that can be processed in the automaton. */
    private final static int SYMBOLES_ASCII = 256;

    /** The initial state of the NDFA. */
    public final Etat etatInitial;

    /** The accepting state of the NDFA. */
    public final Etat etatAcceptant;

    /**
     * Constructs an NDFA with the specified initial and accepting states.
     *
     * @param etatInitial The initial state of the NDFA.
     * @param etatAcceptant The accepting state of the NDFA.
     */
    public NDFA_Struct(Etat etatInitial, Etat etatAcceptant) {
        this.etatInitial = etatInitial;
        this.etatAcceptant = etatAcceptant;
    }

    /**
     * The {@code Etat} class represents a state in the NDFA.
     * Each state can have transitions to other states based on specific symbols or epsilon (ε) transitions.
     */
    public static class Etat {

        /** Static counter to assign unique IDs to states. */
        public static int compteur = 0;

        /** The unique ID of this state. */
        public final int id;

        /** A map of transitions, where each symbol maps to a set of states this state can transition to. */
        public final Map<Integer, Set<Etat>> transitions;

        /** A set of states this state can transition to via epsilon (ε) transitions. */
        public final Set<Etat> transitionsEpsilon;

        /**
         * Constructs a new state with a unique ID and initializes the transition maps.
         */
        public Etat() {
            id = compteur++;
            transitions = new HashMap<>();
            transitionsEpsilon = new HashSet<>();
        }

        /**
         * Adds a transition from this state to another state based on a specific symbol.
         *
         * @param symbole The ASCII symbol for the transition.
         * @param suivant The state to transition to when the symbol is encountered.
         */
        public void ajouterTransition(int symbole, Etat suivant) {
            Set<Etat> etats = transitions.computeIfAbsent(symbole, k -> new HashSet<>());
            etats.add(suivant);
        }

        /**
         * Adds an epsilon (ε) transition from this state to another state.
         *
         * @param suivant The state to transition to via epsilon (ε).
         */
        public void ajouterTransition(Etat suivant) {
            transitionsEpsilon.add(suivant);
        }

        /**
         * Retrieves the set of states that this state can transition to based on the given symbol.
         *
         * @param symbole The ASCII symbol for which to find the transitions.
         * @return A set of states to which this state can transition using the given symbol, or {@code null} if no such transitions exist.
         */
        public Set<Etat> obtenirTransition(int symbole) {
            return transitions.get(symbole);
        }

        /**
         * Recursively prints the NDFA structure starting from this state, showing transitions and reachable states.
         *
         * @param visites A set of visited states to prevent infinite loops during printing.
         * @return A string representation of the NDFA from this state.
         */
        public String imprimer(HashSet<Etat> visites) {
            if (!visites.add(this))
                return null;

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Integer, Set<Etat>> entree : transitions.entrySet()) {
                for (Etat etat : entree.getValue()) {
                    sb.append(id).append(" -- ").append((char) entree.getKey().intValue()).append(" --> ").append(etat.id).append("\n");
                    String seq = etat.imprimer(visites);
                    if (seq != null) sb.append(seq);
                }
            }

            for (Etat etat : transitionsEpsilon) {
                sb.append(id).append(" -- EPSILON --> ").append(etat.id).append("\n");
                String seq = etat.imprimer(visites);
                if (seq != null) sb.append(seq);
            }
            return sb.toString();
        }

        /**
         * Overrides the {@code toString} method to return the NDFA structure starting from this state.
         *
         * @return A string representation of the NDFA starting from this state.
         */
        @Override
        public String toString() {
            return imprimer(new HashSet<>());
        }
    }

    /**
     * Overrides the {@code toString} method to return the string representation of the NDFA.
     *
     * @return A string representation of the NDFA structure.
     */
    @Override
    public String toString() {
        return etatInitial.toString();
    }
}
