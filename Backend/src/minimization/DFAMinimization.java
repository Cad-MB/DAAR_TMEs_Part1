package src.minimization;

import src.dfa.DFA;
import src.ndfa.NDFA;

import java.util.*;

/**
 * The {@code DFAMinimization} class provides the functionality to minimize a given DFA.
 */
public class DFAMinimization {

    /**
     * Minimizes the given DFA using the partition refinement method.
     *
     * @param dfa The DFA to be minimized.
     * @return A new minimized DFA.
     */
    public static DFA minimize(DFA dfa) {
        // Step 1: Remove unreachable states
        Set<DFA.Etat> reachableStates = getReachableStates(dfa);

        // Step 2: Partition the states into accepting and non-accepting
        Set<DFA.Etat> acceptingStates = new HashSet<>(dfa.etatAcceptant);  // Fixed: Now dfa.etatAcceptant is a Set
        Set<DFA.Etat> nonAcceptingStates = new HashSet<>(reachableStates);
        nonAcceptingStates.removeAll(acceptingStates);

        // Step 3: Apply partition refinement
        List<Set<DFA.Etat>> partitions = new ArrayList<>();
        partitions.add(acceptingStates);
        partitions.add(nonAcceptingStates);

        boolean isRefined;
        do {
            isRefined = false;
            List<Set<DFA.Etat>> newPartitions = new ArrayList<>();

            for (Set<DFA.Etat> group : partitions) {
                Map<Map<Integer, DFA.Etat>, Set<DFA.Etat>> splitterMap = new HashMap<>();

                for (DFA.Etat state : group) {
                    Map<Integer, DFA.Etat> transitions = new HashMap<>();
                    for (int symbol = 0; symbol < 256; symbol++) {
                        // Ensure the transitions for this symbol are not null
                        Set<NDFA.Etat> transitionSet = state.obtenirTransition(symbol);
                        if (transitionSet != null) {
                            DFA.Etat target = (DFA.Etat) transitionSet.stream().findFirst().orElse(null);
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
        Map<DFA.Etat, DFA.Etat> stateMap = new HashMap<>();
        Set<DFA.Etat> minimizedStates = new HashSet<>();
        Set<DFA.Etat> minimizedAcceptingStates = new HashSet<>();

        for (Set<DFA.Etat> partition : partitions) {
            DFA.Etat representative = partition.iterator().next();
            DFA.Etat newState = new DFA.Etat();
            stateMap.put(representative, newState);
            minimizedStates.add(newState);
            if (acceptingStates.contains(representative)) {
                minimizedAcceptingStates.add(newState);
            }
        }

        DFA.Etat newInitialState = stateMap.get(getPartitionRepresentative((DFA.Etat) dfa.etatInitial, partitions));

        for (DFA.Etat oldState : reachableStates) {
            DFA.Etat newState = stateMap.get(getPartitionRepresentative(oldState, partitions));
            for (int symbol = 0; symbol < 256; symbol++) {
                Set<NDFA.Etat> transitionSet = oldState.obtenirTransition(symbol);
                if (transitionSet != null) {
                    DFA.Etat oldTarget = (DFA.Etat) transitionSet.stream().findFirst().orElse(null);
                    if (oldTarget != null) {
                        DFA.Etat newTarget = stateMap.get(getPartitionRepresentative(oldTarget, partitions));
                        newState.ajouterTransition(symbol, newTarget);
                    }
                }
            }
        }

        return new DFA(newInitialState, minimizedAcceptingStates);
    }

    /**
     * Returns the set of reachable states from the initial state of the DFA.
     *
     * @param dfa The DFA for which to find reachable states.
     * @return The set of reachable states.
     */
    private static Set<DFA.Etat> getReachableStates(DFA dfa) {
        Set<DFA.Etat> reachableStates = new HashSet<>();
        Queue<DFA.Etat> toProcess = new LinkedList<>();
        toProcess.add((DFA.Etat) dfa.etatInitial);

        while (!toProcess.isEmpty()) {
            DFA.Etat current = toProcess.poll();
            if (reachableStates.add(current)) {
                for (int symbol = 0; symbol < 256; symbol++) {
                    Set<NDFA.Etat> transitions = current.obtenirTransition(symbol);
                    if (transitions != null) {
                        for (NDFA.Etat transitionState : transitions) {
                            if (transitionState instanceof DFA.Etat) {
                                toProcess.add((DFA.Etat) transitionState);
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
    private static DFA.Etat getPartitionRepresentative(DFA.Etat state, List<Set<DFA.Etat>> partitions) {
        for (Set<DFA.Etat> partition : partitions) {
            if (partition.contains(state)) {
                return partition.iterator().next();
            }
        }
        return null;
    }
}
