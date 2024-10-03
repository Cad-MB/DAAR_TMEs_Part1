package src.dfa.export;

import src.dfa.DFA;
import src.dfa.DFADeterminisation;
import src.ndfa.NDFA;
import src.ndfa.export.NDFAGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The {@code DFAMinimizationGenerator} class reads regular expressions from a CSV-like file,
 * converts each expression into an NDFA, determinizes the NDFA into a DFA, and exports the DFA as a JSON file.
 * This class extends the functionality of {@code NDFAGenerator}.
 */
public class DFAGenerator extends NDFAGenerator {

    private static final List<String> regexList = new ArrayList<>();  // Store the list of regular expressions

    /**
     * Override the method to build a DFA from a regular expression by first creating an NDFA and then determinizing it.
     *
     * @param regEx The regular expression to parse and convert into a DFA.
     * @return The DFA representing the DFA created from the NDFA.
     */
    public static DFA buildDFAFromRegex(String regEx) {
        // Reset state IDs before building a new NDFA
        resetStateCounter();

        // Call the superclass method to build the NDFA from the regular expression
        buildNDFAFromRegex(regEx);  // This builds the NDFA and stores it in the superclass

        // Determinize the NDFA into a DFA

        return DFADeterminisation.determinise(getNDFA());  // Return the determinized DFA
    }

    /**
     * Helper method to get the NDFA built by the superclass.
     *
     * @return The NDFA created by the superclass.
     */
    public static NDFA getNDFA() {
        // Access the NDFA that was created in the superclass (since it's static in the superclass)
        return ndfa;
    }

    /**
     * Main method to execute the conversion from regular expressions to NDFA.
     * <p>
     * The input CSV file and output folder are both in {@code src.step2_TreeToNDFA.export}.
     */
    public static void main(String[] args) {
        String inputFilePath = "Backend/resources/regexLists/regexList0.csv";
        String outputFolderPath = "Backend/resources/samples/dfa";

        // Check if the specified file exists
        File inputFile = new File(inputFilePath);
        if (!inputFile.exists()) {
            System.out.println("Error: The file regexList0.csv was not found in the input folder.");
            return;
        }

        // Process the specified file
        processRegexFile(inputFilePath, outputFolderPath);
    }

    public static void processRegexFile(String inputFilePath, String outputFolderPath) {
        // Ensure output folder exists
        File folder = new File(outputFolderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            int regexIndex = 1;

            System.out.println("Building Ndfa from the regex");

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

            System.out.println("Building minimized DFA from the DFA.");
            // Now process the DFA for each regex
            for (int i = 0; i < regexList.size(); i++) {
                String regEx = regexList.get(i);
                // Build DFA from NDFA
                DFA dfa = buildDFAFromRegex(regEx);

                // Define the DFA JSON file name
                String dfaFilename = outputFolderPath + "/DFASample" + (i + 1) + ".json";
                // Export the DFA to a JSON file
                DFAExporter dfaExporter = new DFAExporter();
                dfaExporter.toJsonFile(dfa, dfaFilename);
            }

            System.out.println("DFA JSON generation complete.");

        } catch (IOException e) {
            System.err.println("Error reading the regex file: " + e.getMessage());
        }
    }
}
