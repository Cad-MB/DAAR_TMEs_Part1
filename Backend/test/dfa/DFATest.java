package test.dfa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.dfa.DFA;
import src.ndfa.NDFA;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@code DFA} and its inner class {@code Etat}.
 */
public class DFATest {

    private DFA.Etat etat1;
    private DFA.Etat etat2;
    private DFA.Etat etat3;

    @BeforeEach
    public void setUp() {
        // Initialize DFA states before each test
        NDFA.Etat.compteur = 0; // Reset state counter for each test
        etat1 = new DFA.Etat();
        etat2 = new DFA.Etat();
        etat3 = new DFA.Etat();
    }

    @Test
    public void testEtatCreation() {
        // Ensure that states are created with unique IDs
        assertEquals(0, etat1.id);
        assertEquals(1, etat2.id);
        assertEquals(2, etat3.id);
    }

    @Test
    public void testAddDeterministicTransition() {
        // Add a deterministic transition from etat1 to etat2 on symbol 'a'
        etat1.ajouterTransition('a', etat2);
        Set<NDFA.Etat> result = etat1.obtenirTransition('a');

        // Ensure that the deterministic transition was correctly added
        assertNotNull(result);
        assertEquals(1, result.size()); // DFA should have only one state for the transition
        assertTrue(result.contains(etat2));
    }

    @Test
    public void testOverrideTransition() {
        // Add a transition from etat1 to etat2 on symbol 'a'
        etat1.ajouterTransition('a', etat2);
        // Now override the transition with etat3 for the same symbol 'a'
        etat1.ajouterTransition('a', etat3);

        Set<NDFA.Etat> result = etat1.obtenirTransition('a');

        // Ensure that the transition now points to etat3, not etat2
        assertNotNull(result);
        assertEquals(1, result.size()); // Only one transition is allowed in DFA
        assertTrue(result.contains(etat3)); // It should contain etat3 now
        assertFalse(result.contains(etat2)); // etat2 should no longer be part of the transition
    }

    @Test
    public void testNoEpsilonTransitionInDFA() {
        // Try to add an epsilon transition, which is not allowed in DFA
        assertThrows(UnsupportedOperationException.class, () -> etat1.ajouterTransition(etat2));
    }

    @Test
    public void testNoTransition() {
        // Ensure that etat1 has no transitions initially
        assertNull(etat1.obtenirTransition('a'));
    }

    @Test
    public void testDFAToString() {
        // Add deterministic transitions
        etat1.ajouterTransition('a', etat2);
        etat2.ajouterTransition('b', etat3);

        // Create a DFA with etat1 as the initial state and etat3 as the accepting state
        DFA dfa = new DFA(etat1, Set.of(etat3));

        // Expected string output
        String expectedOutput = """
                0 -- a --> 1
                1 -- b --> 2
                """;

        // Check if the DFA string representation matches the expected output
        assertEquals(expectedOutput.trim(), dfa.toString().trim());
    }

    @Test
    public void testAddMultipleDeterministicTransitions() {
        // Add multiple transitions for different symbols
        etat1.ajouterTransition('a', etat2);
        etat1.ajouterTransition('b', etat3);

        // Validate the transition for 'a'
        Set<NDFA.Etat> transitionA = etat1.obtenirTransition('a');
        assertNotNull(transitionA);
        assertTrue(transitionA.contains(etat2));

        // Validate the transition for 'b'
        Set<NDFA.Etat> transitionB = etat1.obtenirTransition('b');
        assertNotNull(transitionB);
        assertTrue(transitionB.contains(etat3));
    }

    @Test
    public void testEmptyDFA() {
        // Create an empty DFA (no transitions)
        DFA dfa = new DFA(etat1, Set.of(etat1));

        // Ensure the DFA has no transitions
        assertEquals("", dfa.toString().trim());
    }
}
