package test.step2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.step1_RegExToTree.RegExTree_Struct;
import src.step1_RegExToTree.RegExTree_from_RegEx_Parser;
import src.step2_TreeToNDFA.NDFA_Struct;
import src.step2_TreeToNDFA.NDFA_from_RegExTree_Parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@code NDFA_from_RegExTree_Parser} class.
 * These tests validate the conversion of regular expression syntax trees to NDFAs.
 */
public class NDFA_from_RegExTree_ParserTest {

    @BeforeEach
    public void setUp() {
        // Reset state counter before each test (if needed).
        NDFA_Struct.Etat.compteur = 0;
    }

    /**
     * Test NDFA conversion for alternation (a|b)
     */
    @Test
    public void testAlternation() throws Exception {
        String regex = "a|b";
        RegExTree_Struct tree = RegExTree_from_RegEx_Parser.parse(regex);
        NDFA_Struct ndfa = NDFA_from_RegExTree_Parser.parseTreeToNDFA(tree);

        // Check the NDFA has valid initial and accepting states
        assertNotNull(ndfa);
        assertNotNull(ndfa.etatInitial);
        assertNotNull(ndfa.etatAcceptant);

        // Get the epsilon transitions from the initial state
        Set<NDFA_Struct.Etat> epsilonTransitions = ndfa.etatInitial.transitionsEpsilon;

        // Ensure there are two epsilon transitions
        assertEquals(2, epsilonTransitions.size());

        // Separate the two paths for 'a' and 'b'
        Iterator<NDFA_Struct.Etat> iterator = epsilonTransitions.iterator();
        NDFA_Struct.Etat etatA = iterator.next();
        NDFA_Struct.Etat etatB = iterator.next();

        // Check the 'a' path
        assertNotNull(etatA.obtenirTransition((int) 'a'));
        NDFA_Struct.Etat etatA2 = etatA.obtenirTransition((int) 'a').iterator().next();
        assertNotNull(etatA2.transitionsEpsilon);
        assertTrue(etatA2.transitionsEpsilon.contains(ndfa.etatAcceptant));

        // Check the 'b' path
        assertNotNull(etatB.obtenirTransition((int) 'b'));
        NDFA_Struct.Etat etatB2 = etatB.obtenirTransition((int) 'b').iterator().next();
        assertNotNull(etatB2.transitionsEpsilon);
        assertTrue(etatB2.transitionsEpsilon.contains(ndfa.etatAcceptant));
    }



    /**
     * Test NDFA conversion for concatenation (ab)
     */
    @Test
    public void testConcatenation() throws Exception {
        String regex = "ab";
        RegExTree_Struct tree = RegExTree_from_RegEx_Parser.parse(regex);
        NDFA_Struct ndfa = NDFA_from_RegExTree_Parser.parseTreeToNDFA(tree);

        // Validate the transitions for 'a' from the initial state
        assertNotNull(ndfa.etatInitial.obtenirTransition((int) 'a'));
        NDFA_Struct.Etat etatAfterA = ndfa.etatInitial.obtenirTransition((int) 'a').iterator().next();
        assertNotNull(etatAfterA);

        // Validate the epsilon transition after 'a'
        assertNotNull(etatAfterA.transitionsEpsilon);
        NDFA_Struct.Etat etatAfterEpsilon = etatAfterA.transitionsEpsilon.iterator().next();
        assertNotNull(etatAfterEpsilon);

        // Validate the transitions for 'b' from the state after the epsilon transition
        assertNotNull(etatAfterEpsilon.obtenirTransition((int) 'b'));
        NDFA_Struct.Etat etatAfterB = etatAfterEpsilon.obtenirTransition((int) 'b').iterator().next();
        assertNotNull(etatAfterB);

        // Verify that the final state corresponds to the accepting state
        assertEquals(ndfa.etatAcceptant, etatAfterB);
    }


    /**
     * Test NDFA conversion for Kleene star (a*)
     */
    @Test
    public void testKleeneStar() throws Exception {
        String regex = "a*";
        RegExTree_Struct tree = RegExTree_from_RegEx_Parser.parse(regex);
        NDFA_Struct ndfa = NDFA_from_RegExTree_Parser.parseTreeToNDFA(tree);

        // Validate epsilon transition from initial state (State 0) to State 1
        assertFalse(ndfa.etatInitial.transitionsEpsilon.isEmpty());
        NDFA_Struct.Etat etat1 = ndfa.etatInitial.transitionsEpsilon.stream()
                .filter(etat -> etat != ndfa.etatAcceptant)  // Skip direct transition to accepting state
                .findFirst().orElse(null);
        assertNotNull(etat1);

        // Validate epsilon transition from initial state (State 0) directly to accepting state (State 3)
        assertTrue(ndfa.etatInitial.transitionsEpsilon.contains(ndfa.etatAcceptant));

        // Validate transition from State 1 on 'a' to State 2
        assertNotNull(etat1.obtenirTransition((int) 'a'));
        NDFA_Struct.Etat etat2 = etat1.obtenirTransition((int) 'a').iterator().next();
        assertNotNull(etat2);

        // Validate epsilon transition from State 2 back to State 1 (looping behavior for Kleene star)
        assertTrue(etat2.transitionsEpsilon.contains(etat1));

        // Validate epsilon transition from State 2 to the accepting state (State 3)
        assertTrue(etat2.transitionsEpsilon.contains(ndfa.etatAcceptant));
    }
}
