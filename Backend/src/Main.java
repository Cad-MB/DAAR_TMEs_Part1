package src;

import src.dfa.DFA;
import src.dfa.DFADeterminisation;
import src.minimization.DFAMinimization;
import src.ndfa.NDFA;
import src.regex.RegExTree;
import src.regex.RegExTreeParser;
import src.ndfa.NDFAParser;

import java.io.IOException;
import java.nio.file.Paths;

// A main class that we can test be executing the jar and giving the methode, regex-pattern and the filename as params to the command.
public class Main {
    public static final String GREEN = "\u001B[32m";
    public static final String BOLD = "\033[1m";   // Bold text
    public static final String RESET = "\u001B[0m";

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java -jar myprogram.jar <method> <regex-pattern> <filename>");
            System.out.println("method: 'automate' for DFA or 'kmp' for KMP algorithm");
            return;
        }

        String method = args[0];
        String regex = args[1];
        String filePath = args[2];

        try {
            // Read the file content into a String
            String text = DFASearch.readFile(filePath);

            if (method.equalsIgnoreCase("automate")) {
                runAutomateMethod(regex, text);
            } else if (method.equalsIgnoreCase("kmp")) {
                runKMPMethod(regex, text);
            } else {
                System.out.println("Unknown method. Please choose either 'automate' or 'kmp'.");
            }

        } catch (IOException e) {
            System.err.println("Error: File not found at " + Paths.get(filePath).toAbsolutePath());
        }
    }

    private static void runAutomateMethod(String regex, String text) {
        try {
            // Create NDFA from regex and convert to minimized DFA
            RegExTree tree = RegExTreeParser.parse(regex);
            if (tree == null) {
                System.err.println("Error, Parsed Regex Tree is null");
                return;
            }
            NDFA ndfa = NDFAParser.parseTreeToNDFA(tree);
            DFA dfa = DFADeterminisation.determinise(ndfa);
            dfa = DFAMinimization.minimize(dfa);

            // Search for the pattern in the text and highlight lines containing it
            int foundWords = DFASearch.highlightPatternInText(text, dfa, true);

        } catch (Exception e) {
            System.err.println("Error in the automate method: " + e.getMessage());
        }
    }

    private static void runKMPMethod(String regex, String text) {
        KMPAlgorithm kmpMatcher = new KMPAlgorithm(regex, text);

        kmpMatcher.generatePatternCharacters();
        kmpMatcher.generateLpsTable();

        var matchingLines = kmpMatcher.searchPatternInText();

        System.out.println("Search Results:");
        for (String line : matchingLines) {
            // Highlighting the found pattern
            String highlightedLine = line.replace(regex, KMPAlgorithm.GREEN + regex + KMPAlgorithm.RESET);
            System.out.println(highlightedLine);
        }
        System.out.println("There are " + GREEN + BOLD + matchingLines.size() + RESET + " Matched lines");
    }
}
