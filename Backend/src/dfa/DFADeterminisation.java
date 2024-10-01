package src.dfa;

import src.ndfa.NDFA;

import java.util.*;

/**
 * The {@code DFADeterminisation} class is responsible for converting a Non-Deterministic Finite Automaton (NDFA)
 * to a Deterministic Finite Automaton (DFA) using the subset construction algorithm (powerset construction).
 */
public class DFADeterminisation {

    private final static int SYMBOLES_ASCII = 256;
    // Map pour mémoriser la correspondance entre ensembles d'états NDFA et états DFA
    private static final Map<Set<NDFA.Etat>, DFA.Etat> ndfaToDfaMap = new HashMap<>();

    /**
     * Convertit un ensemble d'états NDFA en un état DFA unique.
     * @param ndfaStates L'ensemble d'états NDFA.
     * @return L'état DFA correspondant à cet ensemble d'états NDFA.
     */
    public static DFA.Etat convertNDFASetToDFA(Set<NDFA.Etat> ndfaStates) {
        // Sort the NDFA states by their ID to ensure consistent ordering
        List<NDFA.Etat> sortedStates = new ArrayList<>(ndfaStates);
        sortedStates.sort(Comparator.comparingInt(etat -> etat.id));

        // Create a unique name by concatenating the sorted NDFA state IDs
        String stateName = sortedStates.stream()
                .map(etat -> String.valueOf(etat.id))
                .reduce((s1, s2) -> s1 + "-" + s2)
                .orElse("Unknown");

        // Create a new DFA state with this name if it doesn't already exist
        if (!ndfaToDfaMap.containsKey(ndfaStates)) {
            DFA.Etat newDfaState = new DFA.Etat(stateName);
            ndfaToDfaMap.put(ndfaStates, newDfaState);
        }

        return ndfaToDfaMap.get(ndfaStates);
    }


    /**
     * Converts the given NDFA into a DFA using the subset construction (powerset) method.
     *
     * @param ndfa The NDFA to convert.
     * @return A {@code DFA} representing the deterministic version of the NDFA.
     */
    public static DFA determinise(NDFA ndfa) {
        // Subset construction algorithm (powerset construction)
        Set<DFA.Etat> dfaStates = new HashSet<>();
        Map<Set<NDFA.Etat>, DFA.Etat> dfaStateMap = new HashMap<>();

        // Create initial DFA state from NDFA initial epsilon-closure
        Set<NDFA.Etat> initialSet = epsilonClosure(Set.of(ndfa.etatInitial));
        DFA.Etat initialDFAState = convertNDFASetToDFA(initialSet);  // Use your conversion function
        dfaStateMap.put(initialSet, initialDFAState);
        dfaStates.add(initialDFAState);

        Queue<Set<NDFA.Etat>> toProcess = new LinkedList<>();
        toProcess.add(initialSet);

        // Process each DFA state (representing a set of NDFA states)
        while (!toProcess.isEmpty()) {
            Set<NDFA.Etat> currentSet = toProcess.poll();
            DFA.Etat currentDFAState = dfaStateMap.get(currentSet);

            // Process each possible input symbol
            for (int symbole = 0; symbole < SYMBOLES_ASCII; symbole++) {
                Set<NDFA.Etat> nextSet = new HashSet<>();
                for (NDFA.Etat ndfaState : currentSet) {
                    Set<NDFA.Etat> transitions = ndfaState.obtenirTransition(symbole);
                    if (transitions != null) {
                        nextSet.addAll(transitions);
                    }
                }

                if (!nextSet.isEmpty()) {
                    // Compute the epsilon-closure of the next set of NDFA states
                    Set<NDFA.Etat> epsilonClosureSet = epsilonClosure(nextSet);

                    // Check if this leads to the same set of states (i.e., a self-loop)
                    if (epsilonClosureSet.equals(currentSet)) {
                        // Self-loop, add a transition back to the current DFA state
                        currentDFAState.ajouterTransition(symbole, currentDFAState);
                    } else {
                        // Otherwise, check if the next state already exists in the DFA
                        DFA.Etat nextDFAState = dfaStateMap.get(epsilonClosureSet);
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
        Set<DFA.Etat> dfaAcceptingStates = new HashSet<>();
        for (Map.Entry<Set<NDFA.Etat>, DFA.Etat> entry : dfaStateMap.entrySet()) {
            for (NDFA.Etat ndfaState : entry.getKey()) {
                if (ndfaState.equals(ndfa.etatAcceptant)) {
                    dfaAcceptingStates.add(entry.getValue());
                    break;
                }
            }
        }

        return new DFA(initialDFAState, dfaAcceptingStates);
    }



    /**
     * Computes the epsilon-closure for a set of NDFA states.
     *
     * @param states The set of NDFA states.
     * @return The epsilon-closure of the given states.
     */
    private static Set<NDFA.Etat> epsilonClosure(Set<NDFA.Etat> states) {
        Set<NDFA.Etat> closure = new HashSet<>(states);
        Stack<NDFA.Etat> toProcess = new Stack<>();
        toProcess.addAll(states);

        while (!toProcess.isEmpty()) {
            NDFA.Etat current = toProcess.pop();
            for (NDFA.Etat epsilonTarget : current.transitionsEpsilon) {
                if (closure.add(epsilonTarget)) {
                    toProcess.add(epsilonTarget);
                }
            }
        }

        return closure;
    }

}
