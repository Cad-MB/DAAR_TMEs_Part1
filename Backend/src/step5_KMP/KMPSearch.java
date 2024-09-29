package src.step5_KMP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class KMPSearch {

    // ANSI escape code for green text
    public static final String GREEN = "\u001B[32m";
    public static final String RESET = "\u001B[0m";

    /**
     * Method to search for a pattern in the given text using the KMP algorithm.
     *
     * @param text The text where the pattern will be searched.
     * @param pattern The pattern (factor) to search in the text.
     * @return The starting index of the first occurrence of the pattern in the text, or -1 if not found.
     */
    public static int search(String text, String pattern) {
        int[] retenue = KMP_algo.buildRetentionTable(pattern);
        int i = 0;
        int j = 0;

        while (i < text.length()) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }

            if (j == pattern.length()) {
                return i - j; // Pattern found, return the starting index
            } else if (i < text.length() && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = retenue[j];
                } else {
                    i++;
                }
            }
        }

        return -1; // Pattern not found
    }

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
     * Method to find and highlight all lines containing the pattern.
     *
     * @param text The full text in which to search for the pattern.
     * @param pattern The pattern to search for and highlight.
     */
    public static void highlightPatternInText(String text, String pattern) {
        String[] lines = text.split("\n");  // Split text by lines

        for (String line : lines) {
            int index = line.toLowerCase().indexOf(pattern.toLowerCase()); // Case-insensitive search
            if (index != -1) {
                // Highlight the word
                String highlightedLine = line.substring(0, index)
                        + GREEN + line.substring(index, index + pattern.length()) + RESET
                        + line.substring(index + pattern.length());
                System.out.println(highlightedLine);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt the user for the pattern

        // File to search the pattern in

        try (scanner) {
            System.out.print("Pattern (word) to search for in the text: ");
            String pattern = scanner.nextLine();
            String filename = "Backend/test/step5/41011-0.txt";
            // Read the file content into a String
            String text = readFile(filename);

            // Search for the pattern in the text and highlight lines containing it
            highlightPatternInText(text, pattern);

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }
}
