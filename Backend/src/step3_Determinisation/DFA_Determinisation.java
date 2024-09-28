package src.step3_Determinisation;

import src.step2_NDFA.NDFA_Struct;
import java.util.*;

/**
 * The {@code DFA_Determinisation} class is responsible for converting a Non-Deterministic Finite Automaton (NDFA)
 * to a Deterministic Finite Automaton (DFA) using the subset construction algorithm (powerset construction).
 */
public class DFA_Determinisation {

    private final static int SYMBOLES_ASCII = 256;
    // Map pour mémoriser la correspondance entre ensembles d'états NDFA et états DFA
    private static final Map<Set<NDFA_Struct.Etat>, DFA_Struct.Etat> ndfaToDfaMap = new HashMap<>();

    /**
     * Convertit un ensemble d'états NDFA en un état DFA unique.
     * @param ndfaStates L'ensemble d'états NDFA.
     * @return L'état DFA correspondant à cet ensemble d'états NDFA.
     */
    public static DFA_Struct.Etat convertNDFASetToDFA(Set<NDFA_Struct.Etat> ndfaStates) {
        // Sort the NDFA states by their ID to ensure consistent ordering
        List<NDFA_Struct.Etat> sortedStates = new ArrayList<>(ndfaStates);
        sortedStates.sort(Comparator.comparingInt(etat -> etat.id));

        // Create a unique name by concatenating the sorted NDFA state IDs
        String stateName = sortedStates.stream()
                .map(etat -> String.valueOf(etat.id))
                .reduce((s1, s2) -> s1 + "-" + s2)
                .orElse("Unknown");

        // Create a new DFA state with this name if it doesn't already exist
        if (!ndfaToDfaMap.containsKey(ndfaStates)) {
            DFA_Struct.Etat newDfaState = new DFA_Struct.Etat(stateName);
            ndfaToDfaMap.put(ndfaStates, newDfaState);
        }

        return ndfaToDfaMap.get(ndfaStates);
    }


    /**
     * Converts the given NDFA into a DFA using the subset construction (powerset) method.
     *
     * @param ndfa The NDFA to convert.
     * @return A {@code DFA_Struct} representing the deterministic version of the NDFA.
     */
    public static DFA_Struct determinise(NDFA_Struct ndfa) {
        // Subset construction algorithm (powerset construction)
        Set<DFA_Struct.Etat> dfaStates = new HashSet<>();
        Map<Set<NDFA_Struct.Etat>, DFA_Struct.Etat> dfaStateMap = new HashMap<>();

        // Create initial DFA state from NDFA initial epsilon-closure
        Set<NDFA_Struct.Etat> initialSet = epsilonClosure(Set.of(ndfa.etatInitial));
        DFA_Struct.Etat initialDFAState = convertNDFASetToDFA(initialSet);  // Use your conversion function
        dfaStateMap.put(initialSet, initialDFAState);
        dfaStates.add(initialDFAState);

        Queue<Set<NDFA_Struct.Etat>> toProcess = new LinkedList<>();
        toProcess.add(initialSet);

        // Process each DFA state (representing a set of NDFA states)
        while (!toProcess.isEmpty()) {
            Set<NDFA_Struct.Etat> currentSet = toProcess.poll();
            DFA_Struct.Etat currentDFAState = dfaStateMap.get(currentSet);

            // Process each possible input symbol
            for (int symbole = 0; symbole < SYMBOLES_ASCII; symbole++) {
                Set<NDFA_Struct.Etat> nextSet = new HashSet<>();
                for (NDFA_Struct.Etat ndfaState : currentSet) {
                    Set<NDFA_Struct.Etat> transitions = ndfaState.obtenirTransition(symbole);
                    if (transitions != null) {
                        nextSet.addAll(transitions);
                    }
                }

                if (!nextSet.isEmpty()) {
                    // Compute the epsilon-closure of the next set of NDFA states
                    Set<NDFA_Struct.Etat> epsilonClosureSet = epsilonClosure(nextSet);

                    // Check if this leads to the same set of states (i.e., a self-loop)
                    if (epsilonClosureSet.equals(currentSet)) {
                        // Self-loop, add a transition back to the current DFA state
                        currentDFAState.ajouterTransition(symbole, currentDFAState);
                    } else {
                        // Otherwise, check if the next state already exists in the DFA
                        DFA_Struct.Etat nextDFAState = dfaStateMap.get(epsilonClosureSet);
                        if (nextDFAState == null) {
                            // If not, create a new DFA state and add it to the map and queue
                            nextDFAState = convertNDFASetToDFA(epsilonClosureSet);
                            dfaStateMap.put(epsilonClosureSet, nextDFAState);
                            dfaStates.add(nextDFAState);
                            toProcess.add(epsilonClosureSet);
                        }
                        // Add the transition from the current DFA state to the next DFA state
                        currentDFAState.ajouterTransition(symbole, nextDFAState);
                    }
                }
            }
        }

        // Determine accepting states in DFA
        Set<DFA_Struct.Etat> dfaAcceptingStates = new HashSet<>();
        for (Map.Entry<Set<NDFA_Struct.Etat>, DFA_Struct.Etat> entry : dfaStateMap.entrySet()) {
            for (NDFA_Struct.Etat ndfaState : entry.getKey()) {
                if (ndfaState.equals(ndfa.etatAcceptant)) {
                    dfaAcceptingStates.add(entry.getValue());
                    break;
                }
            }
        }

        return new DFA_Struct(initialDFAState, dfaAcceptingStates);
    }



    /**
     * Computes the epsilon-closure for a set of NDFA states.
     *
     * @param states The set of NDFA states.
     * @return The epsilon-closure of the given states.
     */
    private static Set<NDFA_Struct.Etat> epsilonClosure(Set<NDFA_Struct.Etat> states) {
        Set<NDFA_Struct.Etat> closure = new HashSet<>(states);
        Stack<NDFA_Struct.Etat> toProcess = new Stack<>();
        toProcess.addAll(states);

        while (!toProcess.isEmpty()) {
            NDFA_Struct.Etat current = toProcess.pop();
            for (NDFA_Struct.Etat epsilonTarget : current.transitionsEpsilon) {
                if (closure.add(epsilonTarget)) {
                    toProcess.add(epsilonTarget);
                }
            }
        }

        return closure;
    }

}
