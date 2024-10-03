package src;

import src.dfa.DFA;
import src.dfa.DFADeterminisation;
import src.kmp.KMPAlgorithm;
import src.minimization.DFAMinimization;
import src.ndfa.NDFA;
import src.regex.RegExTree;
import src.regex.RegExTreeParser;
import src.ndfa.NDFAParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    // ANSI escape codes for colored output
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
        StringBuilder fileContent = new StringBuilder();

        try {
            Scanner fileScanner = new Scanner(new File(filename));
            while (fileScanner.hasNextLine()) {
                fileContent.append(fileScanner.nextLine()).append("\n");
            }
            fileScanner.close();
            return fileContent.toString();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found at " + filename);
            return null;
        }
    }


    /**
     * Method to choose which search algorithm to use.
     *
     * @param pattern  The pattern to search for.
     * @param filename The file in which to search.
     */
    public static void search(String pattern, String filename) {
        Scanner scanner = new Scanner(System.in);

        try {
            String filePath = "Backend/resources/texts/" + filename;
            String text = readFile(filePath);  // Read the file content

            System.out.println("Choose the search method:");
            System.out.println("1. Automaton (DFA)");
            System.out.println("2. KMP Algorithm");
            System.out.print("Enter your choice (1/2): ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Clear the scanner buffer

            if (choice == 1) {
                // Automaton approach
                automatonSearch(pattern, text);
            } else if (choice == 2) {
                // KMP approach
                kmpSearch(pattern, text);
            } else {
                System.out.println("Invalid choice. Please choose 1 or 2.");
            }

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    /**
     * Method to search using the DFA/Automaton approach.
     *
     * @param pattern The regular expression to search for.
     * @param text    The text to search in.
     */
    public static void automatonSearch(String pattern, String text) {
        try {
            // Parse the regular expression into a tree
            RegExTree tree = RegExTreeParser.parse(pattern);
            if (tree == null) {
                System.err.println("Error, Parsed Regex Tree is null");
                return;
            }
            // Convert the regex tree into an NDFA, determinize, and minimize it into a DFA
            NDFA ndfa = NDFAParser.parseTreeToNDFA(tree);
            DFA dfa = DFADeterminisation.determinise(ndfa);
            dfa = DFAMinimization.minimize(dfa);

            // Highlight matches in the text
            DFASearch.highlightPatternInText(text, dfa);

        } catch (Exception e) {
            System.err.println("Error processing DFA search: " + e.getMessage());
        }
    }

    /**
     * Method to search using the KMP Algorithm.
     *
     * @param pattern The pattern to search for.
     * @param text    The text to search in.
     */
    public static void kmpSearch(String pattern, String text) {
        KMPAlgorithm kmpMatcher = new KMPAlgorithm(pattern, text);

        // Prepare KMP for searching
        kmpMatcher.generatePatternCharacters();
        kmpMatcher.generateLpsTable();

        // Get matching lines
        ArrayList<String> matchingLines = kmpMatcher.searchPatternInText();

        // Display results with highlighted patterns
        System.out.println("Search Results:");
        for (String line : matchingLines) {
            String highlightedLine = line.replace(pattern, GREEN + pattern + RESET);
            System.out.println(highlightedLine);
        }
        System.out.println(matchingLines.size() + " lines found.");
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Main <pattern> <filename>");
            return;
        }

        String pattern = args[0];
        String filename = args[1];

        search(pattern, filename);  // Start the search
    }
}
