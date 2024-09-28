package test.step3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.step1_RegExTreeConversion.RegExTree_Struct;
import src.step1_RegExTreeConversion.RegExTree_from_RegEx_Parser;
import src.step2_NDFA.NDFA_Struct;
import src.step2_NDFA.NDFA_from_RegExTree_Parser;
import src.step3_Determinisation.DFA_Determinisation;
import src.step3_Determinisation.DFA_Struct;

import static org.junit.jupiter.api.Assertions.*;

public class DFA_DeterminisationTest {

    @BeforeEach
    public void setUp() {
        NDFA_Struct.Etat.compteur = 0;  // Reset state counter before each test (if needed)
    }

    /**
     * Helper method to get a human-readable representation of DFA transitions.
     */
    private String getTransitionString(DFA_Struct.Etat source, int symbol) {
        DFA_Struct.Etat target = (DFA_Struct.Etat) source.obtenirTransition(symbol).iterator().next();
        if (target != null) {
            return "[" + source.id + " -- " + (char) symbol + " --> " + target.id + "]";
        }
        return "";
    }

    /**
     * Test for alternation (a|b)
     */
    @Test
    public void testAlternation() throws Exception {
        // Use NDFA to create DFA
        String regex = "a|b";
        RegExTree_Struct tree = RegExTree_from_RegEx_Parser.parse(regex);
        NDFA_Struct ndfa = NDFA_from_RegExTree_Parser.parseTreeToNDFA(tree);
        DFA_Struct dfa = DFA_Determinisation.determinise(ndfa);

        // Ensure DFA and initial/accepting states are valid
        assertNotNull(dfa);
        assertNotNull(dfa.etatInitial);
        assertNotNull(dfa.etatAcceptant);

        // Extract initial state (dynamic, not relying on exact ID)
        DFA_Struct.Etat initialState = (DFA_Struct.Etat) dfa.etatInitial;

        // Check transitions based on DFA states
        DFA_Struct.Etat stateAfterA = (DFA_Struct.Etat) initialState.obtenirTransition('a').iterator().next();
        DFA_Struct.Etat stateAfterB = (DFA_Struct.Etat) initialState.obtenirTransition('b').iterator().next();

        assertNotNull(stateAfterA);
        assertNotNull(stateAfterB);

        // Validate transitions
        assertEquals("[6 -- a --> 7]", getTransitionString(initialState, 'a'));
        assertEquals("[6 -- b --> 8]", getTransitionString(initialState, 'b'));
    }

    /**
     * Test for concatenation (ab)
     */
    @Test
    public void testConcatenation() throws Exception {
        // Use NDFA to create DFA
        String regex = "ab";
        RegExTree_Struct tree = RegExTree_from_RegEx_Parser.parse(regex);
        NDFA_Struct ndfa = NDFA_from_RegExTree_Parser.parseTreeToNDFA(tree);
        DFA_Struct dfa = DFA_Determinisation.determinise(ndfa);

        // Ensure DFA and initial/accepting states are valid
        assertNotNull(dfa);
        assertNotNull(dfa.etatInitial);
        assertNotNull(dfa.etatAcceptant);

        // Extract initial state
        DFA_Struct.Etat initialState = (DFA_Struct.Etat) dfa.etatInitial;

        // Check transitions
        DFA_Struct.Etat stateAfterA = (DFA_Struct.Etat) initialState.obtenirTransition('a').iterator().next();
        DFA_Struct.Etat stateAfterB = (DFA_Struct.Etat) stateAfterA.obtenirTransition('b').iterator().next();

        assertNotNull(stateAfterA);
        assertNotNull(stateAfterB);

        // Validate transitions
        assertEquals("[4 -- a --> 5]", getTransitionString(initialState, 'a'));
        assertEquals("[5 -- b --> 6]", getTransitionString(stateAfterA, 'b'));

        // Ensure stateAfterB is the accepting state
        assertEquals(dfa.etatAcceptant, stateAfterB);
    }

    /**
     * Test for Kleene star (a*)
     */
    @Test
    public void testKleeneStar() throws Exception {
        // Use NDFA to create DFA
        String regex = "a*";
        RegExTree_Struct tree = RegExTree_from_RegEx_Parser.parse(regex);
        NDFA_Struct ndfa = NDFA_from_RegExTree_Parser.parseTreeToNDFA(tree);
        DFA_Struct dfa = DFA_Determinisation.determinise(ndfa);

        // Ensure DFA and initial/accepting states are valid
        assertNotNull(dfa);
        assertNotNull(dfa.etatInitial);
        assertNotNull(dfa.etatAcceptant);

        // Extract initial state
        DFA_Struct.Etat initialState = (DFA_Struct.Etat) dfa.etatInitial;

        // Check transition on 'a'
        DFA_Struct.Etat stateAfterA = (DFA_Struct.Etat) initialState.obtenirTransition('a').iterator().next();

        // Ensure loop transition (a*)
        assertNotNull(stateAfterA);

        // Validate loop transitions
        assertEquals("[4 -- a --> 5]", getTransitionString(initialState, 'a'));
        assertEquals("[5 -- a --> 5]", getTransitionString(stateAfterA, 'a'));

        // Ensure initial state is also the accepting state (as per Kleene star behavior)
        assertEquals(dfa.etatInitial, dfa.etatAcceptant);
    }
}
