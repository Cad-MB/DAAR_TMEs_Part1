package src.etudeExp;
import src.DFASearch;
import src.dfa.DFA;
import src.dfa.DFADeterminisation;
import src.kmp.KMPAlgorithm;
import src.minimization.DFAMinimization;
import src.ndfa.NDFA;
import src.ndfa.NDFAParser;
import src.regex.RegExTree;
import src.regex.RegExTreeParser;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class EtudeExp {
    public static void main(String[] args) throws IOException {
        // Path to the book text and test words
        String bookPath = "Backend/resources/texts/56667-0.txt";
        String testWordsPath = "Backend/src/etudeExp/test_words.txt";

        // Read the book text
        String text = readFile(bookPath);

        // Read test words
        List<String> testWords = Files.readAllLines(Paths.get(testWordsPath));

        // clean up out results file in order to create new testemonies
        cleanupResultsFile();

        // Loop through the test words
        for (String word : testWords) {
            System.out.println("Testing word: " + word);
            int AutomatefoundWords = 0;
            ArrayList<String> matchingLines = new ArrayList<>();
            BufferedReader reader = null;
            // Time Method 1
            long startTimeAutomate = System.currentTimeMillis();

            try {
                // Create NDFA from regex and convert to minimized DFA
                RegExTree tree = RegExTreeParser.parse(word);
                if (tree == null) {
                    System.err.println("Error, Parsed Regex Tree is null");
                    return;
                }
                NDFA ndfa = NDFAParser.parseTreeToNDFA(tree);
                DFA dfa = DFADeterminisation.determinise(ndfa);
                dfa = DFAMinimization.minimize(dfa);

                // Search for the pattern in the text and highlight lines containing it
                AutomatefoundWords = DFASearch.highlightPatternInText(text, dfa, false);

            } catch (Exception e) {
                System.err.println("Error in the Ahu-ullman method: " + e.getMessage());
            }


            long endTimeAutomate = System.currentTimeMillis();
            long AutomateTime = endTimeAutomate - startTimeAutomate;


            // Testing KMP method:

            long startTimeKMP = System.currentTimeMillis();

            try {
                KMPAlgorithm kmpMatcher = new KMPAlgorithm(word, text);

                kmpMatcher.generatePatternCharacters();
                kmpMatcher.generateLpsTable();

                matchingLines = kmpMatcher.searchPatternInText();
            } catch (Exception e) {
                System.err.println("Error in the KMP method: " + e.getMessage());
            }

            long endTimeKMP = System.currentTimeMillis();

            long KMPTime = endTimeKMP - startTimeKMP;

            // Testing the real `egrep` :
            long startTimeEgrep = System.currentTimeMillis();

            try {
                // Create a process to execute egrep with the desired pattern and file
                ProcessBuilder processBuilder = new ProcessBuilder("egrep", word, bookPath);

                // Redirect the error stream to avoid issues
                processBuilder.redirectErrorStream(true);

                // Start the process and get the result
                Process process = processBuilder.start();

                // Capture the output of the command
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));


                // Wait for the process to finish
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.err.println("egrep command failed with exit code: " + exitCode);
                }

            } catch (Exception e) {
                System.err.println("Error in the egrep method: " + e.getMessage());
            }

            long endTimeEgrep = System.currentTimeMillis();

            long EgrepTime = endTimeEgrep - startTimeEgrep;

            // Output results
            System.out.println("Method 1 (Automate) Time: " + AutomateTime + "ms");
            System.out.println("Method 2 (KMP) Time: " + KMPTime + "ms");
            System.out.println("Method 3 (egrep) Time: " + EgrepTime + "ms");

            // Optionally, save results to file for analysis later
            saveResultsToFile(word, AutomateTime, AutomatefoundWords , KMPTime, matchingLines.size(), EgrepTime, reader );
        }
    }

    // Method to read the file
    public static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static void cleanupResultsFile()  throws IOException{
        File file = new File("Backend/src/etudeExp/results.csv");

        // Check if the file exists and is not empty
        if (file.exists() && file.length() > 0) {
            // Empty the file content before adding new results
            PrintWriter writer = new PrintWriter(file);
            writer.print("");  // This empties the file
            writer.close();
        }

        // Now append the results
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

        writer.write("word,AutomateTime,isThereAWordAutomate,KMPTime,isThereAWordKMP,egrepTime,isThereAWordEgrep\n");
        writer.close();
    }

    // Method to save results to a CSV file
    public static void saveResultsToFile(String word, long AutomateTime, int AutomatefoundWords, long KMPTime, int KMPfoundWords, long egrepTime, BufferedReader reader ) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("Backend/src/etudeExp/results.csv", true));

        boolean isThereAWordAutomate = AutomatefoundWords > 0;
        boolean isThereAWordKMP = KMPfoundWords > 0;
        boolean isThereAWordEgrep = reader != null;
        writer.write(word + "," + AutomateTime + "," + isThereAWordAutomate + "," + KMPTime + "," + isThereAWordKMP + "," + egrepTime + "," + isThereAWordEgrep);
        writer.newLine();
        writer.close();
    }

}
