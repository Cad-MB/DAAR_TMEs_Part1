package src.step3_Determinisation;

import src.step2_NDFA.NDFA_Struct;
import java.util.*;

/**
 * The {@code DFA_Determinisation} class is responsible for converting a Non-Deterministic Finite Automaton (NDFA)
 * to a Deterministic Finite Automaton (DFA) using the subset construction algorithm (powerset construction).
 */
public class DFA_Determinisation {

    private final static int SYMBOLES_ASCII = 256;

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
        DFA_Struct.Etat initialDFAState = new DFA_Struct.Etat();
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
                    Set<NDFA_Struct.Etat> epsilonClosureSet = epsilonClosure(nextSet);
                    DFA_Struct.Etat nextDFAState = dfaStateMap.get(epsilonClosureSet);
                    if (nextDFAState == null) {
                        nextDFAState = new DFA_Struct.Etat();
                        dfaStateMap.put(epsilonClosureSet, nextDFAState);
                        dfaStates.add(nextDFAState);
                        toProcess.add(epsilonClosureSet);
                    }
                    currentDFAState.ajouterTransition(symbole, nextDFAState);
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
