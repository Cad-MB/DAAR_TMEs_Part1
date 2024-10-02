package test.dfa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.dfa.DFA;
import src.dfa.DFADeterminisation;
import src.regex.RegExTree;
import src.regex.RegExTreeParser;
import src.ndfa.NDFA;
import src.ndfa.NDFAParser;
import src.minimization.DFAMinimization;

import static org.junit.jupiter.api.Assertions.*;

public class DFADeterminisationTest {

    @BeforeEach
    public void setUp() {
        NDFA.Etat.compteur = 0;  // Reset state counter before each test (if needed)
    }

    /**
     * Helper method to get a human-readable representation of DFA transitions.
     */
    private String getTransitionString(DFA.Etat source, int symbol) {
        DFA.Etat target = (DFA.Etat) source.obtenirTransition(symbol).iterator().next();
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
        RegExTree tree = RegExTreeParser.parse(regex);
        NDFA ndfa = NDFAParser.parseTreeToNDFA(tree);
        DFA dfa = DFADeterminisation.determinise(ndfa);

        // Ensure DFA and initial/accepting states are valid
        assertNotNull(dfa);
        assertNotNull(dfa.etatInitial);
        assertNotNull(dfa.etatAcceptant);

        // Extract initial state (dynamic, not relying on exact ID)
        DFA.Etat initialState = (DFA.Etat) dfa.etatInitial;

        // Check transitions based on DFA states
        DFA.Etat stateAfterA = (DFA.Etat) initialState.obtenirTransition('a').iterator().next();
        DFA.Etat stateAfterB = (DFA.Etat) initialState.obtenirTransition('b').iterator().next();

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
        RegExTree tree = RegExTreeParser.parse(regex);
        NDFA ndfa = NDFAParser.parseTreeToNDFA(tree);
        DFA dfa = DFADeterminisation.determinise(ndfa);

        // Ensure DFA and initial/accepting states are valid
        assertNotNull(dfa);
        assertNotNull(dfa.etatInitial);
        assertNotNull(dfa.etatAcceptant);

        // Extract initial state
        DFA.Etat initialState = (DFA.Etat) dfa.etatInitial;

        // Check transitions
        DFA.Etat stateAfterA = (DFA.Etat) initialState.obtenirTransition('a').iterator().next();
        DFA.Etat stateAfterB = (DFA.Etat) stateAfterA.obtenirTransition('b').iterator().next();

        assertNotNull(stateAfterA);
        assertNotNull(stateAfterB);

        // Validate transitions
        assertEquals("[4 -- a --> 5]", getTransitionString(initialState, 'a'));
        assertEquals("[5 -- b --> 6]", getTransitionString(stateAfterA, 'b'));

        // Ensure stateAfterB is the accepting state
        assertTrue(dfa.etatAcceptant.contains(stateAfterB));
    }

    /**
     * Test for Kleene star (a*)
     */
    @Test
    public void testKleeneStar() throws Exception {
        // Use NDFA to create DFA
        String regex = "a*";
        RegExTree tree = RegExTreeParser.parse(regex);
        NDFA ndfa = NDFAParser.parseTreeToNDFA(tree);
        DFA dfa = DFADeterminisation.determinise(ndfa);

        // Ensure DFA and initial/accepting states are valid
        assertNotNull(dfa);
        assertNotNull(dfa.etatInitial);
        assertNotNull(dfa.etatAcceptant);

        // Extract initial state
        DFA.Etat initialState = (DFA.Etat) dfa.etatInitial;

        // Check transition on 'a'
        DFA.Etat stateAfterA = (DFA.Etat) initialState.obtenirTransition('a').iterator().next();

        // Ensure loop transition (a*)
        assertNotNull(stateAfterA);

        // Validate loop transitions
        assertEquals("[4 -- a --> 5]", getTransitionString(initialState, 'a'));
        assertEquals("[5 -- a --> 5]", getTransitionString(stateAfterA, 'a'));
    }

    @Test
    public void testMultipleEndings () throws Exception {
        String regex = "a*b|cd*";
        RegExTree tree = RegExTreeParser.parse(regex);
        NDFA ndfa = NDFAParser.parseTreeToNDFA(tree);
        DFA dfa = DFADeterminisation.determinise(ndfa);
        DFA dfaMin = DFAMinimization.minimize(dfa);

        System.out.println(dfaMin.etatInitial);
    }

    @Test
    public void testInitialState () throws Exception {
        String regex = "a*b|cd*|rd*rf";
        RegExTree tree = RegExTreeParser.parse(regex);
        NDFA ndfa = NDFAParser.parseTreeToNDFA(tree);
        DFA dfa = DFADeterminisation.determinise(ndfa);
        DFA dfaMin = DFAMinimization.minimize(dfa);

        System.out.println(dfaMin.etatInitial.transitions);
    }

}
