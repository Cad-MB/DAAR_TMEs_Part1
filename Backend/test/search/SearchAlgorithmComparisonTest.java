package test.search;

import org.junit.jupiter.api.Test;
import src.KMPAlgorithm;
import src.DFASearch;
import src.dfa.DFA;
import src.regex.RegExTree;
import src.regex.RegExTreeParser;
import src.ndfa.NDFA;
import src.ndfa.NDFAParser;
import src.minimization.DFAMinimization;
import src.dfa.DFADeterminisation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchAlgorithmComparisonTest {

    private static final String FILENAME = "Backend/resources/texts/41011-0.txt";
    private static final String PATTERN1 = "Chihuahua";
    private static final String PATTERN2 = "chihuahua";
    private static final String PATTERN3 = "CHIHUAHUA";
    public static final String RED = "\u001B[31m";
    public static final String BOLD = "\033[1m";   // Bold text
    public static final String GREEN = "\u001B[32m";
    public static final String RESET = "\u001B[0m";

    @Test
    public void compareHighlightingBetweenKMPAndDFA1() throws IOException {
        // Read the text from the file
        String text = readFile(FILENAME);

        // Capture highlighted lines from KMP algorithm
        List<String> kmpHighlightedLines = captureKMPSearchOutput(text, PATTERN1);

        // Capture highlighted lines from DFA algorithm
        List<String> dfaHighlightedLines = captureDFASearchOutput(text, buildDFAFromRegex(PATTERN1));

        // Check if the results match
        if (kmpHighlightedLines.equals(dfaHighlightedLines)) {
            System.out.println("Match: Both algorithms highlighted the same lines.");
        } else {
            System.out.println("Mismatch: The algorithms highlighted different lines.");
            reportDifferences(kmpHighlightedLines, dfaHighlightedLines);
        }

        // Assert that both results match
        assertTrue(kmpHighlightedLines.equals(dfaHighlightedLines), "KMP and DFA did not match");
    }

    @Test
    public void compareHighlightingBetweenKMPAndDFA2() throws IOException {
        // Read the text from the file
        String text = readFile(FILENAME);

        // Capture highlighted lines from KMP algorithm
        List<String> kmpHighlightedLines = captureKMPSearchOutput(text, PATTERN2);

        // Capture highlighted lines from DFA algorithm
        List<String> dfaHighlightedLines = captureDFASearchOutput(text, buildDFAFromRegex(PATTERN2));

        // Check if the results match
        if (kmpHighlightedLines.equals(dfaHighlightedLines)) {
            System.out.println("Match: Both algorithms highlighted the same lines.");
        } else {
            System.out.println("Mismatch: The algorithms highlighted different lines.");
            reportDifferences(kmpHighlightedLines, dfaHighlightedLines);
        }

        // Assert that both results match
        assertTrue(kmpHighlightedLines.equals(dfaHighlightedLines), "KMP and DFA did not match");
    }

    @Test
    public void compareHighlightingBetweenKMPAndDFA3() throws IOException {
        // Read the text from the file
        String text = readFile(FILENAME);

        // Capture highlighted lines from KMP algorithm
        List<String> kmpHighlightedLines = captureKMPSearchOutput(text, PATTERN3);

        // Capture highlighted lines from DFA algorithm
        List<String> dfaHighlightedLines = captureDFASearchOutput(text, buildDFAFromRegex(PATTERN3));

        // Check if the results match
        if (kmpHighlightedLines.equals(dfaHighlightedLines)) {
            System.out.println("Match: Both algorithms highlighted the same lines.");
        } else {
            System.out.println("Mismatch: The algorithms highlighted different lines.");
            reportDifferences(kmpHighlightedLines, dfaHighlightedLines);
        }

        // Assert that both results match
        assertTrue(kmpHighlightedLines.equals(dfaHighlightedLines), "KMP and DFA did not match");
    }

    /**
     * Helper method to print the differences between KMP and DFA results
     */
    private void reportDifferences(List<String> kmpHighlighted, List<String> dfaHighlighted) {
        System.out.println("KMP found these lines that DFA didn't:");
        for (String line : kmpHighlighted) {
            if (!dfaHighlighted.contains(line)) {
                System.out.println("KMP: " + line);
            }
        }

        System.out.println("DFA found these lines that KMP didn't:");
        for (String line : dfaHighlighted) {
            if (!kmpHighlighted.contains(line)) {
                System.out.println("DFA: " + line);
            }
        }
    }

    /**
     * Helper method to capture the output of DFASearch.highlightPatternInText
     */
    private List<String> captureDFASearchOutput(String text, DFA dfa) {

        // Create a stream to hold the output
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;  // Save the original System.out

        // Temporarily redirect System.out to the new stream
        System.setOut(new PrintStream(byteArrayOutputStream));

        // Call the DFA search method (which prints to System.out)
        DFASearch.highlightPatternInText(text, dfa, true);

        // Restore System.out to its original state
        System.setOut(originalOut);

        // Convert the captured output to a list of lines
        String[] outputLines = byteArrayOutputStream.toString().split("\n");
        List<String> result = new ArrayList<>();
        for (String line : outputLines) {
            result.add(line.trim());  // Trim any extra whitespace/newlines
        }

        return result;
    }

    /**
     * Helper method to capture the output of KMPSearch.highlightPatternInText
     */
    private List<String> captureKMPSearchOutput(String text, String pattern) {
        // Create a stream to hold the output
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;  // Save the original System.out

        // Temporarily redirect System.out to the new stream
        System.setOut(new PrintStream(byteArrayOutputStream));

        runKMPSearch(pattern, text);
        // Restore System.out to its original state
        System.setOut(originalOut);

        // Convert the captured output to a list of lines
        String[] outputLines = byteArrayOutputStream.toString().split("\n");
        List<String> result = new ArrayList<>();
        for (String line : outputLines) {
            result.add(line.trim());  // Trim any extra whitespace/newlines
        }
        return result;
    }

    private void runKMPSearch(String pattern, String text) {
        KMPAlgorithm kmpMatcher = new KMPAlgorithm(pattern, text);

        kmpMatcher.generatePatternCharacters();
        kmpMatcher.generateLpsTable();

        ArrayList<String> matchingLines = kmpMatcher.searchPatternInText();
        for (String line : matchingLines) {
            // Highlighting the found pattern
            String highlightedLine = line.replace(pattern, RED + BOLD + pattern + RESET);
            System.out.println(highlightedLine);
        }
        System.out.println("There are " + GREEN + BOLD + matchingLines.size() + RESET + " Matched lines");
    }
    /**
     * Builds a DFA from a regex pattern.
     */
    private DFA buildDFAFromRegex(String regex) {
        try {
            RegExTree tree = RegExTreeParser.parse(regex);
            NDFA ndfa = NDFAParser.parseTreeToNDFA(tree);
            DFA dfa = DFADeterminisation.determinise(ndfa);
            return DFAMinimization.minimize(dfa);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}
