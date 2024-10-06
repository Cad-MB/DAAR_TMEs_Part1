package src.ndfa.export;

import src.ndfa.NDFA;
import src.regex.RegExTree;
import src.regex.RegExTreeParser;
import src.ndfa.NDFAParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * The {@code NDFAGenerator} class reads regular expressions from a CSV-like file,
 * converts each expression into a Non-Deterministic Finite Automaton (NDFA),
 * and exports each NDFA as a JSON file.
 *
 * <p>The input file and output JSON files are located in the directory
 * {@code src.step2_TreeToNDFA.export}.</p>
 */
public class NDFAGenerator {

    protected static NDFA ndfa;

    /**
     * Resets the state ID counter before generating a new NDFA.
     */
    public static void resetStateCounter() {
        NDFA.Etat.compteur = 0;
    }

    /**
     * Parses a regular expression, converts it into an NDFA, and stores it in a static field.
     *
     * @param regEx The regular expression to parse and convert.
     */
    public static void buildNDFAFromRegex(String regEx) {
        // Reset state IDs before building a new NDFA
        resetStateCounter();

        System.out.println("Processing regex: " + regEx);
        RegExTree parsedRegex = RegExTreeParser.parse(regEx);

        // Convert the parsed regular expression into an NDFA
        assert parsedRegex != null;
        ndfa = NDFAParser.parseTreeToNDFA(parsedRegex);
    }

    /**
     * Reads a CSV-like file containing regular expressions (one per line) and converts each expression
     * into a corresponding NDFA, which is then exported to a JSON file.
     *
     * <p>The file containing regular expressions is located in {@code src.step2_TreeToNDFA.export},
     * and the JSON files will be generated in the same folder.</p>
     *
     * @param inputFilePath The path to the CSV file containing regular expressions.
     * @param outputFolderPath The path to the directory where JSON files will be saved.
     */
    public static void processRegexFile(String inputFilePath, String outputFolderPath) {
        File folder = new File(outputFolderPath);

        // Create the output folder if it doesn't exist
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            int regexIndex = 1;

            // Read the file line by line
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }

                // Build NDFA from the current regex line
                buildNDFAFromRegex(line.trim());

                String filename = outputFolderPath + "/NDFASample" + regexIndex + ".json";
                regexIndex++;

                // Export the NDFA to a JSON file
                NDFAExporter exporter = new NDFAExporter();
                exporter.toJsonFile(ndfa, filename);

                System.out.println("Generated JSON for regex: " + line);
            }

        } catch (IOException e) {
            System.err.println("Error reading the regex file: " + e.getMessage());
        }
    }

    /**
     * Main method to execute the conversion from regular expressions to NDFA.
     * <p>
     * The input CSV file and output folder are both in {@code src.step2_TreeToNDFA.export}.
     */
    public static void main(String[] args) {
        String inputFilePath = "Backend/resources/regexLists/regexList0.csv";
        String outputFolderPath = "Backend/resources/samples/ndfa";

        // Check if the specified file exists
        File inputFile = new File(inputFilePath);
        if (!inputFile.exists()) {
            System.out.println("Error: The file regexList0.csv was not found in the input folder.");
            return;
        }

        // Process the specified file
        processRegexFile(inputFilePath, outputFolderPath);
    }
}