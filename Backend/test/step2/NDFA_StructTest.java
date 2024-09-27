package test.step2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.step2_NDFA.NDFA_Struct;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@code DFA_Struct} and its inner class {@code Etat}.
 */
public class NDFA_StructTest {

    private NDFA_Struct.Etat etat1;
    private NDFA_Struct.Etat etat2;
    private NDFA_Struct.Etat etat3;

    @BeforeEach
    public void setUp() {
        // Initialize states before each test
        NDFA_Struct.Etat.compteur = 0; // Reset state counter for each test
        etat1 = new NDFA_Struct.Etat();
        etat2 = new NDFA_Struct.Etat();
        etat3 = new NDFA_Struct.Etat();
    }

    @Test
    public void testEtatCreation() {
        // Ensure that states are created with unique IDs
        assertEquals(0, etat1.id);
        assertEquals(1, etat2.id);
        assertEquals(2, etat3.id);
    }

    @Test
    public void testAddTransition() {
        // Add transition from etat1 to etat2 on symbol 'a'
        etat1.ajouterTransition('a', etat2);
        Set<NDFA_Struct.Etat> result = etat1.obtenirTransition('a');

        // Ensure that the transition was correctly added
        assertNotNull(result);
        assertTrue(result.contains(etat2));
    }

    @Test
    public void testAddMultipleTransitions() {
        // Add multiple transitions from etat1 to etat2 on different symbols
        etat1.ajouterTransition('a', etat2);
        etat1.ajouterTransition('b', etat3);

        // Check transitions for 'a' and 'b'
        Set<NDFA_Struct.Etat> transitionA = etat1.obtenirTransition('a');
        Set<NDFA_Struct.Etat> transitionB = etat1.obtenirTransition('b');

        assertNotNull(transitionA);
        assertTrue(transitionA.contains(etat2));

        assertNotNull(transitionB);
        assertTrue(transitionB.contains(etat3));
    }

    @Test
    public void testEpsilonTransition() {
        // Add an epsilon transition from etat1 to etat2
        etat1.ajouterTransition(etat2);

        // Check epsilon transition
        assertTrue(etat1.transitionsEpsilon.contains(etat2));
    }

    @Test
    public void testNoTransition() {
        // Check that etat1 has no transitions initially
        assertNull(etat1.obtenirTransition('a'));
    }

    @Test
    public void testNDFAtoString() {
        // Add transitions and epsilon transitions
        etat1.ajouterTransition('a', etat2);
        etat2.ajouterTransition('b', etat3);
        etat1.ajouterTransition(etat3); // Epsilon transition

        // Create an NDFA from etat1 to etat3
        NDFA_Struct ndfa = new NDFA_Struct(etat1, etat3);

        // Expected string output
        String expectedOutput = """
                0 -- a --> 1
                1 -- b --> 2
                0 -- EPSILON --> 2
                """;

        // Check if the NDFA string representation matches the expected output
        assertEquals(expectedOutput.trim(), ndfa.toString().trim());
    }

    @Test
    public void testMultipleEpsilonTransitions() {
        // Add multiple epsilon transitions
        etat1.ajouterTransition(etat2);
        etat1.ajouterTransition(etat3);

        // Check that both transitions exist
        assertTrue(etat1.transitionsEpsilon.contains(etat2));
        assertTrue(etat1.transitionsEpsilon.contains(etat3));
    }

    @Test
    public void testEmptyNDFA() {
        // Create an empty NDFA (no transitions)
        NDFA_Struct ndfa = new NDFA_Struct(etat1, etat1);

        // Ensure the NDFA has no transitions
        assertEquals("", ndfa.toString().trim());
    }
}
