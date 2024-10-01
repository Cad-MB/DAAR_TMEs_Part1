package src.minimization.export;

import src.dfa.DFA;
import src.dfa.DFADeterminisation;
import src.dfa.export.DFAGenerator;
import src.minimization.DFAMinimization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The {@code DFAMinimizationGenerator} class reads regular expressions from a CSV-like file,
 * converts each expression into a minimized DFA, and exports the DFA as a JSON file.
 * This class extends the functionality of {@code DFAGenerator}.
 */
public class DFAMinimizationGenerator extends DFAGenerator {

    private static final List<String> regexList = new ArrayList<>();  // Store the list of regular expressions

    /**
     * Override the method to build a minimized DFA from a regular expression by first creating an NDFA,
     * determinizing it into a DFA, and then minimizing it.
     *
     * @param regEx The regular expression to parse and convert into a minimized DFA.
     * @return The DFA representing the minimized DFA.
     */
    public static DFA buildMinimizedDFAFromRegex(String regEx) {
        // Reset state IDs before building a new NDFA
        resetStateCounter();

        // Call the superclass method to build the NDFA from the regular expression
        System.out.println("Processing regex (NDFA): " + regEx);
        buildNDFAFromRegex(regEx);  // This builds the NDFA and stores it in the superclass

        // Determinize the NDFA into a DFA
        DFA dfa = DFADeterminisation.determinise(getNDFA());

        // Minimize the DFA

        return DFAMinimization.minimize(dfa);  // Return the minimized DFA
    }

    /**
     * Main method to execute the conversion from regular expressions to NDFA.
     * <p>
     * The input CSV file and output folder are both in {@code src.step2_TreeToNDFA.export}.
     */
    public static void main(String[] args) {
        String inputFilePath = "Backend/resources/regexLists/regexList0.csv";
        String outputFolderPath = "Backend/resources/samples/minimization";

        // Check if the specified file exists
        File inputFile = new File(inputFilePath);
        if (!inputFile.exists()) {
            System.out.println("Error: The file regexList0.csv was not found in the input folder.");
            return;
        }

        // Process the specified file
        processRegexFile(inputFilePath, outputFolderPath);
    }

    /**
     * Processes the regular expression file and generates DFA JSON files for each expression.
     *
     * @param inputFilePath   The path to the input file containing regular expressions.
     * @param outputFolderPath The folder where the DFA JSON files will be saved.
     */
    public static void processRegexFile(String inputFilePath, String outputFolderPath) {
        // Ensure output folder exists
        File folder = new File(outputFolderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            int regexIndex = 1;

            // Read the file line by line and store each regex
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                // Add regex to the list for later DFA processing
                regexList.add(line.trim());

                // Build NDFA from the current regex line
                buildNDFAFromRegex(line.trim());

                regexIndex++;
            }

            // Now process the DFA for each regex
            for (int i = 0; i < regexList.size(); i++) {
                String regEx = regexList.get(i);
                System.out.println("Converting NDFA to DFA for regex: " + regEx);

                // Build and minimize DFA from NDFA
                DFA dfa = buildMinimizedDFAFromRegex(regEx);

                // Define the DFA JSON file name
                String dfaFilename = outputFolderPath + "/DFAMinimizedSample" + (i + 1) + ".json";

                // Export the minimized DFA to a JSON file
                DFAMinimizationExporter dfaExporter = new DFAMinimizationExporter();
                dfaExporter.toJsonFile(dfa, dfaFilename);
            }

            System.out.println("DFA JSON generation complete.");

        } catch (IOException e) {
            System.err.println("Error reading the regex file: " + e.getMessage());
        }
    }
}
