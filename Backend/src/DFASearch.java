package src;

import src.dfa.DFA;
import src.dfa.DFADeterminisation;
import src.minimization.DFAMinimization;
import src.ndfa.NDFA;
import src.regex.RegExTree;
import src.regex.RegExTreeParser;
import src.ndfa.NDFAParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

public class DFASearch {

    // ANSI escape code for red text
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BOLD = "\033[1m";   // Bold text
    public static final String RESET = "\u001B[0m";

    /**
     * Method to read the contents of the file into a String.
     *
     * @param filename The name of the file to be read.
     * @return The text contained in the file as a single String.
     * @throws IOException If there is an error while reading the file.
     */
    public static String readFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    /**
     * Method to check if a string is accepted by the DFA, case-insensitive.
     *
     * @param dfa The DFA to use for matching.
     * @param input The input string to match.
     * @return True if the string is accepted by the DFA, false otherwise.
     */
    public static boolean isAcceptedByDFA(DFA dfa, String input) {
        DFA.Etat currentState = (DFA.Etat) dfa.etatInitial;
        for (char c : input.toCharArray()) {

            // Try to find a transition using the lowercase character
            Set<NDFA.Etat> transitions = currentState.obtenirTransition(c);
            if (transitions == null || transitions.isEmpty()) {
                return false; // No valid transition, input rejected
            }
            currentState = (DFA.Etat) transitions.iterator().next(); // Deterministic: one target state
        }

        return dfa.etatAcceptant.contains(currentState); // Check if the final state is accepting
    }

    /**
     * Method to find and highlight all lines containing the pattern recognized by DFA, case-insensitive.
     * We convert both the text and the pattern to lowercase for matching, but we highlight in the original text.
     *
     * @param text The full text in which to search for the pattern.
     * @param dfa  The DFA representing the minimized regex.
     */
    public static void highlightPatternInText(String text, DFA dfa) {
        String[] lines = text.split("\n");  // Split the text into lines

        int matchedLinesCount = 0;

        for (String line : lines) {
            String originalLine = line;  // Keep the original line for highlighting

            // Iterate through the entire line looking for matches
            int index = 0;
            while (index < line.length()) {
                boolean matchFound = false;
                // Attempt to find the longest match from the current position
                for (int end = index + 1; end <= line.length(); end++) {
                    String Substring = line.substring(index, end);  // Case-insensitive substring

                    if (isAcceptedByDFA(dfa, Substring)) {
                        // Highlight the original case substring in the line
                        String highlightedLine = originalLine.substring(0, index)
                                + RED + BOLD + originalLine.substring(index, end) + RESET
                                + originalLine.substring(end);
                        System.out.println(highlightedLine);
                        // Move index to the end of the found match
                        index = end;
                        matchFound = true;
                        matchedLinesCount ++ ;
                        break;  // Break to avoid multiple highlights in the same line
                    }
                }

                if (!matchFound) {
                    // If no match is found, move to the next character
                    index++;
                }
            }
        }
        System.out.println("There are " + GREEN + BOLD + matchedLinesCount + RESET + " Matched lines");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (scanner) {
            System.out.print("Enter the regular expression to create the DFA: ");
            String regex = scanner.nextLine(); // Assume the regex will be converted to a DFA

            RegExTree tree = RegExTreeParser.parse(regex);
            if (tree == null) {
                System.err.println("Error, Parsed Regex Tree is null");
                return;
            }
            NDFA ndfa = NDFAParser.parseTreeToNDFA(tree);
            DFA dfa = DFADeterminisation.determinise(ndfa);
            dfa = DFAMinimization.minimize(dfa);


            // Minimize the DFA
            DFA minimizedDFA = DFAMinimization.minimize(dfa);

            // Read the file content into a String
            String filename = "Backend/resources/texts/56667-0.txt";
            String text = readFile(filename);

            // Search for the pattern in the text and highlight lines containing it
            highlightPatternInText(text, minimizedDFA);

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

}
