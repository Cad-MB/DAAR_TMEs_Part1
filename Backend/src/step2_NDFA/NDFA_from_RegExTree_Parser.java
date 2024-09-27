package src.step2_NDFA;

import src.step1_RegExTreeConversion.RegExTree_from_RegEx_Parser;
import src.step1_RegExTreeConversion.RegExTree_Struct;

/**
 * The {@code DFA_Determinisation} class is responsible for converting a regular expression
 * syntax tree (represented by {@code RegExTree_Struct}) into a Non-Deterministic Finite Automaton (NDFA).
 *
 * <p>This class processes the structure of the regular expression tree, handling basic regex operations
 * such as concatenation, alternation, and Kleene star, and converts them into an NDFA. It also supports
 * individual characters or wildcard expressions (DOT).</p>
 *
 * <p>The generated NDFA is composed of states and transitions, and it follows the formal rules of
 * non-deterministic finite automata (NDFA), with epsilon (ε) transitions when necessary.</p>
 */
public class NDFA_from_RegExTree_Parser {
    private static final int SYMBOLES_ASCII = 256; // Number of ASCII symbols to be processed in the automaton.

        /**
     * Converts a regular expression syntax tree into a Non-Deterministic Finite Automaton (NDFA).
     *
     * <p>This method processes the tree structure of the regular expression and builds the corresponding NDFA.
     * It recursively processes the nodes in the tree and handles the following cases:
     * <ul>
     *   <li>Individual characters or wildcard (DOT)</li>
     *   <li>Concatenation of expressions</li>
     *   <li>Alternation (OR) of expressions</li>
     *   <li>Kleene star (repetition)</li>
     * </ul>
     * </p>
     *
     * @param arbreRegEx The regular expression syntax tree to be converted into an NDFA.
     * @return An {@code DFA_Struct} representing the generated NDFA from the regular expression tree.
     */
    public static NDFA_Struct parseTreeToNDFA(RegExTree_Struct arbreRegEx) {
        // Handle individual character or wildcard (DOT)
        if (arbreRegEx.subTrees.isEmpty()) {
            NDFA_Struct.Etat etatDebut = new NDFA_Struct.Etat(); // Start state
            NDFA_Struct.Etat etatFin = new NDFA_Struct.Etat();   // Accepting state

            // If not DOT (wildcard), add a specific transition for the character
            if (arbreRegEx.getRoot() != RegExTree_from_RegEx_Parser.DOT) {
                etatDebut.ajouterTransition(arbreRegEx.getRoot(), etatFin);
            } else {
                // Handle DOT, which matches any character
                for (int i = 0; i < SYMBOLES_ASCII; i++) {
                    etatDebut.ajouterTransition(i, etatFin);
                }
            }
            return new NDFA_Struct(etatDebut, etatFin);
        }

        // Handle concatenation (e.g., "ab" means first "a", then "b")
        if (arbreRegEx.getRoot() == RegExTree_from_RegEx_Parser.CONCAT) {
            NDFA_Struct gauche = parseTreeToNDFA(arbreRegEx.subTrees.get(0)); // Left subtree
            NDFA_Struct droite = parseTreeToNDFA(arbreRegEx.subTrees.get(1)); // Right subtree
            gauche.etatAcceptant.ajouterTransition(droite.etatInitial);       // Connect left accept state to right start state
            return new NDFA_Struct(gauche.etatInitial, droite.etatAcceptant); // Return combined NDFA
        }

        // Handle alternation (e.g., "a|b" means either "a" or "b")
        if (arbreRegEx.getRoot() == RegExTree_from_RegEx_Parser.ALTERN) {
            NDFA_Struct.Etat etatDebut = new NDFA_Struct.Etat();               // New start state
            NDFA_Struct gauche = parseTreeToNDFA(arbreRegEx.subTrees.get(0));  // Left subtree
            NDFA_Struct droite = parseTreeToNDFA(arbreRegEx.subTrees.get(1));  // Right subtree
            NDFA_Struct.Etat etatFin = new NDFA_Struct.Etat();                 // New accepting state

            etatDebut.ajouterTransition(gauche.etatInitial); // Epsilon transition to left start
            etatDebut.ajouterTransition(droite.etatInitial); // Epsilon transition to right start

            gauche.etatAcceptant.ajouterTransition(etatFin); // Left accept to final
            droite.etatAcceptant.ajouterTransition(etatFin); // Right accept to final

            return new NDFA_Struct(etatDebut, etatFin);      // Return combined NDFA
        }

        // Handle Kleene star (e.g., "a*" means repeat "a" zero or more times)
        if (arbreRegEx.getRoot() == RegExTree_from_RegEx_Parser.ETOILE) {
            NDFA_Struct.Etat etatDebut = new NDFA_Struct.Etat();               // New start state
            NDFA_Struct gauche = parseTreeToNDFA(arbreRegEx.subTrees.getFirst()); // Left subtree
            NDFA_Struct.Etat etatFin = new NDFA_Struct.Etat();                 // New accepting state

            etatDebut.ajouterTransition(gauche.etatInitial);   // Epsilon transition to start
            etatDebut.ajouterTransition(etatFin);              // Epsilon transition to accept state
            gauche.etatAcceptant.ajouterTransition(gauche.etatInitial); // Loop back (repetition)
            gauche.etatAcceptant.ajouterTransition(etatFin);   // Epsilon transition to accept state

            return new NDFA_Struct(etatDebut, etatFin);        // Return the NDFA
        }

        // Fallback case: If the expression is unknown or unsupported, return a default NDFA
        return new NDFA_Struct(new NDFA_Struct.Etat(), new NDFA_Struct.Etat());
    }
}