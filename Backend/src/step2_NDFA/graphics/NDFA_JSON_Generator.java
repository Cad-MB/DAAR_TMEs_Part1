package src.step2_NDFA.graphics;

import src.step1_RegExTreeConversion.RegExTree_Struct;
import src.step1_RegExTreeConversion.RegExTree_from_RegEx_Parser;
import src.step2_NDFA.NDFA_Struct;
import src.step2_NDFA.NDFA_from_RegExTree_Parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * The {@code NDFA_JSON_Generator} class reads regular expressions from a CSV-like file,
 * converts each expression into a Non-Deterministic Finite Automaton (NDFA),
 * and exports each NDFA as a JSON file.
 *
 * <p>The input file and output JSON files are located in the directory
 * {@code src.step2_TreeToNDFA.graphics}.</p>
 */
public class NDFA_JSON_Generator {

    protected static NDFA_Struct ndfa;

    /**
     * Resets the state ID counter before generating a new NDFA.
     */
    public static void resetStateCounter() {
        NDFA_Struct.Etat.compteur = 0;
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
        RegExTree_Struct parsedRegex = RegExTree_from_RegEx_Parser.parse(regEx);

        // Convert the parsed regular expression into an NDFA
        assert parsedRegex != null;
        ndfa = NDFA_from_RegExTree_Parser.parseTreeToNDFA(parsedRegex);
    }

    /**
     * Reads a CSV-like file containing regular expressions (one per line) and converts each expression
     * into a corresponding NDFA, which is then exported to a JSON file.
     *
     * <p>The file containing regular expressions is located in {@code src.step2_TreeToNDFA.graphics},
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

                String filename = outputFolderPath + "/NDFA_Sample_" + regexIndex + ".json";
                regexIndex++;

                // Export the NDFA to a JSON file
                NDFA_JSON_Exporter exporter = new NDFA_JSON_Exporter();
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
     * The input CSV file and output folder are both in {@code src.step2_TreeToNDFA.graphics}.
     */
    public static void main(String[] args) {
        String inputFilePath = "Backend/src/step2_NDFA/graphics/Samples/regex_list.csv";
        String outputFolderPath = "Backend/src/step2_NDFA/graphics/Samples";

        processRegexFile(inputFilePath, outputFolderPath);
    }
}