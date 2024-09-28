package src.step4_minimisation;

import src.step2_NDFA.NDFA_Struct;
import src.step3_DFA.DFA_Struct;
import java.util.*;

/**
 * The {@code DFA_Minimization} class provides the functionality to minimize a given DFA.
 */
public class DFA_Minimization {

    /**
     * Minimizes the given DFA using the partition refinement method.
     *
     * @param dfa The DFA to be minimized.
     * @return A new minimized DFA.
     */
    public static DFA_Struct minimize(DFA_Struct dfa) {
        // Step 1: Remove unreachable states
        Set<DFA_Struct.Etat> reachableStates = getReachableStates(dfa);

        // Step 2: Partition the states into accepting and non-accepting
        Set<DFA_Struct.Etat> acceptingStates = new HashSet<>(dfa.etatAcceptant);  // Fixed: Now dfa.etatAcceptant is a Set
        Set<DFA_Struct.Etat> nonAcceptingStates = new HashSet<>(reachableStates);
        nonAcceptingStates.removeAll(acceptingStates);

        // Step 3: Apply partition refinement
        List<Set<DFA_Struct.Etat>> partitions = new ArrayList<>();
        partitions.add(acceptingStates);
        partitions.add(nonAcceptingStates);

        boolean isRefined;
        do {
            isRefined = false;
            List<Set<DFA_Struct.Etat>> newPartitions = new ArrayList<>();

            for (Set<DFA_Struct.Etat> group : partitions) {
                Map<Map<Integer, DFA_Struct.Etat>, Set<DFA_Struct.Etat>> splitterMap = new HashMap<>();

                for (DFA_Struct.Etat state : group) {
                    Map<Integer, DFA_Struct.Etat> transitions = new HashMap<>();
                    for (int symbol = 0; symbol < 256; symbol++) {
                        // Ensure the transitions for this symbol are not null
                        Set<NDFA_Struct.Etat> transitionSet = state.obtenirTransition(symbol);
                        if (transitionSet != null) {
                            DFA_Struct.Etat target = (DFA_Struct.Etat) transitionSet.stream().findFirst().orElse(null);
                            transitions.put(symbol, getPartitionRepresentative(target, partitions));
                        }
                    }
                    splitterMap.computeIfAbsent(transitions, k -> new HashSet<>()).add(state);
                }

                newPartitions.addAll(splitterMap.values());
                if (splitterMap.size() > 1) {
                    isRefined = true;
                }
            }

            partitions = newPartitions;
        } while (isRefined);

        // Step 4: Create the minimized DFA
        Map<DFA_Struct.Etat, DFA_Struct.Etat> stateMap = new HashMap<>();
        Set<DFA_Struct.Etat> minimizedStates = new HashSet<>();
        Set<DFA_Struct.Etat> minimizedAcceptingStates = new HashSet<>();

        for (Set<DFA_Struct.Etat> partition : partitions) {
            DFA_Struct.Etat representative = partition.iterator().next();
            DFA_Struct.Etat newState = new DFA_Struct.Etat();
            stateMap.put(representative, newState);
            minimizedStates.add(newState);
            if (acceptingStates.contains(representative)) {
                minimizedAcceptingStates.add(newState);
            }
        }

        DFA_Struct.Etat newInitialState = stateMap.get(getPartitionRepresentative((DFA_Struct.Etat) dfa.etatInitial, partitions));

        for (DFA_Struct.Etat oldState : reachableStates) {
            DFA_Struct.Etat newState = stateMap.get(getPartitionRepresentative(oldState, partitions));
            for (int symbol = 0; symbol < 256; symbol++) {
                Set<NDFA_Struct.Etat> transitionSet = oldState.obtenirTransition(symbol);
                if (transitionSet != null) {
                    DFA_Struct.Etat oldTarget = (DFA_Struct.Etat) transitionSet.stream().findFirst().orElse(null);
                    if (oldTarget != null) {
                        DFA_Struct.Etat newTarget = stateMap.get(getPartitionRepresentative(oldTarget, partitions));
                        newState.ajouterTransition(symbol, newTarget);
                    }
                }
            }
        }

        return new DFA_Struct(newInitialState, minimizedAcceptingStates);
    }

    /**
     * Returns the set of reachable states from the initial state of the DFA.
     *
     * @param dfa The DFA for which to find reachable states.
     * @return The set of reachable states.
     */
    private static Set<DFA_Struct.Etat> getReachableStates(DFA_Struct dfa) {
        Set<DFA_Struct.Etat> reachableStates = new HashSet<>();
        Queue<DFA_Struct.Etat> toProcess = new LinkedList<>();
        toProcess.add((DFA_Struct.Etat) dfa.etatInitial);

        while (!toProcess.isEmpty()) {
            DFA_Struct.Etat current = toProcess.poll();
            if (reachableStates.add(current)) {
                for (int symbol = 0; symbol < 256; symbol++) {
                    Set<NDFA_Struct.Etat> transitions = current.obtenirTransition(symbol);
                    if (transitions != null) {
                        for (NDFA_Struct.Etat transitionState : transitions) {
                            if (transitionState instanceof DFA_Struct.Etat) {
                                toProcess.add((DFA_Struct.Etat) transitionState);
                            }
                        }
                    }
                }
            }
        }
        return reachableStates;
    }

    /**
     * Returns the representative state of the partition containing the given state.
     *
     * @param state      The state for which to find the partition representative.
     * @param partitions The list of partitions.
     * @return The representative state of the partition containing the state.
     */
    private static DFA_Struct.Etat getPartitionRepresentative(DFA_Struct.Etat state, List<Set<DFA_Struct.Etat>> partitions) {
        for (Set<DFA_Struct.Etat> partition : partitions) {
            if (partition.contains(state)) {
                return partition.iterator().next();
            }
        }
        return null;
    }
}
